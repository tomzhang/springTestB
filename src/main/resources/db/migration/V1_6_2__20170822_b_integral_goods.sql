ALTER TABLE `b_integral_goods` ADD COLUMN `limit_each` int(10) DEFAULT '0' COMMENT '每人限制兑换' AFTER `limit_count`;
ALTER TABLE `b_integral_goods` modify COLUMN store_ids VARCHAR(5000) DEFAULT '' comment '自提门店id';