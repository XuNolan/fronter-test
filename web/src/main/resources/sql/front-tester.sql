use front_tester;

create table if not exists `usecase`
(
    `id`  bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用例名',
    `description` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用例描述',
    `created`          int(11) NOT NULL DEFAULT '0' COMMENT '创建时间',
    `updated`          int(11) NOT NULL DEFAULT '0' COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
AUTO_INCREMENT = 1
DEFAULT Charset = utf8mb4
COLLATE = utf8mb4_unicode_ci COMMENT = '用例信息';


create table if not exists `script`
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `usecase_id` bigint(20) NOT NULL,
    `version` varchar(16) NOT NULL,
    `name` varchar(20) NOT NULL default '' COLLATE utf8mb4_unicode_ci COMMENT '用例名',
    `description` varchar(64) NOT NULL default '' COLLATE utf8mb4_unicode_ci COMMENT '简单介绍',
    `data`       varchar(1024) NOT NULL default '' COLLATE utf8mb4_unicode_ci COMMENT '脚本数据',
    `is_active` tinyint NOT NULL COMMENT '是否活动：0否. 1.是， 一个usecase至多有一个活动',
    `created`          int(11) NOT NULL DEFAULT '0' COMMENT '创建时间',
    `updated`          int(11) NOT NULL DEFAULT '0' COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY (`version`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT Charset = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '用例脚本信息';


# # 用例组信息（执行管理） 功能与执行组重复。暂时不考虑用例组。
#
# create table if not exists `usecase_group`
# (
#     `group_id` bigint(20) NOT NULL,
#     `group_name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用例组名',
#     `created`          int(11) NOT NULL DEFAULT '0' COMMENT '创建时间',
#     `updated`          int(11) NOT NULL DEFAULT '0' COMMENT '更新时间',
#     `is_deleted`       tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0.不删除 1.删除',
#     PRIMARY KEY (`group_id`)
# )ENGINE = InnoDB
#  DEFAULT Charset = utf8mb4
#  COLLATE = utf8mb4_unicode_ci COMMENT = '用例组信息';
#
# create table if not exists `usecase_group_relation`
# (
#     `group_id` bigint(20) NOT NULL COMMENT '用例组id',
#     `usecase_id` bigint(20) NOT NULL COMMENT '用例id',
#     PRIMARY KEY (`group_id`,`usecase_id`)
# ) ENGINE = InnoDB
#   DEFAULT Charset = utf8mb4
#   COLLATE = utf8mb4_unicode_ci COMMENT = '用例组关联信息';


create table if not exists `execute_log`
(
    `id`  bigint(20) NOT NULL AUTO_INCREMENT,
    `script_id` bigint(20) NOT NULL COMMENT '所属的脚本id',
    `execute_group_id` bigint(20) NOT NULL COMMENT '批量执行状态下的组别id，若仅为单例执行则为固定值或null',
    `status` tinyint(4) NOT NULL COMMENT '执行状态：0执行成功 1执行失败 2执行中止',
    `log_data` varchar(1024) COMMENT '执行log信息， 所有脚本执行的文本信息记录在此处',
    `is_recorded` tinyint(4) NOT NULL COMMENT '是否有录像',
    `record_id` bigint(20) COMMENT '录像信息',
    `execute_time` int(11) NOT NULL DEFAULT '0' COMMENT '单日志执行时间',
    PRIMARY KEY (`id`),
    KEY(`execute_group_id`)

)   ENGINE = InnoDB
    DEFAULT Charset = utf8mb4
    COLLATE = utf8mb4_unicode_ci COMMENT = '单一用例执行日志';

create table if not exists `execute_group_id`
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `execute_name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '执行组名',
    `group_execute_status` tinyint(4) NOT NULL COMMENT '整个组的执行状态：0执行成功 1执行失败 2执行中止',
     PRIMARY KEY (`id`)
) ENGINE = InnoDB
 DEFAULT Charset = utf8mb4
 COLLATE = utf8mb4_unicode_ci COMMENT = '执行组信息';

# 未来对组添加定时信息。和其他配置信息（单次、多次、周期）
# 每一次批量执行都创建一个临时组。

create table if not exists `record_info`
(
    `record_id` bigint(20)  NOT NULL auto_increment COMMENT '执行记录id',
    `log_id` bigint(20) NOT NULL COMMENT '日志id',
    `record_data`longblob NOT NULL COMMENT '录像信息',
    PRIMARY KEY (`record_id`)
) ENGINE = InnoDB
  DEFAULT Charset = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '录像信息关联信息';

insert into usecase (id, name, description, created, updated) values (1, '用例名1', '描述1', 1646274195, 1646274195);
insert into usecase (id, name, description, created, updated) values (2, '用例名2', '描述2', 1646274144, 1646274144);

insert into script (id, usecase_id, version, name, description, data, is_active, created, updated)
values (1, 1, '1.0', '脚本1', '用例1 的脚本1', 'script \n test test \n test', 1, 1646274195, 1646274195);

insert into script (id, usecase_id, version, name, description, data, is_active, created, updated)
values (2, 1, '2.0', '脚本2', '用例1 的脚本2', 'script \n test test \n test failed', 0, 1646274122, 1646274122);

insert into script (id, usecase_id, version, name, description, data, is_active, created, updated)
values (3, 2, '1.0', '脚本1', '用例2 的脚本1', 'scriptvslduvgls \n test test \n test', 1, 1646274155, 1646274155);