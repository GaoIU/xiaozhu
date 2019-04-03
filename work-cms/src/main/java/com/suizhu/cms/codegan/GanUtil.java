package com.suizhu.cms.codegan;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import cn.hutool.core.util.StrUtil;

public class GanUtil {

	/**
	 * @dec 生成代码
	 * @date Feb 22, 2019
	 * @author gaochao
	 * @param driverClassName
	 * @param url
	 * @param username
	 * @param password
	 * @param codeGan
	 */
	public static void codeGan(String driverClassName, String url, String username, String password, CodeGan codeGan) {
		AutoGenerator mpg = new AutoGenerator();

		// 选择 freemarker 引擎，默认 Veloctiy
		// mpg.setTemplateEngine(new FreemarkerTemplateEngine());

		// 全局配置
		GlobalConfig gc = new GlobalConfig();
		gc.setOutputDir(codeGan.getDir());
		gc.setOpen(false);
		gc.setFileOverride(true);
		gc.setActiveRecord(true);// 不需要ActiveRecord特性的请改为false
		gc.setEnableCache(false);// XML 二级缓存
		gc.setBaseResultMap(true);// XML ResultMap
		gc.setBaseColumnList(false);// XML columList
		// gc.setKotlin(true) 是否生成 kotlin 代码
		gc.setAuthor(codeGan.getAuthor());

		// 自定义文件命名，注意 %s 会自动填充表实体属性！
		gc.setMapperName("%sMapper");
		gc.setXmlName("%sMapper");
		gc.setServiceName("%sService");
		gc.setServiceImplName("%sServiceImpl");
		gc.setControllerName("%sController");

		mpg.setGlobalConfig(gc);

		// 数据源配置
		DataSourceConfig dsc = new DataSourceConfig();
		dsc.setUrl(url);
		// dsc.setSchemaName("public");
		dsc.setDriverName(driverClassName);
		dsc.setUsername(username);
		dsc.setPassword(password);
		mpg.setDataSource(dsc);

		// 包配置
		PackageConfig pc = new PackageConfig();
		pc.setModuleName(codeGan.getModelName());

		pc.setParent(codeGan.getParentName());
		mpg.setPackageInfo(pc);

		// 自定义配置
		InjectionConfig cfg = new InjectionConfig() {
			@Override
			public void initMap() {
				// to do nothing
			}
		};

		// 如果模板引擎是 freemarker
		String templatePath = "/templates/mapper.xml.ftl";
		// 如果模板引擎是 velocity
		// String templatePath = "/templates/mapper.xml.vm";

		// 自定义输出配置
		List<FileOutConfig> focList = new ArrayList<>();
		// 自定义配置会被优先输出
		focList.add(new FileOutConfig(templatePath) {
			@Override
			public String outputFile(TableInfo tableInfo) {
				// 自定义输出文件名
				return codeGan.getDir() + pc.getModuleName() + "/" + tableInfo.getEntityName() + "Mapper"
						+ StringPool.DOT_XML;
			}
		});

		cfg.setFileOutConfigList(focList);
		mpg.setCfg(cfg);

		// 配置模板
		TemplateConfig templateConfig = new TemplateConfig();

		// 配置自定义输出模板
		// templateConfig.setEntity();
		// templateConfig.setService();
		// templateConfig.setController();

		templateConfig.setXml(null);
		mpg.setTemplate(templateConfig);

		// 策略配置
		StrategyConfig strategy = new StrategyConfig();
		strategy.setNaming(NamingStrategy.underline_to_camel);
		strategy.setColumnNaming(NamingStrategy.underline_to_camel);
		// strategy.setSuperEntityClass("com.baomidou.ant.common.BaseEntity");
		strategy.setEntityLombokModel(true);
		strategy.setRestControllerStyle(true);
		strategy.setSuperServiceClass("com.suizhu.common.core.service.IService");
		strategy.setSuperServiceImplClass("com.suizhu.common.core.service.impl.ServiceImpl");
		// strategy.setSuperControllerClass("com.baomidou.ant.common.BaseController");
		strategy.setInclude(codeGan.getTableNames());
		// strategy.setSuperEntityColumns("id");
		strategy.setControllerMappingHyphenStyle(true);
		if (StrUtil.isNotBlank(codeGan.getTablePrefix())) {
			String[] tps = codeGan.getTablePrefix().split(",");
			strategy.setTablePrefix(tps);
		}
		mpg.setStrategy(strategy);
		mpg.setTemplateEngine(new FreemarkerTemplateEngine());
		mpg.execute();
	}

	private GanUtil() {
	}

}
