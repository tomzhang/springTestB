ALTER TABLE `b_goodsextd`
  ADD COLUMN `net_wt`  varchar(32) NULL DEFAULT '' COMMENT '净含量' AFTER `goods_batch_no`;