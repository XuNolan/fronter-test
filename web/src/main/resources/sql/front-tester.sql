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
    `execute_time` int(11) NOT NULL DEFAULT '0' COMMENT '执行时间',
    `status` tinyint(4) NOT NULL COMMENT '执行状态：0执行成功 1执行失败 2执行中止',
    PRIMARY KEY (`id`),
    KEY(`execute_group_id`)
)   ENGINE = InnoDB
    DEFAULT Charset = utf8mb4
    COLLATE = utf8mb4_unicode_ci COMMENT = '单一脚本执行日志，必定存在';

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

create table if not exists `execute_log_record_related`(
    `id`  bigint(20) NOT NULL AUTO_INCREMENT,
   `record_id` bigint(20) NOT NULL COMMENT '录制id',
   `execute_group_id` bigint(20) NOT NULL COMMENT '脚本id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
 DEFAULT Charset = utf8mb4
 COLLATE = utf8mb4_unicode_ci COMMENT = '录制文件与脚本关联id';


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

-- ===== 示例数据 ======
-- 插入示例用例
INSERT INTO `usecase` (`id`, `name`, `description`, `created`, `updated`) VALUES
(1, '登录功能测试', '测试用户登录流程', UNIX_TIMESTAMP(), UNIX_TIMESTAMP()),
(2, '商品浏览测试', '测试商品列表和详情页', UNIX_TIMESTAMP(), UNIX_TIMESTAMP()),
(3, '购物车测试', '测试购物车添加和结算', UNIX_TIMESTAMP(), UNIX_TIMESTAMP());

-- 插入示例脚本
INSERT INTO `script` (`id`, `usecase_id`, `version`, `name`, `description`, `data`, `is_active`, `created`, `updated`) VALUES
(1, 1, 'v1.0', '正常登录', '使用正确的用户名密码登录', 'Feature: 登录测试\nScenario: 正常登录', 1, UNIX_TIMESTAMP(), UNIX_TIMESTAMP()),
(2, 1, 'v1.1', '错误密码登录', '使用错误密码登录', 'Feature: 登录测试\nScenario: 错误密码', 0, UNIX_TIMESTAMP(), UNIX_TIMESTAMP()),
(3, 2, 'v1.0', '浏览商品列表', '打开商品列表页面', 'Feature: 商品浏览\nScenario: 列表页', 1, UNIX_TIMESTAMP(), UNIX_TIMESTAMP()),
(4, 2, 'v1.0', '查看商品详情', '点击商品查看详情', 'Feature: 商品浏览\nScenario: 详情页', 0, UNIX_TIMESTAMP(), UNIX_TIMESTAMP()),
(5, 3, 'v1.0', '添加商品到购物车', '将商品添加到购物车', 'Feature: 购物车\nScenario: 添加商品', 1, UNIX_TIMESTAMP(), UNIX_TIMESTAMP()),
(6, 3, 'v1.0', '购物车结算', '在购物车中进行结算', 'Feature: 购物车\nScenario: 结算', 0, UNIX_TIMESTAMP(), UNIX_TIMESTAMP());

-- 插入示例执行组
INSERT INTO `execute_group_id` (`id`, `group_name`, `created`, `updated`) VALUES
(1, '完整购物流程测试', UNIX_TIMESTAMP(), UNIX_TIMESTAMP()),
(2, '登录相关测试', UNIX_TIMESTAMP(), UNIX_TIMESTAMP());

-- 插入执行组与脚本关联（完整购物流程：登录 -> 浏览商品 -> 添加购物车 -> 结算）
INSERT INTO `execute_group_script_related` (`execute_group_id`, `script_id`, `index`) VALUES
(1, 1, 0),  -- 第一步：正常登录
(1, 3, 1),  -- 第二步：浏览商品列表
(1, 4, 2),  -- 第三步：查看商品详情
(1, 5, 3),  -- 第四步：添加到购物车
(1, 6, 4);  -- 第五步：购物车结算

-- 插入执行组与脚本关联（登录相关测试）
INSERT INTO `execute_group_script_related` (`execute_group_id`, `script_id`, `index`) VALUES
(2, 1, 0),  -- 正常登录
(2, 2, 1);  -- 错误密码登录