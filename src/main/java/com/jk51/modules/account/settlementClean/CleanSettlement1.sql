/*(1) 100182 武汉市东明药房连锁有限公司

迁移时间：2017-6-12 18:00

迁移之后的新的订单数：2

SELECT site_id,trades_id,create_time FROM b_trades  WHERE site_id='100182'  AND create_time > '2017-6-12 18:00'

最后结算时间：2017-6-11	23:59:59

结算日：2017-6-23

符合新结算订单数：0
*/
SELECT site_id,trades_id,create_time,
CASE trades_status
when 110 THEN '110(等侍买家付款), '
when 120 THEN '120(等待卖家发货),'
when 130 THEN '130(等侍买家确认收货),'
when 140 THEN '140(买家已签收，货到付款专用)，'
when 150 THEN '150(交易成功)，'
when 160 THEN '160(用户未付款主动关闭)，'
when 170 THEN '170(超时未付款，系统关闭)，'
when 180 THEN '180(商家关闭订单)，'
when 200 THEN '200(待取货|待自提，直购和自提专用),'
when 210 THEN '210（已取货|已自提 直购和自提专用），'
when 900 THEN '900（已退款），'
when 220 THEN '220(用户确认收货)，'
when 230 THEN '230(门店确认收货)，'
when 800 THEN '800（系统确认收货）,'
when 240 THEN '240(已取消【门店自提待自提后可取消订单】)'
else 'oo'
end '交易状态',
is_payment,pay_time,pay_style,ROUND(real_pay/100,2)  real_pay,ROUND(plat_split/100,2) plat_split,ROUND(trades_split/100,2) trades_split,ROUND(O2O_freight/100,2) O2O_freight,
deal_finish_status,end_time,settlement_status,finance_no
FROM b_trades
WHERE site_id='100182'
 AND create_time > '2017-6-12 18:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-12 00:00:00' AND '2017-6-22 12:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-12 18:00:00'
and site_id=100182 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-12 18:00:00'
and site_id=100182 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-12 18:00:00'
and site_id=100182 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-12 18:00:00'
and site_id=100182 /*清洗医保卡手续费*/;

/*6.14问题修复*/
update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-12 18:00:00'
and site_id=100182/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-12 18:00:00'
and site_id=100182 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-12 18:00:00'
and site_id=100182 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-12 18:00:00'
and site_id=100182 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100182  AND create_time>'2017-6-12 18:00:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100182  AND create_time>'2017-6-12 18:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100182  AND create_time>'2017-6-12 18:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流')/*清洗O2O配送费*/;




SELECT *  from  (
	SELECT		id,b.trades_id, 	b.create_time, 	b.pay_time, 	deal_finish_status, 	b.end_time,
 	pay_style,
 	ROUND(real_pay / 100, 2) real_pay,
 	ROUND(plat_split / 100, 2) plat_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2plat_split,
 	ROUND(plat_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '手续费差值',
 	ROUND(trades_split / 100, 2) trades_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 	ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 	ROUND(O2O_freight / 100, 2) O2O_freight,
 	ROUND(real_pay * 0.05 / 100, 2) O2O,
 	ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 	settlement_status,
 	CASE trades_status
 WHEN 110 THEN 	'110(等侍买家付款) '
 WHEN 120 THEN 	'120(等待卖家发货)'
 WHEN 130 THEN 	'130(等侍买家确认收货)'
 WHEN 140 THEN 	'140(买家已签收，货到付款专用)'
 WHEN 150 THEN 	'150(交易成功)'
 WHEN 160 THEN 	'160(用户未付款主动关闭)'
 WHEN 170 THEN 	'170(超时未付款，系统关闭)'
 WHEN 180 THEN 	'180(商家关闭订单)XXXXXXXX'
 WHEN 200 THEN 	'200(待取货|待自提，直购和自提专用)'
 WHEN 210 THEN 	'210(已取货|已自提 直购和自提专用)'
 WHEN 900 THEN 	'900(已退款)xxxxxxxxxx'
 WHEN 220 THEN 	'220(用户确认收货)'
 WHEN 230 THEN 	'230(门店确认收货)'
 WHEN 800 THEN 	'800(系统确认收货)'
 WHEN 240 THEN 	'240(已取消【门店自提待自提后可取消订单】)'
 ELSE	'oo'
 END '交易状态'
 FROM 	b_trades b
 WHERE site_id='100182'
 AND create_time > '2017-6-12 18:00:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-12 00:00:00' AND '2017-6-22 12:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style IN ('wx', 'ali') /*交易结束状态*/
 UNION

SELECT			id, 		b.trades_id, b.create_time,b.pay_time,deal_finish_status,b.end_time,pay_style,
 		ROUND(real_pay / 100, 2) real_pay,
 		ROUND(plat_split / 100, 2) plat_split,
 		0.00 2plat_split,
 		ROUND(plat_split / 100, 2) - 0.00 '手续费差值',
 		ROUND(trades_split / 100, 2) trades_split,
 		ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 		ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 		ROUND(O2O_freight / 100, 2) O2O_freight,
 		ROUND(real_pay * 0.05 / 100, 2) O2O,
 		ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 		settlement_status,
 		CASE trades_status
 	WHEN 110 THEN 		'110(等侍买家付款) '
 	WHEN 120 THEN 		'120(等待卖家发货)'
 	WHEN 130 THEN 		'130(等侍买家确认收货)'
 	WHEN 140 THEN 		'140(买家已签收，货到付款专用)'
 	WHEN 150 THEN 		'150(交易成功)'
 	WHEN 160 THEN 		'160(用户未付款主动关闭)'
 	WHEN 170 THEN 		'170(超时未付款，系统关闭)'
 	WHEN 180 THEN 		'180(商家关闭订单)XXXXXXXX'
 	WHEN 200 THEN 		'200(待取货|待自提，直购和自提专用)'
 	WHEN 210 THEN 		'210(已取货|已自提 直购和自提专用)'
 	WHEN 900 THEN 		'900(已退款)xxxxxxxxxx'
 	WHEN 220 THEN 		'220(用户确认收货)'
 	WHEN 230 THEN 		'230(门店确认收货)'
 	WHEN 800 THEN 		'800(系统确认收货)'
 	WHEN 240 THEN 		'240(已取消【门店自提待自提后可取消订单】)'
 	ELSE 		'oo'
 	END '交易状态'
 	FROM 		b_trades b
 WHERE site_id='100182'
 AND create_time > '2017-6-12 18:00:00'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-12 00:00:00' AND '2017-6-22 12:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;






/*(2) 100098	深圳市中源大药房连锁有限公司

迁移时间：2017-6-16 9:23

迁移之后的新的订单数：0

SELECT site_id,trades_id,create_time FROM b_trades  WHERE site_id='100098'  AND create_time > '2017-6-16 9:23'


最后结算时间：2017-6-15	23:59:59

结算日：2017-6-23

符合订单数：0
*/

/*查询结算周期内符合条件的订单*/
SELECT site_id,trades_id,create_time,
CASE trades_status
when 110 THEN '110(等侍买家付款), '
when 120 THEN '120(等待卖家发货),'
when 130 THEN '130(等侍买家确认收货),'
when 140 THEN '140(买家已签收，货到付款专用)，'
when 150 THEN '150(交易成功)，'
when 160 THEN '160(用户未付款主动关闭)，'
when 170 THEN '170(超时未付款，系统关闭)，'
when 180 THEN '180(商家关闭订单)，'
when 200 THEN '200(待取货|待自提，直购和自提专用),'
when 210 THEN '210（已取货|已自提 直购和自提专用），'
when 900 THEN '900（已退款），'
when 220 THEN '220(用户确认收货)，'
when 230 THEN '230(门店确认收货)，'
when 800 THEN '800（系统确认收货）,'
when 240 THEN '240(已取消【门店自提待自提后可取消订单】)'
else 'oo'
end '交易状态',
is_payment,pay_time,pay_style,ROUND(real_pay/100,2)  real_pay,ROUND(plat_split/100,2) plat_split,ROUND(trades_split/100,2) trades_split,ROUND(O2O_freight/100,2) O2O_freight,
deal_finish_status,end_time,settlement_status,finance_no
FROM b_trades
WHERE site_id='100098'
 AND create_time > '2017-6-16 9:23' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-22 12:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;


/*
	清洗数据
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:23:00'
and site_id=100098 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:23:00'
and site_id=100098 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-16 09:23:00'
and site_id=100098 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-16 09:23:00'
and site_id=100098 /*清洗医保卡手续费*/;

/*6.14问题修复*/
update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:23:00'
and site_id=100098/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:23:00'
and site_id=100098 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:23:00'
and site_id=100098 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:23:00'
and site_id=100098 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100098  AND create_time>'2017-6-16 09:23:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100098  AND create_time>'2017-6-16 09:23:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100098  AND create_time>'2017-6-16 09:23:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流')/*清洗O2O配送费*/;




/*检查金额*/
SELECT *  from  (
	SELECT		id,b.trades_id, 	b.create_time, 	b.pay_time, 	deal_finish_status, 	b.end_time,
 	pay_style,
 	ROUND(real_pay / 100, 2) real_pay,
 	ROUND(plat_split / 100, 2) plat_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2plat_split,
 	ROUND(plat_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '手续费差值',
 	ROUND(trades_split / 100, 2) trades_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 	ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 	ROUND(O2O_freight / 100, 2) O2O_freight,
 	ROUND(real_pay * 0.05 / 100, 2) O2O,
 	ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 	settlement_status,
 	CASE trades_status
 WHEN 110 THEN 	'110(等侍买家付款) '
 WHEN 120 THEN 	'120(等待卖家发货)'
 WHEN 130 THEN 	'130(等侍买家确认收货)'
 WHEN 140 THEN 	'140(买家已签收，货到付款专用)'
 WHEN 150 THEN 	'150(交易成功)'
 WHEN 160 THEN 	'160(用户未付款主动关闭)'
 WHEN 170 THEN 	'170(超时未付款，系统关闭)'
 WHEN 180 THEN 	'180(商家关闭订单)XXXXXXXX'
 WHEN 200 THEN 	'200(待取货|待自提，直购和自提专用)'
 WHEN 210 THEN 	'210(已取货|已自提 直购和自提专用)'
 WHEN 900 THEN 	'900(已退款)xxxxxxxxxx'
 WHEN 220 THEN 	'220(用户确认收货)'
 WHEN 230 THEN 	'230(门店确认收货)'
 WHEN 800 THEN 	'800(系统确认收货)'
 WHEN 240 THEN 	'240(已取消【门店自提待自提后可取消订单】)'
 ELSE	'oo'
 END '交易状态'
 FROM 	b_trades b
 WHERE site_id='100098'
 AND create_time > '2017-6-16 9:23' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-22 12:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style IN ('wx', 'ali') /*交易结束状态*/
 UNION

SELECT			id, 		b.trades_id, b.create_time,b.pay_time,deal_finish_status,b.end_time,pay_style,
 		ROUND(real_pay / 100, 2) real_pay,
 		ROUND(plat_split / 100, 2) plat_split,
 		0.00 2plat_split,
 		ROUND(plat_split / 100, 2) - 0.00 '手续费差值',
 		ROUND(trades_split / 100, 2) trades_split,
 		ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 		ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 		ROUND(O2O_freight / 100, 2) O2O_freight,
 		ROUND(real_pay * 0.05 / 100, 2) O2O,
 		ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 		settlement_status,
 		CASE trades_status
 	WHEN 110 THEN 		'110(等侍买家付款) '
 	WHEN 120 THEN 		'120(等待卖家发货)'
 	WHEN 130 THEN 		'130(等侍买家确认收货)'
 	WHEN 140 THEN 		'140(买家已签收，货到付款专用)'
 	WHEN 150 THEN 		'150(交易成功)'
 	WHEN 160 THEN 		'160(用户未付款主动关闭)'
 	WHEN 170 THEN 		'170(超时未付款，系统关闭)'
 	WHEN 180 THEN 		'180(商家关闭订单)XXXXXXXX'
 	WHEN 200 THEN 		'200(待取货|待自提，直购和自提专用)'
 	WHEN 210 THEN 		'210(已取货|已自提 直购和自提专用)'
 	WHEN 900 THEN 		'900(已退款)xxxxxxxxxx'
 	WHEN 220 THEN 		'220(用户确认收货)'
 	WHEN 230 THEN 		'230(门店确认收货)'
 	WHEN 800 THEN 		'800(系统确认收货)'
 	WHEN 240 THEN 		'240(已取消【门店自提待自提后可取消订单】)'
 	ELSE 		'oo'
 	END '交易状态'
 	FROM 		b_trades b
 WHERE site_id='100098'
 AND create_time > '2017-6-16 9:23' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-22 12:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;




/*(3) 100176	广济堂医药连锁有限公司
迁移时间：2017-6-20 9:20

迁移之后的新的订单数：6

SELECT site_id,trades_id,create_time FROM b_trades  WHERE site_id='100176'  AND create_time > '2017-6-20 9:20'

最后结算时间：2017-6-19	23:59:59
结算日：2017-6-23
符合订单数：2
*/

/*查询结算周期内符合条件的订单----------------------------------*/
SELECT site_id,trades_id,create_time,
CASE trades_status
when 110 THEN '110(等侍买家付款), '
when 120 THEN '120(等待卖家发货),'
when 130 THEN '130(等侍买家确认收货),'
when 140 THEN '140(买家已签收，货到付款专用)，'
when 150 THEN '150(交易成功)，'
when 160 THEN '160(用户未付款主动关闭)，'
when 170 THEN '170(超时未付款，系统关闭)，'
when 180 THEN '180(商家关闭订单)，'
when 200 THEN '200(待取货|待自提，直购和自提专用),'
when 210 THEN '210（已取货|已自提 直购和自提专用），'
when 900 THEN '900（已退款），'
when 220 THEN '220(用户确认收货)，'
when 230 THEN '230(门店确认收货)，'
when 800 THEN '800（系统确认收货）,'
when 240 THEN '240(已取消【门店自提待自提后可取消订单】)'
else 'oo'
end '交易状态',
is_payment,pay_time,pay_style,ROUND(real_pay/100,2)  real_pay,ROUND(plat_split/100,2) plat_split,ROUND(trades_split/100,2) trades_split,ROUND(O2O_freight/100,2) O2O_freight,
deal_finish_status,end_time,settlement_status,finance_no
FROM b_trades
WHERE site_id='100198'
 AND create_time > '2017-6-20 9:20:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-22 12:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
清洗数据
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 09:20:00'
and site_id=100176 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 09:20:00'
and site_id=100176 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-20 09:20:00'
and site_id=100176 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-20 09:20:00'
and site_id=100176 /*清洗医保卡手续费*/;

/*6.14问题修复*/
update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 09:20:00'
and site_id=100176/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 09:20:00'
and site_id=100176 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 09:20:00'
and site_id=100176 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 09:20:00'
and site_id=100176 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100176  AND create_time>'2017-6-20 09:20:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100176  AND create_time>'2017-6-20 09:20:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100176  AND create_time>'2017-6-20 09:20:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流')/*清洗O2O配送费*/;








/*检查金额----------------------------------------------------*/
SELECT *  from  (
	SELECT		id,b.trades_id, 	b.create_time, 	b.pay_time, 	deal_finish_status, 	b.end_time,
 	pay_style,
 	ROUND(real_pay / 100, 2) real_pay,
 	ROUND(plat_split / 100, 2) plat_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2plat_split,
 	ROUND(plat_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '手续费差值',
 	ROUND(trades_split / 100, 2) trades_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 	ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 	ROUND(O2O_freight / 100, 2) O2O_freight,
 	ROUND(real_pay * 0.05 / 100, 2) O2O,
 	ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 	settlement_status,
 	CASE trades_status
 WHEN 110 THEN 	'110(等侍买家付款) '
 WHEN 120 THEN 	'120(等待卖家发货)'
 WHEN 130 THEN 	'130(等侍买家确认收货)'
 WHEN 140 THEN 	'140(买家已签收，货到付款专用)'
 WHEN 150 THEN 	'150(交易成功)'
 WHEN 160 THEN 	'160(用户未付款主动关闭)'
 WHEN 170 THEN 	'170(超时未付款，系统关闭)'
 WHEN 180 THEN 	'180(商家关闭订单)XXXXXXXX'
 WHEN 200 THEN 	'200(待取货|待自提，直购和自提专用)'
 WHEN 210 THEN 	'210(已取货|已自提 直购和自提专用)'
 WHEN 900 THEN 	'900(已退款)xxxxxxxxxx'
 WHEN 220 THEN 	'220(用户确认收货)'
 WHEN 230 THEN 	'230(门店确认收货)'
 WHEN 800 THEN 	'800(系统确认收货)'
 WHEN 240 THEN 	'240(已取消【门店自提待自提后可取消订单】)'
 ELSE	'oo'
 END '交易状态'
 FROM 	b_trades b
WHERE site_id='100198'
 AND create_time > '2017-6-20 9:20:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-22 12:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style IN ('wx', 'ali') /*交易结束状态*/
 UNION

SELECT			id, 		b.trades_id, b.create_time,b.pay_time,deal_finish_status,b.end_time,pay_style,
 		ROUND(real_pay / 100, 2) real_pay,
 		ROUND(plat_split / 100, 2) plat_split,
 		0.00 2plat_split,
 		ROUND(plat_split / 100, 2) - 0.00 '手续费差值',
 		ROUND(trades_split / 100, 2) trades_split,
 		ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 		ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 		ROUND(O2O_freight / 100, 2) O2O_freight,
 		ROUND(real_pay * 0.05 / 100, 2) O2O,
 		ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 		settlement_status,
 		CASE trades_status
 	WHEN 110 THEN 		'110(等侍买家付款) '
 	WHEN 120 THEN 		'120(等待卖家发货)'
 	WHEN 130 THEN 		'130(等侍买家确认收货)'
 	WHEN 140 THEN 		'140(买家已签收，货到付款专用)'
 	WHEN 150 THEN 		'150(交易成功)'
 	WHEN 160 THEN 		'160(用户未付款主动关闭)'
 	WHEN 170 THEN 		'170(超时未付款，系统关闭)'
 	WHEN 180 THEN 		'180(商家关闭订单)XXXXXXXX'
 	WHEN 200 THEN 		'200(待取货|待自提，直购和自提专用)'
 	WHEN 210 THEN 		'210(已取货|已自提 直购和自提专用)'
 	WHEN 900 THEN 		'900(已退款)xxxxxxxxxx'
 	WHEN 220 THEN 		'220(用户确认收货)'
 	WHEN 230 THEN 		'230(门店确认收货)'
 	WHEN 800 THEN 		'800(系统确认收货)'
 	WHEN 240 THEN 		'240(已取消【门店自提待自提后可取消订单】)'
 	ELSE 		'oo'
 	END '交易状态'
 	FROM 		b_trades b
WHERE site_id='100198'
 AND create_time > '2017-6-20 9:20:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-22 12:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;



/*(4) 100104	沈阳奉天堂大药房连锁有限公司

迁移时间：2017-6-16 9:28

迁移之后的新的订单数：3

SELECT site_id,trades_id,create_time FROM b_trades  WHERE site_id='100104'  AND create_time > '2017-6-16 9:28'

最后结算时间：2017-6-15	23:59:59

结算日：2017-6-24

符合订单数：0
*/

/*查询结算周期内符合条件的订单----------------------------------*/
SELECT site_id,trades_id,create_time,
CASE trades_status
when 110 THEN '110(等侍买家付款), '
when 120 THEN '120(等待卖家发货),'
when 130 THEN '130(等侍买家确认收货),'
when 140 THEN '140(买家已签收，货到付款专用)，'
when 150 THEN '150(交易成功)，'
when 160 THEN '160(用户未付款主动关闭)，'
when 170 THEN '170(超时未付款，系统关闭)，'
when 180 THEN '180(商家关闭订单)，'
when 200 THEN '200(待取货|待自提，直购和自提专用),'
when 210 THEN '210（已取货|已自提 直购和自提专用），'
when 900 THEN '900（已退款），'
when 220 THEN '220(用户确认收货)，'
when 230 THEN '230(门店确认收货)，'
when 800 THEN '800（系统确认收货）,'
when 240 THEN '240(已取消【门店自提待自提后可取消订单】)'
else 'oo'
end '交易状态',
is_payment,pay_time,pay_style,ROUND(real_pay/100,2)  real_pay,ROUND(plat_split/100,2) plat_split,ROUND(trades_split/100,2) trades_split,ROUND(O2O_freight/100,2) O2O_freight,
deal_finish_status,end_time,settlement_status,finance_no
FROM b_trades
WHERE site_id='100104'
 AND create_time > '2017-6-16 9:28:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

SELECT * FROM b_trades  WHERE site_id=100104   AND create_time > '2017-6-16 9:28:00' ;

/*
清洗数据
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:28:00'
and site_id=100104 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:28:00'
and site_id=100104 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-16 09:28:00'
and site_id=100104 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-16 09:28:00'
and site_id=100104 /*清洗医保卡手续费*/;

/*6.14问题修复*/
update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:28:00'
and site_id=100104/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:28:00'
and site_id=100104 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:28:00'
and site_id=100104 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:28:00'
and site_id=100104 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100104  AND create_time>'2017-6-16 09:28:00'
/*清洗直购分销状态*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100104  AND create_time>'2017-6-16 09:28:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100104  AND create_time>'2017-6-16 09:28:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流')/*清洗O2O配送费*/;







/*检查金额----------------------------------------------------*/
SELECT *  from  (
	SELECT		id,b.trades_id, 	b.create_time, 	b.pay_time, 	deal_finish_status, 	b.end_time,
 	pay_style,
 	ROUND(real_pay / 100, 2) real_pay,
 	ROUND(plat_split / 100, 2) plat_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2plat_split,
 	ROUND(plat_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '手续费差值',
 	ROUND(trades_split / 100, 2) trades_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 	ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 	ROUND(O2O_freight / 100, 2) O2O_freight,
 	ROUND(real_pay * 0.05 / 100, 2) O2O,
 	ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 	settlement_status,
 	CASE trades_status
 WHEN 110 THEN 	'110(等侍买家付款) '
 WHEN 120 THEN 	'120(等待卖家发货)'
 WHEN 130 THEN 	'130(等侍买家确认收货)'
 WHEN 140 THEN 	'140(买家已签收，货到付款专用)'
 WHEN 150 THEN 	'150(交易成功)'
 WHEN 160 THEN 	'160(用户未付款主动关闭)'
 WHEN 170 THEN 	'170(超时未付款，系统关闭)'
 WHEN 180 THEN 	'180(商家关闭订单)XXXXXXXX'
 WHEN 200 THEN 	'200(待取货|待自提，直购和自提专用)'
 WHEN 210 THEN 	'210(已取货|已自提 直购和自提专用)'
 WHEN 900 THEN 	'900(已退款)xxxxxxxxxx'
 WHEN 220 THEN 	'220(用户确认收货)'
 WHEN 230 THEN 	'230(门店确认收货)'
 WHEN 800 THEN 	'800(系统确认收货)'
 WHEN 240 THEN 	'240(已取消【门店自提待自提后可取消订单】)'
 ELSE	'oo'
 END '交易状态'
 FROM 	b_trades b
WHERE site_id='100198'
 AND create_time > '2017-6-20 9:20:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-22 12:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 250	OR settlement_status = 200)
 AND pay_style IN ('wx', 'ali') /*交易结束状态*/
 UNION

SELECT			id, 		b.trades_id, b.create_time,b.pay_time,deal_finish_status,b.end_time,pay_style,
 		ROUND(real_pay / 100, 2) real_pay,
 		ROUND(plat_split / 100, 2) plat_split,
 		0.00 2plat_split,
 		ROUND(plat_split / 100, 2) - 0.00 '手续费差值',
 		ROUND(trades_split / 100, 2) trades_split,
 		ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 		ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 		ROUND(O2O_freight / 100, 2) O2O_freight,
 		ROUND(real_pay * 0.05 / 100, 2) O2O,
 		ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 		settlement_status,
 		CASE trades_status
 	WHEN 110 THEN 		'110(等侍买家付款) '
 	WHEN 120 THEN 		'120(等待卖家发货)'
 	WHEN 130 THEN 		'130(等侍买家确认收货)'
 	WHEN 140 THEN 		'140(买家已签收，货到付款专用)'
 	WHEN 150 THEN 		'150(交易成功)'
 	WHEN 160 THEN 		'160(用户未付款主动关闭)'
 	WHEN 170 THEN 		'170(超时未付款，系统关闭)'
 	WHEN 180 THEN 		'180(商家关闭订单)XXXXXXXX'
 	WHEN 200 THEN 		'200(待取货|待自提，直购和自提专用)'
 	WHEN 210 THEN 		'210(已取货|已自提 直购和自提专用)'
 	WHEN 900 THEN 		'900(已退款)xxxxxxxxxx'
 	WHEN 220 THEN 		'220(用户确认收货)'
 	WHEN 230 THEN 		'230(门店确认收货)'
 	WHEN 800 THEN 		'800(系统确认收货)'
 	WHEN 240 THEN 		'240(已取消【门店自提待自提后可取消订单】)'
 	ELSE 		'oo'
 	END '交易状态'
 	FROM 		b_trades b
WHERE site_id='100198'
 AND create_time > '2017-6-20 9:20:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-22 12:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 250	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;


*******************************************************************************************************8
/*(5) 100030	上海宝岛大药房连锁有限公司

迁移时间：2017-6-21 21:25

迁移之后的新的订单数：49

SELECT site_id,trades_id,create_time FROM b_trades  WHERE site_id='100030'  AND create_time > '2017-6-21 21:25'


最后结算时间：2017-6-20	23:59:59

结算日：2017-6-24

符合订单数：2
*/

/*查询结算周期内符合条件的订单----------------------------------*/
SELECT site_id,trades_id,create_time,
CASE trades_status
when 110 THEN '110(等侍买家付款), '
when 120 THEN '120(等待卖家发货),'
when 130 THEN '130(等侍买家确认收货),'
when 140 THEN '140(买家已签收，货到付款专用)，'
when 150 THEN '150(交易成功)，'
when 160 THEN '160(用户未付款主动关闭)，'
when 170 THEN '170(超时未付款，系统关闭)，'
when 180 THEN '180(商家关闭订单)，'
when 200 THEN '200(待取货|待自提，直购和自提专用),'
when 210 THEN '210（已取货|已自提 直购和自提专用），'
when 900 THEN '900（已退款），'
when 220 THEN '220(用户确认收货)，'
when 230 THEN '230(门店确认收货)，'
when 800 THEN '800（系统确认收货）,'
when 240 THEN '240(已取消【门店自提待自提后可取消订单】)'
else 'oo'
end '交易状态',
is_payment,pay_time,pay_style,ROUND(real_pay/100,2)  real_pay,ROUND(plat_split/100,2) plat_split,ROUND(trades_split/100,2) trades_split,ROUND(O2O_freight/100,2) O2O_freight,
deal_finish_status,end_time,settlement_status,finance_no
FROM b_trades
WHERE site_id='100030'
 AND create_time > '2017-6-21 21:25:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-21 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*SELECT * FROM b_trades  WHERE site_id=100030   AND create_time > '2017-6-21 21:25:00' ;*/

/*
清洗结算
*/



select is_payment,plat_split,count(*)  from b_trades where site_id = 100030 AND create_time > '2017-6-21 21:25:00'
group by is_payment,plat_split




update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 21:25:00'
and site_id=100030 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 21:25:00'
and site_id=100030 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-21 21:25:00'
and site_id=100030 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-21 21:25:00'
and site_id=100030 /*清洗医保卡手续费*/;

/*6.14问题修复*/
update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 21:25:00'
and site_id=100030/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 21:25:00'
and site_id=100030 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 21:25:00'
and site_id=100030 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 21:25:00'
and site_id=100030 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100030  AND create_time>'2017-6-21 21:25:00'
/*清洗直购分销状态*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100030  AND create_time>'2017-6-21 21:25:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100030  AND create_time>'2017-6-21 21:25:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流')/*清洗O2O配送费*/;




/*检查金额----------------------------------------------------*/
SELECT *  from  (
	SELECT		id,b.trades_id, 	b.create_time, 	b.pay_time, 	deal_finish_status, 	b.end_time,
 	pay_style,
 	ROUND(real_pay / 100, 2) real_pay,
 	ROUND(plat_split / 100, 2) plat_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2plat_split,
 	ROUND(plat_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '手续费差值',
 	ROUND(trades_split / 100, 2) trades_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 	ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 	ROUND(O2O_freight / 100, 2) O2O_freight,
 	ROUND(real_pay * 0.05 / 100, 2) O2O,
 	ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 	settlement_status,
 	CASE trades_status
 WHEN 110 THEN 	'110(等侍买家付款) '
 WHEN 120 THEN 	'120(等待卖家发货)'
 WHEN 130 THEN 	'130(等侍买家确认收货)'
 WHEN 140 THEN 	'140(买家已签收，货到付款专用)'
 WHEN 150 THEN 	'150(交易成功)'
 WHEN 160 THEN 	'160(用户未付款主动关闭)'
 WHEN 170 THEN 	'170(超时未付款，系统关闭)'
 WHEN 180 THEN 	'180(商家关闭订单)XXXXXXXX'
 WHEN 200 THEN 	'200(待取货|待自提，直购和自提专用)'
 WHEN 210 THEN 	'210(已取货|已自提 直购和自提专用)'
 WHEN 900 THEN 	'900(已退款)xxxxxxxxxx'
 WHEN 220 THEN 	'220(用户确认收货)'
 WHEN 230 THEN 	'230(门店确认收货)'
 WHEN 800 THEN 	'800(系统确认收货)'
 WHEN 240 THEN 	'240(已取消【门店自提待自提后可取消订单】)'
 ELSE	'oo'
 END '交易状态'
 FROM 	b_trades b
WHERE site_id='100030'
 AND create_time > '2017-6-21 21:25:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-21 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style IN ('wx', 'ali') /*交易结束状态*/
 UNION

SELECT			id, 		b.trades_id, b.create_time,b.pay_time,deal_finish_status,b.end_time,pay_style,
 		ROUND(real_pay / 100, 2) real_pay,
 		ROUND(plat_split / 100, 2) plat_split,
 		0.00 2plat_split,
 		ROUND(plat_split / 100, 2) - 0.00 '手续费差值',
 		ROUND(trades_split / 100, 2) trades_split,
 		ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 		ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 		ROUND(O2O_freight / 100, 2) O2O_freight,
 		ROUND(real_pay * 0.05 / 100, 2) O2O,
 		ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 		settlement_status,
 		CASE trades_status
 	WHEN 110 THEN 		'110(等侍买家付款) '
 	WHEN 120 THEN 		'120(等待卖家发货)'
 	WHEN 130 THEN 		'130(等侍买家确认收货)'
 	WHEN 140 THEN 		'140(买家已签收，货到付款专用)'
 	WHEN 150 THEN 		'150(交易成功)'
 	WHEN 160 THEN 		'160(用户未付款主动关闭)'
 	WHEN 170 THEN 		'170(超时未付款，系统关闭)'
 	WHEN 180 THEN 		'180(商家关闭订单)XXXXXXXX'
 	WHEN 200 THEN 		'200(待取货|待自提，直购和自提专用)'
 	WHEN 210 THEN 		'210(已取货|已自提 直购和自提专用)'
 	WHEN 900 THEN 		'900(已退款)xxxxxxxxxx'
 	WHEN 220 THEN 		'220(用户确认收货)'
 	WHEN 230 THEN 		'230(门店确认收货)'
 	WHEN 800 THEN 		'800(系统确认收货)'
 	WHEN 240 THEN 		'240(已取消【门店自提待自提后可取消订单】)'
 	ELSE 		'oo'
 	END '交易状态'
 	FROM 		b_trades b
WHERE site_id='100030'
 AND create_time > '2017-6-21 21:25:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-21 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;



/*(6) 100097	金华市老百姓医药连锁有限公司

迁移时间：2017-6-12 18:00

迁移之后的新的订单数：118

SELECT site_id,trades_id,create_time FROM b_trades  WHERE site_id='100097'  AND create_time > '2017-6-12 18:00'

最后结算时间：2017-6-11	23:59:59

结算日：2017-6-26

符合订单数：11
*/

/*查询结算周期内符合条件的订单----------------------------------*/
SELECT site_id,trades_id,create_time,
CASE trades_status
when 110 THEN '110(等侍买家付款), '
when 120 THEN '120(等待卖家发货),'
when 130 THEN '130(等侍买家确认收货),'
when 140 THEN '140(买家已签收，货到付款专用)，'
when 150 THEN '150(交易成功)，'
when 160 THEN '160(用户未付款主动关闭)，'
when 170 THEN '170(超时未付款，系统关闭)，'
when 180 THEN '180(商家关闭订单)，'
when 200 THEN '200(待取货|待自提，直购和自提专用),'
when 210 THEN '210（已取货|已自提 直购和自提专用），'
when 900 THEN '900（已退款），'
when 220 THEN '220(用户确认收货)，'
when 230 THEN '230(门店确认收货)，'
when 800 THEN '800（系统确认收货）,'
when 240 THEN '240(已取消【门店自提待自提后可取消订单】)'
else 'oo'
end '交易状态',
is_payment,pay_time,pay_style,ROUND(real_pay/100,2)  real_pay,ROUND(plat_split/100,2) plat_split,ROUND(trades_split/100,2) trades_split,ROUND(O2O_freight/100,2) O2O_freight,
deal_finish_status,end_time,settlement_status,finance_no
FROM b_trades
WHERE site_id='100097'
 AND create_time > '2017-6-12 18:00:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-12 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*SELECT * FROM b_trades  WHERE site_id=100030   AND create_time > '2017-6-21 21:25:00' ;*/

/*
清洗结算数据
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-12 18:00:00' AND is_payment = 1
and site_id=100097 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-12 18:00:00' AND is_payment = 1
and site_id=100097 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-12 18:00:00'
and site_id=100097 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-12 18:00:00'
and site_id=100097 /*清洗医保卡手续费*/;

/*6.14问题修复*/
update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-12 18:00:00'
and site_id=100097/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-12 18:00:00'
and site_id=100097 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-12 18:00:00'
and site_id=100097 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-12 18:00:00'
and site_id=100097 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100097 AND create_time>'2017-6-12 18:00:00'
/*清洗直购分销状态*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100097  AND create_time>'2017-6-12 18:00:00' AND is_payment = 1
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100097  AND create_time>'2017-6-12 18:00:00' AND is_payment = 1
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流')/*清洗O2O配送费*/;






/*检查金额----------------------------------------------------*/
SELECT *  from  (
	SELECT		id,b.trades_id, 	b.create_time, 	b.pay_time, 	deal_finish_status, 	b.end_time,
 	pay_style,
 	ROUND(real_pay / 100, 2) real_pay,
 	ROUND(plat_split / 100, 2) plat_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2plat_split,
 	ROUND(plat_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '手续费差值',
 	ROUND(trades_split / 100, 2) trades_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 	ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 	ROUND(O2O_freight / 100, 2) O2O_freight,
 	ROUND(real_pay * 0.05 / 100, 2) O2O,
 	ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 	settlement_status,
 	CASE trades_status
 WHEN 110 THEN 	'110(等侍买家付款) '
 WHEN 120 THEN 	'120(等待卖家发货)'
 WHEN 130 THEN 	'130(等侍买家确认收货)'
 WHEN 140 THEN 	'140(买家已签收，货到付款专用)'
 WHEN 150 THEN 	'150(交易成功)'
 WHEN 160 THEN 	'160(用户未付款主动关闭)'
 WHEN 170 THEN 	'170(超时未付款，系统关闭)'
 WHEN 180 THEN 	'180(商家关闭订单)XXXXXXXX'
 WHEN 200 THEN 	'200(待取货|待自提，直购和自提专用)'
 WHEN 210 THEN 	'210(已取货|已自提 直购和自提专用)'
 WHEN 900 THEN 	'900(已退款)xxxxxxxxxx'
 WHEN 220 THEN 	'220(用户确认收货)'
 WHEN 230 THEN 	'230(门店确认收货)'
 WHEN 800 THEN 	'800(系统确认收货)'
 WHEN 240 THEN 	'240(已取消【门店自提待自提后可取消订单】)'
 ELSE	'oo'
 END '交易状态'
 FROM 	b_trades b
WHERE site_id='100097'
 AND create_time > '2017-6-12 18:00:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-12 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style IN ('wx', 'ali') /*交易结束状态*/
 UNION

SELECT			id, 		b.trades_id, b.create_time,b.pay_time,deal_finish_status,b.end_time,pay_style,
 		ROUND(real_pay / 100, 2) real_pay,
 		ROUND(plat_split / 100, 2) plat_split,
 		0.00 2plat_split,
 		ROUND(plat_split / 100, 2) - 0.00 '手续费差值',
 		ROUND(trades_split / 100, 2) trades_split,
 		ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 		ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 		ROUND(O2O_freight / 100, 2) O2O_freight,
 		ROUND(real_pay * 0.05 / 100, 2) O2O,
 		ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 		settlement_status,
 		CASE trades_status
 	WHEN 110 THEN 		'110(等侍买家付款) '
 	WHEN 120 THEN 		'120(等待卖家发货)'
 	WHEN 130 THEN 		'130(等侍买家确认收货)'
 	WHEN 140 THEN 		'140(买家已签收，货到付款专用)'
 	WHEN 150 THEN 		'150(交易成功)'
 	WHEN 160 THEN 		'160(用户未付款主动关闭)'
 	WHEN 170 THEN 		'170(超时未付款，系统关闭)'
 	WHEN 180 THEN 		'180(商家关闭订单)XXXXXXXX'
 	WHEN 200 THEN 		'200(待取货|待自提，直购和自提专用)'
 	WHEN 210 THEN 		'210(已取货|已自提 直购和自提专用)'
 	WHEN 900 THEN 		'900(已退款)xxxxxxxxxx'
 	WHEN 220 THEN 		'220(用户确认收货)'
 	WHEN 230 THEN 		'230(门店确认收货)'
 	WHEN 800 THEN 		'800(系统确认收货)'
 	WHEN 240 THEN 		'240(已取消【门店自提待自提后可取消订单】)'
 	ELSE 		'oo'
 	END '交易状态'
 	FROM 		b_trades b
WHERE site_id='100097'
 AND create_time > '2017-6-12 18:00:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-12 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;


/*(7) 100012	青岛春天之星大药房医药连锁有限公司
迁移时间：2017-6-16 9:20

迁移之后的新的订单数：1

SELECT site_id,trades_id,create_time FROM b_trades  WHERE site_id='100012'  AND create_time > '2017-6-16 9:20'

最后结算时间：2017-6-15	23:59:59
结算日：2017-6-26
符合订单数：0
*/

/*查询结算周期内符合条件的订单----------------------------------*/
SELECT site_id,trades_id,create_time,
CASE trades_status
when 110 THEN '110(等侍买家付款), '
when 120 THEN '120(等待卖家发货),'
when 130 THEN '130(等侍买家确认收货),'
when 140 THEN '140(买家已签收，货到付款专用)，'
when 150 THEN '150(交易成功)，'
when 160 THEN '160(用户未付款主动关闭)，'
when 170 THEN '170(超时未付款，系统关闭)，'
when 180 THEN '180(商家关闭订单)，'
when 200 THEN '200(待取货|待自提，直购和自提专用),'
when 210 THEN '210（已取货|已自提 直购和自提专用），'
when 900 THEN '900（已退款），'
when 220 THEN '220(用户确认收货)，'
when 230 THEN '230(门店确认收货)，'
when 800 THEN '800（系统确认收货）,'
when 240 THEN '240(已取消【门店自提待自提后可取消订单】)'
else 'oo'
end '交易状态',
is_payment,pay_time,pay_style,ROUND(real_pay/100,2)  real_pay,ROUND(plat_split/100,2) plat_split,ROUND(trades_split/100,2) trades_split,ROUND(O2O_freight/100,2) O2O_freight,
deal_finish_status,end_time,settlement_status,finance_no
FROM b_trades
WHERE site_id='100102'
 AND create_time > '2017-6-16 9:20:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*

SELECT * FROM b_trades  WHERE site_id=100102   AND create_time > '2017-6-16 9:20:00'
ORDER BY create_time DESC;

*/

/*
清洗数据
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:20:00'
and site_id=100012 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:20:00'
and site_id=100012 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-16 09:20:00'
and site_id=100012 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-16 09:20:00'
and site_id=100012 /*清洗医保卡手续费*/;

/*6.14问题修复*/
update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:20:00'
and site_id=100012/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:20:00'
and site_id=100012 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:20:00'
and site_id=100012 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:20:00'
and site_id=100012 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100012  AND create_time>'2017-6-16 09:20:00'
/*清洗直购分销状态*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100012  AND create_time>'2017-6-16 09:20:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100012  AND create_time>'2017-6-16 09:20:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流')/*清洗O2O配送费*/;







/*检查金额----------------------------------------------------*/
SELECT *  from  (
	SELECT		id,b.trades_id, 	b.create_time, 	b.pay_time, 	deal_finish_status, 	b.end_time,
 	pay_style,
 	ROUND(real_pay / 100, 2) real_pay,
 	ROUND(plat_split / 100, 2) plat_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2plat_split,
 	ROUND(plat_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '手续费差值',
 	ROUND(trades_split / 100, 2) trades_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 	ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 	ROUND(O2O_freight / 100, 2) O2O_freight,
 	ROUND(real_pay * 0.05 / 100, 2) O2O,
 	ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 	settlement_status,
 	CASE trades_status
 WHEN 110 THEN 	'110(等侍买家付款) '
 WHEN 120 THEN 	'120(等待卖家发货)'
 WHEN 130 THEN 	'130(等侍买家确认收货)'
 WHEN 140 THEN 	'140(买家已签收，货到付款专用)'
 WHEN 150 THEN 	'150(交易成功)'
 WHEN 160 THEN 	'160(用户未付款主动关闭)'
 WHEN 170 THEN 	'170(超时未付款，系统关闭)'
 WHEN 180 THEN 	'180(商家关闭订单)XXXXXXXX'
 WHEN 200 THEN 	'200(待取货|待自提，直购和自提专用)'
 WHEN 210 THEN 	'210(已取货|已自提 直购和自提专用)'
 WHEN 900 THEN 	'900(已退款)xxxxxxxxxx'
 WHEN 220 THEN 	'220(用户确认收货)'
 WHEN 230 THEN 	'230(门店确认收货)'
 WHEN 800 THEN 	'800(系统确认收货)'
 WHEN 240 THEN 	'240(已取消【门店自提待自提后可取消订单】)'
 ELSE	'oo'
 END '交易状态'
 FROM 	b_trades b
WHERE site_id='100102'
 AND create_time > '2017-6-16 9:20:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style IN ('wx', 'ali') /*交易结束状态*/
 UNION

SELECT			id, 		b.trades_id, b.create_time,b.pay_time,deal_finish_status,b.end_time,pay_style,
 		ROUND(real_pay / 100, 2) real_pay,
 		ROUND(plat_split / 100, 2) plat_split,
 		0.00 2plat_split,
 		ROUND(plat_split / 100, 2) - 0.00 '手续费差值',
 		ROUND(trades_split / 100, 2) trades_split,
 		ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 		ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 		ROUND(O2O_freight / 100, 2) O2O_freight,
 		ROUND(real_pay * 0.05 / 100, 2) O2O,
 		ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 		settlement_status,
 		CASE trades_status
 	WHEN 110 THEN 		'110(等侍买家付款) '
 	WHEN 120 THEN 		'120(等待卖家发货)'
 	WHEN 130 THEN 		'130(等侍买家确认收货)'
 	WHEN 140 THEN 		'140(买家已签收，货到付款专用)'
 	WHEN 150 THEN 		'150(交易成功)'
 	WHEN 160 THEN 		'160(用户未付款主动关闭)'
 	WHEN 170 THEN 		'170(超时未付款，系统关闭)'
 	WHEN 180 THEN 		'180(商家关闭订单)XXXXXXXX'
 	WHEN 200 THEN 		'200(待取货|待自提，直购和自提专用)'
 	WHEN 210 THEN 		'210(已取货|已自提 直购和自提专用)'
 	WHEN 900 THEN 		'900(已退款)xxxxxxxxxx'
 	WHEN 220 THEN 		'220(用户确认收货)'
 	WHEN 230 THEN 		'230(门店确认收货)'
 	WHEN 800 THEN 		'800(系统确认收货)'
 	WHEN 240 THEN 		'240(已取消【门店自提待自提后可取消订单】)'
 	ELSE 		'oo'
 	END '交易状态'
 	FROM 		b_trades b
WHERE site_id='100102'
 AND create_time > '2017-6-16 9:20:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;



/*(8) 100016	国药控股国大药房山西益源连锁有限公司

迁移时间：2017-6-21 14:42

迁移之后的新的订单数：2

SELECT site_id,trades_id,create_time FROM b_trades  WHERE site_id='100016'  AND create_time > '2017-6-21 14:42'

最后结算时间：2017-6-20	23:59:59

结算日：2017-6-26

符合订单数：1
*/

/*查询结算周期内符合条件的订单----------------------------------*/
SELECT site_id,trades_id,create_time,
CASE trades_status
when 110 THEN '110(等侍买家付款), '
when 120 THEN '120(等待卖家发货),'
when 130 THEN '130(等侍买家确认收货),'
when 140 THEN '140(买家已签收，货到付款专用)，'
when 150 THEN '150(交易成功)，'
when 160 THEN '160(用户未付款主动关闭)，'
when 170 THEN '170(超时未付款，系统关闭)，'
when 180 THEN '180(商家关闭订单)，'
when 200 THEN '200(待取货|待自提，直购和自提专用),'
when 210 THEN '210（已取货|已自提 直购和自提专用），'
when 900 THEN '900（已退款），'
when 220 THEN '220(用户确认收货)，'
when 230 THEN '230(门店确认收货)，'
when 800 THEN '800（系统确认收货）,'
when 240 THEN '240(已取消【门店自提待自提后可取消订单】)'
else 'oo'
end '交易状态',
is_payment,pay_time,pay_style,ROUND(real_pay/100,2)  real_pay,ROUND(plat_split/100,2) plat_split,ROUND(trades_split/100,2) trades_split,ROUND(O2O_freight/100,2) O2O_freight,
deal_finish_status,end_time,settlement_status,finance_no
FROM b_trades
WHERE site_id='100016'
 AND create_time > '2017-6-21 14:42:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-21 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*

SELECT * FROM b_trades  WHERE site_id=100102   AND create_time > '2017-6-16 9:20:00'
ORDER BY create_time DESC;

*/
/*
清理结算
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 14:42:00' AND is_payment = 1
and site_id=100016 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 14:42:00' AND is_payment = 1
and site_id=100016 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-21 14:42:00'
and site_id=100016 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-21 14:42:00'
and site_id=100016 /*清洗医保卡手续费*/;

/*6.14问题修复*/
update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 14:42:00'
and site_id=100016/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 14:42:00'
and site_id=100016 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 14:42:00'
and site_id=100016 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 14:42:00'
and site_id=100016 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100016  AND create_time>'2017-6-21 14:42:00'
/*清洗直购分销状态*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100016  AND create_time>'2017-6-21 14:42:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100016  AND create_time>'2017-6-21 14:42:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流')/*清洗O2O配送费*/;






/*检查金额----------------------------------------------------*/
SELECT *  from  (
	SELECT		id,b.trades_id, 	b.create_time, 	b.pay_time, 	deal_finish_status, 	b.end_time,
 	pay_style,
 	ROUND(real_pay / 100, 2) real_pay,
 	ROUND(plat_split / 100, 2) plat_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2plat_split,
 	ROUND(plat_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '手续费差值',
 	ROUND(trades_split / 100, 2) trades_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 	ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 	ROUND(O2O_freight / 100, 2) O2O_freight,
 	ROUND(real_pay * 0.05 / 100, 2) O2O,
 	ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 	settlement_status,
 	CASE trades_status
 WHEN 110 THEN 	'110(等侍买家付款) '
 WHEN 120 THEN 	'120(等待卖家发货)'
 WHEN 130 THEN 	'130(等侍买家确认收货)'
 WHEN 140 THEN 	'140(买家已签收，货到付款专用)'
 WHEN 150 THEN 	'150(交易成功)'
 WHEN 160 THEN 	'160(用户未付款主动关闭)'
 WHEN 170 THEN 	'170(超时未付款，系统关闭)'
 WHEN 180 THEN 	'180(商家关闭订单)XXXXXXXX'
 WHEN 200 THEN 	'200(待取货|待自提，直购和自提专用)'
 WHEN 210 THEN 	'210(已取货|已自提 直购和自提专用)'
 WHEN 900 THEN 	'900(已退款)xxxxxxxxxx'
 WHEN 220 THEN 	'220(用户确认收货)'
 WHEN 230 THEN 	'230(门店确认收货)'
 WHEN 800 THEN 	'800(系统确认收货)'
 WHEN 240 THEN 	'240(已取消【门店自提待自提后可取消订单】)'
 ELSE	'oo'
 END '交易状态'
 FROM 	b_trades b
WHERE site_id='100016'
 AND create_time > '2017-6-21 14:42:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-21 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style IN ('wx', 'ali') /*交易结束状态*/
 UNION

SELECT			id, 		b.trades_id, b.create_time,b.pay_time,deal_finish_status,b.end_time,pay_style,
 		ROUND(real_pay / 100, 2) real_pay,
 		ROUND(plat_split / 100, 2) plat_split,
 		0.00 2plat_split,
 		ROUND(plat_split / 100, 2) - 0.00 '手续费差值',
 		ROUND(trades_split / 100, 2) trades_split,
 		ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 		ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 		ROUND(O2O_freight / 100, 2) O2O_freight,
 		ROUND(real_pay * 0.05 / 100, 2) O2O,
 		ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 		settlement_status,
 		CASE trades_status
 	WHEN 110 THEN 		'110(等侍买家付款) '
 	WHEN 120 THEN 		'120(等待卖家发货)'
 	WHEN 130 THEN 		'130(等侍买家确认收货)'
 	WHEN 140 THEN 		'140(买家已签收，货到付款专用)'
 	WHEN 150 THEN 		'150(交易成功)'
 	WHEN 160 THEN 		'160(用户未付款主动关闭)'
 	WHEN 170 THEN 		'170(超时未付款，系统关闭)'
 	WHEN 180 THEN 		'180(商家关闭订单)XXXXXXXX'
 	WHEN 200 THEN 		'200(待取货|待自提，直购和自提专用)'
 	WHEN 210 THEN 		'210(已取货|已自提 直购和自提专用)'
 	WHEN 900 THEN 		'900(已退款)xxxxxxxxxx'
 	WHEN 220 THEN 		'220(用户确认收货)'
 	WHEN 230 THEN 		'230(门店确认收货)'
 	WHEN 800 THEN 		'800(系统确认收货)'
 	WHEN 240 THEN 		'240(已取消【门店自提待自提后可取消订单】)'
 	ELSE 		'oo'
 	END '交易状态'
 	FROM 		b_trades b
WHERE site_id='100016'
 AND create_time > '2017-6-21 14:42:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-21 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;






/*(9) 100193	山东立健药店连锁有限公司


迁移时间：2017-6-21 13:56

迁移之后的新的订单数：4

SELECT site_id,trades_id,create_time FROM b_trades  WHERE site_id='100193'  AND create_time > '2017-6-21 13:56'

最后结算时间：2017-6-20	23:59:59

结算日：2017-6-27

符合订单数：0
*/

/*查询结算周期内符合条件的订单----------------------------------*/
SELECT site_id,trades_id,create_time,
CASE trades_status
when 110 THEN '110(等侍买家付款), '
when 120 THEN '120(等待卖家发货),'
when 130 THEN '130(等侍买家确认收货),'
when 140 THEN '140(买家已签收，货到付款专用)，'
when 150 THEN '150(交易成功)，'
when 160 THEN '160(用户未付款主动关闭)，'
when 170 THEN '170(超时未付款，系统关闭)，'
when 180 THEN '180(商家关闭订单)，'
when 200 THEN '200(待取货|待自提，直购和自提专用),'
when 210 THEN '210（已取货|已自提 直购和自提专用），'
when 900 THEN '900（已退款），'
when 220 THEN '220(用户确认收货)，'
when 230 THEN '230(门店确认收货)，'
when 800 THEN '800（系统确认收货）,'
when 240 THEN '240(已取消【门店自提待自提后可取消订单】)'
else 'oo'
end '交易状态',
is_payment,pay_time,pay_style,ROUND(real_pay/100,2)  real_pay,ROUND(plat_split/100,2) plat_split,ROUND(trades_split/100,2) trades_split,ROUND(O2O_freight/100,2) O2O_freight,
deal_finish_status,end_time,settlement_status,finance_no
FROM b_trades
WHERE site_id='100193'
 AND create_time > '2017-6-21 13:56:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-21 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*

SELECT create_time FROM b_trades  WHERE site_id=100102
ORDER BY create_time DESC;

*/

/*
清洗结算
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 13:56:00' AND is_payment = 1
and site_id=100193 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 13:56:00' AND is_payment = 1
and site_id=100193 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-21 13:56:00'
and site_id=100193 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-21 13:56:00'
and site_id=100193 /*清洗医保卡手续费*/;

/*6.14问题修复*/
update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 13:56:00'
and site_id=100193/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 13:56:00'
and site_id=100193 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 13:56:00'
and site_id=100193 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 13:56:00'
and site_id=100193 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100193  AND create_time>'2017-6-21 13:56:00'
/*清洗直购分销状态*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100193  AND create_time>'2017-6-21 13:56:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100193  AND create_time>'2017-6-21 13:56:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流')/*清洗O2O配送费*/;




/*检查金额----------------------------------------------------*/
SELECT *  from  (
	SELECT		id,b.trades_id, 	b.create_time, 	b.pay_time, 	deal_finish_status, 	b.end_time,
 	pay_style,
 	ROUND(real_pay / 100, 2) real_pay,
 	ROUND(plat_split / 100, 2) plat_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2plat_split,
 	ROUND(plat_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '手续费差值',
 	ROUND(trades_split / 100, 2) trades_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 	ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 	ROUND(O2O_freight / 100, 2) O2O_freight,
 	ROUND(real_pay * 0.05 / 100, 2) O2O,
 	ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 	settlement_status,
 	CASE trades_status
 WHEN 110 THEN 	'110(等侍买家付款) '
 WHEN 120 THEN 	'120(等待卖家发货)'
 WHEN 130 THEN 	'130(等侍买家确认收货)'
 WHEN 140 THEN 	'140(买家已签收，货到付款专用)'
 WHEN 150 THEN 	'150(交易成功)'
 WHEN 160 THEN 	'160(用户未付款主动关闭)'
 WHEN 170 THEN 	'170(超时未付款，系统关闭)'
 WHEN 180 THEN 	'180(商家关闭订单)XXXXXXXX'
 WHEN 200 THEN 	'200(待取货|待自提，直购和自提专用)'
 WHEN 210 THEN 	'210(已取货|已自提 直购和自提专用)'
 WHEN 900 THEN 	'900(已退款)xxxxxxxxxx'
 WHEN 220 THEN 	'220(用户确认收货)'
 WHEN 230 THEN 	'230(门店确认收货)'
 WHEN 800 THEN 	'800(系统确认收货)'
 WHEN 240 THEN 	'240(已取消【门店自提待自提后可取消订单】)'
 ELSE	'oo'
 END '交易状态'
 FROM 	b_trades b
WHERE site_id='100193'
 AND create_time > '2017-6-21 13:56:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-21 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style IN ('wx', 'ali') /*交易结束状态*/
 UNION

SELECT			id, 		b.trades_id, b.create_time,b.pay_time,deal_finish_status,b.end_time,pay_style,
 		ROUND(real_pay / 100, 2) real_pay,
 		ROUND(plat_split / 100, 2) plat_split,
 		0.00 2plat_split,
 		ROUND(plat_split / 100, 2) - 0.00 '手续费差值',
 		ROUND(trades_split / 100, 2) trades_split,
 		ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 		ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 		ROUND(O2O_freight / 100, 2) O2O_freight,
 		ROUND(real_pay * 0.05 / 100, 2) O2O,
 		ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 		settlement_status,
 		CASE trades_status
 	WHEN 110 THEN 		'110(等侍买家付款) '
 	WHEN 120 THEN 		'120(等待卖家发货)'
 	WHEN 130 THEN 		'130(等侍买家确认收货)'
 	WHEN 140 THEN 		'140(买家已签收，货到付款专用)'
 	WHEN 150 THEN 		'150(交易成功)'
 	WHEN 160 THEN 		'160(用户未付款主动关闭)'
 	WHEN 170 THEN 		'170(超时未付款，系统关闭)'
 	WHEN 180 THEN 		'180(商家关闭订单)XXXXXXXX'
 	WHEN 200 THEN 		'200(待取货|待自提，直购和自提专用)'
 	WHEN 210 THEN 		'210(已取货|已自提 直购和自提专用)'
 	WHEN 900 THEN 		'900(已退款)xxxxxxxxxx'
 	WHEN 220 THEN 		'220(用户确认收货)'
 	WHEN 230 THEN 		'230(门店确认收货)'
 	WHEN 800 THEN 		'800(系统确认收货)'
 	WHEN 240 THEN 		'240(已取消【门店自提待自提后可取消订单】)'
 	ELSE 		'oo'
 	END '交易状态'
 	FROM 		b_trades b
WHERE site_id='100193'
 AND create_time > '2017-6-21 13:56:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-21 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;



/*(10) 100165	深圳市瑞康大药房连锁有限公司

迁移时间：2017-6-16 9:37

迁移之后的新的订单数：0

SELECT site_id,trades_id,create_time FROM b_trades  WHERE site_id='100165'  AND create_time > '2017-6-16 9:37'


最后结算时间：2017-6-15	23:59:59

结算日：2017-6-28

符合订单数：0
*/

/*查询结算周期内符合条件的订单----------------------------------*/
SELECT site_id,trades_id,create_time,
CASE trades_status
when 110 THEN '110(等侍买家付款), '
when 120 THEN '120(等待卖家发货),'
when 130 THEN '130(等侍买家确认收货),'
when 140 THEN '140(买家已签收，货到付款专用)，'
when 150 THEN '150(交易成功)，'
when 160 THEN '160(用户未付款主动关闭)，'
when 170 THEN '170(超时未付款，系统关闭)，'
when 180 THEN '180(商家关闭订单)，'
when 200 THEN '200(待取货|待自提，直购和自提专用),'
when 210 THEN '210（已取货|已自提 直购和自提专用），'
when 900 THEN '900（已退款），'
when 220 THEN '220(用户确认收货)，'
when 230 THEN '230(门店确认收货)，'
when 800 THEN '800（系统确认收货）,'
when 240 THEN '240(已取消【门店自提待自提后可取消订单】)'
else 'oo'
end '交易状态',
is_payment,pay_time,pay_style,ROUND(real_pay/100,2)  real_pay,ROUND(plat_split/100,2) plat_split,ROUND(trades_split/100,2) trades_split,ROUND(O2O_freight/100,2) O2O_freight,
deal_finish_status,end_time,settlement_status,finance_no
FROM b_trades
WHERE site_id='100165'
 AND create_time > '2017-6-16 9:37:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*

SELECT create_time FROM b_trades  WHERE site_id=100165
ORDER BY create_time DESC;

*/

/*
	清除结算
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:37:00'
and site_id=100165 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:37:00'
and site_id=100165 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-16 09:37:00'
and site_id=100165 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-16 09:37:00'
and site_id=100165 /*清洗医保卡手续费*/;

/*6.14问题修复*/
update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:37:00'
and site_id=100165/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:37:00'
and site_id=100165 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:37:00'
and site_id=100165 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:37:00'
and site_id=100165 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100165  AND create_time>'2017-6-16 09:37:00'
/*清洗直购分销状态*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100165  AND create_time>'2017-6-16 09:37:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100165  AND create_time>'2017-6-16 09:37:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流')/*清洗O2O配送费*/;





/*检查金额----------------------------------------------------*/
SELECT *  from  (
	SELECT		id,b.trades_id, 	b.create_time, 	b.pay_time, 	deal_finish_status, 	b.end_time,
 	pay_style,
 	ROUND(real_pay / 100, 2) real_pay,
 	ROUND(plat_split / 100, 2) plat_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2plat_split,
 	ROUND(plat_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '手续费差值',
 	ROUND(trades_split / 100, 2) trades_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 	ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 	ROUND(O2O_freight / 100, 2) O2O_freight,
 	ROUND(real_pay * 0.05 / 100, 2) O2O,
 	ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 	settlement_status,
 	CASE trades_status
 WHEN 110 THEN 	'110(等侍买家付款) '
 WHEN 120 THEN 	'120(等待卖家发货)'
 WHEN 130 THEN 	'130(等侍买家确认收货)'
 WHEN 140 THEN 	'140(买家已签收，货到付款专用)'
 WHEN 150 THEN 	'150(交易成功)'
 WHEN 160 THEN 	'160(用户未付款主动关闭)'
 WHEN 170 THEN 	'170(超时未付款，系统关闭)'
 WHEN 180 THEN 	'180(商家关闭订单)XXXXXXXX'
 WHEN 200 THEN 	'200(待取货|待自提，直购和自提专用)'
 WHEN 210 THEN 	'210(已取货|已自提 直购和自提专用)'
 WHEN 900 THEN 	'900(已退款)xxxxxxxxxx'
 WHEN 220 THEN 	'220(用户确认收货)'
 WHEN 230 THEN 	'230(门店确认收货)'
 WHEN 800 THEN 	'800(系统确认收货)'
 WHEN 240 THEN 	'240(已取消【门店自提待自提后可取消订单】)'
 ELSE	'oo'
 END '交易状态'
 FROM 	b_trades b
WHERE site_id='100165'
 AND create_time > '2017-6-16 9:37:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style IN ('wx', 'ali') /*交易结束状态*/
 UNION

SELECT			id, 		b.trades_id, b.create_time,b.pay_time,deal_finish_status,b.end_time,pay_style,
 		ROUND(real_pay / 100, 2) real_pay,
 		ROUND(plat_split / 100, 2) plat_split,
 		0.00 2plat_split,
 		ROUND(plat_split / 100, 2) - 0.00 '手续费差值',
 		ROUND(trades_split / 100, 2) trades_split,
 		ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 		ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 		ROUND(O2O_freight / 100, 2) O2O_freight,
 		ROUND(real_pay * 0.05 / 100, 2) O2O,
 		ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 		settlement_status,
 		CASE trades_status
 	WHEN 110 THEN 		'110(等侍买家付款) '
 	WHEN 120 THEN 		'120(等待卖家发货)'
 	WHEN 130 THEN 		'130(等侍买家确认收货)'
 	WHEN 140 THEN 		'140(买家已签收，货到付款专用)'
 	WHEN 150 THEN 		'150(交易成功)'
 	WHEN 160 THEN 		'160(用户未付款主动关闭)'
 	WHEN 170 THEN 		'170(超时未付款，系统关闭)'
 	WHEN 180 THEN 		'180(商家关闭订单)XXXXXXXX'
 	WHEN 200 THEN 		'200(待取货|待自提，直购和自提专用)'
 	WHEN 210 THEN 		'210(已取货|已自提 直购和自提专用)'
 	WHEN 900 THEN 		'900(已退款)xxxxxxxxxx'
 	WHEN 220 THEN 		'220(用户确认收货)'
 	WHEN 230 THEN 		'230(门店确认收货)'
 	WHEN 800 THEN 		'800(系统确认收货)'
 	WHEN 240 THEN 		'240(已取消【门店自提待自提后可取消订单】)'
 	ELSE 		'oo'
 	END '交易状态'
 	FROM 		b_trades b
WHERE site_id='100165'
 AND create_time > '2017-6-16 9:37:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;




/*(11) 100173	深圳市中联大药房有限公司

迁移时间：2017-6-20 13:17

迁移之后的新的订单数：3

SELECT site_id,trades_id,create_time FROM b_trades  WHERE site_id='100173'  AND create_time > '2017-6-20 13:17'

最后结算时间：2017-6-19	23:59:59

结算日：2017-6-28

符合订单数：0
*/

/*查询结算周期内符合条件的订单----------------------------------*/
SELECT site_id,trades_id,create_time,
CASE trades_status
when 110 THEN '110(等侍买家付款), '
when 120 THEN '120(等待卖家发货),'
when 130 THEN '130(等侍买家确认收货),'
when 140 THEN '140(买家已签收，货到付款专用)，'
when 150 THEN '150(交易成功)，'
when 160 THEN '160(用户未付款主动关闭)，'
when 170 THEN '170(超时未付款，系统关闭)，'
when 180 THEN '180(商家关闭订单)，'
when 200 THEN '200(待取货|待自提，直购和自提专用),'
when 210 THEN '210（已取货|已自提 直购和自提专用），'
when 900 THEN '900（已退款），'
when 220 THEN '220(用户确认收货)，'
when 230 THEN '230(门店确认收货)，'
when 800 THEN '800（系统确认收货）,'
when 240 THEN '240(已取消【门店自提待自提后可取消订单】)'
else 'oo'
end '交易状态',
is_payment,pay_time,pay_style,ROUND(real_pay/100,2)  real_pay,ROUND(plat_split/100,2) plat_split,ROUND(trades_split/100,2) trades_split,ROUND(O2O_freight/100,2) O2O_freight,
deal_finish_status,end_time,settlement_status,finance_no
FROM b_trades
WHERE site_id='100173'
 AND create_time > '2017-6-20 13:17:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*

SELECT create_time FROM b_trades  WHERE site_id=100173
ORDER BY create_time DESC;

*/
/*
清除结算
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 13:17:00' AND is_payment = 1
and site_id=100173 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 13:17:00' AND is_payment = 1
and site_id=100173 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-20 13:17:00'
and site_id=100173 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-20 13:17:00'
and site_id=100173 /*清洗医保卡手续费*/;

/*6.14问题修复*/
update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 13:17:00'
and site_id=100173/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 13:17:00'
and site_id=100173 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 13:17:00'
and site_id=100173 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 13:17:00'
and site_id=100173 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100173  AND create_time>'2017-6-20 13:17:00'
/*清洗直购分销状态*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100173  AND create_time>'2017-6-20 13:17:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100173  AND create_time>'2017-6-20 13:17:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流')/*清洗O2O配送费*/;





/*检查金额----------------------------------------------------*/
SELECT *  from  (
	SELECT		id,b.trades_id, 	b.create_time, 	b.pay_time, 	deal_finish_status, 	b.end_time,
 	pay_style,
 	ROUND(real_pay / 100, 2) real_pay,
 	ROUND(plat_split / 100, 2) plat_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2plat_split,
 	ROUND(plat_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '手续费差值',
 	ROUND(trades_split / 100, 2) trades_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 	ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 	ROUND(O2O_freight / 100, 2) O2O_freight,
 	ROUND(real_pay * 0.05 / 100, 2) O2O,
 	ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 	settlement_status,
 	CASE trades_status
 WHEN 110 THEN 	'110(等侍买家付款) '
 WHEN 120 THEN 	'120(等待卖家发货)'
 WHEN 130 THEN 	'130(等侍买家确认收货)'
 WHEN 140 THEN 	'140(买家已签收，货到付款专用)'
 WHEN 150 THEN 	'150(交易成功)'
 WHEN 160 THEN 	'160(用户未付款主动关闭)'
 WHEN 170 THEN 	'170(超时未付款，系统关闭)'
 WHEN 180 THEN 	'180(商家关闭订单)XXXXXXXX'
 WHEN 200 THEN 	'200(待取货|待自提，直购和自提专用)'
 WHEN 210 THEN 	'210(已取货|已自提 直购和自提专用)'
 WHEN 900 THEN 	'900(已退款)xxxxxxxxxx'
 WHEN 220 THEN 	'220(用户确认收货)'
 WHEN 230 THEN 	'230(门店确认收货)'
 WHEN 800 THEN 	'800(系统确认收货)'
 WHEN 240 THEN 	'240(已取消【门店自提待自提后可取消订单】)'
 ELSE	'oo'
 END '交易状态'
 FROM 	b_trades b
WHERE site_id='100173'
 AND create_time > '2017-6-20 13:17:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style IN ('wx', 'ali') /*交易结束状态*/
 UNION

SELECT			id, 		b.trades_id, b.create_time,b.pay_time,deal_finish_status,b.end_time,pay_style,
 		ROUND(real_pay / 100, 2) real_pay,
 		ROUND(plat_split / 100, 2) plat_split,
 		0.00 2plat_split,
 		ROUND(plat_split / 100, 2) - 0.00 '手续费差值',
 		ROUND(trades_split / 100, 2) trades_split,
 		ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 		ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 		ROUND(O2O_freight / 100, 2) O2O_freight,
 		ROUND(real_pay * 0.05 / 100, 2) O2O,
 		ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 		settlement_status,
 		CASE trades_status
 	WHEN 110 THEN 		'110(等侍买家付款) '
 	WHEN 120 THEN 		'120(等待卖家发货)'
 	WHEN 130 THEN 		'130(等侍买家确认收货)'
 	WHEN 140 THEN 		'140(买家已签收，货到付款专用)'
 	WHEN 150 THEN 		'150(交易成功)'
 	WHEN 160 THEN 		'160(用户未付款主动关闭)'
 	WHEN 170 THEN 		'170(超时未付款，系统关闭)'
 	WHEN 180 THEN 		'180(商家关闭订单)XXXXXXXX'
 	WHEN 200 THEN 		'200(待取货|待自提，直购和自提专用)'
 	WHEN 210 THEN 		'210(已取货|已自提 直购和自提专用)'
 	WHEN 900 THEN 		'900(已退款)xxxxxxxxxx'
 	WHEN 220 THEN 		'220(用户确认收货)'
 	WHEN 230 THEN 		'230(门店确认收货)'
 	WHEN 800 THEN 		'800(系统确认收货)'
 	WHEN 240 THEN 		'240(已取消【门店自提待自提后可取消订单】)'
 	ELSE 		'oo'
 	END '交易状态'
 	FROM 		b_trades b
WHERE site_id='100173'
 AND create_time > '2017-6-20 13:17:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;



/*(12) 100050	辽宁建联医药连锁有限公司

迁移时间：2017-6-21 14:35

迁移之后的新的订单数：0

SELECT site_id,trades_id,create_time FROM b_trades  WHERE site_id='100050'  AND create_time > '2017-6-21 14:35'



最后结算时间：2017-6-20	23:59:59

结算日：2017-6-28

符合订单数：0
*/

/*查询结算周期内符合条件的订单----------------------------------*/
SELECT site_id,trades_id,create_time,
CASE trades_status
when 110 THEN '110(等侍买家付款), '
when 120 THEN '120(等待卖家发货),'
when 130 THEN '130(等侍买家确认收货),'
when 140 THEN '140(买家已签收，货到付款专用)，'
when 150 THEN '150(交易成功)，'
when 160 THEN '160(用户未付款主动关闭)，'
when 170 THEN '170(超时未付款，系统关闭)，'
when 180 THEN '180(商家关闭订单)，'
when 200 THEN '200(待取货|待自提，直购和自提专用),'
when 210 THEN '210（已取货|已自提 直购和自提专用），'
when 900 THEN '900（已退款），'
when 220 THEN '220(用户确认收货)，'
when 230 THEN '230(门店确认收货)，'
when 800 THEN '800（系统确认收货）,'
when 240 THEN '240(已取消【门店自提待自提后可取消订单】)'
else 'oo'
end '交易状态',
is_payment,pay_time,pay_style,ROUND(real_pay/100,2)  real_pay,ROUND(plat_split/100,2) plat_split,ROUND(trades_split/100,2) trades_split,ROUND(O2O_freight/100,2) O2O_freight,
deal_finish_status,end_time,settlement_status,finance_no
FROM b_trades
WHERE site_id='100050'
 AND create_time > '2017-6-21 14:35:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-21 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*

SELECT create_time FROM b_trades  WHERE site_id=100050
ORDER BY create_time DESC;

*/
/*
清除结算
*/

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 14:35:00'
and site_id=100050 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 14:35:00'
and site_id=100050 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-21 14:35:00'
and site_id=100050 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-21 14:35:00'
and site_id=100050 /*清洗医保卡手续费*/;

/*6.14问题修复*/
update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 14:35:00'
and site_id=100050/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 14:35:00'
and site_id=100050 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 14:35:00'
and site_id=100050 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 14:35:00'
and site_id=100050 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100050  AND create_time>'2017-6-21 14:35:00'
/*清洗直购分销状态*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100050  AND create_time>'2017-6-21 14:35:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100050  AND create_time>'2017-6-21 14:35:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流')/*清洗O2O配送费*/;




/*检查金额----------------------------------------------------*/
SELECT *  from  (
	SELECT		id,b.trades_id, 	b.create_time, 	b.pay_time, 	deal_finish_status, 	b.end_time,
 	pay_style,
 	ROUND(real_pay / 100, 2) real_pay,
 	ROUND(plat_split / 100, 2) plat_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2plat_split,
 	ROUND(plat_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '手续费差值',
 	ROUND(trades_split / 100, 2) trades_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 	ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 	ROUND(O2O_freight / 100, 2) O2O_freight,
 	ROUND(real_pay * 0.05 / 100, 2) O2O,
 	ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 	settlement_status,
 	CASE trades_status
 WHEN 110 THEN 	'110(等侍买家付款) '
 WHEN 120 THEN 	'120(等待卖家发货)'
 WHEN 130 THEN 	'130(等侍买家确认收货)'
 WHEN 140 THEN 	'140(买家已签收，货到付款专用)'
 WHEN 150 THEN 	'150(交易成功)'
 WHEN 160 THEN 	'160(用户未付款主动关闭)'
 WHEN 170 THEN 	'170(超时未付款，系统关闭)'
 WHEN 180 THEN 	'180(商家关闭订单)XXXXXXXX'
 WHEN 200 THEN 	'200(待取货|待自提，直购和自提专用)'
 WHEN 210 THEN 	'210(已取货|已自提 直购和自提专用)'
 WHEN 900 THEN 	'900(已退款)xxxxxxxxxx'
 WHEN 220 THEN 	'220(用户确认收货)'
 WHEN 230 THEN 	'230(门店确认收货)'
 WHEN 800 THEN 	'800(系统确认收货)'
 WHEN 240 THEN 	'240(已取消【门店自提待自提后可取消订单】)'
 ELSE	'oo'
 END '交易状态'
 FROM 	b_trades b
WHERE site_id='100050'
 AND create_time > '2017-6-21 14:35:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-21 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style IN ('wx', 'ali') /*交易结束状态*/
 UNION

SELECT			id, 		b.trades_id, b.create_time,b.pay_time,deal_finish_status,b.end_time,pay_style,
 		ROUND(real_pay / 100, 2) real_pay,
 		ROUND(plat_split / 100, 2) plat_split,
 		0.00 2plat_split,
 		ROUND(plat_split / 100, 2) - 0.00 '手续费差值',
 		ROUND(trades_split / 100, 2) trades_split,
 		ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 		ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 		ROUND(O2O_freight / 100, 2) O2O_freight,
 		ROUND(real_pay * 0.05 / 100, 2) O2O,
 		ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 		settlement_status,
 		CASE trades_status
 	WHEN 110 THEN 		'110(等侍买家付款) '
 	WHEN 120 THEN 		'120(等待卖家发货)'
 	WHEN 130 THEN 		'130(等侍买家确认收货)'
 	WHEN 140 THEN 		'140(买家已签收，货到付款专用)'
 	WHEN 150 THEN 		'150(交易成功)'
 	WHEN 160 THEN 		'160(用户未付款主动关闭)'
 	WHEN 170 THEN 		'170(超时未付款，系统关闭)'
 	WHEN 180 THEN 		'180(商家关闭订单)XXXXXXXX'
 	WHEN 200 THEN 		'200(待取货|待自提，直购和自提专用)'
 	WHEN 210 THEN 		'210(已取货|已自提 直购和自提专用)'
 	WHEN 900 THEN 		'900(已退款)xxxxxxxxxx'
 	WHEN 220 THEN 		'220(用户确认收货)'
 	WHEN 230 THEN 		'230(门店确认收货)'
 	WHEN 800 THEN 		'800(系统确认收货)'
 	WHEN 240 THEN 		'240(已取消【门店自提待自提后可取消订单】)'
 	ELSE 		'oo'
 	END '交易状态'
 	FROM 		b_trades b
WHERE site_id='100050'
 AND create_time > '2017-6-21 14:35:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-21 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;


/*(13) 100180	湖南千金大药房连锁有限公司

迁移时间：2017-4-27 20:00

迁移之后的新的订单数：145

SELECT site_id,trades_id,create_time FROM b_trades  WHERE site_id='100180'  AND create_time > '2017-4-27 20:00'


最后结算时间：2017-4-26	23:59:59

结算日：2017-6-1

符合订单数：29
*/

/*查询结算周期内符合条件的订单----------------------------------*/
SELECT site_id,trades_id,create_time,
CASE trades_status
when 110 THEN '110(等侍买家付款), '
when 120 THEN '120(等待卖家发货),'
when 130 THEN '130(等侍买家确认收货),'
when 140 THEN '140(买家已签收，货到付款专用)，'
when 150 THEN '150(交易成功)，'
when 160 THEN '160(用户未付款主动关闭)，'
when 170 THEN '170(超时未付款，系统关闭)，'
when 180 THEN '180(商家关闭订单)，'
when 200 THEN '200(待取货|待自提，直购和自提专用),'
when 210 THEN '210（已取货|已自提 直购和自提专用），'
when 900 THEN '900（已退款），'
when 220 THEN '220(用户确认收货)，'
when 230 THEN '230(门店确认收货)，'
when 800 THEN '800（系统确认收货）,'
when 240 THEN '240(已取消【门店自提待自提后可取消订单】)'
else 'oo'
end '交易状态',
is_payment,pay_time,pay_style,ROUND(real_pay/100,2)  real_pay,ROUND(plat_split/100,2) plat_split,ROUND(trades_split/100,2) trades_split,ROUND(O2O_freight/100,2) O2O_freight,
deal_finish_status,end_time,settlement_status,finance_no
FROM b_trades
WHERE site_id='100180'
 AND create_time > '2017-4-27 20:00:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-4-27 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*

SELECT create_time FROM b_trades  WHERE site_id=100050
ORDER BY create_time DESC;

*/

/*
清除结算
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-4-27 20:00:00' AND is_payment = 1
and site_id=100180 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-4-27 20:00:00' AND is_payment = 1
and site_id=100180 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-4-27 20:00:00'
and site_id=100180 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-4-27 20:00:00'
and site_id=100180 /*清洗医保卡手续费*/;

/*6.14问题修复*/
update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-4-27 20:00:00'
and site_id=100180/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-4-27 20:00:00'
and site_id=100180 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-4-27 20:00:00'
and site_id=100180 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-4-27 20:00:00'
and site_id=100180 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100180  AND create_time>'2017-4-27 20:00:00'
/*清洗直购分销状态*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100180  AND create_time>'2017-4-27 20:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100180  AND create_time>'2017-4-27 20:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流')/*清洗O2O配送费*/;




/*检查金额----------------------------------------------------*/
SELECT *  from  (
	SELECT		id,b.trades_id, 	b.create_time, 	b.pay_time, 	deal_finish_status, 	b.end_time,
 	pay_style,
 	ROUND(real_pay / 100, 2) real_pay,
 	ROUND(plat_split / 100, 2) plat_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2plat_split,
 	ROUND(plat_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '手续费差值',
 	ROUND(trades_split / 100, 2) trades_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 	ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 	ROUND(O2O_freight / 100, 2) O2O_freight,
 	ROUND(real_pay * 0.05 / 100, 2) O2O,
 	ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 	settlement_status,
 	CASE trades_status
 WHEN 110 THEN 	'110(等侍买家付款) '
 WHEN 120 THEN 	'120(等待卖家发货)'
 WHEN 130 THEN 	'130(等侍买家确认收货)'
 WHEN 140 THEN 	'140(买家已签收，货到付款专用)'
 WHEN 150 THEN 	'150(交易成功)'
 WHEN 160 THEN 	'160(用户未付款主动关闭)'
 WHEN 170 THEN 	'170(超时未付款，系统关闭)'
 WHEN 180 THEN 	'180(商家关闭订单)XXXXXXXX'
 WHEN 200 THEN 	'200(待取货|待自提，直购和自提专用)'
 WHEN 210 THEN 	'210(已取货|已自提 直购和自提专用)'
 WHEN 900 THEN 	'900(已退款)xxxxxxxxxx'
 WHEN 220 THEN 	'220(用户确认收货)'
 WHEN 230 THEN 	'230(门店确认收货)'
 WHEN 800 THEN 	'800(系统确认收货)'
 WHEN 240 THEN 	'240(已取消【门店自提待自提后可取消订单】)'
 ELSE	'oo'
 END '交易状态'
 FROM 	b_trades b
WHERE site_id='100180'
 AND create_time > '2017-4-27 20:00:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-4-27 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style IN ('wx', 'ali') /*交易结束状态*/
 UNION

SELECT			id, 		b.trades_id, b.create_time,b.pay_time,deal_finish_status,b.end_time,pay_style,
 		ROUND(real_pay / 100, 2) real_pay,
 		ROUND(plat_split / 100, 2) plat_split,
 		0.00 2plat_split,
 		ROUND(plat_split / 100, 2) - 0.00 '手续费差值',
 		ROUND(trades_split / 100, 2) trades_split,
 		ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 		ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 		ROUND(O2O_freight / 100, 2) O2O_freight,
 		ROUND(real_pay * 0.05 / 100, 2) O2O,
 		ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 		settlement_status,
 		CASE trades_status
 	WHEN 110 THEN 		'110(等侍买家付款) '
 	WHEN 120 THEN 		'120(等待卖家发货)'
 	WHEN 130 THEN 		'130(等侍买家确认收货)'
 	WHEN 140 THEN 		'140(买家已签收，货到付款专用)'
 	WHEN 150 THEN 		'150(交易成功)'
 	WHEN 160 THEN 		'160(用户未付款主动关闭)'
 	WHEN 170 THEN 		'170(超时未付款，系统关闭)'
 	WHEN 180 THEN 		'180(商家关闭订单)XXXXXXXX'
 	WHEN 200 THEN 		'200(待取货|待自提，直购和自提专用)'
 	WHEN 210 THEN 		'210(已取货|已自提 直购和自提专用)'
 	WHEN 900 THEN 		'900(已退款)xxxxxxxxxx'
 	WHEN 220 THEN 		'220(用户确认收货)'
 	WHEN 230 THEN 		'230(门店确认收货)'
 	WHEN 800 THEN 		'800(系统确认收货)'
 	WHEN 240 THEN 		'240(已取消【门店自提待自提后可取消订单】)'
 	ELSE 		'oo'
 	END '交易状态'
 	FROM 		b_trades b
WHERE site_id='100180'
 AND create_time > '2017-4-27 20:00:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-4-27 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;


/*(14) 100166	杭州九洲大药房连锁有限公司


迁移时间：2017-5-11 2:25

迁移之后的新的订单数：6488

SELECT site_id,trades_id,create_time FROM b_trades  WHERE site_id='100166'  AND create_time > '2017-5-11 2:25'


最后结算时间：2017-5-10	23:59:59

结算日：2017-6-20

符合订单数：
*/

/*查询结算周期内符合条件的订单----------------------------------*/
SELECT site_id,trades_id,create_time,
CASE trades_status
when 110 THEN '110(等侍买家付款), '
when 120 THEN '120(等待卖家发货),'
when 130 THEN '130(等侍买家确认收货),'
when 140 THEN '140(买家已签收，货到付款专用)，'
when 150 THEN '150(交易成功)，'
when 160 THEN '160(用户未付款主动关闭)，'
when 170 THEN '170(超时未付款，系统关闭)，'
when 180 THEN '180(商家关闭订单)，'
when 200 THEN '200(待取货|待自提，直购和自提专用),'
when 210 THEN '210（已取货|已自提 直购和自提专用），'
when 900 THEN '900（已退款），'
when 220 THEN '220(用户确认收货)，'
when 230 THEN '230(门店确认收货)，'
when 800 THEN '800（系统确认收货）,'
when 240 THEN '240(已取消【门店自提待自提后可取消订单】)'
else 'oo'
end '交易状态',
is_payment,pay_time,pay_style,ROUND(real_pay/100,2)  real_pay,ROUND(plat_split/100,2) plat_split,ROUND(trades_split/100,2) trades_split,ROUND(O2O_freight/100,2) O2O_freight,
deal_finish_status,end_time,settlement_status,finance_no
FROM b_trades
WHERE site_id='100166'
 AND create_time > '2017-5-11 2:25:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-5-11 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*

SELECT create_time FROM b_trades  WHERE site_id=100050
ORDER BY create_time DESC;

*/
/*
清除结算
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-5-11 02:25:00' AND is_payment = 1
and site_id=100166 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-5-11 02:25:00' AND is_payment = 1
and site_id=100166 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-5-11 02:25:00'
and site_id=100166 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-5-11 02:25:00'
and site_id=100166 /*清洗医保卡手续费*/;

/*6.14问题修复*/
update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-5-11 02:25:00'
and site_id=100166/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-5-11 02:25:00'
and site_id=100166 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-5-11 02:25:00'
and site_id=100166 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-5-11 02:25:00'
and site_id=100166 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100166  AND create_time>'2017-5-11 02:25:00'
/*清洗直购分销状态*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100166  AND create_time>'2017-5-11 02:25:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100166  AND create_time>'2017-5-11 02:25:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流')/*清洗O2O配送费*/;





/*检查金额----------------------------------------------------*/
SELECT *  from  (
	SELECT		id,b.trades_id, 	b.create_time, 	b.pay_time, 	deal_finish_status, 	b.end_time,
 	pay_style,
 	ROUND(real_pay / 100, 2) real_pay,
 	ROUND(plat_split / 100, 2) plat_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2plat_split,
 	ROUND(plat_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '手续费差值',
 	ROUND(trades_split / 100, 2) trades_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 	ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 	ROUND(O2O_freight / 100, 2) O2O_freight,
 	ROUND(real_pay * 0.05 / 100, 2) O2O,
 	ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 	settlement_status,
 	CASE trades_status
 WHEN 110 THEN 	'110(等侍买家付款) '
 WHEN 120 THEN 	'120(等待卖家发货)'
 WHEN 130 THEN 	'130(等侍买家确认收货)'
 WHEN 140 THEN 	'140(买家已签收，货到付款专用)'
 WHEN 150 THEN 	'150(交易成功)'
 WHEN 160 THEN 	'160(用户未付款主动关闭)'
 WHEN 170 THEN 	'170(超时未付款，系统关闭)'
 WHEN 180 THEN 	'180(商家关闭订单)XXXXXXXX'
 WHEN 200 THEN 	'200(待取货|待自提，直购和自提专用)'
 WHEN 210 THEN 	'210(已取货|已自提 直购和自提专用)'
 WHEN 900 THEN 	'900(已退款)xxxxxxxxxx'
 WHEN 220 THEN 	'220(用户确认收货)'
 WHEN 230 THEN 	'230(门店确认收货)'
 WHEN 800 THEN 	'800(系统确认收货)'
 WHEN 240 THEN 	'240(已取消【门店自提待自提后可取消订单】)'
 ELSE	'oo'
 END '交易状态'
 FROM 	b_trades b
WHERE site_id='100166'
 AND create_time > '2017-5-11 2:25:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-5-11 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style IN ('wx', 'ali') /*交易结束状态*/
 UNION

SELECT			id, 		b.trades_id, b.create_time,b.pay_time,deal_finish_status,b.end_time,pay_style,
 		ROUND(real_pay / 100, 2) real_pay,
 		ROUND(plat_split / 100, 2) plat_split,
 		0.00 2plat_split,
 		ROUND(plat_split / 100, 2) - 0.00 '手续费差值',
 		ROUND(trades_split / 100, 2) trades_split,
 		ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 		ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 		ROUND(O2O_freight / 100, 2) O2O_freight,
 		ROUND(real_pay * 0.05 / 100, 2) O2O,
 		ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 		settlement_status,
 		CASE trades_status
 	WHEN 110 THEN 		'110(等侍买家付款) '
 	WHEN 120 THEN 		'120(等待卖家发货)'
 	WHEN 130 THEN 		'130(等侍买家确认收货)'
 	WHEN 140 THEN 		'140(买家已签收，货到付款专用)'
 	WHEN 150 THEN 		'150(交易成功)'
 	WHEN 160 THEN 		'160(用户未付款主动关闭)'
 	WHEN 170 THEN 		'170(超时未付款，系统关闭)'
 	WHEN 180 THEN 		'180(商家关闭订单)XXXXXXXX'
 	WHEN 200 THEN 		'200(待取货|待自提，直购和自提专用)'
 	WHEN 210 THEN 		'210(已取货|已自提 直购和自提专用)'
 	WHEN 900 THEN 		'900(已退款)xxxxxxxxxx'
 	WHEN 220 THEN 		'220(用户确认收货)'
 	WHEN 230 THEN 		'230(门店确认收货)'
 	WHEN 800 THEN 		'800(系统确认收货)'
 	WHEN 240 THEN 		'240(已取消【门店自提待自提后可取消订单】)'
 	ELSE 		'oo'
 	END '交易状态'
 	FROM 		b_trades b
WHERE site_id='100166'
 AND create_time > '2017-5-11 2:25:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-5-11 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;





/*(15) 100073	枣庄市百姓药业零售连锁有限公司	2017-5-17 2:00



迁移时间：2017-5-17 2:00

迁移之后的新的订单数：6149

SELECT site_id,trades_id,create_time FROM b_trades  WHERE site_id='100166'  AND create_time > '2017-5-17 2:00'


最后结算时间：2017-5-16	23:59:59

结算日：2017-6-1

符合订单数：92
*/

/*查询结算周期内符合条件的订单----------------------------------*/
SELECT site_id,trades_id,create_time,
CASE trades_status
when 110 THEN '110(等侍买家付款), '
when 120 THEN '120(等待卖家发货),'
when 130 THEN '130(等侍买家确认收货),'
when 140 THEN '140(买家已签收，货到付款专用)，'
when 150 THEN '150(交易成功)，'
when 160 THEN '160(用户未付款主动关闭)，'
when 170 THEN '170(超时未付款，系统关闭)，'
when 180 THEN '180(商家关闭订单)，'
when 200 THEN '200(待取货|待自提，直购和自提专用),'
when 210 THEN '210（已取货|已自提 直购和自提专用），'
when 900 THEN '900（已退款），'
when 220 THEN '220(用户确认收货)，'
when 230 THEN '230(门店确认收货)，'
when 800 THEN '800（系统确认收货）,'
when 240 THEN '240(已取消【门店自提待自提后可取消订单】)'
else 'oo'
end '交易状态',
is_payment,pay_time,pay_style,ROUND(real_pay/100,2)  real_pay,ROUND(plat_split/100,2) plat_split,ROUND(trades_split/100,2) trades_split,ROUND(O2O_freight/100,2) O2O_freight,
deal_finish_status,end_time,settlement_status,finance_no
FROM b_trades
WHERE site_id='100073'
 AND create_time > '2017-5-17 2:00:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-5-17 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*

SELECT create_time FROM b_trades  WHERE site_id=100050
ORDER BY create_time DESC;

*/
/*
清除结算
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-5-17 02:00:00' AND is_payment = 1
and site_id=100073 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-5-17 02:00:00' AND is_payment = 1
and site_id=100073 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-5-17 02:00:00'
and site_id=100073 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-5-17 02:00:00'
and site_id=100073 /*清洗医保卡手续费*/;

/*6.14问题修复*/
update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-5-17 02:00:00'
and site_id=100073/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-5-17 02:00:00'
and site_id=100073 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-5-17 02:00:00'
and site_id=100073 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-5-17 02:00:00'
and site_id=100073 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100073  AND create_time>'2017-5-17 02:00:00'
/*清洗直购分销状态*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100073  AND create_time>'2017-5-17 02:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100073  AND create_time>'2017-5-17 02:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流')/*清洗O2O配送费*/;






/*检查金额----------------------------------------------------*/
SELECT *  from  (
	SELECT		id,b.trades_id, 	b.create_time, 	b.pay_time, 	deal_finish_status, 	b.end_time,
 	pay_style,
 	ROUND(real_pay / 100, 2) real_pay,
 	ROUND(plat_split / 100, 2) plat_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2plat_split,
 	ROUND(plat_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '手续费差值',
 	ROUND(trades_split / 100, 2) trades_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 	ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 	ROUND(O2O_freight / 100, 2) O2O_freight,
 	ROUND(real_pay * 0.05 / 100, 2) O2O,
 	ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 	settlement_status,
 	CASE trades_status
 WHEN 110 THEN 	'110(等侍买家付款) '
 WHEN 120 THEN 	'120(等待卖家发货)'
 WHEN 130 THEN 	'130(等侍买家确认收货)'
 WHEN 140 THEN 	'140(买家已签收，货到付款专用)'
 WHEN 150 THEN 	'150(交易成功)'
 WHEN 160 THEN 	'160(用户未付款主动关闭)'
 WHEN 170 THEN 	'170(超时未付款，系统关闭)'
 WHEN 180 THEN 	'180(商家关闭订单)XXXXXXXX'
 WHEN 200 THEN 	'200(待取货|待自提，直购和自提专用)'
 WHEN 210 THEN 	'210(已取货|已自提 直购和自提专用)'
 WHEN 900 THEN 	'900(已退款)xxxxxxxxxx'
 WHEN 220 THEN 	'220(用户确认收货)'
 WHEN 230 THEN 	'230(门店确认收货)'
 WHEN 800 THEN 	'800(系统确认收货)'
 WHEN 240 THEN 	'240(已取消【门店自提待自提后可取消订单】)'
 ELSE	'oo'
 END '交易状态'
 FROM 	b_trades b
WHERE site_id='100073'
 AND create_time > '2017-5-17 2:00:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-5-17 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style IN ('wx', 'ali') /*交易结束状态*/
 UNION

SELECT			id, 		b.trades_id, b.create_time,b.pay_time,deal_finish_status,b.end_time,pay_style,
 		ROUND(real_pay / 100, 2) real_pay,
 		ROUND(plat_split / 100, 2) plat_split,
 		0.00 2plat_split,
 		ROUND(plat_split / 100, 2) - 0.00 '手续费差值',
 		ROUND(trades_split / 100, 2) trades_split,
 		ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 		ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 		ROUND(O2O_freight / 100, 2) O2O_freight,
 		ROUND(real_pay * 0.05 / 100, 2) O2O,
 		ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 		settlement_status,
 		CASE trades_status
 	WHEN 110 THEN 		'110(等侍买家付款) '
 	WHEN 120 THEN 		'120(等待卖家发货)'
 	WHEN 130 THEN 		'130(等侍买家确认收货)'
 	WHEN 140 THEN 		'140(买家已签收，货到付款专用)'
 	WHEN 150 THEN 		'150(交易成功)'
 	WHEN 160 THEN 		'160(用户未付款主动关闭)'
 	WHEN 170 THEN 		'170(超时未付款，系统关闭)'
 	WHEN 180 THEN 		'180(商家关闭订单)XXXXXXXX'
 	WHEN 200 THEN 		'200(待取货|待自提，直购和自提专用)'
 	WHEN 210 THEN 		'210(已取货|已自提 直购和自提专用)'
 	WHEN 900 THEN 		'900(已退款)xxxxxxxxxx'
 	WHEN 220 THEN 		'220(用户确认收货)'
 	WHEN 230 THEN 		'230(门店确认收货)'
 	WHEN 800 THEN 		'800(系统确认收货)'
 	WHEN 240 THEN 		'240(已取消【门店自提待自提后可取消订单】)'
 	ELSE 		'oo'
 	END '交易状态'
 	FROM 		b_trades b
WHERE site_id='100073'
 AND create_time > '2017-5-17 2:00:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-5-17 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;



/*(16) 100154	邯郸市康业怀仁医药连锁有限公司



迁移时间：2017-5-26 16:30

迁移之后的新的订单数：22

SELECT site_id,trades_id,create_time FROM b_trades  WHERE site_id='100154'  AND create_time > '2017-5-26 16:30'


最后结算时间：2017-5-25	23:59:59

结算日：2017-6-11

符合订单数：14
*/

/*查询结算周期内符合条件的订单----------------------------------*/
SELECT site_id,trades_id,create_time,
CASE trades_status
when 110 THEN '110(等侍买家付款), '
when 120 THEN '120(等待卖家发货),'
when 130 THEN '130(等侍买家确认收货),'
when 140 THEN '140(买家已签收，货到付款专用)，'
when 150 THEN '150(交易成功)，'
when 160 THEN '160(用户未付款主动关闭)，'
when 170 THEN '170(超时未付款，系统关闭)，'
when 180 THEN '180(商家关闭订单)，'
when 200 THEN '200(待取货|待自提，直购和自提专用),'
when 210 THEN '210（已取货|已自提 直购和自提专用），'
when 900 THEN '900（已退款），'
when 220 THEN '220(用户确认收货)，'
when 230 THEN '230(门店确认收货)，'
when 800 THEN '800（系统确认收货）,'
when 240 THEN '240(已取消【门店自提待自提后可取消订单】)'
else 'oo'
end '交易状态',
is_payment,pay_time,pay_style,ROUND(real_pay/100,2)  real_pay,ROUND(plat_split/100,2) plat_split,ROUND(trades_split/100,2) trades_split,ROUND(O2O_freight/100,2) O2O_freight,
deal_finish_status,end_time,settlement_status,finance_no
FROM b_trades
WHERE site_id='100154'
 AND create_time > '2017-5-26 16:30:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-5-26 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*

SELECT create_time FROM b_trades  WHERE site_id=100050
ORDER BY create_time DESC;

*/

/*
清除结算
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-5-26 16:30:00' AND is_payment = 1
and site_id=100154 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-5-26 16:30:00' AND is_payment = 1
and site_id=100154 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-5-26 16:30:00'
and site_id=100154 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-5-26 16:30:00'
and site_id=100154 /*清洗医保卡手续费*/;

/*6.14问题修复*/
update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-5-26 16:30:00'
and site_id=100154/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-5-26 16:30:00'
and site_id=100154 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-5-26 16:30:00'
and site_id=100154 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-5-26 16:30:00'
and site_id=100154 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100154  AND create_time>'2017-5-26 16:30:00'
/*清洗直购分销状态*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100154  AND create_time>'2017-5-26 16:30:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100154  AND create_time>'2017-5-26 16:30:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流')/*清洗O2O配送费*/;





/*检查金额----------------------------------------------------*/
SELECT *  from  (
	SELECT		id,b.trades_id, 	b.create_time, 	b.pay_time, 	deal_finish_status, 	b.end_time,
 	pay_style,
 	ROUND(real_pay / 100, 2) real_pay,
 	ROUND(plat_split / 100, 2) plat_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2plat_split,
 	ROUND(plat_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '手续费差值',
 	ROUND(trades_split / 100, 2) trades_split,
 	ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 	ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 	ROUND(O2O_freight / 100, 2) O2O_freight,
 	ROUND(real_pay * 0.05 / 100, 2) O2O,
 	ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 	settlement_status,
 	CASE trades_status
 WHEN 110 THEN 	'110(等侍买家付款) '
 WHEN 120 THEN 	'120(等待卖家发货)'
 WHEN 130 THEN 	'130(等侍买家确认收货)'
 WHEN 140 THEN 	'140(买家已签收，货到付款专用)'
 WHEN 150 THEN 	'150(交易成功)'
 WHEN 160 THEN 	'160(用户未付款主动关闭)'
 WHEN 170 THEN 	'170(超时未付款，系统关闭)'
 WHEN 180 THEN 	'180(商家关闭订单)XXXXXXXX'
 WHEN 200 THEN 	'200(待取货|待自提，直购和自提专用)'
 WHEN 210 THEN 	'210(已取货|已自提 直购和自提专用)'
 WHEN 900 THEN 	'900(已退款)xxxxxxxxxx'
 WHEN 220 THEN 	'220(用户确认收货)'
 WHEN 230 THEN 	'230(门店确认收货)'
 WHEN 800 THEN 	'800(系统确认收货)'
 WHEN 240 THEN 	'240(已取消【门店自提待自提后可取消订单】)'
 ELSE	'oo'
 END '交易状态'
 FROM 	b_trades b
WHERE site_id='100154'
 AND create_time > '2017-5-26 16:30:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-5-26 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style IN ('wx', 'ali') /*交易结束状态*/
 UNION

SELECT			id, 		b.trades_id, b.create_time,b.pay_time,deal_finish_status,b.end_time,pay_style,
 		ROUND(real_pay / 100, 2) real_pay,
 		ROUND(plat_split / 100, 2) plat_split,
 		0.00 2plat_split,
 		ROUND(plat_split / 100, 2) - 0.00 '手续费差值',
 		ROUND(trades_split / 100, 2) trades_split,
 		ROUND(real_pay * 0.01 / 100, 2) 2trades_split,
 		ROUND(trades_split / 100, 2) - ROUND(real_pay * 0.01 / 100, 2) '佣金差值',
 		ROUND(O2O_freight / 100, 2) O2O_freight,
 		ROUND(real_pay * 0.05 / 100, 2) O2O,
 		ROUND(O2O_freight / 100, 2) - ROUND(real_pay * 0.05 / 100, 2) '配送费差值',
 		settlement_status,
 		CASE trades_status
 	WHEN 110 THEN 		'110(等侍买家付款) '
 	WHEN 120 THEN 		'120(等待卖家发货)'
 	WHEN 130 THEN 		'130(等侍买家确认收货)'
 	WHEN 140 THEN 		'140(买家已签收，货到付款专用)'
 	WHEN 150 THEN 		'150(交易成功)'
 	WHEN 160 THEN 		'160(用户未付款主动关闭)'
 	WHEN 170 THEN 		'170(超时未付款，系统关闭)'
 	WHEN 180 THEN 		'180(商家关闭订单)XXXXXXXX'
 	WHEN 200 THEN 		'200(待取货|待自提，直购和自提专用)'
 	WHEN 210 THEN 		'210(已取货|已自提 直购和自提专用)'
 	WHEN 900 THEN 		'900(已退款)xxxxxxxxxx'
 	WHEN 220 THEN 		'220(用户确认收货)'
 	WHEN 230 THEN 		'230(门店确认收货)'
 	WHEN 800 THEN 		'800(系统确认收货)'
 	WHEN 240 THEN 		'240(已取消【门店自提待自提后可取消订单】)'
 	ELSE 		'oo'
 	END '交易状态'
 	FROM 		b_trades b
WHERE site_id='100154'
 AND create_time > '2017-5-26 16:30:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-5-26 00:00:00' AND '2017-6-22 12:59:59' /* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;


