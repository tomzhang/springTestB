TRUNCATE yb_pay_data_import;

TRUNCATE yb_settlement_detail;

TRUNCATE yb_finances;

TRUNCATE yb_classified_statistic;

TRUNCATE yb_finance_audit_log;

UPDATE b_trades set settlement_status=200 , finance_no='' where settlement_status=250 and site_id=100166 ;

update b_trades SET account_checking_status=0 and is_payment=1 and settlement_status=200 where create_time>'2017-5-11 02:25:00' and site_id=100166;

update b_refund set account_checking_status=0 where create_time>'2017-5-11 02:25:00' and site_id=100166;


UPDATE b_trades set settlement_status=200 , finance_no='' where settlement_status=250 and site_id=100073 ;

update b_trades SET account_checking_status=0 and is_payment=1 and settlement_status=200 where create_time>'2017-5-17 02:00:00' and site_id=100073;

update b_refund set account_checking_status=0 where create_time>'2017-5-17 02:00:00' and site_id=100073;
