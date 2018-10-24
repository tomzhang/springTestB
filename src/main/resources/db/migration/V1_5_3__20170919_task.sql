# === begin create table ===
CREATE TABLE IF NOT EXISTS `t_examination` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `site_id` mediumint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '商户id 默认0 51健康添加',
  `admin_type` tinyint NOT NULL COMMENT '创建人类型',
  `admin_id` INT NOT NULL COMMENT '创建人ID',
  `admin_name` varchar(32) NOT NULL COMMENT '创建人名称',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `drug_category` int UNSIGNED NOT NULL COMMENT '药品分类',
  `category_name` VARCHAR(32) NOT NULL COMMENT '药品分类名',
  `disease_category` VARCHAR(255) NOT NULL COMMENT '疾病分类',
  `trained_category` int UNSIGNED NOT NULL COMMENT '培训分类',
  `quest_num` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '问题数量',
  `second_total` MEDIUMINT UNSIGNED NOT NULL COMMENT '答题时间',
  `quest_types` varchar(32) NOT NULL COMMENT '10 单选 20 多选 逗号分割',
  `brand` varchar(32) NOT NULL DEFAULT '' COMMENT '品牌',
  `enterprise` varchar(32) NOT NULL DEFAULT '',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 10 COMMENT '状态 10有效 20无效',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
)
COMMENT = '试卷';

CREATE TABLE IF NOT EXISTS `t_examination_ext` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `exam_id` INT UNSIGNED NOT NULL COMMENT '试卷id',
  `content` text NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY idx_exam_id(`exam_id`)
)
COMMENT = '试卷扩展表';

CREATE TABLE IF NOT EXISTS `t_question` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `exam_id` int UNSIGNED NOT NULL COMMENT '试卷编号 ',
  `num` tinyint UNSIGNED NOT NULL COMMENT '第多少题',
  `content` varchar(1000) NOT NULL COMMENT '题干',
  `expound` varchar(1000) NOT NULL COMMENT '讲解',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 10 COMMENT '状态 10 有效 20 无效',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
)
COMMENT = '试卷问题';

CREATE TABLE IF NOT EXISTS `t_answer` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `quest_id` int UNSIGNED NOT NULL,
  `num` enum('A','B','C','D','E','F','G','H') NOT NULL,
  `content` varchar(255) NOT NULL,
  `checked` bit NOT NULL DEFAULT 0 COMMENT '正确答案',
  PRIMARY KEY (`id`)
)
COMMENT = '题目答案';

CREATE TABLE IF NOT EXISTS `t_disease` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `type` smallint UNSIGNED NOT NULL,
  PRIMARY KEY (`id`)
)
COMMENT = '疾病分类';

CREATE TABLE IF NOT EXISTS `t_trained` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `type` smallint UNSIGNED NOT NULL,
  PRIMARY KEY (`id`)
)
COMMENT = '培训分类';;

CREATE TABLE IF NOT EXISTS `b_exam_answerlog` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `site_id` mediumint unsigned NOT NULL COMMENT '商家编号',
  `plan_id` int unsigned NOT NULL COMMENT '计划id',
  `task_id` int unsigned NOT NULL COMMENT '任务id',
  `execute_id` int unsigned NOT NULL COMMENT '执行计划id',
  `exam_id` int unsigned NOT NULL COMMENT '试卷id',
  `name` varchar(32) NOT NULL COMMENT '店员名称',
  `num` tinyint unsigned NOT NULL COMMENT '答题正确数',
  `total` tinyint unsigned NOT NULL COMMENT '总题数',
  `store_admin_id` int unsigned NOT NULL COMMENT '店员编号 对应b_storeadmin.id',
  `store_id` int unsigned NOT NULL COMMENT '门店id',
  `store_name` varchar(32) NOT NULL COMMENT '门店名称',
  `clerk_invitation_code` varchar(32) NOT NULL DEFAULT '' COMMENT '店员邀请码',
  `reward` int NOT NULL COMMENT '奖励值',
  `reward_type` tinyint unsigned NOT NULL COMMENT '奖励类型 和b_task一致',
  `remark` varchar(255) NOT NULL DEFAULT '',
  `snapshot` varchar(1024) NOT NULL COMMENT '答题信息 不包括题目信息 只包含问题和选择的答案编号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_site_exam_id` (`site_id`,`exam_id`)
) COMMENT='试卷答题记录';

CREATE TABLE `yb_jkexcrecord` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `site_id` mediumint(255) UNSIGNED NOT NULL ,
  `store_id` int(255) UNSIGNED NOT NULL ,
  `store_admin_id` int(255) NOT NULL,
  `exc_id` bigint UNSIGNED NOT NULL COMMENT '订单号',
  `money` varchar(255) NOT NULL COMMENT '钱',
  `jkd` varchar(255) NOT NULL COMMENT '健康豆',
  `is_use` bit NOT NULL DEFAULT b'0' COMMENT '是否兑换',
  `status` TINYINT UNSIGNED NOT NULL DEFAULT 10 COMMENT '是否有效 10 有效 20 无效',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY(`id`)
)
COMMENT = '健康豆兑换记录';

CREATE TABLE `b_task_consume_log` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `msg_id` varchar(50) NOT NULL DEFAULT ''  COMMENT '消息id ',
  `msg_type` tinyint UNSIGNED NOT NULL COMMENT '消息类型 10 count消息',
  `type_id` int UNSIGNED NOT NULL COMMENT '消息类型对应数据的唯一字段 count类型对应b_taskcount表的id字段',
  `handle_status` tinyint UNSIGNED NOT NULL COMMENT '处理状态 10 成功 20 失败',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '消费时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY(`id`)
)
COMMENT = '任务消息消费记录表';
# === end create table ===

# === begin alter table ===
ALTER TABLE `b_task_blob` ADD COLUMN `is_all` tinyint(2) NOT NULL DEFAULT '1' COMMENT '是否全选商户下的商品: 1 全选，0不全选（指定商品）' AFTER `goods_id`;
ALTER TABLE `b_task_blob` ADD COLUMN `examination_id` int(10) NOT NULL DEFAULT '0' COMMENT '试卷id，对应t_examination.id' AFTER `is_all`;

ALTER TABLE `51jk_db`.`b_task_execute`
  MODIFY COLUMN `plan_id` int UNSIGNED NOT NULL COMMENT '计划id' AFTER `task_id`,
  ADD COLUMN `count_value` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '统计值' AFTER `plan_id`,
  MODIFY COLUMN `complete` bit(1) NOT NULL DEFAULT b'0' COMMENT '完成' AFTER `count_value`;

ALTER TABLE `51jk_db`.`b_taskreward`
  MODIFY COLUMN `reward` int NOT NULL DEFAULT 0 COMMENT '奖励值 根据reward_type区分 如果是人名币 单位是分  奖励是正数  惩罚是负数' AFTER `reward_type`,
  ADD COLUMN `count_value` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '统计值' AFTER `reward`;

ALTER TABLE `51jk_db`.`b_task`
  ADD INDEX `idx_site_admin_id`(`site_id`, `admin_type`, `admin_id`) USING BTREE;

ALTER TABLE b_taskplan
  ADD COLUMN `admin_id` INT (10) UNSIGNED NOT NULL COMMENT '账号id' AFTER `source_type`,
  ADD COLUMN `admin_name` VARCHAR (32) NOT NULL COMMENT '账号名称' AFTER `admin_id`,
  MODIFY COLUMN `active_type` tinyint UNSIGNED NOT NULL COMMENT '任务有效日类型 10 每天 20 每周 30 每月' AFTER `end_time`;

ALTER TABLE b_taskplan ADD INDEX idx_admin_id( `admin_id` ) USING BTREE;

ALTER TABLE t_examination ADD INDEX idx_admin_id( `admin_id` ) USING BTREE;

ALTER TABLE b_task modify column `reward_detail` varchar(500) NULL COMMENT '奖励规则 存json';

# === end alter table ===

# === begin begin insert ===
INSERT INTO `t_quotagroup` (`id`, `name`, `type`) VALUES (3, '店员学习类', 30);
INSERT INTO `t_quota` (`id`, `group_id`, `name`, `type`, `enable`, `sort`) VALUES (8, 3, '答题', 80, 1, 1);
INSERT INTO `t_counttype` (`id`, `group_id`, `name`, `tbl_name`, `filter_condition`) VALUES (8, 3, 'APP内', '', '');
INSERT INTO t_disease (id, name, type) VALUES
  (1, '呼吸系统疾病', 10),
  (2, '消化系统疾病', 20),
  (3, '心血管疾病', 30),
  (4, '妇科疾病', 40),
  (5, '骨伤科疾病', 50),
  (6, '皮肤科疾病', 60),
  (7, '泌尿科疾病', 70),
  (8, '其他疾病', 80);
INSERT INTO t_trained (id, name, type) VALUES
  (1, '产品培训', 10),
  (2, '销售技能', 20),
  (3, '药学知识', 30),
  (4, '企业文化类', 40),
  (5, 'GSP规范类', 50),
  (6, '疾病知识类', 60),
  (7, '其他', 70);
INSERT INTO `51jk_db`.`b_message_setting`(`site_id`, `message_type`, `message_title`, `message_icon`, `message_summary`, `message_content`, `message_whereabouts`, `notification_title`, `notification_text`, `notification_logo`, `notification_logo_url`, `notification_ring`, `notification_vibrate`, `notification_clearable`, `mandatory_reminder`, `ext`, `offline`, `wifi`, `sound`) VALUES (999999, 'task_newExam', '新答题任务提醒', '任务icon', '新答题任务', '新答题任务提醒', '任务列表页', '新答题任务提醒', '新答题任务', '', '', 1, 1, 1, 0, '', 1, 0, NULL);

# === end begin insert ===

