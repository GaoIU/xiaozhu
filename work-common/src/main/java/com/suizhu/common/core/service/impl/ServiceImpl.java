package com.suizhu.common.core.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.suizhu.common.constant.emnus.SqlEmnus;
import com.suizhu.common.core.service.IService;
import com.suizhu.common.util.StrUtil;

import cn.hutool.core.util.ReflectUtil;

/**
 * IService 实现类（ 泛型：M 是 mapper 对象，T 是实体 ， PK 是主键泛型 ）
 * 
 * @author gaochao
 * @date Feb 19, 2019
 * @param <M>
 * @param <T>
 */
@SuppressWarnings("unchecked")
public class ServiceImpl<M extends BaseMapper<T>, T> implements IService<T> {

	@Autowired
	protected M baseMapper;

	/**
	 * <p>
	 * 判断数据库操作是否成功
	 * </p>
	 *
	 * @param result 数据库操作返回影响条数
	 * @return boolean
	 */
	protected boolean retBool(Integer result) {
		return SqlHelper.retBool(result);
	}

	protected Class<T> currentModelClass() {
		return ReflectionKit.getSuperClassGenericType(getClass(), 1);
	}

	/**
	 * <p>
	 * 批量操作 SqlSession
	 * </p>
	 */
	protected SqlSession sqlSessionBatch() {
		return SqlHelper.sqlSessionBatch(currentModelClass());
	}

	/**
	 * 释放sqlSession
	 *
	 * @param sqlSession session
	 */
	protected void closeSqlSession(SqlSession sqlSession) {
		SqlSessionUtils.closeSqlSession(sqlSession, GlobalConfigUtils.currentSessionFactory(currentModelClass()));
	}

	/**
	 * 获取SqlStatement
	 *
	 * @param sqlMethod
	 * @return
	 */
	protected String sqlStatement(SqlMethod sqlMethod) {
		return SqlHelper.table(currentModelClass()).getSqlStatement(sqlMethod.getMethod());
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean save(T entity) {
		return retBool(baseMapper.insert(entity));
	}

	/**
	 * 批量插入
	 *
	 * @param entityList
	 * @param batchSize
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveBatch(Collection<T> entityList, int batchSize) {
		int i = 0;
		String sqlStatement = sqlStatement(SqlMethod.INSERT_ONE);
		try (SqlSession batchSqlSession = sqlSessionBatch()) {
			for (T anEntityList : entityList) {
				batchSqlSession.insert(sqlStatement, anEntityList);
				if (i >= 1 && i % batchSize == 0) {
					batchSqlSession.flushStatements();
				}
				i++;
			}
			batchSqlSession.flushStatements();
		}
		return true;
	}

	/**
	 * <p>
	 * TableId 注解存在更新记录，否插入一条记录
	 * </p>
	 *
	 * @param entity 实体对象
	 * @return boolean
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveOrUpdate(T entity) {
		if (null != entity) {
			Class<?> cls = entity.getClass();
			TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
			if (null != tableInfo && StringUtils.isNotEmpty(tableInfo.getKeyProperty())) {
				Object idVal = ReflectionKit.getMethodValue(cls, entity, tableInfo.getKeyProperty());
				return StringUtils.checkValNull(idVal) || Objects.isNull(getById((Serializable) idVal)) ? save(entity)
						: updateById(entity);
			} else {
				throw ExceptionUtils.mpe("Error:  Can not execute. Could not find @TableId.");
			}
		}
		return false;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
		if (CollectionUtils.isEmpty(entityList)) {
			throw new IllegalArgumentException("Error: entityList must not be empty");
		}
		Class<?> cls = currentModelClass();
		TableInfo tableInfo = TableInfoHelper.getTableInfo(cls);
		int i = 0;
		try (SqlSession batchSqlSession = sqlSessionBatch()) {
			for (T anEntityList : entityList) {
				if (null != tableInfo && StringUtils.isNotEmpty(tableInfo.getKeyProperty())) {
					Object idVal = ReflectionKit.getMethodValue(cls, anEntityList, tableInfo.getKeyProperty());
					if (StringUtils.checkValNull(idVal) || Objects.isNull(getById((Serializable) idVal))) {
						batchSqlSession.insert(sqlStatement(SqlMethod.INSERT_ONE), anEntityList);
					} else {
						MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
						param.put(Constants.ENTITY, anEntityList);
						batchSqlSession.update(sqlStatement(SqlMethod.UPDATE_BY_ID), param);
					}
					// 不知道以后会不会有人说更新失败了还要执行插入 😂😂😂
					if (i >= 1 && i % batchSize == 0) {
						batchSqlSession.flushStatements();
					}
					i++;
				} else {
					throw ExceptionUtils.mpe("Error:  Can not execute. Could not find @TableId.");
				}
				batchSqlSession.flushStatements();
			}
		}
		return true;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean removeById(Serializable id) {
		return SqlHelper.delBool(baseMapper.deleteById(id));
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean removeByMap(Map<String, Object> columnMap) {
		if (ObjectUtils.isEmpty(columnMap)) {
			throw ExceptionUtils.mpe("removeByMap columnMap is empty.");
		}
		return SqlHelper.delBool(baseMapper.deleteByMap(columnMap));
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean remove(Wrapper<T> wrapper) {
		return SqlHelper.delBool(baseMapper.delete(wrapper));
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean removeByIds(Collection<? extends Serializable> idList) {
		return SqlHelper.delBool(baseMapper.deleteBatchIds(idList));
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean updateById(T entity) {
		return retBool(baseMapper.updateById(entity));
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean update(T entity, Wrapper<T> updateWrapper) {
		return retBool(baseMapper.update(entity, updateWrapper));
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean updateBatchById(Collection<T> entityList, int batchSize) {
		if (CollectionUtils.isEmpty(entityList)) {
			throw new IllegalArgumentException("Error: entityList must not be empty");
		}
		int i = 0;
		String sqlStatement = sqlStatement(SqlMethod.UPDATE_BY_ID);
		try (SqlSession batchSqlSession = sqlSessionBatch()) {
			for (T anEntityList : entityList) {
				MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
				param.put(Constants.ENTITY, anEntityList);
				batchSqlSession.update(sqlStatement, param);
				if (i >= 1 && i % batchSize == 0) {
					batchSqlSession.flushStatements();
				}
				i++;
			}
			batchSqlSession.flushStatements();
		}
		return true;
	}

	@Override
	public T getById(Serializable id) {
		return baseMapper.selectById(id);
	}

	@Override
	public Collection<T> listByIds(Collection<? extends Serializable> idList) {
		return baseMapper.selectBatchIds(idList);
	}

	@Override
	public Collection<T> listByMap(Map<String, Object> columnMap) {
		return baseMapper.selectByMap(columnMap);
	}

	@Override
	public T getOne(Wrapper<T> queryWrapper, boolean throwEx) {
		if (throwEx) {
			return baseMapper.selectOne(queryWrapper);
		}
		return SqlHelper.getObject(baseMapper.selectList(queryWrapper));
	}

	@Override
	public Map<String, Object> getMap(Wrapper<T> queryWrapper) {
		return SqlHelper.getObject(baseMapper.selectMaps(queryWrapper));
	}

	@Override
	public int count(Wrapper<T> queryWrapper) {
		return SqlHelper.retCount(baseMapper.selectCount(queryWrapper));
	}

	@Override
	public List<T> list(Wrapper<T> queryWrapper) {
		return baseMapper.selectList(queryWrapper);
	}

	@Override
	public IPage<T> page(IPage<T> page, Wrapper<T> queryWrapper) {
		return baseMapper.selectPage(page, queryWrapper);
	}

	@Override
	public List<Map<String, Object>> listMaps(Wrapper<T> queryWrapper) {
		return baseMapper.selectMaps(queryWrapper);
	}

	@Override
	public List<Object> listObjs(Wrapper<T> queryWrapper) {
		return baseMapper.selectObjs(queryWrapper).stream().filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public IPage<Map<String, Object>> pageMaps(IPage<T> page, Wrapper<T> queryWrapper) {
		return baseMapper.selectMapsPage(page, queryWrapper);
	}

	@Override
	public boolean exist(Wrapper<T> queryWrapper) {
		int count = count(queryWrapper);
		return count > 0;
	}

	@Override
	public boolean exist(String column, SqlEmnus sqlEmnus, Object val) {
		QueryWrapper<T> queryWrapper = addCondition(column, sqlEmnus, val);
		int retCount = SqlHelper.retCount(baseMapper.selectCount(queryWrapper));
		return retCount > 0;
	}

	private QueryWrapper<T> addCondition(String column, SqlEmnus sqlEmnus, Object val) {
		QueryWrapper<T> queryWrapper = new QueryWrapper<>();
		switch (sqlEmnus) {
		case EQ:
			queryWrapper.eq(column, val);
			break;

		case NE:
			queryWrapper.ne(column, val);
			break;

		case GT:
			queryWrapper.gt(column, val);
			break;

		case GE:
			queryWrapper.ge(column, val);
			break;

		case LT:
			queryWrapper.lt(column, val);
			break;

		case LE:
			queryWrapper.le(column, val);
			break;

		case LIKE_ANY:
			queryWrapper.like(column, val);
			break;

		case NOT_LIKE:
			queryWrapper.notLike(column, val);
			break;

		case LIKE_LEFT:
			queryWrapper.likeLeft(column, val);
			break;

		case LIKE_RIGHT:
			queryWrapper.likeRight(column, val);
			break;

		case BETWEEN:
			Map<String, Object> between = (Map<String, Object>) val;
			queryWrapper.between(column, between.get("val1"), between.get("val2"));
			break;

		case NOT_BETWEEN:
			Map<String, Object> notBetween = (Map<String, Object>) val;
			queryWrapper.notBetween(column, notBetween.get("val1"), notBetween.get("val2"));
			break;

		case IS_NULL:
			queryWrapper.isNull(column).or(i -> i.eq(column, ""));
			break;

		case IS_NOTNULL:
			queryWrapper.isNotNull(column);
			break;

		case IN:
			Collection<?> in = (Collection<?>) val;
			queryWrapper.in(column, in);
			break;

		case NOT_IN:
			Collection<?> notIn = (Collection<?>) val;
			queryWrapper.notIn(column, notIn);
			break;

		default:
			break;
		}

		return queryWrapper;
	}

	@Override
	public boolean checkOnly(Serializable id, String column, Object val) {
		boolean exist = exist(column, SqlEmnus.EQ, val);
		if (id != null) {
			T entity = getOne(column, SqlEmnus.EQ, val, column);
			if (entity != null) {
				Object fieldValue = ReflectUtil.getFieldValue(entity, StrUtil.underlineToCamel(column));
				if (val.equals(fieldValue)) {
					exist = false;
				}
			}
		}
		return !exist;
	}

	@Override
	public T getOne(String column, SqlEmnus sqlEmnus, Object val) {
		QueryWrapper<T> queryWrapper = addCondition(column, sqlEmnus, val);
		return SqlHelper.getObject(baseMapper.selectList(queryWrapper));
	}

	@Override
	public T getOne(String column, SqlEmnus sqlEmnus, Object val, String... columns) {
		QueryWrapper<T> queryWrapper = addCondition(column, sqlEmnus, val);
		queryWrapper.select(columns);
		return SqlHelper.getObject(baseMapper.selectList(queryWrapper));
	}

	@Override
	public List<T> list(String column, SqlEmnus sqlEmnus, Object val) {
		QueryWrapper<T> queryWrapper = addCondition(column, sqlEmnus, val);
		return list(queryWrapper);
	}

	@Override
	public List<T> list(String column, SqlEmnus sqlEmnus, Object val, String... columns) {
		QueryWrapper<T> queryWrapper = addCondition(column, sqlEmnus, val);
		queryWrapper.select(columns);
		return list(queryWrapper);
	}

	@Override
	public boolean remove(String column, SqlEmnus sqlEmnus, Object val) {
		QueryWrapper<T> queryWrapper = addCondition(column, sqlEmnus, val);
		return remove(queryWrapper);
	}

	@Override
	public int count(String column, SqlEmnus sqlEmnus, Object val) {
		QueryWrapper<T> queryWrapper = addCondition(column, sqlEmnus, val);
		return count(queryWrapper);
	}
}