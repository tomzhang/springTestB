/*(17) 100015 武汉市东明药房连锁有限公司

迁移时间：2017-6-01 18:00

迁移之后的新的订单数：0

老系统结算日：2017-6-3

最后结算日：2017-05-31	23:59:59

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
WHERE site_id='100015'
 AND create_time > '2017-6-01 18:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-01 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00' and is_payment=1
and site_id=100015 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00' and is_payment=1
and site_id=100015 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-01 18:00:00'
and site_id=100015 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-01 18:00:00'
and site_id=100015 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100015/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100015 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100015 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100015 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100015  AND create_time>'2017-6-01 18:00:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100015  AND create_time>'2017-6-01 18:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100015  AND create_time>'2017-6-01 18:00:00'
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
 WHERE site_id='100015'
 AND create_time > '2017-6-01 18:00:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-01 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 WHERE site_id='100015'
 AND create_time > '2017-6-01 18:00:00'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-01 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;




/*(18) 100102 无锡山禾集团健康参药连锁有限公司

迁移时间：2017-6-01 18:00

迁移之后的新的订单数：1

老系统结算日：2017-6-4

最后结算日：2017-05-31	23:59:59

符合新结算订单数：1
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
WHERE site_id='100102'
 AND create_time > '2017-6-01 18:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-01 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00' and is_payment=1
and site_id=100102 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00' and is_payment=1
and site_id=100102 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-01 18:00:00'
and site_id=100102 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-01 18:00:00'
and site_id=100102 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100102/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100102 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100102 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100102 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100102  AND create_time>'2017-6-01 18:00:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100102  AND create_time>'2017-6-01 18:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100102  AND create_time>'2017-6-01 18:00:00'
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
 WHERE site_id='100102'
 AND create_time > '2017-6-01 18:00:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-01 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 AND create_time > '2017-6-01 18:00:00'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-01 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;





/*(19) 100155 深圳市杏林春堂大药房连锁有限公司

迁移时间：2017-6-01 18:00

迁移之后的新的订单数：0

老系统结算日：2017-6-14

最后结算日：2017-05-31	23:59:59

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
WHERE site_id='100155'
 AND create_time > '2017-6-01 18:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-01 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00' and is_payment=1
and site_id=100155 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00' and is_payment=1
and site_id=100155 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-01 18:00:00'
and site_id=100155 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-01 18:00:00'
and site_id=100155 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100155/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100155 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100155 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100155 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100155  AND create_time>'2017-6-01 18:00:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100155  AND create_time>'2017-6-01 18:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100155  AND create_time>'2017-6-01 18:00:00'
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
 WHERE site_id='100155'
 AND create_time > '2017-6-01 18:00:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-01 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 WHERE site_id='100155'
 AND create_time > '2017-6-01 18:00:00'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-01 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;





/*(20) 100171 深圳市正洲大药房连锁有限公司

迁移时间：2017-6-01 18:00

迁移之后的新的订单数：1

老系统结算日：2017-6-18

最后结算日：2017-05-31	23:59:59

符合新结算订单数：1
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
WHERE site_id='100171'
 AND create_time > '2017-6-01 18:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-01 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00' and is_payment=1
and site_id=100171 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00' and is_payment=1
and site_id=100171 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-01 18:00:00'
and site_id=100171 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-01 18:00:00'
and site_id=100171 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100171/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100171 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100171 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100171 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100171  AND create_time>'2017-6-01 18:00:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100171  AND create_time>'2017-6-01 18:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100171  AND create_time>'2017-6-01 18:00:00'
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
 WHERE site_id='100171'
 AND create_time > '2017-6-01 18:00:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-01 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 WHERE site_id='100171'
 AND create_time > '2017-6-01 18:00:00'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-01 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;



/*(21) 100192 金华市尖峰大药房连锁有限公司

迁移时间：2017-6-01 18:00

迁移之后的新的订单数：0

老系统结算日：2017-6-21

最后结算日：2017-05-31	23:59:59

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
WHERE site_id='100192'
 AND create_time > '2017-6-01 18:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-01 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00' and is_payment=1
and site_id=100192 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00' and is_payment=1
and site_id=100192 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-01 18:00:00'
and site_id=100192 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-01 18:00:00'
and site_id=100192 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100192/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100192 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100192 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100192 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100192  AND create_time>'2017-6-01 18:00:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100192  AND create_time>'2017-6-01 18:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100192  AND create_time>'2017-6-01 18:00:00'
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
 WHERE site_id='100192'
 AND create_time > '2017-6-01 18:00:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-01 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 WHERE site_id='100192'
 AND create_time > '2017-6-01 18:00:00'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-01 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;



/*(22) 100094 宁波彩虹大药房有限公司

迁移时间：2017-6-01 18:00

迁移之后的新的订单数：6

老系统结算日：2017-6-22

最后结算日：2017-05-31	23:59:59

符合新结算订单数：5
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
WHERE site_id='100094'
 AND create_time > '2017-6-01 18:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-01 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00' and is_payment=1
and site_id=100094 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00' and is_payment=1
and site_id=100094 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-01 18:00:00'
and site_id=100094 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-01 18:00:00'
and site_id=100094 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100094/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100094 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100094 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-01 18:00:00'
and site_id=100094 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100094  AND create_time>'2017-6-01 18:00:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100094  AND create_time>'2017-6-01 18:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100094  AND create_time>'2017-6-01 18:00:00'
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
 WHERE site_id='100094'
 AND create_time > '2017-6-01 18:00:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-01 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 WHERE site_id='100094'
 AND create_time > '2017-6-01 18:00:00'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-01 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;


/*(23) 100161 昆山市万家春医药连锁有限公司

迁移时间：2017-6-12 18:00

迁移之后的新的订单数：15

老系统结算日：2017-6-09

最后结算日：2017-06-11	23:59:59

符合新结算订单数：10
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
WHERE site_id='100161'
 AND create_time > '2017-6-12 18:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-12 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-12 18:00:00' and is_payment=1
and site_id=100161 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-12 18:00:00' and is_payment=1
and site_id=100161 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-12 18:00:00'
and site_id=100161 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-12 18:00:00'
and site_id=100161 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-12 18:00:00'
and site_id=100161/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-12 18:00:00'
and site_id=100161 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-12 18:00:00'
and site_id=100161 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-12 18:00:00'
and site_id=100161 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100161  AND create_time>'2017-6-12 18:00:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100161  AND create_time>'2017-6-12 18:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100161  AND create_time>'2017-6-12 18:00:00'
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
 WHERE site_id='100161'
 AND create_time > '2017-6-12 18:00:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-12 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 WHERE site_id='100161'
 AND create_time > '2017-6-12 18:00:00'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-12 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;



/*(24) 100042 宁夏国大药房连锁有限公司

迁移时间：2017-6-12 18:00

迁移之后的新的订单数：0

老系统结算日：2017-6-04

最后结算日：2017-06-12	23:59:59

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
WHERE site_id='100042'
 AND create_time > '2017-6-13 18:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-13 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-13 18:00:00' and is_payment=1
and site_id=100042 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-13 18:00:00' and is_payment=1
and site_id=100042 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-13 18:00:00'
and site_id=100042 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-13 18:00:00'
and site_id=100042 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-13 18:00:00'
and site_id=100042/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-13 18:00:00'
and site_id=100042 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-13 18:00:00'
and site_id=100042 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-13 18:00:00'
and site_id=100042 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100042  AND create_time>'2017-6-13 18:00:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100042  AND create_time>'2017-6-13 18:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100042  AND create_time>'2017-6-13 18:00:00'
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
 WHERE site_id='100042'
 AND create_time > '2017-6-13 18:00:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-13 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 WHERE site_id='100042'
 AND create_time > '2017-6-13 18:00:00'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-13 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;


/*(25) 100055 衡水百草堂医药连锁有限公司

迁移时间：2017-6-14 09:00

迁移之后的新的订单数：19

老系统结算日：2017-6-13

最后结算日：2017-06-13	23:59:59

符合新结算订单数：15
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
WHERE site_id='100055'
 AND create_time > '2017-6-14 09:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-14 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-14 09:00' and is_payment=1
and site_id=100055 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-14 09:00' and is_payment=1
and site_id=100055 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-14 09:00'
and site_id=100055 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-14 09:00'
and site_id=100055 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-14 09:00'
and site_id=100055/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-14 09:00'
and site_id=100055 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-14 09:00'
and site_id=100055 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-14 09:00'
and site_id=100055 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100055  AND create_time>'2017-6-14 09:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100055  AND create_time>'2017-6-14 09:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100055  AND create_time>'2017-6-14 09:00'
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
 WHERE site_id='100055'
 AND create_time > '2017-6-14 09:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-14 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 WHERE site_id='100055'
 AND create_time > '2017-6-14 09:00'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-14 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;



/*(26) 100151 广州博济康之选医药连锁有限公司

迁移时间：2017-6-16 09:32

迁移之后的新的订单数：1

老系统结算日：2017-6-5

最后结算日：2017-06-15	23:59:59

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
WHERE site_id='100151'
 AND create_time > '2017-6-16 09:32' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:32' and is_payment=1
and site_id=100151 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:32' and is_payment=1
and site_id=100151 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-16 09:32'
and site_id=100151 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-16 09:32'
and site_id=100151 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:32'
and site_id=100151/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:32'
and site_id=100151 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:32'
and site_id=100151 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:32'
and site_id=100151 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100151  AND create_time>'2017-6-16 09:32';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100151  AND create_time>'2017-6-16 09:32'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100151  AND create_time>'2017-6-16 09:32'
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
 WHERE site_id='100151'
 AND create_time > '2017-6-16 09:32' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 WHERE site_id='100151'
 AND create_time > '2017-6-16 09:32'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;






/*(27) 100156 深圳市桐济堂医药有限公司

迁移时间：2017-6-16 09:34

迁移之后的新的订单数：0

老系统结算日：2017-6-18

最后结算日：2017-06-15	23:59:59

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
WHERE site_id='100156'
 AND create_time > '2017-6-16 09:34' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:34' and is_payment=1
and site_id=100156 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:34' and is_payment=1
and site_id=100156 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-16 09:34'
and site_id=100156 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-16 09:34'
and site_id=100156 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:34'
and site_id=100156/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:34'
and site_id=100156 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:34'
and site_id=100156 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:34'
and site_id=100156 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100156  AND create_time>'2017-6-16 09:34';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100156  AND create_time>'2017-6-16 09:34'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100156  AND create_time>'2017-6-16 09:34'
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
 WHERE site_id='100156'
 AND create_time > '2017-6-16 09:34' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 WHERE site_id='100156'
 AND create_time > '2017-6-16 09:34'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;




/*(28) 100175 深圳市欧健药业有限公司

迁移时间：2017-6-16 09:39

迁移之后的新的订单数：0

老系统结算日：2017-6-22

最后结算日：2017-06-15	23:59:59

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
WHERE site_id='100175'
 AND create_time > '2017-6-16 09:39' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:39' and is_payment=1
and site_id=100175 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:39' and is_payment=1
and site_id=100175 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-16 09:39'
and site_id=100175 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-16 09:39'
and site_id=100175 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:39'
and site_id=100175/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:39'
and site_id=100175 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:39'
and site_id=100175 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:39'
and site_id=100175 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100175  AND create_time>'2017-6-16 09:39';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100175 AND create_time>'2017-6-16 09:39'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100175  AND create_time>'2017-6-16 09:39'
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
 WHERE site_id='100175'
 AND create_time > '2017-6-16 09:39' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 WHERE site_id='100175'
 AND create_time > '2017-6-16 09:39'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;







/*(29) 100186 深圳市泰康堂医药有限公司

迁移时间：2017-6-16 09:40

迁移之后的新的订单数：0

老系统结算日：2017-6-20

最后结算日：2017-06-15	23:59:59

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
WHERE site_id='100186'
 AND create_time > '2017-6-16 09:40' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:40' and is_payment=1
and site_id=100186 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:40' and is_payment=1
and site_id=100186 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-16 09:40'
and site_id=100186 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-16 09:40'
and site_id=100186 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:40'
and site_id=100186/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:40'
and site_id=100186 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:40'
and site_id=100186 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-16 09:40'
and site_id=100186 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100186 AND create_time>'2017-6-16 09:40';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100186 AND create_time>'2017-6-16 09:40'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100186 AND create_time>'2017-6-16 09:40'
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
 WHERE site_id='100186'
 AND create_time > '2017-6-16 09:40' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 WHERE site_id='100186'
 AND create_time > '2017-6-16 09:40'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-16 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;


/*(30) 100058 无锡星洲百姓人家药店连锁有限公司

迁移时间：2017-6-20 10:20

迁移之后的新的订单数：0

老系统结算日：2017-6-13

最后结算日：2017-06-19	23:59:59

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
WHERE site_id='100058'
 AND create_time > '2017-6-20 10:20' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 10:20' and is_payment=1
and site_id=100058 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 10:20' and is_payment=1
and site_id=100058 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-20 10:20'
and site_id=100058 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-20 10:20'
and site_id=100058 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 10:20'
and site_id=100058/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 10:20'
and site_id=100058 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 10:20'
and site_id=100058 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 10:20'
and site_id=100058 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100058  AND  create_time>'2017-6-20 10:20';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100058  AND create_time>'2017-6-16 09:34'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100058  AND create_time>'2017-6-16 09:34'
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
 WHERE site_id='100058'
 AND create_time > '2017-6-20 10:20' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 WHERE site_id='100058'
 AND create_time > '2017-6-20 10:20'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;


/*(31) 100093 宁波甬药医药有限公司

迁移时间：2017-6-20 11:26

迁移之后的新的订单数：0

老系统结算日：2017-6-22

最后结算日：2017-06-19	23:59:59

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
WHERE site_id='100093'
 AND create_time > '2017-6-20 11:26' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 11:26' and is_payment=1
and site_id=100093 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 11:26' and is_payment=1
and site_id=100093 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-20 11:26'
and site_id=100093 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-20 11:26'
and site_id=100093 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 11:26'
and site_id=100093/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 11:26'
and site_id=100093 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 11:26'
and site_id=100093 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 11:26'
and site_id=100093 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100093  AND create_time>'2017-6-20 11:26';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100093  AND create_time>'2017-6-20 11:26'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100093  AND create_time>'2017-6-20 11:26'
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
 WHERE site_id='100093'
 AND create_time > '2017-6-20 11:26' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 WHERE site_id='100093'
 AND create_time > '2017-6-20 11:26'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;




	/*(32) 100190 无锡天润医药连锁有限公司

迁移时间：2017-6-20 14:11

迁移之后的新的订单数：2

老系统结算日：2017-6-20

最后结算日：2017-06-19	23:59:59

符合新结算订单数：1
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
WHERE site_id='100190'
 AND create_time > '2017-6-20 14:11' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 14:11' and is_payment=1
and site_id=100190 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 14:11' and is_payment=1
and site_id=100190 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-20 14:11'
and site_id=100190 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-20 14:11'
and site_id=100190 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 14:11'
and site_id=100190/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 14:11'
and site_id=100190 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 14:11'
and site_id=100190 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 14:11'
and site_id=100190 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100190  AND create_time>'2017-6-20 14:11';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100190  AND create_time>'2017-6-20 14:11'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100190  AND create_time>'2017-6-20 14:11'
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
 WHERE site_id='100190'
 AND create_time > '2017-6-20 14:11' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 WHERE site_id='100190'
 AND create_time > '2017-6-20 14:11'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;



	/*(33) 100187 滨州神奇大药房有限公司

迁移时间：2017-6-20 14:37

迁移之后的新的订单数：13

老系统结算日：2017-6-07

最后结算日：2017-06-19	23:59:59

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
WHERE site_id='100187'
 AND create_time > '2017-6-20 14:37' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 14:37' and is_payment=1
and site_id=100187 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 14:37' and is_payment=1
and site_id=100187 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-20 14:37'
and site_id=100187 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-20 14:37'
and site_id=100187 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 14:37'
and site_id=100187/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 14:37'
and site_id=100187 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 14:37'
and site_id=100187 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 14:37'
and site_id=100187 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100187  AND create_time>'2017-6-20 14:37';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100187  AND create_time>'2017-6-20 14:37'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100187  AND create_time>'2017-6-20 14:37'
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
 WHERE site_id='100187'
 AND create_time > '2017-6-20 14:37' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 WHERE site_id='100187'
 AND create_time > '2017-6-20 14:37'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;

	/*(34) 100011 湖州德泰恒大药房连锁有限公司

迁移时间：2017-6-20 15:00

迁移之后的新的订单数：2

老系统结算日：2017-6-03

最后结算日：2017-06-19	23:59:59

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
WHERE site_id='100011'
 AND create_time > '2017-6-20 15:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 15:00' and is_payment=1
and site_id=100011 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 15:00' and is_payment=1
and site_id=100011 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-20 15:00'
and site_id=100011 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-20 15:00'
and site_id=100011 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 15:00'
and site_id=100011/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 15:00'
and site_id=100011 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 15:00'
and site_id=100011 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-20 15:00'
and site_id=100011 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100011  AND create_time>'2017-6-20 15:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100011  AND create_time>'2017-6-20 15:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100011  AND create_time>'2017-6-20 15:00'
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
 WHERE site_id='100011'
 AND create_time > '2017-6-20 15:00' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 WHERE site_id='100011'
 AND create_time > '2017-6-20 15:00'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-20 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;

	/*(35) 100106 邯郸市志和堂医药连锁有限公司

迁移时间：2017-6-21 13:40

迁移之后的新的订单数：0

老系统结算日：2017-6-01

最后结算日：2017-06-20	23:59:59

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
WHERE site_id='100106'
 AND create_time > '2017-6-21 13:40' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-21 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 13:40' and is_payment=1
and site_id=100106 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 13:40' and is_payment=1
and site_id=100106 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-21 13:40'
and site_id=100106 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-21 13:40'
and site_id=100106 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 13:40'
and site_id=100106/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 13:40'
and site_id=100106 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 13:40'
and site_id=100106 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 13:40'
and site_id=100106 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100106  AND create_time>'2017-6-21 13:40';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100106  AND create_time>'2017-6-21 13:40'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100106  AND create_time>'2017-6-21 13:40'
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
 WHERE site_id='100106'
 AND create_time > '2017-6-21 13:40' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-21 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 WHERE site_id='100106'
 AND create_time > '2017-6-21 13:40'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-21 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;


	/*(36) 100129 廊坊市健生堂医药零售连锁有限公司

迁移时间：2017-6-21 14:03

迁移之后的新的订单数：0

老系统结算日：2017-6-01

最后结算日：2017-06-20	23:59:59

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
WHERE site_id='100129'
 AND create_time > '2017-6-21 14:03' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-21 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
ORDER BY create_time;

/*
数据清洗
*/
update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 14:03' and is_payment=1
and site_id=100129 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 14:03' and is_payment=1
and site_id=100129 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-6-21 14:03'
and site_id=100129 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-6-21 14:03'
and site_id=100129 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 14:03'
and site_id=100129/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 14:03'
and site_id=100129 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 14:03'
and site_id=100129 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-6-21 14:03'
and site_id=100129 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100129  AND create_time>'2017-6-21 14:03';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100129  AND create_time>'2017-6-21 14:03'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100129  AND create_time>'2017-6-21 14:03'
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
 WHERE site_id='100129'
 AND create_time > '2017-6-21 14:03' /* 迁移时间*/
 AND is_payment = 1 /* 已付款 */
 AND deal_finish_status = 1 /*交易结束*/
 AND end_time BETWEEN '2017-6-21 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
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
 WHERE site_id='100129'
 AND create_time > '2017-6-21 14:03'    /* 迁移时间*/
 AND is_payment = 1    /* 已付款 */
 AND deal_finish_status = 1   /*交易结束*/
 AND end_time BETWEEN '2017-6-21 00:00:00' AND '2017-6-28 11:59:59'/* 交易结束时间：结算周期*/
 AND (settlement_status = 150	OR settlement_status = 200)
 AND pay_style NOT IN ('wx', 'ali')  /*交易结束状态*/
	)aa
	where 佣金差值!=0 or 手续费差值!=0;