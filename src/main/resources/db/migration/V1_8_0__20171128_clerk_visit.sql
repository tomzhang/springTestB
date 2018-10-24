# === begin create table ===
CREATE TABLE IF NOT EXISTS `b_clerk_visit` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `site_id` mediumint UNSIGNED NOT NULL COMMENT '商户ID',
  `store_admin_id` int UNSIGNED NOT NULL COMMENT '门店店员ID',
  `admin_name` varchar(32) NOT NULL DEFAULT '' COMMENT '店员名称',
  `store_id` int UNSIGNED NOT NULL COMMENT '门店ID',
  `store_name` varchar(32) NOT NULL COMMENT '门店名称',
  `buyer_id` int UNSIGNED NOT NULL COMMENT '会员ID',
  `buyer_name` varchar(32) NOT NULL COMMENT '会员姓名',
  `buyer_mobile` bigint UNSIGNED NOT NULL COMMENT '会员手机号',
  `activity_ids` varchar(255) NOT NULL COMMENT '回访活动id 用,分隔',
  `goods_ids` varchar(255) NOT NULL COMMENT '顾客订单中符合活动商品的ID集合, 用逗号分割',
  `visit_time` timestamp NOT NULL COMMENT '回访时间',
  `real_visit_time` timestamp NOT NULL DEFAULT 0 COMMENT '实际回访时间',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `type` int(10) DEFAULT '10' COMMENT '回访类型 默认为10 复购',
  `status` tinyint UNSIGNED NOT NULL DEFAULT 10 COMMENT '状态 10 待回访 20回访中 30 已回访 40 未回访',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) COMMENT = '店员回访';
TRUNCATE TABLE `b_clerk_visit`;

CREATE TABLE IF NOT EXISTS `b_visit_deploy` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `site_id` mediumint(8) unsigned NOT NULL COMMENT '商户ID',
  `clerk_visit_id` int(10) unsigned NOT NULL COMMENT '回访任务ID',
  `pre_store_id` int(10) NOT NULL COMMENT '调配之前门店ID',
  `pre_admin_id` int(10) unsigned NOT NULL COMMENT '调配之前店员ID',
  `pre_admin_name` varchar(32) NOT NULL COMMENT '调配之前店员姓名',
  `store_id` int(10) NOT NULL COMMENT '调配之后门店ID',
  `admin_id` int(11) NOT NULL COMMENT '调配之后店员ID',
  `admin_name` varchar(32) NOT NULL COMMENT '调配之后店员姓名',
  `operator_id` int(11) NOT NULL COMMENT '操作人ID',
  `operator_name` varchar(255) NOT NULL COMMENT '操作人名字',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
)
COMMENT = '回访调配记录';
TRUNCATE TABLE `b_visit_deploy`;

CREATE TABLE IF NOT EXISTS `b_visit_feedback` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `site_id` mediumint UNSIGNED NOT NULL,
  `visit_id` int UNSIGNED NOT NULL COMMENT '回访记录id',
  `tel_result` tinyint(255) UNSIGNED NOT NULL COMMENT '电话拨打情况 10 未拨打 20 空号 30 停机 40打通未接 50 打通被挂断 60 不是顾客本人 70 完成正常通话',
  `content` varchar(255) NOT NULL DEFAULT '' COMMENT '反馈内容',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) COMMENT '回访反馈';
TRUNCATE TABLE `b_visit_feedback`;

CREATE TABLE IF NOT EXISTS `b_visit_statistics` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `site_id` mediumint(255) UNSIGNED NOT NULL,
  `activity_id` int(255) UNSIGNED NOT NULL COMMENT '活动ID',
  `activity_name` varchar(32) NOT NULL COMMENT '活动名称',
  `member_num` int UNSIGNED NOT NULL COMMENT '应访会员数',
  `real_member_num` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '实访会员数',
  `store_num` int UNSIGNED NOT NULL COMMENT '门店数',
  `clerk_num` int UNSIGNED NOT NULL COMMENT '店员数',
  `trade_num` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '回访中店员下单数',
  `send_num` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '回访中店员发券数',
  `send_used_num` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '发券核销数',
  `sms_num` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '短信发送数',
  `page_open_num` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '页面打开数',
  `goods_num` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '活动期间回访顾客购买活动商品数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) COMMENT = '活动回访统计';
TRUNCATE TABLE `b_visit_statistics`;

CREATE TABLE IF NOT EXISTS `b_visit_desc` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `site_id` mediumint UNSIGNED NOT NULL,
  `visit_id` int(255) UNSIGNED NOT NULL COMMENT '回访ID',
  `store_admin_id` int UNSIGNED NOT NULL COMMENT '店员ID',
  `admin_name` varchar(32) NOT NULL COMMENT '店员姓名',
  `admin_mobile` long NOT NULL COMMENT '店员手机号',
  `store_id` int UNSIGNED NOT NULL COMMENT '店员门店ID',
  `store_name` varchar(32) NOT NULL COMMENT '门店名称',
  `buyer_id` int UNSIGNED NOT NULL COMMENT '会员ID',
  `buyer_mobile` long NOT NULL COMMENT '会员手机号',
  `send_coupon_num` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '店员发券数',
  `sms_status` tinyint UNSIGNED NOT NULL DEFAULT 10 COMMENT '短信发送情况  10 未发  20 已发',
  `page_status` tinyint UNSIGNED NOT NULL DEFAULT 10 COMMENT '会员是否打开活动页面 10 未发送 20 已发送顾客未打开 30 顾客已浏览',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) COMMENT = '店员回访数据';
TRUNCATE TABLE `b_visit_desc`;

CREATE TABLE `b_visit_trade` (
  `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `site_id` mediumint UNSIGNED NOT NULL,
  `visit_id` int UNSIGNED NOT NULL COMMENT '回访ID',
  `trades_id` long NOT NULL COMMENT '订单ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) COMMENT = '回访订单';
TRUNCATE TABLE `b_visit_trade`;
# === end create table ===

