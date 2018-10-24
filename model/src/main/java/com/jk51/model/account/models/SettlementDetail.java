package com.jk51.model.account.models;

import com.jk51.commons.pricetransform.DoubleTransformUtil;
import org.apache.poi.ss.usermodel.Cell;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * filename :com.jk51.model.account.models.
 * author   :zw
 * date     :2017/2/16
 * Update   :
 * 新的对账明细表
 */
public class SettlementDetail {
    private Integer id;
    private long trades_id;
    private String pay_style;
    private String pay_number;
    private Integer account_checking_status;
    private Integer checking_user_type;
    private String remark;
    private String business_types;
    private String finance_no;
    private Integer summary_status;
    private Integer refund_fee;
    private Integer refund_checking_status;
    private Integer platform_payment_amount;
    private Integer platform_fashionable_amount;
    private Integer platform_service_fee;
    private Integer platform_refund_fee;
    private Timestamp create_time;
    private Timestamp update_time;
    private String account_status;
    private String interest_rate;
    private String trades_status;
    private double rate;
    private Timestamp remit_acount_time;
    private String isChargeOff;
    private Integer remit_service_fee;
    private  String remit_account_status;

    public Integer getRemit_service_fee() {
        return remit_service_fee;
    }

    public void setRemit_service_fee(Integer remit_service_fee) {
        this.remit_service_fee = remit_service_fee;
    }

    public String getRemit_account_status() {
        return remit_account_status;
    }

    public void setRemit_account_status(String remit_account_status) {
        this.remit_account_status = remit_account_status;
    }

    public String getIsChargeOff() {
        return isChargeOff;
    }

    public void setIsChargeOff(String isChargeOff) {
        this.isChargeOff = isChargeOff;
    }

    @Override
    public String toString() {
        return "SettlementDetail{" +
                "id=" + id +
                ", trades_id=" + trades_id +
                ", pay_style='" + pay_style + '\'' +
                ", pay_number='" + pay_number + '\'' +
                ", account_checking_status=" + account_checking_status +
                ", checking_user_type=" + checking_user_type +
                ", remark='" + remark + '\'' +
                ", business_types='" + business_types + '\'' +
                ", finance_no='" + finance_no + '\'' +
                ", summary_status=" + summary_status +
                ", refund_fee=" + refund_fee +
                ", refund_checking_status=" + refund_checking_status +
                ", platform_payment_amount=" + platform_payment_amount +
                ", platform_fashionable_amount=" + platform_fashionable_amount +
                ", platform_service_fee=" + platform_service_fee +
                ", platform_refund_fee=" + platform_refund_fee +
                ", create_time=" + create_time +
                ", update_time=" + update_time +
                ", account_status='" + account_status + '\'' +
                ", interest_rate='" + interest_rate + '\'' +
                ", trades_status='" + trades_status + '\'' +
                ", rate=" + rate +
                ", remit_acount_time=" + remit_acount_time +
                '}';
    }

    public Timestamp getRemit_acount_time() {
        return remit_acount_time;
    }

    public void setRemit_acount_time(Timestamp remit_acount_time) {
        this.remit_acount_time = remit_acount_time;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getTrades_status() {
        return trades_status;
    }
    public void setTrades_status(String trades_status) {
        this.trades_status = trades_status;
    }


    public String getInterest_rate() {
        return interest_rate;
    }

    public void setInterest_rate(String interest_rate) {
        this.interest_rate = interest_rate;
    }

    public String getAccount_status() {
        return account_status;
    }

    public void setAccount_status(String account_status) {
        this.account_status = account_status;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getTrades_id() {
        return trades_id;
    }

    public void setTrades_id(long trades_id) {
        this.trades_id = trades_id;
    }

    public String getPay_style() {
        return pay_style;
    }

    public void setPay_style(String pay_style) {
        this.pay_style = pay_style;
    }

    public String getPay_number() {
        return pay_number;
    }

    public void setPay_number(String pay_number) {
        this.pay_number = pay_number;
    }

    public Integer getAccount_checking_status() {
        return account_checking_status;
    }

    public void setAccount_checking_status(Integer account_checking_status) {
        this.account_checking_status = account_checking_status;
    }

    public Integer getChecking_user_type() {
        return checking_user_type;
    }

    public void setChecking_user_type(Integer checking_user_type) {
        this.checking_user_type = checking_user_type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBusiness_types() {
        return business_types;
    }

    public void setBusiness_types(String business_types) {
        this.business_types = business_types;
    }

    public String getFinance_no() {
        return finance_no;
    }

    public void setFinance_no(String finance_no) {
        this.finance_no = finance_no;
    }

    public Integer getSummary_status() {
        return summary_status;
    }

    public void setSummary_status(Integer summary_status) {
        this.summary_status = summary_status;
    }

    public Integer getRefund_fee() {
        return refund_fee;
    }

    public void setRefund_fee(Integer refund_fee) {
        this.refund_fee = refund_fee;
    }

    public Integer getRefund_checking_status() {
        return refund_checking_status;
    }

    public void setRefund_checking_status(Integer refund_checking_status) {
        this.refund_checking_status = refund_checking_status;
    }

    public Integer getPlatform_payment_amount() {
        return platform_payment_amount;
    }

    public void setPlatform_payment_amount(Integer platform_payment_amount) {
        this.platform_payment_amount = platform_payment_amount;
    }

    public Integer getPlatform_fashionable_amount() {
        return platform_fashionable_amount;
    }

    public void setPlatform_fashionable_amount(Integer platform_fashionable_amount) {
        this.platform_fashionable_amount = platform_fashionable_amount;
    }

    public Integer getPlatform_service_fee() {
        return platform_service_fee;
    }

    public void setPlatform_service_fee(Integer platform_service_fee) {
        this.platform_service_fee = platform_service_fee;
    }
    public Integer getPlatform_refund_fee() {
        return platform_refund_fee;
    }

    public void setPlatform_refund_fee(Integer platform_refund_fee) {
        this.platform_refund_fee = platform_refund_fee;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }


    /**
     * 微信银行明细解析
     * @param data
     * @return
     */
    public static ArrayList<SettlementDetail> wechat_bank_buildList(ArrayList<ArrayList<Cell>> data){
        ArrayList<SettlementDetail> result=new ArrayList<>();
        for(int i=1;i<data.size();i++){
            result.add(weChatBankbuild(data.get(i)));
        }
        return result;
    }
    private static int stringConvertInt(String param){
        String str = param.substring(1,param.length()-1);
//        Double i = Double.parseDouble(str)*100;
        Double i = DoubleTransformUtil.multiplystr(str,100).doubleValue();
        return (new Double(i)).intValue();
    }
    public static SettlementDetail weChatBankbuild(ArrayList<Cell> data){
        SettlementDetail settlementDetail= new SettlementDetail();
        String  str=data.get(6).getStringCellValue();
        settlementDetail.setTrades_id( Long.valueOf(str.substring(1,str.length()-1)));
        settlementDetail.setPay_style("weixin");
        settlementDetail.setRefund_fee(stringConvertInt(data.get(16).getStringCellValue()));
        settlementDetail.setPlatform_payment_amount(stringConvertInt(data.get(12).getStringCellValue()));
        settlementDetail.setPlatform_service_fee(stringConvertInt(data.get(22).getStringCellValue()));

        settlementDetail.setInterest_rate((data.get(23).getStringCellValue()));

        String  str5=data.get(9).getStringCellValue();
        settlementDetail.setTrades_status(str5.substring(1,str5.length()-1));
       // settlementDetail.setAccount_status("已到账");
        return settlementDetail;
    }

    /**
     * 支付宝银行明细解析
     * @param data
     * @return
     */
    public static ArrayList<SettlementDetail> ali_bank_buildList (ArrayList<ArrayList<Cell>> data){
        ArrayList<SettlementDetail> result=new ArrayList<>();
        for(int i=5;i<data.size();i++){
            result.add(aliBankbuild(data.get(i)));
        }
        return result;
    }
    public static SettlementDetail aliBankbuild(ArrayList<Cell> data){
        SettlementDetail settlementDetail= new SettlementDetail();
        settlementDetail.setTrades_id(Long.valueOf(data.get(1).getStringCellValue().trim()));
        settlementDetail.setPay_style("ali");
        /*  data.get(22).setCellType(Cell.CELL_TYPE_STRING);
        settlementDetail.setPlatform_service_fee(stringConvertInt(data.get(22).getStringCellValue()));*/
        /* settlementDetail.setPlatform_service_fee(Integer.parseInt(data.get(22).getStringCellValue()));*/
        //settlementDetail.setPlatform_payment_amount(Integer.parseInt(data.get(11).getStringCellValue()));
        //settlementDetail.setAccount_status("已到账");
        return settlementDetail;
    }





}
