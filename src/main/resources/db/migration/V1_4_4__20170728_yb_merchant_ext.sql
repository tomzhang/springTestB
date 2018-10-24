ALTER TABLE `yb_merchant_ext`
ADD COLUMN `integral_shop`  int NULL DEFAULT 0 COMMENT '0:积分商城未开启 1：开启' AFTER `has_erp_price`,
ADD COLUMN `integral_name`  varchar(50) NULL DEFAULT '积分' COMMENT '积分名称' AFTER `integral_shop`;