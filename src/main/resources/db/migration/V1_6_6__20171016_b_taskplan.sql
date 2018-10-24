ALTER TABLE `b_taskplan` modify column `source_type`  tinyint(4) DEFAULT '10' COMMENT '发送者 10 总部 20 门店 30 51后台';
ALTER TABLE`b_taskreward` ADD COLUMN `complete_time` timestamp NULL  COMMENT '一级目标完成时间' AFTER `count_value`;
ALTER TABLE `b_task_blob` ADD UNIQUE INDEX `idx_task_id`(`task_id`) USING BTREE;
