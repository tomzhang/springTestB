package com.jk51.modules.account.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.interceptor.LoginInterceptor;
import com.jk51.model.account.models.PayDataImport;
import com.jk51.model.account.models.SettlementDetail;
import com.jk51.model.order.Refund;
import com.jk51.model.order.Trades;
import com.jk51.modules.account.constants.AccountConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static com.jk51.modules.account.constants.AccountConstants.IS_RETURNED;

/**
 * filename :com.jk51.modules.account.goodsService.
 * author   :zw
 * date     :2017/2/21
 * Update   :
 */


@Service
public class ProcessService {
    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);



    public static SettlementDetail buildCashAndHealthInsurance(Trades trades) {
        SettlementDetail settlementDetail = new SettlementDetail();
        settlementDetail.setTrades_id(trades.getTradesId()); //平台订单号
        settlementDetail.setPay_style(trades.getPayStyle()); //买家支付方式
        settlementDetail.setPay_number(trades.getPayNumber()); //支付业务流水号
        settlementDetail.setPlatform_payment_amount(trades.getRealPay());
        settlementDetail.setRefund_checking_status(-1);
        settlementDetail.setPlatform_service_fee(0);
        settlementDetail.setRefund_fee(0);
        settlementDetail.setPlatform_refund_fee(0);
        settlementDetail.setAccount_checking_status(AccountConstants.ACCOUNT_CHECKING_STATUS_SUCCESS);
        return settlementDetail;
    }

    public static SettlementDetail buildRefundCashAndHealthInsurance(Refund refund) {
        SettlementDetail settlementDetail = new SettlementDetail();
        settlementDetail.setTrades_id( Long.parseLong(refund.getTradeId())); //平台订单号
        settlementDetail.setRefund_fee(refund.getRealRefundMoney());
        settlementDetail.setPlatform_refund_fee(refund.getRealRefundMoney());
        settlementDetail.setRefund_checking_status(AccountConstants.REFUND_CHECKING_SUCCESS);
        return settlementDetail;
    }

    public static SettlementDetail buildByAli(Trades trades, Map<String, PayDataImport> payDataImport) {
        SettlementDetail settlementDetail = new SettlementDetail();
        settlementDetail.setTrades_id(Long.valueOf(payDataImport.get("买家已支付").getTrades_id())); //dingdan hao
        settlementDetail.setPay_number(payDataImport.get("买家已支付").getBusiness_order_sn()); //业务流水号
        settlementDetail.setPay_style(payDataImport.get("买家已支付").getPay_style()); //支付类型\
        // 对账：支付金额和支付状态比对
        Integer money =null;
        try {
            if (payDataImport.get("蚂蚁优惠集分宝补贴")==null && payDataImport.get("红包")!=null){
                money=Integer.parseInt(payDataImport.get("红包").getIncome_amount());
            }else if (payDataImport.get("蚂蚁优惠集分宝补贴")!=null){
                money=Integer.parseInt(payDataImport.get("蚂蚁优惠集分宝补贴").getIncome_amount());
            }else{
                money=0;
            }
        } catch (Exception e) {
            money=0;
            e.printStackTrace();
        }
        if (trades.getRealPay() == (Integer.parseInt(payDataImport.get("买家已支付").getIncome_amount()) + money)) {
            settlementDetail.setAccount_checking_status(AccountConstants.ACCOUNT_CHECKING_STATUS_SUCCESS);
        } else {
            settlementDetail.setAccount_checking_status(AccountConstants.ACCOUNT_CHECKING_STATUS_FAIL);
            //默认退款对账为为对账
            settlementDetail.setRefund_checking_status(-1);
        }
        //平台付款金额
        settlementDetail.setPlatform_payment_amount(Integer.parseInt(payDataImport.get("买家已支付").getIncome_amount())+ money);
        //平台对账渠道状态
        settlementDetail.setBusiness_types(payDataImport.get("买家已支付").getTrades_status());
        Optional.ofNullable(payDataImport.get("交易分账")).map(t -> t.getSpending_amount()).orElse(null);

        //默认退款金额
        settlementDetail.setRefund_fee(0);


         //settlementDetail.setPlatform_fashionable_amount(Integer.parseInt(Optional.ofNullable(payDataImport.get("交易分账")).map(t -> t.getSpending_amount()).orElse(null)));

        try {
            settlementDetail.setPlatform_fashionable_amount(Integer.parseInt(payDataImport.get("交易分账").getSpending_amount()));
        } catch (Exception e) {
            settlementDetail.setPlatform_fashionable_amount(0);
            e.printStackTrace();
        }


        //平台服务费

       // settlementDetail.setPlatform_service_fee(Integer.parseInt(Optional.ofNullable(payDataImport.get("服务费")).map(t -> t.getSpending_amount()).orElse(null)));

        try {
            settlementDetail.setPlatform_service_fee(Integer.parseInt(payDataImport.get("服务费").getSpending_amount()));
        } catch (Exception e) {
            settlementDetail.setPlatform_service_fee(0);
            e.printStackTrace();
        }

        //平台退款金额
        settlementDetail.setPlatform_refund_fee(0);

        return settlementDetail;
    }
    private static int stringConvertInt(String str){
        double a = 0d;
        try {
            a = Double.parseDouble(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        int b = (int)a;
        return b;
    }

    public static SettlementDetail buildByWx(Trades trades, PayDataImport payDataImport) {
        SettlementDetail settlementDetail = new SettlementDetail();
        settlementDetail.setPay_number(payDataImport.getBusiness_order_sn()); //业务流水号
        settlementDetail.setTrades_id(Long.valueOf(payDataImport.getTrades_id())); //订单号
        settlementDetail.setPay_style(payDataImport.getPay_style()); //支付类型
        if (trades.getRealPay() == stringConvertInt(payDataImport.getIncome_amount())
                && java.util.Arrays.asList(AccountConstants.PAYMENT_STATUS).contains(payDataImport.getTrades_status())) {
            settlementDetail.setAccount_checking_status(AccountConstants.ACCOUNT_CHECKING_STATUS_SUCCESS);
        }else {
            settlementDetail.setAccount_checking_status(AccountConstants.ACCOUNT_CHECKING_STATUS_FAIL);
        }
        //默认退款对账为未对账
        settlementDetail.setRefund_checking_status(-1);
        //默认退款金额
        settlementDetail.setRefund_fee(0);

        //平台付款金额
        try {
            settlementDetail.setPlatform_payment_amount(stringConvertInt(payDataImport.getIncome_amount()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //平台对账渠道状态
        settlementDetail.setBusiness_types(payDataImport.getTrades_status());
        //平台分账金额
        settlementDetail.setPlatform_fashionable_amount(0);
        //平台服务费
        settlementDetail.setPlatform_service_fee(0);
        //平台退款金额
        settlementDetail.setPlatform_refund_fee(0);
        return settlementDetail;
    }


    /**
     * 创建要更新的划账对象
     * @param trades
     * @param payDataImport
     * @return
     */
    public static SettlementDetail buildByRemit(Trades trades, PayDataImport payDataImport) {

        String rate = "";
        if(!StringUtil.isEmpty(payDataImport.getRate())){
            rate = payDataImport.getRate().replace("%","");
        }
        SettlementDetail settlementDetail = new SettlementDetail();
        settlementDetail.setTrades_id(Long.parseLong(payDataImport.getTrades_id()));
        settlementDetail.setRate(Double.parseDouble(rate)); //划账费率
        settlementDetail.setRemit_acount_time(payDataImport.getPay_time());  //划账时间
        settlementDetail.setRemit_service_fee(Integer.valueOf(payDataImport.getPlatform_payment_amount()));     //划账服务费
        boolean isReturnedAndReturnMonryIsEquals = isReturnedAndReturnMonryIsEquals(trades,payDataImport);    //如果为已退款状态且金额一致返回TRUE，否则返回false
        boolean notReturnedAndReturnMonryIsEquals = notReturnedAndReturnMonryIsEquals(trades,payDataImport);  //如果为非退款状态且金额一致返回TRUE，否则返回false

        if (isReturnedAndReturnMonryIsEquals||notReturnedAndReturnMonryIsEquals){
            settlementDetail.setRemit_account_status("1");
        }else {
            settlementDetail.setRemit_account_status("2");
        }
        return  settlementDetail;
    }
    //如果为已退款状态且金额一致返回TRUE，否则返回false
    private static boolean isReturnedAndReturnMonryIsEquals(Trades trades,PayDataImport payDataImport){

        if(!trades.getTradesStatus().equals(IS_RETURNED)){
            return false;
        }

        if(!trades.getRealPay().equals(Integer.valueOf(payDataImport.getSpending_amount()))){
            return false;
        }
        return true;
    }
    //如果为非退款状态且金额一致返回TRUE，否则返回false
    private static boolean notReturnedAndReturnMonryIsEquals(Trades trades,PayDataImport payDataImport){

        if(trades.getTradesStatus().equals(IS_RETURNED)){
            return false;
        }

        if(!trades.getRealPay().equals(Integer.valueOf(payDataImport.getIncome_amount()))){
            return false;
        }
        return true;
    }





    public static SettlementDetail refundByWx(Trades trades, PayDataImport payDataImport) {
        SettlementDetail settlementDetail = new SettlementDetail();

        try {
            settlementDetail.setPay_number(payDataImport.getBusiness_order_sn()); //业务流水号
            settlementDetail.setTrades_id(Long.valueOf(payDataImport.getTrades_id())); //订单号
            settlementDetail.setPay_style(payDataImport.getPay_style()); //支付类型
        } catch (Exception e) {
            e.printStackTrace();
        }
        settlementDetail.setAccount_checking_status(AccountConstants.ACCOUNT_CHECKING_STATUS_SUCCESS);
        //默认退款金额
        settlementDetail.setRefund_fee(0);
        //默认退款对账为未对账
        settlementDetail.setRefund_checking_status(-1);
        //平台付款金额
        settlementDetail.setPlatform_payment_amount(Integer.parseInt(payDataImport.getIncome_amount()));
        //平台对账渠道状态
        settlementDetail.setBusiness_types(payDataImport.getTrades_status());
        //平台分账金额
        settlementDetail.setPlatform_fashionable_amount(0);
        //平台服务费
        settlementDetail.setPlatform_service_fee(0);
        //平台退款金额
        settlementDetail.setPlatform_refund_fee(Integer.parseInt(payDataImport.getSpending_amount()));
        return settlementDetail;
    }



    public static SettlementDetail refund(Refund refund, PayDataImport payDataImport) {
        SettlementDetail settlementDetail = new SettlementDetail();
        settlementDetail.setTrades_id(Long.valueOf(refund.getTradeId())); //订单号
        settlementDetail.setRefund_fee(refund.getRealRefundMoney());//订单退款金额
        if (payDataImport.getIncome_amount()==null){
            settlementDetail.setPlatform_payment_amount(0);
        }else{
            settlementDetail.setPlatform_payment_amount(Integer.parseInt(payDataImport.getIncome_amount()));
        }
            settlementDetail.setPlatform_fashionable_amount(0);
            settlementDetail.setPlatform_service_fee(0);
        settlementDetail.setPlatform_refund_fee(Integer.parseInt(payDataImport.getSpending_amount())); //平台退款金额
        if ((refund.getRealRefundMoney() == Integer.parseInt(payDataImport.getSpending_amount()))&& (refund.getStatus()==120)) {
            settlementDetail.setRefund_checking_status(AccountConstants.REFUND_CHECKING_SUCCESS);
        } else if ((refund.getRealRefundMoney() == Integer.parseInt(payDataImport.getSpending_amount()))&& (refund.getStatus()==110)){
            settlementDetail.setRefund_checking_status(-1);//未对账，如果是拒绝退款则不对账
        }else {
            settlementDetail.setRefund_checking_status(AccountConstants.REFUND_CHECKING_FAIL);
        }
        return settlementDetail;
    }

    /**
     * 包装对账信息
     * @param trades
     * @param payData
     * @return
     */
    public static SettlementDetail buildAli(Trades trades, PayDataImport payData) {
        SettlementDetail settlementDetail = new SettlementDetail();
        try {
            settlementDetail.setTrades_id(Long.valueOf(payData.getTrades_id()));
            settlementDetail.setPay_number(payData.getBusiness_order_sn()); //业务流水号
            settlementDetail.setPay_style(payData.getPay_style()); //支付类型
            // 对账：支付金额和支付状态比对
            if (trades.getRealPay() == (Integer.parseInt(payData.getIncome_amount()))) {
                settlementDetail.setAccount_checking_status(AccountConstants.ACCOUNT_CHECKING_STATUS_SUCCESS);
            } else {
                settlementDetail.setAccount_checking_status(AccountConstants.ACCOUNT_CHECKING_STATUS_FAIL);
                //默认退款对账为为对账
                settlementDetail.setRefund_checking_status(-1);
            }
            //平台付款金额
            settlementDetail.setPlatform_payment_amount(Integer.parseInt(payData.getIncome_amount()));
            //平台对账渠道状态
            settlementDetail.setBusiness_types("买家已支付");
            //默认退款金额
            settlementDetail.setRefund_fee(0);
            settlementDetail.setPlatform_fashionable_amount(Integer.parseInt(payData.getSpending_amount()));
            //平台服务费
            settlementDetail.setPlatform_service_fee(0);
            //平台退款金额
            settlementDetail.setPlatform_refund_fee(0);
            return settlementDetail;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("订单对账出错，错误信息：{}",e);
        }
        return settlementDetail;
    }
}
