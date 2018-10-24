# === begin create table ===
CREATE TABLE IF NOT EXISTS `t_quota` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `group_id` int UNSIGNED NOT NULL COMMENT '指标分组id',
  `name` varchar(32) NOT NULL,
  `type` tinyint UNSIGNED NULL COMMENT '类型',
  `enable` bit NULL DEFAULT 1 COMMENT '是否启动',
  `sort` tinyint(4) unsigned NOT NULL DEFAULT 0 COMMENT '排序字段',
  PRIMARY KEY (`id`)
)
COMMENT = '任务指标项';

CREATE TABLE IF NOT EXISTS `t_quotagroup` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL COMMENT '指标分组名称',
  `type` TINYINT NOT NULL COMMENT '类型',
  PRIMARY KEY (`id`)
)
COMMENT = '任务指标组';

CREATE TABLE IF NOT EXISTS `t_counttype` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `group_id` int UNSIGNED NOT NULL COMMENT '任务指标组id',
  `name` varchar(32) NOT NULL COMMENT '展示名称',
  `tbl_name` varchar(32) NOT NULL COMMENT '类型 ',
  `filter_condition` varchar(255) NULL COMMENT '过滤条件',
  PRIMARY KEY (`id`)
)
COMMENT = '统计类型表';

CREATE TABLE IF NOT EXISTS `b_task` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `site_id` mediumint UNSIGNED NOT NULL COMMENT '商户编号',
  `name` varchar(32) NOT NULL  COMMENT '任务名称',
  `target_id` int UNSIGNED NOT NULL COMMENT '任务指标id',
  `type_ids` varchar(255) NOT NULL COMMENT '统计类型列表 用,分隔',
  `time_type` tinyint UNSIGNED NOT NULL COMMENT '任务时间 10 自然日 20 自然周 30 自然月',
  `object` tinyint UNSIGNED NOT NULL COMMENT '任务对象 10 门店 20 店员',
  `reward_type` tinyint NOT NULL COMMENT '任务奖励 10 人民币 20 绩效',
  `reward_detail` varchar(255) NULL COMMENT '奖励规则 存json',
  `explain` varchar(255) NULL DEFAULT '' COMMENT '说明',
  `status` tinyint UNSIGNED NOT NULL COMMENT '任务状态 10 未开始 20 进行中 30 已结束',
  `admin_type` tinyint UNSIGNED NULL DEFAULT 10 COMMENT '创建人类型 10 总部账号 20 门店店员账号',
  `admin_id` int UNSIGNED NOT NULL COMMENT '账号id',
  `admin_name` varchar(32) NOT NULL COMMENT '账号名称',
  `reward_limit` int UNSIGNED NULL DEFAULT 0 COMMENT '任务奖励限额 0表示不限额',
  `reward_total` int(10) unsigned DEFAULT '0' COMMENT '已确认奖励总额',
  `low_target` int UNSIGNED NULL DEFAULT 0 COMMENT '惩罚最低条件 0表示无',
  `punish` int UNSIGNED NULL DEFAULT 0 COMMENT '惩罚值 只有当low_target大于0有效',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
)
COMMENT = '任务表';

CREATE TABLE IF NOT EXISTS `b_taskplan` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `site_id` mediumint UNSIGNED NOT NULL COMMENT '商户编号',
  `name` varchar(32) NOT NULL COMMENT '任务计划名称',
  `task_ids` varchar(255) NOT NULL COMMENT '任务id列表用,分隔',
  `join_type` tinyint NOT NULL DEFAULT 10 COMMENT '发送对象 10 门店 20 店员',
  `join_ids` text NOT NULL COMMENT '发送对象id列表 用,分割',
  `start_time` timestamp NOT NULL DEFAULT '0000:00:00 00:00:00',
  `end_time` timestamp NOT NULL DEFAULT '0000:00:00 00:00:00',
  `active_type` varchar(255) NOT NULL COMMENT '任务有效日类型 10 每天 20 每周 30 每月',
  `day_num` varchar(255) NULL DEFAULT '' COMMENT '每天 这个字段为空 每周 1,2,3,4,5,6,7 每月1,2,....31',
  `source_type` tinyint NULL DEFAULT 10 COMMENT '发送者 10 总部 20 门店',
  `app_position` tinyint NULL DEFAULT 10 COMMENT 'app显示位置 10 任务列表页',
  `explan` varchar(255) NULL DEFAULT '' COMMENT '说明',
  `status` tinyint NOT NULL COMMENT '计划状态 10 未开始 20 进行中 30 已结束',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
)
COMMENT = '任务计划表';

CREATE TABLE IF NOT EXISTS `b_taskreward` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `site_id` mediumint(8) unsigned NOT NULL COMMENT '商户id',
  `execute_id` int(10) unsigned NOT NULL COMMENT '任务执行计划id',
  `task_id` int(10) unsigned NOT NULL COMMENT '任务id',
  `task_name` varchar(32) NOT NULL COMMENT '任务名',
  `plan_id` int(10) unsigned NOT NULL COMMENT '计划id',
  `plan_name` varchar(32) NOT NULL COMMENT '计划名称',
  `join_type` tinyint(3) unsigned NOT NULL COMMENT '奖励对象 10门店 20 店员',
  `join_id` int(3) unsigned NOT NULL COMMENT '奖励对象id ',
  `join_name` varchar(32) NOT NULL COMMENT 'join_type=10 门店名称 join_type=20 店员名称',
  `reward_type` tinyint(4) NOT NULL COMMENT '任务奖励 10 人民币 20 绩效',
  `reward` int(11) DEFAULT '0' COMMENT '奖励值 根据reward_type区分 如果是人名币 单位是分  奖励是正数  惩罚是负数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_join_execute_id` (`site_id`,`join_id`,`execute_id`) USING BTREE
)
COMMENT='任务奖励表';

CREATE TABLE IF NOT EXISTS `b_taskreward_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `site_id` mediumint UNSIGNED NOT NULL,
  `reward_id` int unsigned NOT NULL,
  `join_type` tinyint UNSIGNED NOT NULL,
  `join_id` int UNSIGNED NOT NULL,
  `execute_id` int UNSIGNED NOT NULL,
  `task_id` int UNSIGNED NOT NULL,
  `task_name` varchar(32) NOT NULL,
  `reward_type` tinyint UNSIGNED NOT NULL,
  `value` int NOT NULL DEFAULT 0,
  `count_value` int(10) unsigned NOT NULL DEFAULT 0,
  `remark` varchar(255) NOT NULL DEFAULT '',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) ,
  INDEX `idx_site_join_id` (`site_id` ASC, `join_type` ASC, `join_id` ASC)
)
COMMENT = '任务奖励日志';

CREATE TABLE IF NOT EXISTS `b_task_blob` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `task_id` int UNSIGNED NOT NULL COMMENT '任务id',
  `goods_id` text NULL COMMENT '商品id列表用,分隔',
  PRIMARY KEY (`id`)
)
COMMENT = '任务表 大字段存储在这个表';

CREATE TABLE IF NOT EXISTS `b_taskcount` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `site_id`  mediumint UNSIGNED NOT NULL COMMENT '商户id',
  `task_id` int UNSIGNED NOT NULL COMMENT '任务id',
  `plan_id` int UNSIGNED NOT NULL COMMENT '计划id',
  `execute_id` int UNSIGNED NOT NULL COMMENT '任务执行计划id',
  `join_type` tinyint UNSIGNED NOT NULL COMMENT '统计对象类型 10 门店 20 店员 和b_task.object一致',
  `join_id` int UNSIGNED NOT NULL COMMENT '对象id join_type是10为门店id 20店员id',
  `count_value` int UNSIGNED NOT NULL COMMENT '统计值  比如订单量存10  如果是金额值单位为分 100元存10000',
  `count_start` timestamp NOT NULL DEFAULT 0 COMMENT '统计开始时间',
  `count_end` timestamp NOT NULL DEFAULT 0 COMMENT '统计结束时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
)
COMMENT = '任务统计记录表';

CREATE TABLE IF NOT EXISTS `b_task_execute` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `task_id` int UNSIGNED NOT NULL COMMENT '任务id',
  `plan_id` int NOT NULL COMMENT '计划id',
  `complete` bit NULL DEFAULT 0 COMMENT '完成',
  `start_day` date NOT NULL COMMENT '开始日期',
  `end_day` date NOT NULL COMMENT '结束日期',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_plan_task_time` (`plan_id`,`task_id`,`start_day`)
)
COMMENT = '任务执行记录表';

# === end create table ===

# === begin insert data ===
#init t_quotagroup 任务指标分组
INSERT INTO `t_quotagroup` (`id`, `name`, `type`) VALUES
  (1, '销售类', 10),
  (2, '会员服务类', 20);

# init t_counttype 统计口径
INSERT INTO `t_counttype` (`id`, `group_id`, `name`, `tbl_name`, `filter_condition`) VALUES
  (1, 1, '送货上门订单', 'b_trades', 'post_style=150'),
  (2, 1, '自提订单', 'b_trades', 'post_style=160'),
  (3, 1, '直购订单', 'b_trades', 'post_style=170'),
  (4, 2, '微商城内', 'b_member', 'mem_source=120'),
  (5, 2, '门店助手APP内', 'b_member', 'mem_source=130'),
  (6, 2, '门店后台系统内', 'b_member', 'mem_source=150'),
  (7, 2, '总部后台系统内', 'b_member', 'mem_source=140');


# init t_quota 任务指标数据
INSERT INTO `t_quota` (`id`, `group_id`, `name`, `type`, `enable`, `sort`) VALUES
  (1, 1, '订单量', 10, 1, 1),
  (2, 1, '商品销售数量', 20, 1, 4),
  (3, 1, '订单金额(商品总价)', 30, 1 , 2),
  (4, 1, '商品销售金额(商品总价)', 40, 1, 5),
  (5, 1, '订单金额(实付金额)', 50, 1, 3),
  (6, 1, '商品销售金额(实付金额)', 60, 1, 6),
  (7, 2, '注册会员量', 70, 1, 1);

# add job
INSERT INTO `schedule_meta` (`name`, `bean_name`, `method`, `server_addr`, `cron_exp`, `status`) SELECT '任务统计', 'taskSchedule', 'schedule', server_addr, '0 0/10 * * * ?', 1 FROM schedule_meta ORDER BY id desc LIMIT 1;
INSERT INTO `schedule_meta` (`name`, `bean_name`, `method`, `server_addr`, `cron_exp`, `status`) SELECT '定时修改任务计划状态', 'taskSchedule', 'autoChangeStatus', server_addr, '0 0/1 * * * ?', 1 FROM schedule_meta ORDER BY id desc LIMIT 1;
INSERT INTO `schedule_meta` (`name`, `bean_name`, `method`, `server_addr`, `cron_exp`, `status`) SELECT '定时发布任务消息', 'taskSchedule', 'autoNotifyMes', server_addr, '0 0 9 * * ?', 1 FROM schedule_meta ORDER BY id desc LIMIT 1;
# === end insert data ===
