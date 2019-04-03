/*
 Navicat Premium Data Transfer

 Source Server         : 本地环境
 Source Server Type    : MySQL
 Source Server Version : 80013
 Source Host           : localhost:3306
 Source Schema         : work

 Target Server Type    : MySQL
 Target Server Version : 80013
 File Encoding         : 65001

 Date: 26/02/2019 22:54:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for BATCH_JOB_EXECUTION
-- ----------------------------
DROP TABLE IF EXISTS `BATCH_JOB_EXECUTION`;
CREATE TABLE `BATCH_JOB_EXECUTION` (
  `JOB_EXECUTION_ID` bigint(20) NOT NULL,
  `VERSION` bigint(20) DEFAULT NULL,
  `JOB_INSTANCE_ID` bigint(20) NOT NULL,
  `CREATE_TIME` datetime NOT NULL,
  `START_TIME` datetime DEFAULT NULL,
  `END_TIME` datetime DEFAULT NULL,
  `STATUS` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EXIT_CODE` varchar(2500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EXIT_MESSAGE` varchar(2500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `LAST_UPDATED` datetime DEFAULT NULL,
  `JOB_CONFIGURATION_LOCATION` varchar(2500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`JOB_EXECUTION_ID`),
  KEY `JOB_INST_EXEC_FK` (`JOB_INSTANCE_ID`),
  CONSTRAINT `JOB_INST_EXEC_FK` FOREIGN KEY (`JOB_INSTANCE_ID`) REFERENCES `batch_job_instance` (`job_instance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for BATCH_JOB_EXECUTION_CONTEXT
-- ----------------------------
DROP TABLE IF EXISTS `BATCH_JOB_EXECUTION_CONTEXT`;
CREATE TABLE `BATCH_JOB_EXECUTION_CONTEXT` (
  `JOB_EXECUTION_ID` bigint(20) NOT NULL,
  `SHORT_CONTEXT` varchar(2500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `SERIALIZED_CONTEXT` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`JOB_EXECUTION_ID`),
  CONSTRAINT `JOB_EXEC_CTX_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `batch_job_execution` (`job_execution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for BATCH_JOB_EXECUTION_PARAMS
-- ----------------------------
DROP TABLE IF EXISTS `BATCH_JOB_EXECUTION_PARAMS`;
CREATE TABLE `BATCH_JOB_EXECUTION_PARAMS` (
  `JOB_EXECUTION_ID` bigint(20) NOT NULL,
  `TYPE_CD` varchar(6) COLLATE utf8mb4_unicode_ci NOT NULL,
  `KEY_NAME` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `STRING_VAL` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `DATE_VAL` datetime DEFAULT NULL,
  `LONG_VAL` bigint(20) DEFAULT NULL,
  `DOUBLE_VAL` double DEFAULT NULL,
  `IDENTIFYING` char(1) COLLATE utf8mb4_unicode_ci NOT NULL,
  KEY `JOB_EXEC_PARAMS_FK` (`JOB_EXECUTION_ID`),
  CONSTRAINT `JOB_EXEC_PARAMS_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `batch_job_execution` (`job_execution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for BATCH_JOB_EXECUTION_SEQ
-- ----------------------------
DROP TABLE IF EXISTS `BATCH_JOB_EXECUTION_SEQ`;
CREATE TABLE `BATCH_JOB_EXECUTION_SEQ` (
  `ID` bigint(20) NOT NULL,
  `UNIQUE_KEY` char(1) COLLATE utf8mb4_unicode_ci NOT NULL,
  UNIQUE KEY `UNIQUE_KEY_UN` (`UNIQUE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of BATCH_JOB_EXECUTION_SEQ
-- ----------------------------
BEGIN;
INSERT INTO `BATCH_JOB_EXECUTION_SEQ` VALUES (0, '0');
COMMIT;

-- ----------------------------
-- Table structure for BATCH_JOB_INSTANCE
-- ----------------------------
DROP TABLE IF EXISTS `BATCH_JOB_INSTANCE`;
CREATE TABLE `BATCH_JOB_INSTANCE` (
  `JOB_INSTANCE_ID` bigint(20) NOT NULL,
  `VERSION` bigint(20) DEFAULT NULL,
  `JOB_NAME` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `JOB_KEY` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`JOB_INSTANCE_ID`),
  UNIQUE KEY `JOB_INST_UN` (`JOB_NAME`,`JOB_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for BATCH_JOB_SEQ
-- ----------------------------
DROP TABLE IF EXISTS `BATCH_JOB_SEQ`;
CREATE TABLE `BATCH_JOB_SEQ` (
  `ID` bigint(20) NOT NULL,
  `UNIQUE_KEY` char(1) COLLATE utf8mb4_unicode_ci NOT NULL,
  UNIQUE KEY `UNIQUE_KEY_UN` (`UNIQUE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of BATCH_JOB_SEQ
-- ----------------------------
BEGIN;
INSERT INTO `BATCH_JOB_SEQ` VALUES (0, '0');
COMMIT;

-- ----------------------------
-- Table structure for BATCH_STEP_EXECUTION
-- ----------------------------
DROP TABLE IF EXISTS `BATCH_STEP_EXECUTION`;
CREATE TABLE `BATCH_STEP_EXECUTION` (
  `STEP_EXECUTION_ID` bigint(20) NOT NULL,
  `VERSION` bigint(20) NOT NULL,
  `STEP_NAME` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `JOB_EXECUTION_ID` bigint(20) NOT NULL,
  `START_TIME` datetime NOT NULL,
  `END_TIME` datetime DEFAULT NULL,
  `STATUS` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `COMMIT_COUNT` bigint(20) DEFAULT NULL,
  `READ_COUNT` bigint(20) DEFAULT NULL,
  `FILTER_COUNT` bigint(20) DEFAULT NULL,
  `WRITE_COUNT` bigint(20) DEFAULT NULL,
  `READ_SKIP_COUNT` bigint(20) DEFAULT NULL,
  `WRITE_SKIP_COUNT` bigint(20) DEFAULT NULL,
  `PROCESS_SKIP_COUNT` bigint(20) DEFAULT NULL,
  `ROLLBACK_COUNT` bigint(20) DEFAULT NULL,
  `EXIT_CODE` varchar(2500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `EXIT_MESSAGE` varchar(2500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `LAST_UPDATED` datetime DEFAULT NULL,
  PRIMARY KEY (`STEP_EXECUTION_ID`),
  KEY `JOB_EXEC_STEP_FK` (`JOB_EXECUTION_ID`),
  CONSTRAINT `JOB_EXEC_STEP_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `batch_job_execution` (`job_execution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for BATCH_STEP_EXECUTION_CONTEXT
-- ----------------------------
DROP TABLE IF EXISTS `BATCH_STEP_EXECUTION_CONTEXT`;
CREATE TABLE `BATCH_STEP_EXECUTION_CONTEXT` (
  `STEP_EXECUTION_ID` bigint(20) NOT NULL,
  `SHORT_CONTEXT` varchar(2500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `SERIALIZED_CONTEXT` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`STEP_EXECUTION_ID`),
  CONSTRAINT `STEP_EXEC_CTX_FK` FOREIGN KEY (`STEP_EXECUTION_ID`) REFERENCES `batch_step_execution` (`step_execution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for BATCH_STEP_EXECUTION_SEQ
-- ----------------------------
DROP TABLE IF EXISTS `BATCH_STEP_EXECUTION_SEQ`;
CREATE TABLE `BATCH_STEP_EXECUTION_SEQ` (
  `ID` bigint(20) NOT NULL,
  `UNIQUE_KEY` char(1) COLLATE utf8mb4_unicode_ci NOT NULL,
  UNIQUE KEY `UNIQUE_KEY_UN` (`UNIQUE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of BATCH_STEP_EXECUTION_SEQ
-- ----------------------------
BEGIN;
INSERT INTO `BATCH_STEP_EXECUTION_SEQ` VALUES (0, '0');
COMMIT;

-- ----------------------------
-- Table structure for QRTZ_BLOB_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
CREATE TABLE `QRTZ_BLOB_TRIGGERS` (
  `SCHED_NAME` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `SCHED_NAME` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for QRTZ_CALENDARS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
CREATE TABLE `QRTZ_CALENDARS` (
  `SCHED_NAME` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `CALENDAR_NAME` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for QRTZ_CRON_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
CREATE TABLE `QRTZ_CRON_TRIGGERS` (
  `SCHED_NAME` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `CRON_EXPRESSION` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TIME_ZONE_ID` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for QRTZ_FIRED_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
CREATE TABLE `QRTZ_FIRED_TRIGGERS` (
  `SCHED_NAME` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ENTRY_ID` varchar(95) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `INSTANCE_NAME` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `SCHED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL,
  `JOB_NAME` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `JOB_GROUP` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`),
  KEY `IDX_QRTZ_FT_TRIG_INST_NAME` (`SCHED_NAME`,`INSTANCE_NAME`),
  KEY `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY` (`SCHED_NAME`,`INSTANCE_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QRTZ_FT_J_G` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_FT_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_FT_T_G` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_FT_TG` (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for QRTZ_JOB_DETAILS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
CREATE TABLE `QRTZ_JOB_DETAILS` (
  `SCHED_NAME` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `JOB_NAME` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `JOB_GROUP` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `DESCRIPTION` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `IS_DURABLE` varchar(1) COLLATE utf8mb4_unicode_ci NOT NULL,
  `IS_NONCONCURRENT` varchar(1) COLLATE utf8mb4_unicode_ci NOT NULL,
  `IS_UPDATE_DATA` varchar(1) COLLATE utf8mb4_unicode_ci NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) COLLATE utf8mb4_unicode_ci NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_J_REQ_RECOVERY` (`SCHED_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QRTZ_J_GRP` (`SCHED_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for QRTZ_LOCKS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_LOCKS`;
CREATE TABLE `QRTZ_LOCKS` (
  `SCHED_NAME` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `LOCK_NAME` varchar(40) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of QRTZ_LOCKS
-- ----------------------------
BEGIN;
INSERT INTO `QRTZ_LOCKS` VALUES ('schedulerFactoryBean', 'TRIGGER_ACCESS');
COMMIT;

-- ----------------------------
-- Table structure for QRTZ_PAUSED_TRIGGER_GRPS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
CREATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS` (
  `SCHED_NAME` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for QRTZ_SCHEDULER_STATE
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
CREATE TABLE `QRTZ_SCHEDULER_STATE` (
  `SCHED_NAME` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `INSTANCE_NAME` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for QRTZ_SIMPLE_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPLE_TRIGGERS` (
  `SCHED_NAME` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(10) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for QRTZ_SIMPROP_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SIMPROP_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPROP_TRIGGERS` (
  `SCHED_NAME` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `STR_PROP_1` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `STR_PROP_2` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `STR_PROP_3` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `INT_PROP_1` int(11) DEFAULT NULL,
  `INT_PROP_2` int(11) DEFAULT NULL,
  `LONG_PROP_1` bigint(20) DEFAULT NULL,
  `LONG_PROP_2` bigint(20) DEFAULT NULL,
  `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
  `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for QRTZ_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;
CREATE TABLE `QRTZ_TRIGGERS` (
  `SCHED_NAME` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `JOB_NAME` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `JOB_GROUP` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `DESCRIPTION` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL,
  `TRIGGER_TYPE` varchar(8) COLLATE utf8mb4_unicode_ci NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_T_J` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_T_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_T_C` (`SCHED_NAME`,`CALENDAR_NAME`),
  KEY `IDX_QRTZ_T_G` (`SCHED_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_T_STATE` (`SCHED_NAME`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_N_STATE` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_N_G_STATE` (`SCHED_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_NEXT_FIRE_TIME` (`SCHED_NAME`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_ST` (`SCHED_NAME`,`TRIGGER_STATE`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_ST_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_NFT_ST_MISFIRE_GRP` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for schedule_job
-- ----------------------------
DROP TABLE IF EXISTS `schedule_job`;
CREATE TABLE `schedule_job` (
  `id` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '开发主键',
  `job_name` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务名',
  `job_group` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务组',
  `cron` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '时间表达式',
  `concurrent` smallint(6) DEFAULT NULL COMMENT '是否并发',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注描述',
  `status` smallint(6) DEFAULT NULL COMMENT '任务状态：0-开启，1-关闭',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='定时任务表';

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '开发主键',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '配置key',
  `val` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '配置val',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
  `status` smallint(6) DEFAULT '0' COMMENT '配置状态：0-正常，1-禁用',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统配置表';

-- ----------------------------
-- Table structure for sys_resource
-- ----------------------------
DROP TABLE IF EXISTS `sys_resource`;
CREATE TABLE `sys_resource` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '开发主键',
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源编码',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源地址',
  `method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '访问方式',
  `type` smallint(6) DEFAULT NULL COMMENT '资源类型：0-菜单，1-按钮，2-功能',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `icon` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '图标',
  `parent_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '上级资源ID',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '资源描述',
  `status` smallint(6) DEFAULT '0' COMMENT '资源状态：0-正常，1-禁用',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='后台资源表';

-- ----------------------------
-- Records of sys_resource
-- ----------------------------
BEGIN;
INSERT INTO `sys_resource` VALUES ('1078198174882717697', '系统管理', 'SYS_MANAGE', '', 'GET', 0, 0, 'fa fa-gears', '', NULL, 0, '2018-12-27 15:57:32', '2018-12-28 16:56:13');
INSERT INTO `sys_resource` VALUES ('1078199268207747074', '后台资源管理', 'SYS_RESOURCE', '/sysResource/gotoList', 'GET', 0, 0, 'fa fa-file-text', '1078198174882717697', NULL, 0, '2018-12-27 16:01:52', '2018-12-28 16:56:03');
INSERT INTO `sys_resource` VALUES ('1078199737055436802', '后台角色管理', 'SYS_ROLE', '/sysRole/gotoList', 'GET', 0, 1, 'fa fa-id-badge', '1078198174882717697', NULL, 0, '2018-12-27 16:03:44', '2018-12-28 16:55:53');
INSERT INTO `sys_resource` VALUES ('1078200065419108353', '后台用户管理', 'SYS_USER', '/sysUser/gotoList', 'GET', 0, 2, 'fa fa-user', '1078198174882717697', NULL, 0, '2018-12-27 16:05:02', '2018-12-28 16:55:41');
INSERT INTO `sys_resource` VALUES ('1078220737323098114', '后台资源列表', 'SYS_RESOURCE_LIST', '/sysResource', 'GET', 1, 0, 'fa fa-search', '1078199268207747074', NULL, 0, '2018-12-27 17:27:11', '2018-12-28 20:35:54');
INSERT INTO `sys_resource` VALUES ('1078547552591822849', '后台资源新增或修改页面', 'SYS_RESOURCE_INFO', '/sysResource/gotoInfo', 'GET', 2, 0, 'fa fa-info-circle', '1078199268207747074', NULL, 0, '2018-12-28 15:05:50', '2018-12-29 10:45:01');
INSERT INTO `sys_resource` VALUES ('1078549283488808961', '后台资源新增', 'SYS_RESOURCE_SAVE', '/sysResource', 'POST', 1, 1, 'fa fa-plus-circle', '1078199268207747074', NULL, 0, '2018-12-28 15:12:42', NULL);
INSERT INTO `sys_resource` VALUES ('1078567800045015041', '后台资源树形选择', 'SYS_RESOURCE_MENU', '/sysResource/view', 'GET', 2, 2, 'fa fa-tree', '1078199268207747074', NULL, 0, '2018-12-28 16:26:17', NULL);
INSERT INTO `sys_resource` VALUES ('1078571762735259650', '后台资源修改', 'SYS_RESOURCE_UPDATE', '/sysResource', 'PUT', 1, 4, 'fa fa-edit', '1078199268207747074', NULL, 0, '2018-12-28 16:42:02', NULL);
INSERT INTO `sys_resource` VALUES ('1078572985941106689', '后台资源删除', 'SYS_RESOURCE_DELETE', '/sysResource', 'DELETE', 1, 5, 'fa fa-remove', '1078199268207747074', NULL, 0, '2018-12-28 16:46:53', '2018-12-28 17:06:24');
INSERT INTO `sys_resource` VALUES ('1078573433989242881', '后台资源启用/禁用', 'SYS_RESOURCE_USABLE', '/sysResource/usable', 'PUT', 1, 6, 'fa fa-ban', '1078199268207747074', NULL, 0, '2018-12-28 16:48:40', '2018-12-28 16:57:22');
INSERT INTO `sys_resource` VALUES ('1078630546165985282', '后台角色列表', 'SYS_ROLE_LIST', '/sysRole', 'GET', 1, 0, 'fa fa-search', '1078199737055436802', NULL, 0, '2018-12-28 20:35:37', '2018-12-28 20:36:58');
INSERT INTO `sys_resource` VALUES ('1078842905723568129', '后台角色新增', 'SYS_ROLE_SAVE', '/sysRole', 'POST', 1, 1, 'fa fa-plus-circle', '1078199737055436802', NULL, 0, '2018-12-29 10:39:27', NULL);
INSERT INTO `sys_resource` VALUES ('1078843281734533121', '后台角色权限', 'SYS_ROLE_PERMISSION', '/sysRole/getPermission', 'POST', 2, 2, 'fa fa-bolt', '1078199737055436802', NULL, 0, '2018-12-29 10:40:57', NULL);
INSERT INTO `sys_resource` VALUES ('1078844198508724226', '后台角色新增或修改页面', 'SYS_ROLE_INFO', '/sysRole/gotoInfo', 'GET', 2, 1, 'fa fa-info-circle', '1078199737055436802', NULL, 0, '2018-12-29 10:44:36', '2018-12-29 10:44:43');
INSERT INTO `sys_resource` VALUES ('1078852972598452225', '后台角色修改', 'SYS_ROLE_UPDATE', '/sysRole', 'PUT', 1, 3, 'fa fa-edit', '1078199737055436802', NULL, 0, '2018-12-29 11:19:27', NULL);
INSERT INTO `sys_resource` VALUES ('1078853263708315650', '后台角色启用/禁用', 'SYS_ROLE_USABLE', '/sysRole/usable', 'PUT', 1, 4, 'fa fa-ban', '1078199737055436802', NULL, 0, '2018-12-29 11:20:37', NULL);
INSERT INTO `sys_resource` VALUES ('1078853510274670593', '后台角色删除', 'SYS_ROLE_DELETE', '/sysRole', 'DELETE', 1, 5, 'fa fa-remove', '1078199737055436802', NULL, 0, '2018-12-29 11:21:36', NULL);
INSERT INTO `sys_resource` VALUES ('1078863028886847490', '后台用户列表', 'SYS_USER_LIST', '/sysUser', 'GET', 1, 0, 'fa fa-search', '1078200065419108353', NULL, 0, '2018-12-29 11:59:25', NULL);
INSERT INTO `sys_resource` VALUES ('1078863633088921602', '后台用户新增或修改页面', 'SYS_USER_INFO', '/sysUser/gotoInfo', 'GET', 2, 1, 'fa fa-info-circle', '1078200065419108353', NULL, 0, '2018-12-29 12:01:49', NULL);
INSERT INTO `sys_resource` VALUES ('1078864006063210498', '后台用户新增', 'SYS_USER_SAVE', '/sysUser', 'POST', 1, 2, 'fa fa-plus-circle', '1078200065419108353', NULL, 0, '2018-12-29 12:03:18', NULL);
INSERT INTO `sys_resource` VALUES ('1078864464462888961', '后台用户修改', 'SYS_USER_UPDATE', '/sysUser', 'PUT', 1, 3, 'fa fa-edit', '1078200065419108353', NULL, 0, '2018-12-29 12:05:07', NULL);
INSERT INTO `sys_resource` VALUES ('1078864717798850562', '后台用户删除', 'SYS_USER_DELETE', '/sysUser', 'DELETE', 1, 4, 'fa fa-remove', '1078200065419108353', NULL, 0, '2018-12-29 12:06:08', NULL);
INSERT INTO `sys_resource` VALUES ('1078864941732741122', '后台用户启用/禁用', 'SYS_USER_USABLE', '/sysUser/usable', 'PUT', 1, 5, 'fa fa-ban', '1078200065419108353', NULL, 0, '2018-12-29 12:07:01', NULL);
INSERT INTO `sys_resource` VALUES ('1098767030147493890', '系统参数配置', 'SYS_CONFIG', '/sysConfig/gotoList', 'GET', 0, 3, 'fa fa-gamepad', '1078198174882717697', '', 0, '2019-02-22 10:10:49', NULL);
INSERT INTO `sys_resource` VALUES ('1098767387820961794', '系统参数配置列表', 'SYS_CONFIG_LIST', '/sysConfig', 'GET', 1, 0, 'fa fa-search', '1098767030147493890', '', 0, '2019-02-22 10:12:14', NULL);
INSERT INTO `sys_resource` VALUES ('1098767732424978434', '系统配置新增或修改页面', 'SYS_CONFIG_INFO', '/sysConfig/gotoInfo', 'GET', 2, 1, 'fa fa-info-circle', '1098767030147493890', '', 0, '2019-02-22 10:13:36', NULL);
INSERT INTO `sys_resource` VALUES ('1098768754715275265', '验证系统配置编码', 'SYS_CONFIG_CHECKCODE', '/sysConfig/checkCode', 'POST', 2, 2, 'fa fa-exclamation', '1098767030147493890', '', 0, '2019-02-22 10:17:40', NULL);
INSERT INTO `sys_resource` VALUES ('1098769118671810562', '新增系统参数配置', 'SYS_CONFIG_SAVE', '/sysConfig', 'POST', 1, 3, 'fa fa-plus-circle', '1098767030147493890', '', 0, '2019-02-22 10:19:07', '2019-02-22 10:20:16');
INSERT INTO `sys_resource` VALUES ('1098769353380868097', '修改系统参数配置', 'SYS_CONFIG_UPDATE', '/sysConfig', 'PUT', 1, 4, 'fa fa-edit', '1098767030147493890', '', 0, '2019-02-22 10:20:03', NULL);
INSERT INTO `sys_resource` VALUES ('1098769733061849090', '删除系统参数配置', 'SYS_CONFIG_DELETE', '/sysConfig', 'DELETE', 1, 5, '', '1098767030147493890', '', 0, '2019-02-22 10:21:33', NULL);
INSERT INTO `sys_resource` VALUES ('1098770031843094529', '禁用/启用系统参数配置', 'SYS_CONFIG_USABLE', '/sysConfig/usable', 'PUT', 1, 6, 'fa fa-ban', '1098767030147493890', '', 0, '2019-02-22 10:22:44', NULL);
INSERT INTO `sys_resource` VALUES ('1098782455321219074', '代码生成', 'CODEGAN', '/sysConfig/codeGan', 'GET', 0, 4, 'fa fa-database', '1078198174882717697', '', 0, '2019-02-22 11:12:06', NULL);
INSERT INTO `sys_resource` VALUES ('1098839351818330114', '代码生成', 'CODEGANFILE', '/sysConfig/codeGan', 'POST', 1, 0, 'fa fa-file-code-o', '1098782455321219074', '', 0, '2019-02-22 14:58:12', NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '开发主键',
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '角色名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '角色编码',
  `status` smallint(6) DEFAULT '0' COMMENT '角色状态：0-正常，1-禁用',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='后台角色表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` VALUES ('1077888402090254338', '超级管理员', 'ADMINISTRATOR', 0, '2018-12-26 19:26:36', NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_role_resource
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_resource`;
CREATE TABLE `sys_role_resource` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '开发主键',
  `sys_role_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '后台角色ID',
  `sys_resource_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '后台资源ID',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='后台角色 - 后台资源 关联表';

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '开发主键',
  `nick_name` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '昵称',
  `real_name` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
  `username` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
  `mobile` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号码',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头像',
  `status` smallint(6) DEFAULT '0' COMMENT '后台用户状态：0-正常，1-禁用',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='后台用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` VALUES ('1077889648775815170', '超级管理员', '随助', 'admin', '$2a$10$XeFViXMpvX7kpvkCG5d5n.pC51httmH8jQVw70iOf7fLaRacG3ud6', '17357103526', 'http://39.98.59.216/fastdfs/group1/M00/00/00/fwAAAVxAG66AVNssADFh82eEl3s788.gif', 0, '2018-12-26 19:31:33', '2019-02-21 15:31:21');
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '开发主键',
  `sys_user_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '后台用户ID',
  `sys_role_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '后台角色ID',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='后台用户 - 后台角色 关联表';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_role` VALUES ('1077889648851312642', '1077889648775815170', '1077888402090254338', '2018-12-26 19:31:33');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
