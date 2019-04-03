package com.suizhu.common.core.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.suizhu.common.constant.emnus.SqlEmnus;

/**
 * 顶级 Service
 * 
 * @author gaochao
 * @date Feb 19, 2019
 * @param <T>
 */
public interface IService<T> {

	/**
	 * <p>
	 * 插入一条记录（选择字段，策略插入）
	 * </p>
	 *
	 * @param entity 实体对象
	 */
	boolean save(T entity);

	/**
	 * <p>
	 * 插入（批量）
	 * </p>
	 *
	 * @param entityList 实体对象集合
	 */
	default boolean saveBatch(Collection<T> entityList) {
		return saveBatch(entityList, 1000);
	}

	/**
	 * <p>
	 * 插入（批量）
	 * </p>
	 *
	 * @param entityList 实体对象集合
	 * @param batchSize  插入批次数量
	 */
	boolean saveBatch(Collection<T> entityList, int batchSize);

	/**
	 * <p>
	 * 批量修改插入
	 * </p>
	 *
	 * @param entityList 实体对象集合
	 */
	default boolean saveOrUpdateBatch(Collection<T> entityList) {
		return saveOrUpdateBatch(entityList, 1000);
	}

	/**
	 * <p>
	 * 批量修改插入
	 * </p>
	 *
	 * @param entityList 实体对象集合
	 * @param batchSize  每次的数量
	 */
	boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize);

	/**
	 * <p>
	 * 根据 ID 删除
	 * </p>
	 *
	 * @param id 主键ID
	 */
	boolean removeById(Serializable id);

	/**
	 * <p>
	 * 根据 columnMap 条件，删除记录
	 * </p>
	 *
	 * @param columnMap 表字段 map 对象
	 */
	boolean removeByMap(Map<String, Object> columnMap);

	/**
	 * <p>
	 * 根据 entity 条件，删除记录
	 * </p>
	 *
	 * @param queryWrapper 实体包装类
	 *                     {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
	 */
	boolean remove(Wrapper<T> queryWrapper);

	/**
	 * <p>
	 * 删除（根据ID 批量删除）
	 * </p>
	 *
	 * @param idList 主键ID列表
	 */
	boolean removeByIds(Collection<? extends Serializable> idList);

	/**
	 * <p>
	 * 根据 ID 选择修改
	 * </p>
	 *
	 * @param entity 实体对象
	 */
	boolean updateById(T entity);

	/**
	 * <p>
	 * 根据 whereEntity 条件，更新记录
	 * </p>
	 *
	 * @param entity        实体对象
	 * @param updateWrapper 实体对象封装操作类
	 *                      {@link com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper}
	 */
	boolean update(T entity, Wrapper<T> updateWrapper);

	/**
	 * <p>
	 * 根据ID 批量更新
	 * </p>
	 *
	 * @param entityList 实体对象集合
	 */
	default boolean updateBatchById(Collection<T> entityList) {
		return updateBatchById(entityList, 1000);
	}

	/**
	 * <p>
	 * 根据ID 批量更新
	 * </p>
	 *
	 * @param entityList 实体对象集合
	 * @param batchSize  更新批次数量
	 */
	boolean updateBatchById(Collection<T> entityList, int batchSize);

	/**
	 * <p>
	 * TableId 注解存在更新记录，否插入一条记录
	 * </p>
	 *
	 * @param entity 实体对象
	 */
	boolean saveOrUpdate(T entity);

	/**
	 * <p>
	 * 根据 ID 查询
	 * </p>
	 *
	 * @param id 主键ID
	 */
	T getById(Serializable id);

	/**
	 * <p>
	 * 查询（根据ID 批量查询）
	 * </p>
	 *
	 * @param idList 主键ID列表
	 */
	Collection<T> listByIds(Collection<? extends Serializable> idList);

	/**
	 * <p>
	 * 查询（根据 columnMap 条件）
	 * </p>
	 *
	 * @param columnMap 表字段 map 对象
	 */
	Collection<T> listByMap(Map<String, Object> columnMap);

	/**
	 * <p>
	 * 根据 Wrapper，查询一条记录
	 * </p>
	 *
	 * @param queryWrapper 实体对象封装操作类
	 *                     {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
	 */
	default T getOne(Wrapper<T> queryWrapper) {
		return getOne(queryWrapper, false);
	}

	/**
	 * <p>
	 * 根据 Wrapper，查询一条记录
	 * </p>
	 *
	 * @param queryWrapper 实体对象封装操作类
	 *                     {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
	 * @param throwEx      有多个 result 是否抛出异常
	 */
	T getOne(Wrapper<T> queryWrapper, boolean throwEx);

	/**
	 * <p>
	 * 根据 Wrapper，查询一条记录
	 * </p>
	 *
	 * @param queryWrapper 实体对象封装操作类
	 *                     {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
	 */
	Map<String, Object> getMap(Wrapper<T> queryWrapper);

	/**
	 * <p>
	 * 根据 Wrapper，查询一条记录
	 * </p>
	 *
	 * @param queryWrapper 实体对象封装操作类
	 *                     {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
	 */
	default Object getObj(Wrapper<T> queryWrapper) {
		return SqlHelper.getObject(listObjs(queryWrapper));
	}

	/**
	 * <p>
	 * 根据 Wrapper 条件，查询总记录数
	 * </p>
	 *
	 * @param queryWrapper 实体对象封装操作类
	 *                     {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
	 */
	int count(Wrapper<T> queryWrapper);

	/**
	 * <p>
	 * 查询总记录数
	 * </p>
	 *
	 * @see Wrappers#emptyWrapper()
	 */
	default int count() {
		return count(Wrappers.<T>emptyWrapper());
	}

	/**
	 * <p>
	 * 查询列表
	 * </p>
	 *
	 * @param queryWrapper 实体对象封装操作类
	 *                     {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
	 */
	List<T> list(Wrapper<T> queryWrapper);

	/**
	 * <p>
	 * 查询所有
	 * </p>
	 *
	 * @see Wrappers#emptyWrapper()
	 */
	default List<T> list() {
		return list(Wrappers.<T>emptyWrapper());
	}

	/**
	 * <p>
	 * 翻页查询
	 * </p>
	 *
	 * @param page         翻页对象
	 * @param queryWrapper 实体对象封装操作类
	 *                     {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
	 */
	IPage<T> page(IPage<T> page, Wrapper<T> queryWrapper);

	/**
	 * <p>
	 * 无条件翻页查询
	 * </p>
	 *
	 * @param page 翻页对象
	 * @see Wrappers#emptyWrapper()
	 */
	default IPage<T> page(IPage<T> page) {
		return page(page, Wrappers.<T>emptyWrapper());
	}

	/**
	 * <p>
	 * 查询列表
	 * </p>
	 *
	 * @param queryWrapper 实体对象封装操作类
	 *                     {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
	 */
	List<Map<String, Object>> listMaps(Wrapper<T> queryWrapper);

	/**
	 * <p>
	 * 查询所有列表
	 * </p>
	 *
	 * @see Wrappers#emptyWrapper()
	 */
	default List<Map<String, Object>> listMaps() {
		return listMaps(Wrappers.<T>emptyWrapper());
	}

	/**
	 * <p>
	 * 根据 Wrapper 条件，查询全部记录
	 * </p>
	 *
	 * @param queryWrapper 实体对象封装操作类
	 *                     {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
	 */
	List<Object> listObjs(Wrapper<T> queryWrapper);

	/**
	 * <p>
	 * 查询全部记录
	 * </p>
	 *
	 * @see Wrappers#emptyWrapper()
	 */
	default List<Object> listObjs() {
		return listObjs(Wrappers.<T>emptyWrapper());
	}

	/**
	 * <p>
	 * 翻页查询
	 * </p>
	 *
	 * @param page         翻页对象
	 * @param queryWrapper 实体对象封装操作类
	 *                     {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
	 */
	IPage<Map<String, Object>> pageMaps(IPage<T> page, Wrapper<T> queryWrapper);

	/**
	 * <p>
	 * 无条件翻页查询
	 * </p>
	 *
	 * @param page 翻页对象
	 * @see Wrappers#emptyWrapper()
	 */
	default IPage<Map<String, Object>> pageMaps(IPage<T> page) {
		return pageMaps(page, Wrappers.<T>emptyWrapper());
	}

	/**
	 * @dec 是否存在
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param queryWrapper
	 * @return
	 */
	boolean exist(Wrapper<T> queryWrapper);

	/**
	 * @dec 是否存在
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param column
	 * @param sqlEmnus
	 * @param val
	 * @return
	 */
	boolean exist(String column, SqlEmnus sqlEmnus, Object val);

	/**
	 * @dec 验证唯一性
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param id
	 * @param column
	 * @param val
	 * @return
	 */
	boolean checkOnly(Serializable id, String column, Object val);

	/**
	 * @dec 查询一条记录
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param column
	 * @param sqlEmnus
	 * @param val
	 * @return
	 */
	T getOne(String column, SqlEmnus sqlEmnus, Object val);

	/**
	 * @dec 查询一条记录
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param column
	 * @param sqlEmnus
	 * @param val
	 * @param columns
	 * @return
	 */
	T getOne(String column, SqlEmnus sqlEmnus, Object val, String... columns);

	/**
	 * @dec 查询多条记录
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param column
	 * @param sqlEmnus
	 * @param val
	 * @return
	 */
	List<T> list(String column, SqlEmnus sqlEmnus, Object val);

	/**
	 * @dec 查询多条记录
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param column
	 * @param sqlEmnus
	 * @param val
	 * @param columns
	 * @return
	 */
	List<T> list(String column, SqlEmnus sqlEmnus, Object val, String... columns);

	/**
	 * @dec 根据 entity 条件，删除记录
	 * @date Mar 7, 2019
	 * @author gaochao
	 * @param column
	 * @param sqlEmnus
	 * @param val
	 * @return
	 */
	boolean remove(String column, SqlEmnus sqlEmnus, Object val);

	/**
	 * @dec 查询总条数
	 * @date Mar 14, 2019
	 * @author gaochao
	 * @param column
	 * @param sqlEmnus
	 * @param val
	 * @return
	 */
	int count(String column, SqlEmnus sqlEmnus, Object val);
}