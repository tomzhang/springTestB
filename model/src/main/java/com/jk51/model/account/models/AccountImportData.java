package com.jk51.model.account.models;

import com.jk51.commons.pricetransform.DoubleTransformUtil;
import org.apache.poi.ss.usermodel.Cell;


import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者:xiapeng
 * 创建日期:2017/3/16
 */
public class AccountImportData {
    private String trades_id;     //订单号
    private Timestamp remit_acount_time;
    private String platform_payment_amount;//总金额
    private String platform_service_fee;//手续费
    private String rate ;//费率
    private String platform_refund_fee;//退款金额


    public String getTrades_id() {
        return trades_id;
    }

    public void setTrades_id(String trades_id) {
        this.trades_id = trades_id;
    }

    public Timestamp getRemit_acount_time() {
        return remit_acount_time;
    }

    public void setRemit_acount_time(Timestamp remit_acount_time) {
        this.remit_acount_time = remit_acount_time;
    }

    public String getPlatform_payment_amount() {
        return platform_payment_amount;
    }

    public void setPlatform_payment_amount(String platform_payment_amount) {
        this.platform_payment_amount = platform_payment_amount;
    }

    public String getPlatform_service_fee() {
        return platform_service_fee;
    }

    public void setPlatform_service_fee(String platform_service_fee) {
        this.platform_service_fee = platform_service_fee;
    }

    public String getPlatform_refund_fee() {
        return platform_refund_fee;
    }

    public void setPlatform_refund_fee(String platform_refund_fee) {
        this.platform_refund_fee = platform_refund_fee;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    /**
     * 微信到账单导入
     * @param data
     * @return
     */
    public static ArrayList<AccountImportData> wechat_account_buildList(ArrayList<ArrayList<Cell>> data){
        ArrayList<AccountImportData> result=new ArrayList<>();
        for(int i=5;i<data.size();i++){
            result.add(weChatAccountbuild(data.get(i)));
        }
        return result;
    }

    public static AccountImportData weChatAccountbuild(ArrayList<Cell> data){
        AccountImportData accountImportData= new AccountImportData();
        //订单号
        accountImportData.setTrades_id((data.get(1).getStringCellValue().trim()));
        //创建时间
        accountImportData.setRemit_acount_time(new java.sql.Timestamp(data.get(4).getDateCellValue().getTime()));

        //付款金额
//        accountImportData.setPlatform_payment_amount(String.valueOf(data.get(11).getNumericCellValue()*100));
        accountImportData.setPlatform_payment_amount(DoubleTransformUtil.multiplyDou(data.get(11).getNumericCellValue(),100).toString());
        //服务费
//        accountImportData.setPlatform_service_fee(String.valueOf(data.get(21).getNumericCellValue()*100));
        accountImportData.setPlatform_service_fee(DoubleTransformUtil.multiplyDou(data.get(21).getNumericCellValue(),100).toString());

        return accountImportData;
    }

    /**
     * 微信银行退账导入
     * @param data
     * @return
     */

    public static ArrayList<AccountImportData> wechat_refund_bank(ArrayList<ArrayList<Cell>> data){
        ArrayList<AccountImportData> result = new ArrayList<>();
        for (int i=1;i<data.size()-2;i++){
            result.add(wechatRefundBank(data.get(i)));
        }
        return result;
    }

    public static AccountImportData wechatRefundBank(ArrayList<Cell> data){
        AccountImportData accountImportData = new AccountImportData();
        //订单号
        String str =data.get(6).getStringCellValue();
        accountImportData.setTrades_id(String.valueOf(str.substring(1)));
        //总金额
        String str1=data.get(12).getStringCellValue();
        //accountImportData.setPlatform_payment_amount(Integer.valueOf(str1.substring(1))*100);
        accountImportData.setPlatform_payment_amount(String.valueOf(data.get(10).getStringCellValue()).substring(1));

      //  System.out.println("-------------1111111-----------------"+Integer.valueOf(Integer.valueOf(data.get(10).getStringCellValue().substring(1))*100));
        //退款金额
        String str2 = data.get(16).getStringCellValue();
        accountImportData.setPlatform_refund_fee(String.valueOf(str2.substring(1)));
        //手续费
        String str3 = data.get(22).getStringCellValue();
        accountImportData.setPlatform_service_fee(String.valueOf(str3.substring(1)));
        //费率
        String str4 =data.get(23).getStringCellValue();
        accountImportData.setRate((String.valueOf(str4.substring(1))));
        return accountImportData;
    }
}
