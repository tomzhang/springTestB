ALTER TABLE `b_integrallog` ADD COLUMN `type` tinyint(3) UNSIGNED NOT NULL COMMENT '规则类型 10 注册送 20 签到送 30 完善信息送 40 下单满额送 50 咨询评价送 60 订单评价送 70 兑换' AFTER `buyer_nick`;