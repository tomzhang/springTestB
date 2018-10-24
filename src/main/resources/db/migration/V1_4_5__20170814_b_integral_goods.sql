ALTER TABLE `b_integral_goods`
ADD COLUMN `limit_each`  int(10) NULL DEFAULT 0 COMMENT '每人限制兑换' AFTER `limit_count`;

