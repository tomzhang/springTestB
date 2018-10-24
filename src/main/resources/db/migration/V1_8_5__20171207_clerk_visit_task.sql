# add job
INSERT INTO `schedule_meta` (`name`, `bean_name`, `method`, `server_addr`, `cron_exp`, `status`) SELECT '定时通知回访任务', 'VisitSchedule', 'visitSchedule', server_addr, '0 59 8 * * ?', 1 FROM schedule_meta ORDER BY id desc LIMIT 1;
INSERT INTO `schedule_meta` (`name`, `bean_name`, `method`, `server_addr`, `cron_exp`, `status`) SELECT '修改回访任务状态', 'VisitSchedule', 'changeClerkVisitStatus', server_addr, '0 59 23 * * ?', 1 FROM schedule_meta ORDER BY id desc LIMIT 1;
# === end insert data ===
