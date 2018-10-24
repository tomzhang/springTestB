-- 给商品表排序字段增加索引
ALTER TABLE `b_goods` ADD INDEX `idx_update_time` (`update_time`) ;

ALTER TABLE  `yb_goods` ADD INDEX `idx_update_time`(`update_time`) ;

ALTER TABLE `yb_goods_sync_draft` ADD INDEX `idx_update_time`(`update_time`) ;

ALTER TABLE `b_member` ADD INDEX `idx_ban_status` (`ban_status`);