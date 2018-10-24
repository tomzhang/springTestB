INSERT INTO schedule_meta (
	NAME,
	bean_name,
	method,
	server_addr,
	cron_exp,
	STATUS,
	enabled
) SELECT
	'积分商品活动定时任务',
	'integralGoodsTimingService',
	'process',
	server_addr,
	'0 0/1 * * * ?',
	0,
	1
FROM
	schedule_meta
LIMIT 1