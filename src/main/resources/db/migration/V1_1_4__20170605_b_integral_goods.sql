ALTER TABLE `b_integral_goods`
CHANGE COLUMN `id_del` `is_del`  tinyint(1) NOT NULL DEFAULT 1 COMMENT '积分商品软删除（0：表示软删除，默认为1）' AFTER `num`;