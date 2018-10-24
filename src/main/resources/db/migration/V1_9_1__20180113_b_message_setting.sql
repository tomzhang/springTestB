INSERT INTO `b_message_setting` VALUES (999999, 19, 'task_visit', '新回访任务提醒', '', '新回访任务', '新回访任务提醒', '', '新回访任务提醒', '新回访任务', '', '', 1, 1, 1, 0, '2018-1-13 17:10:46', '2018-1-13 17:10:46', '', 1, 0, NULL);

ALTER TABLE `b_visit_desc`
ADD INDEX `idx_visit_id` (`site_id`, `visit_id`) USING BTREE ;
