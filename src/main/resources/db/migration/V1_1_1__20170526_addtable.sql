CREATE TABLE `b_integral_rule` (
`id` int UNSIGNED NOT NULL AUTO_INCREMENT,
`site_id` mediumint UNSIGNED NOT NULL COMMENT '商家id',
`name` varchar(32) NOT NULL DEFAULT '' COMMENT '名称',
`desc` varchar(255) NULL DEFAULT '' COMMENT '描述',
`type` tinyint UNSIGNED NOT NULL COMMENT '规则类型 10 注册送 20 签到送 30 完善信息送 40 下单满额送 50 咨询评价送 60 订单评价送',
`rule` varchar(1024) NOT NULL COMMENT '规则json',
`limit`  int UNSIGNED NULL DEFAULT 0 COMMENT '每日赠送上线',
`status` tinyint NOT NULL DEFAULT 0 COMMENT '状态 0 关闭 1开启',
`create_time` timestamp NULL DEFAULT  CURRENT_TIMESTAMP,
`update_time` timestamp NULL DEFAULT  CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`id`) ,
INDEX `idx_site_id` (`site_id` ASC)
)
COMMENT = '积分规则表';

CREATE TABLE `b_integral_rule_log` (
`id` int UNSIGNED NOT NULL AUTO_INCREMENT,
`rule_id` int UNSIGNED NOT NULL COMMENT '操作规则id',
`rule_name` varchar(32) NOT NULL COMMENT '操作规则名',
`before_status` tinyint NOT NULL COMMENT '历史状态',
`after_status` tinyint NOT NULL COMMENT '修改之后的状态',
`opateror_id` int NOT NULL COMMENT '操作人id',
`operator_name` varchar(32) NOT NULL COMMENT '操作人名称',
`create_time` timestamp NULL DEFAULT  CURRENT_TIMESTAMP,
`update_time` timestamp NULL DEFAULT  CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`id`) ,
INDEX `idx_rule_id` (`rule_id` ASC)
)
COMMENT = '规则修改记录表';

CREATE TABLE `b_integral_goods` (
`id` int UNSIGNED NOT NULL AUTO_INCREMENT,
`site_id` mediumint UNSIGNED NOT NULL COMMENT '商家id',
`goods_id` int UNSIGNED NOT NULL COMMENT '商品id',
`intrgral_exchanges` int UNSIGNED NOT NULL COMMENT '多少积分兑换商品',
`store_ids` varchar(255) NULL DEFAULT '' COMMENT '兑换自提门店',
`status` tinyint NOT NULL COMMENT '状态 0 未开始 10 进行中 20 已结束',
`start_time` timestamp NOT NULL COMMENT '兑换开始时间',
`end_time` timestamp NOT NULL COMMENT '兑换结束时间',
`create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
`update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`id`) ,
UNIQUE `idx_ugoods_id` (`site_id` ASC, `goods_id` ASC)
)
COMMENT = '积分商品';
