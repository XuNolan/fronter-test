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
    `name` varchar(20) NOT NULL default '' COLLATE utf8mb4_unicode_ci COMMENT '脚本名',
    `description` varchar(64) NOT NULL default '' COLLATE utf8mb4_unicode_ci COMMENT '脚本描述',
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

create table if not exists `execute_group_id`
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `group_name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '执行组名',
    `created`          int(11) NOT NULL DEFAULT '0' COMMENT '创建时间',
    `updated`          int(11) NOT NULL DEFAULT '0' COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT Charset = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '执行组信息';

create table if not exists `execute_log`
(
    `id`  bigint(20) NOT NULL AUTO_INCREMENT,
    `usecase_id` bigint(20) NOT NULL COMMENT '所属的用例id',
    `script_id` bigint(20) NOT NULL COMMENT '所属的脚本id',
    `execute_group_id` bigint(20) COMMENT '执行组id。若为空则单例执行',
    `log_data` varchar(2048) COMMENT '执行log信息，此脚本执行的所有文本信息',
    `record_id` bigint(20) COMMENT '录像id',
    `execute_time` int(11) NOT NULL DEFAULT '0' COMMENT '执行时间',
    `status` tinyint(4) NOT NULL COMMENT '执行状态：0执行成功 1执行失败 2执行中止',
    PRIMARY KEY (`id`),
    KEY(`execute_group_id`)
)   ENGINE = InnoDB
    DEFAULT Charset = utf8mb4
    COLLATE = utf8mb4_unicode_ci COMMENT = '单一脚本执行日志';

create table if not exists `record`
(
    `id`  bigint(20) NOT NULL AUTO_INCREMENT,
    `usecase_id` bigint(20) NOT NULL COMMENT '所属的用例id',
    `script_id` bigint(20) NOT NULL COMMENT '所属的脚本id',
    `execute_group_id` bigint(20) NOT NULL COMMENT '批量执行状态下的组别id，若仅为单例执行则为固定值或null',
    `status` tinyint(4) NOT NULL COMMENT '执行状态：0执行成功 1执行失败 2执行中止',
    `storage_type` varchar(20) NOT NULL COMMENT '存储方式，local则存储在后端本地，仅记录文件路径名；database则存储在数据库',
    `record_url` varchar(256) COMMENT 'local前提下存储在后端本地的录制文件位置',
    `record_data` LONGBLOB COMMENT 'database前提下的录制内容',
    `metadata` TEXT NOT NULL COMMENT '录制元数据',
    `record_config_type` tinyint(4) NOT NULL COMMENT '录制质量。目前暂时提供LOW/BANLANCE/HIGH三种类型, 配置固化',
    `execute_time` int(11) NOT NULL DEFAULT '0' COMMENT '单日志执行时间',
    PRIMARY KEY (`id`),
    KEY(`execute_group_id`)

)   ENGINE = InnoDB
    DEFAULT Charset = utf8mb4
    COLLATE = utf8mb4_unicode_ci COMMENT = '单一脚本录制信息';

create table if not exists `execute_group_script_related`
(
    `id`  bigint(20) NOT NULL AUTO_INCREMENT,
    `index` bigint(20) NOT NULL COMMENT '当前脚本关联在组中的执行顺序, 从0开始',
    `script_id` bigint(20) NOT NULL COMMENT '脚本id',
    `execute_group_id` bigint(20) NOT NULL COMMENT '执行组id',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_execute_group_index` (`execute_group_id`, `index`)
)   ENGINE = InnoDB
    DEFAULT Charset = utf8mb4
    COLLATE = utf8mb4_unicode_ci COMMENT = '执行组与脚本关联信息';