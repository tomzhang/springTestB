INSERT INTO `schedule_meta` (`name`, `bean_name`, `method`, `server_addr`, `cron_exp`, `status`) SELECT '定时更新ES索引库', 'EsSearchSchedule', 'autoUpdateESIndex', server_addr, '0 0 0 * * ?', 1 FROM schedule_meta ORDER BY id desc LIMIT 1;
