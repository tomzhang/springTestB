update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-02 23:59:59' and is_payment=1
and site_id=100198 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-02 23:59:59' and is_payment=1
and site_id=100198 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-05-02 23:59:59'
and site_id=100198 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-05-02 23:59:59'
and site_id=100198 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-02 23:59:59'
and site_id=100198/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-02 23:59:59'
and site_id=100198 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-02 23:59:59'
and site_id=100198 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-02 23:59:59'
and site_id=100198 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100198  AND create_time>'2017-05-02 23:59:59';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100198  AND create_time>'2017-05-02 23:59:59'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100198  AND create_time>'2017-05-02 23:59:59'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-02 23:59:59' and is_payment=1
and site_id=100199 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-02 23:59:59' and is_payment=1
and site_id=100199 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-05-02 23:59:59'
and site_id=100199 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-05-02 23:59:59'
and site_id=100199 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-02 23:59:59'
and site_id=100199/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-02 23:59:59'
and site_id=100199 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-02 23:59:59'
and site_id=100199 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-02 23:59:59'
and site_id=100199 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100199  AND create_time>'2017-05-02 23:59:59';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100199  AND create_time>'2017-05-02 23:59:59'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100199  AND create_time>'2017-05-02 23:59:59'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;


update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-07 23:59:59' and is_payment=1
and site_id=100203 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-07 23:59:59' and is_payment=1
and site_id=100203 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-05-07 23:59:59'
and site_id=100203 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-05-07 23:59:59'
and site_id=100203 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-07 23:59:59'
and site_id=100203/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-07 23:59:59'
and site_id=100203 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-07 23:59:59'
and site_id=100203 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-07 23:59:59'
and site_id=100203 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100203  AND create_time>'2017-05-07 23:59:59';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100203  AND create_time>'2017-05-07 23:59:59'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100203  AND create_time>'2017-05-07 23:59:59'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-17 23:59:59' and is_payment=1
and site_id=100204 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-17 23:59:59' and is_payment=1
and site_id=100204 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-05-17 23:59:59'
and site_id=100204 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-05-17 23:59:59'
and site_id=100204 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-17 23:59:59'
and site_id=100204/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-17 23:59:59'
and site_id=100204 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-17 23:59:59'
and site_id=100204 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-17 23:59:59'
and site_id=100204 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100204  AND create_time>'2017-05-17 23:59:59';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100204  AND create_time>'2017-05-17 23:59:59'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100204  AND create_time>'2017-05-17 23:59:59'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;


update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-17 23:59:59' and is_payment=1
and site_id=100205 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-17 23:59:59' and is_payment=1
and site_id=100205 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-05-17 23:59:59'
and site_id=100205 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-05-17 23:59:59'
and site_id=100205 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-17 23:59:59'
and site_id=100205/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-17 23:59:59'
and site_id=100205 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-17 23:59:59'
and site_id=100205 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-17 23:59:59'
and site_id=100205 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100205  AND create_time>'2017-05-17 23:59:59';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100205  AND create_time>'2017-05-17 23:59:59'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100205  AND create_time>'2017-05-17 23:59:59'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;



update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-06 23:59:59' and is_payment=1
and site_id=100215 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-06 23:59:59' and is_payment=1
and site_id=100215 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-06 23:59:59'
and site_id=100215 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-06 23:59:59'
and site_id=100215 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-06 23:59:59'
and site_id=100215/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-06 23:59:59'
and site_id=100215 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-06 23:59:59'
and site_id=100215 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-06 23:59:59'
and site_id=100215 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100215  AND create_time>'2017-06-06 23:59:59';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100215  AND create_time>'2017-06-06 23:59:59'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100215  AND create_time>'2017-06-06 23:59:59'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;


update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-12 23:59:59' and is_payment=1
and site_id=100218 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-12 23:59:59' and is_payment=1
and site_id=100218 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-12 23:59:59'
and site_id=100218 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-12 23:59:59'
and site_id=100218 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-12 23:59:59'
and site_id=100218/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-12 23:59:59'
and site_id=100218 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-12 23:59:59'
and site_id=100218 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-12 23:59:59'
and site_id=100218 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100218  AND create_time>'2017-06-12 23:59:59';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100218  AND create_time>'2017-06-12 23:59:59'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100218  AND create_time>'2017-06-12 23:59:59'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;




update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-14 23:59:59' and is_payment=1
and site_id=100220 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-14 23:59:59' and is_payment=1
and site_id=100220 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-14 23:59:59'
and site_id=100220 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-14 23:59:59'
and site_id=100220 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-14 23:59:59'
and site_id=100220/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-14 23:59:59'
and site_id=100220 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-14 23:59:59'
and site_id=100220 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-14 23:59:59'
and site_id=100220 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100220  AND create_time>'2017-06-14 23:59:59';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100220  AND create_time>'2017-06-14 23:59:59'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100220  AND create_time>'2017-06-14 23:59:59'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;




update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:21:00' and is_payment=1
and site_id=100003 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:21:00' and is_payment=1
and site_id=100003 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-26 15:21:00'
and site_id=100003 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-26 15:21:00'
and site_id=100003 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:21:00'
and site_id=100003/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:21:00'
and site_id=100003 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:21:00'
and site_id=100003 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:21:00'
and site_id=100003 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100003  AND create_time>'2017-06-26 15:21:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100003  AND create_time>'2017-06-26 15:21:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100003  AND create_time>'2017-06-26 15:21:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;


update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 16:32:00' and is_payment=1
and site_id=100007 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 16:32:00' and is_payment=1
and site_id=100007 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-26 16:32:00'
and site_id=100007 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-26 16:32:00'
and site_id=100007 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 16:32:00'
and site_id=100007/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 16:32:00'
and site_id=100007 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 16:32:00'
and site_id=100007 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 16:32:00'
and site_id=100007 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100007  AND create_time>'2017-06-26 16:32:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100007  AND create_time>'2017-06-26 16:32:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100007  AND create_time>'2017-06-26 16:32:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;





update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:34:00' and is_payment=1
and site_id=100009 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:34:00' and is_payment=1
and site_id=100009 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-26 15:34:00'
and site_id=100009 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-26 15:34:00'
and site_id=100009 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:34:00'
and site_id=100009/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:34:00'
and site_id=100009 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:34:00'
and site_id=100009 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:34:00'
and site_id=100009 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100009  AND create_time>'2017-06-26 15:34:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100009  AND create_time>'2017-06-26 15:34:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100009  AND create_time>'2017-06-26 15:34:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;






update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-20 15:00:00' and is_payment=1
and site_id=100011 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-20 15:00:00' and is_payment=1
and site_id=100011 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-20 15:00:00'
and site_id=100011 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-20 15:00:00'
and site_id=100011 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-20 15:00:00'
and site_id=100011/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-20 15:00:00'
and site_id=100011 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-20 15:00:00'
and site_id=100011 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-20 15:00:00'
and site_id=100011 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100011  AND create_time>'2017-06-20 15:00:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100011  AND create_time>'2017-06-20 15:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100011  AND create_time>'2017-06-20 15:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;






update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-16 09:20:00' and is_payment=1
and site_id=100012 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-16 09:20:00' and is_payment=1
and site_id=100012 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-16 09:20:00'
and site_id=100012 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-16 09:20:00'
and site_id=100012 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-16 09:20:00'
and site_id=100012/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-16 09:20:00'
and site_id=100012 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-16 09:20:00'
and site_id=100012 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-16 09:20:00'
and site_id=100012 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100012  AND create_time>'2017-06-16 09:20:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100012  AND create_time>'2017-06-16 09:20:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100012  AND create_time>'2017-06-16 09:20:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;








update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:10:00' and is_payment=1
and site_id=100013 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:10:00' and is_payment=1
and site_id=100013 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-26 15:10:00'
and site_id=100013 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-26 15:10:00'
and site_id=100013 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:10:00'
and site_id=100013/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:10:00'
and site_id=100013 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:10:00'
and site_id=100013 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:10:00'
and site_id=100013 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100013  AND create_time>'2017-06-26 15:10:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100013  AND create_time>'2017-06-26 15:10:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100013  AND create_time>'2017-06-26 15:10:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;







update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 14:42:00' and is_payment=1
and site_id=100016 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 14:42:00' and is_payment=1
and site_id=100016 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-21 14:42:00'
and site_id=100016 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-21 14:42:00'
and site_id=100016 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 14:42:00'
and site_id=100016/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 14:42:00'
and site_id=100016 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 14:42:00'
and site_id=100016 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 14:42:00'
and site_id=100016 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100016  AND create_time>'2017-06-21 14:42:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100016  AND create_time>'2017-06-21 14:42:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100016  AND create_time>'2017-06-21 14:42:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;



update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 21:00:00' and is_payment=1
and site_id=100030 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 21:00:00' and is_payment=1
and site_id=100030 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-21 21:00:00'
and site_id=100030 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-21 21:00:00'
and site_id=100030 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 21:00:00'
and site_id=100030/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 21:00:00'
and site_id=100030 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 21:00:00'
and site_id=100030 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 21:00:00'
and site_id=100030 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100030  AND create_time>'2017-06-21 21:00:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100030  AND create_time>'2017-06-21 21:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100030  AND create_time>'2017-06-21 21:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;




update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 14:22:00' and is_payment=1
and site_id=100037 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 14:22:00' and is_payment=1
and site_id=100037 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-26 14:22:00'
and site_id=100037 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-26 14:22:00'
and site_id=100037 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 14:22:00'
and site_id=100037/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 14:22:00'
and site_id=100037 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 14:22:00'
and site_id=100037 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 14:22:00'
and site_id=100037 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100037  AND create_time>'2017-06-26 14:22:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100037  AND create_time>'2017-06-26 14:22:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100037  AND create_time>'2017-06-26 14:22:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;


update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-13 18:00:00' and is_payment=1
and site_id=100042 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-13 18:00:00' and is_payment=1
and site_id=100042 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-13 18:00:00'
and site_id=100042 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-13 18:00:00'
and site_id=100042 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-13 18:00:00'
and site_id=100042/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-13 18:00:00'
and site_id=100042 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-13 18:00:00'
and site_id=100042 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-13 18:00:00'
and site_id=100042 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100042  AND create_time>'2017-06-13 18:00:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100042  AND create_time>'2017-06-13 18:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100042  AND create_time>'2017-06-13 18:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;




update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 14:35:00' and is_payment=1
and site_id=100050 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 14:35:00' and is_payment=1
and site_id=100050 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-21 14:35:00'
and site_id=100050 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-21 14:35:00'
and site_id=100050 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 14:35:00'
and site_id=100050/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 14:35:00'
and site_id=100050 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 14:35:00'
and site_id=100050 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 14:35:00'
and site_id=100050 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100050  AND create_time>'2017-06-21 14:35:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100050  AND create_time>'2017-06-21 14:35:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100050  AND create_time>'2017-06-21 14:35:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;


update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-17 02:00:00' and is_payment=1
and site_id=100073 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-17 02:00:00' and is_payment=1
and site_id=100073 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-05-17 02:00:00'
and site_id=100073 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-05-17 02:00:00'
and site_id=100073 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-17 02:00:00'
and site_id=100073/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-17 02:00:00'
and site_id=100073 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-17 02:00:00'
and site_id=100073 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-05-17 02:00:00'
and site_id=100073 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100073  AND create_time>'2017-05-17 02:00:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100073  AND create_time>'2017-05-17 02:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100073  AND create_time>'2017-05-17 02:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;


update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-12 18:00:00' and is_payment=1
and site_id=100097 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-12 18:00:00' and is_payment=1
and site_id=100097 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-12 18:00:00'
and site_id=100097 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-12 18:00:00'
and site_id=100097 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-12 18:00:00'
and site_id=100097/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-12 18:00:00'
and site_id=100097 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-12 18:00:00'
and site_id=100097 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-12 18:00:00'
and site_id=100097 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100097  AND create_time>'2017-06-12 18:00:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100097  AND create_time>'2017-06-12 18:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100097  AND create_time>'2017-06-12 18:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;





update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-01 18:00:00' and is_payment=1
and site_id=100102 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-01 18:00:00' and is_payment=1
and site_id=100102 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-01 18:00:00'
and site_id=100102 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-01 18:00:00'
and site_id=100102 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-01 18:00:00'
and site_id=100102/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-01 18:00:00'
and site_id=100102 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-01 18:00:00'
and site_id=100102 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-01 18:00:00'
and site_id=100102 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100102  AND create_time>'2017-06-01 18:00:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100102  AND create_time>'2017-06-01 18:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100102  AND create_time>'2017-06-01 18:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;



update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 14:03:00' and is_payment=1
and site_id=100129 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 14:03:00' and is_payment=1
and site_id=100129 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-21 14:03:00'
and site_id=100129 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-21 14:03:00'
and site_id=100129 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 14:03:00'
and site_id=100129/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 14:03:00'
and site_id=100129 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 14:03:00'
and site_id=100129 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-21 14:03:00'
and site_id=100129 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100129  AND create_time>'2017-06-21 14:03:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100129  AND create_time>'2017-06-21 14:03:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100129  AND create_time>'2017-06-21 14:03:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;



update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-16 09:32:00' and is_payment=1
and site_id=100151 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-16 09:32:00' and is_payment=1
and site_id=100151 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-16 09:32:00'
and site_id=100151 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-16 09:32:00'
and site_id=100151 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-16 09:32:00'
and site_id=100151/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-16 09:32:00'
and site_id=100151 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-16 09:32:00'
and site_id=100151 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-16 09:32:00'
and site_id=100151 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100151  AND create_time>'2017-06-16 09:32:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100151  AND create_time>'2017-06-16 09:32:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100151  AND create_time>'2017-06-16 09:32:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;


update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 10:15:00' and is_payment=1
and site_id=100152 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 10:15:00' and is_payment=1
and site_id=100152 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-26 10:15:00'
and site_id=100152 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-26 10:15:00'
and site_id=100152 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 10:15:00'
and site_id=100152/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 10:15:00'
and site_id=100152 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 10:15:00'
and site_id=100152 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 10:15:00'
and site_id=100152 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100152  AND create_time>'2017-06-26 10:15:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100152  AND create_time>'2017-06-26 10:15:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100152  AND create_time>'2017-06-26 10:15:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;



update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:24:00' and is_payment=1
and site_id=100153 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:24:00' and is_payment=1
and site_id=100153 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-26 15:24:00'
and site_id=100153 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-26 15:24:00'
and site_id=100153 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:24:00'
and site_id=100153/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:24:00'
and site_id=100153 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:24:00'
and site_id=100153 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:24:00'
and site_id=100153 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100153  AND create_time>'2017-06-26 15:24:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100153  AND create_time>'2017-06-26 15:24:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100153  AND create_time>'2017-06-26 15:24:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;








update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-12 18:00:00' and is_payment=1
and site_id=100161 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-12 18:00:00' and is_payment=1
and site_id=100161 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-12 18:00:00'
and site_id=100161 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-12 18:00:00'
and site_id=100161 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-12 18:00:00'
and site_id=100161/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-12 18:00:00'
and site_id=100161 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-12 18:00:00'
and site_id=100161 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-12 18:00:00'
and site_id=100161 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100161  AND create_time>'2017-06-12 18:00:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100161  AND create_time>'2017-06-12 18:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100161  AND create_time>'2017-06-12 18:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;







update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:53:00' and is_payment=1
and site_id=100178 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:53:00' and is_payment=1
and site_id=100178 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-26 15:53:00'
and site_id=100178 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-26 15:53:00'
and site_id=100178 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:53:00'
and site_id=100178/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:53:00'
and site_id=100178 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:53:00'
and site_id=100178 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-26 15:53:00'
and site_id=100178 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100178  AND create_time>'2017-06-26 15:53:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100178  AND create_time>'2017-06-26 15:53:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100178  AND create_time>'2017-06-26 15:53:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;







update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-04-27 20:00:00' and is_payment=1
and site_id=100180 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-04-27 20:00:00' and is_payment=1
and site_id=100180 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-04-27 20:00:00'
and site_id=100180 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-04-27 20:00:00'
and site_id=100180 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-04-27 20:00:00'
and site_id=100180/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-04-27 20:00:00'
and site_id=100180 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-04-27 20:00:00'
and site_id=100180 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-04-27 20:00:00'
and site_id=100180 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100180  AND create_time>'2017-04-27 20:00:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100180  AND create_time>'2017-04-27 20:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100180  AND create_time>'2017-04-27 20:00:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;




update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='wx' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-20 14:37:00' and is_payment=1
and site_id=100187 /*清洗微信平台手续费(0.01)*/;

update  b_trades SET plat_split=ROUND(real_pay*0.01,0) where pay_style='ali' and plat_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-20 14:37:00' and is_payment=1
and site_id=100187 /*清洗支付宝平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='cash' and plat_split!=0
AND create_time>'2017-06-20 14:37:00'
and site_id=100187 /*清洗现金平台手续费*/;

update  b_trades SET plat_split=0 where pay_style='health_insurance' and plat_split!=0
AND create_time>'2017-06-20 14:37:00'
and site_id=100187 /*清洗医保卡手续费*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='wx' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-20 14:37:00'
and site_id=100187/*清洗微信交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='ali' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-20 14:37:00'
and site_id=100187 /*清洗支付宝交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='cash' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-20 14:37:00'
and site_id=100187 /*清洗现金交易佣金*/;

update  b_trades SET trades_split=ROUND(real_pay*0.01,0) where pay_style='health_insurance' and trades_split!=ROUND(real_pay*0.01 ,0)
AND create_time>'2017-06-20 14:37:00'
and site_id=100187 /*清洗医保卡交易佣金*/;

update b_trades set settlement_type=0 where  ISNULL(settlement_type) and site_id=100187  AND create_time>'2017-06-20 14:37:00';
/*清洗直购分销状态*/


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100187  AND create_time>'2017-06-20 14:37:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='蜂鸟配送' and p.status=5)/*清洗O2O配送费*/;


UPDATE  b_trades set O2O_freight=ROUND(real_pay*0.05,0)
where O2O_freight>'0' and site_id=100187  AND create_time>'2017-06-20 14:37:00'
AND trades_id in
(SELECT   order_number from  b_logistics_order p
where p.logistics_name='达达物流' and  p.status in (1,2,3,4,5))/*清洗O2O配送费*/;


