package com.jk51.modules.account.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.account.models.PayDataImport;
import com.jk51.modules.account.constants.AccountConstants;
import com.jk51.modules.account.mapper.PayDataImportMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * filename :com.jk51.modules.account.service.
 * author   :zw
 * date     :2017/5/21
 * Update   :
 */
@Service
public class DataImportProcessService {
    @Autowired
    private static  PayDataImportMapper payDataImportMapper;
    private static DataImportProcessService dataImportProcessService;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DataImportProcessService.class);
    @Autowired
    private TradesMapper tradesMapper;
    @PostConstruct
    public void init(){
        dataImportProcessService=this;
        dataImportProcessService.tradesMapper=this.tradesMapper;
    }

    /**
     * 支付宝收款解析
     *
     * @param data
     * @return
     */
    public static ArrayList<PayDataImport> zf_pay_buildList(ArrayList<ArrayList<Cell>> data) throws IOException {
        ArrayList<PayDataImport> result = new ArrayList<>();
        if (!"账务类型".equals(data.get(2).get(5).getStringCellValue())){
            throw new IOException();
        }
        for (int i = 3; i < data.size() - 1; i++) {
            result.add(aliPayBuild(data.get(i)));
        }
        return result;
    }

    /**
     * 截取trades_id
     *
     * @param trades_id
     * @return
     */

    public static String convertTradesId(String trades_id) {
        String[] str = null;
        if (trades_id != null) {
            str = trades_id.split("_");
            return str[0];
        }
        return "";
    }
    public static PayDataImport aliPayBuild(ArrayList<Cell> data) {
        PayDataImport payDataImport = new PayDataImport();
        payDataImport.setPay_style("ali");
        payDataImport.setData_type(AccountConstants.PAY_DATA_IMPORT);
        //商品订单号
        if(!data.get(5).getStringCellValue().equals("提现")){
            payDataImport.setTrades_id(String.valueOf(data.get(4)));
        }else {
            payDataImport.setTrades_id("");
        }
        payDataImport.setAccount_checking_status(-1);
        payDataImport.setCheck_status(0);
        String excelAmount = null;
        String excelTradesStatus = data.get(5).getStringCellValue();
        if (Arrays.asList(AccountConstants.PAYMENT_STATUS).contains(excelTradesStatus)) {
            excelAmount = data.get(6).getStringCellValue().toString();
        } else {
            excelAmount = data.get(7).getStringCellValue().toString();
        }
        payDataImport.setAccount_balance(data.get(8).getStringCellValue()==null?"0":
            String.valueOf((int) Math.rint(Float.parseFloat(data.get(8).getStringCellValue()) * 100)));//账户余额
        //收入 金额应该乘以100 zw
        String incomeAmount = null;
        try {
            if (Float.parseFloat(excelAmount) < 0)
                incomeAmount = excelAmount;
            else
                incomeAmount = String.valueOf((int) Math.rint(Float.parseFloat(excelAmount) * 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        payDataImport.setIncome_amount(incomeAmount);
        payDataImport.setUser_account(data.get(12).getStringCellValue());
        payDataImport.setPay_time(Timestamp.valueOf(data.get(1).getStringCellValue()));
        //支付宝的流水号
        payDataImport.setBusiness_order_sn(data.get(3).getStringCellValue());
        //支出
        payDataImport.setSpending_amount("0");

        if ("交易".equals(data.get(5).getStringCellValue())) {
            payDataImport.setTrades_status("买家已支付");
        }else if ("集分宝".equals(data.get(5).getStringCellValue())){
            payDataImport.setTrades_status("蚂蚁优惠集分宝补贴");
        } else {
            payDataImport.setTrades_status(data.get(5).getStringCellValue());
        }
        payDataImport.setBusiness(data.get(5).getStringCellValue());
        //支付渠道
        payDataImport.setTrading_scenario(data.get(10).getStringCellValue());
        payDataImport.setRemark(data.get(16).getStringCellValue());
        payDataImport.setAccount_checking_status(-1);
        payDataImport.setCheck_status(0);
        return payDataImport;
    }

    /**
     * 支付宝退款解析
     *
     * @param data
     * @return
     */
    public static ArrayList<PayDataImport> zf_refund_buildList(ArrayList<ArrayList<Cell>> data) throws IOException {

        ArrayList<PayDataImport> result = new ArrayList<>();
        if (!"状态".equals(data.get(2).get(10).getStringCellValue())&&(!"退款状态".equals(data.get(2).get(10).getStringCellValue()))){
            throw new IOException();
        }
        for (int i = 3; i < data.size() - 1; i++) {
            result.add(aliRefundBuild(data.get(i)));
        }
        return result;
    }

    public static PayDataImport aliRefundBuild(ArrayList<Cell> data) {
        PayDataImport payDataImport = new PayDataImport();
        payDataImport.setPay_style(AccountConstants.PAY_TYPE_ALI);
        payDataImport.setData_type(AccountConstants.REFUND_DATA_IMPORT);
        //商品订单号
        payDataImport.setTrades_id(String.valueOf(data.get(4)));
        payDataImport.setAccount_checking_status(-1);
        payDataImport.setCheck_status(0);
        payDataImport.setUser_account(data.get(7).getStringCellValue() + data.get(6).getStringCellValue());
        try {
            payDataImport.setIncome_amount(String.valueOf((int) Math.rint( Float.parseFloat( data.get(8).getStringCellValue()) * 100)));
        } catch (Exception e) {
            payDataImport.setIncome_amount("0");
            e.printStackTrace();
        }
        payDataImport.setTrading_scenario(data.get(12).getStringCellValue());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse(data.get(1).getStringCellValue(), formatter);
        payDataImport.setPay_time(Timestamp.valueOf(ldt));
        // 业务流水号
        payDataImport.setBusiness_order_sn(data.get(11).getStringCellValue());

        if ("成功".equals(data.get(10).getStringCellValue())) {
            payDataImport.setTrades_status("退款成功");
        } else {
            payDataImport.setTrades_status(data.get(10).getStringCellValue());
        }
        //支出金额
        try {
            payDataImport.setSpending_amount(String.valueOf((int) Math.rint( Float.parseFloat( data.get(9).getStringCellValue()) * 100)));
        } catch (Exception e) {
            payDataImport.setSpending_amount("0");
            e.printStackTrace();
        }
        payDataImport.setBusiness(data.get(3).getStringCellValue());
        return payDataImport;
    }

    /**
     * 微信支付解析
     *
     * @param data
     * @return
     */
    public static ArrayList<PayDataImport> wechat_pay_buildList(ArrayList<ArrayList<Cell>> data) throws IOException {

        // 判断数据库trades_id跟trade_status不能重复
        ArrayList<PayDataImport> result = new ArrayList<>();
        if (!"交易状态".equals(data.get(3).get(4).getStringCellValue())){
            throw new IOException();
        }
        for (int i = 4; i < data.size(); i++) {
            PayDataImport payDataImport = weChatPaybuild(data.get(i));
            if (StringUtil.isBlank(payDataImport.getTrades_id())) {
               continue;
            }
            result.add(payDataImport);
        }
        return result;
    }


    public static PayDataImport weChatPaybuild(ArrayList<Cell> data) {
        PayDataImport payDataImport = new PayDataImport();
        if(data.size()!=6){
            return payDataImport;
        }
        if ("买家已支付".equals(data.get(4).getStringCellValue())) {
            String excelTradesId = data.get(2).getStringCellValue().substring(1);
            payDataImport.setTrades_id(excelTradesId);

            String excelBusinessOrderSn = data.get(1).getStringCellValue();
            payDataImport.setBusiness_order_sn(excelBusinessOrderSn.substring(1));
            Timestamp timestamp = new Timestamp(data.get(0).getDateCellValue().getTime());
            payDataImport.setBusiness(data.get(4).getStringCellValue());
            payDataImport.setPay_time(timestamp);
            payDataImport.setPay_style(AccountConstants.PAY_TYPE_WX);
            payDataImport.setData_type(0);
            payDataImport.setAccount_checking_status(-1);
            payDataImport.setCheck_status(0);
            payDataImport.setUser_account(data.get(3).getStringCellValue());
            try {
                payDataImport.setIncome_amount(String.valueOf((int) Math.rint(data.get(5).getNumericCellValue() * 100)));
            } catch (Exception e) {
                payDataImport.setIncome_amount("0");
                e.printStackTrace();
            }

            //支出金额
            payDataImport.setSpending_amount("0");
            payDataImport.setTrading_scenario(data.get(3).getStringCellValue());
            payDataImport.setTrades_status(data.get(4).getStringCellValue());
        }
        return payDataImport;
    }

    /**
     * 微信退款解析
     *
     * @param data
     * @return
     */
    public static ArrayList<PayDataImport> wechat_refund_buildList(ArrayList<ArrayList<Cell>> data) throws IOException {
        ArrayList<PayDataImport> result = new ArrayList<>();
        if (!"退款状态".equals(data.get(3).get(3).getStringCellValue())){
            throw new IOException();
        }
        for (int i = 4; i < data.size(); i++) {
            PayDataImport payDataImport = weChatRefundbuild(data.get(i));
            if(StringUtil.isBlank(payDataImport.getTrades_id())){
                continue;
            }
            result.add(payDataImport);
        }
        return result;
    }


    public static PayDataImport weChatRefundbuild(ArrayList<Cell> data) {
        PayDataImport payDataImport = new PayDataImport();
        if ("退款成功".equals(data.get(3).getStringCellValue())) {
            payDataImport.setPay_style("wx");
            payDataImport.setData_type(1);
            payDataImport.setAccount_checking_status(-1);
            payDataImport.setCheck_status(0);
            //商户订单号

            String excelTradesId = data.get(8).getStringCellValue().substring(1);
            payDataImport.setTrades_id(excelTradesId);

            try {
                payDataImport.setIncome_amount(String.valueOf((int) Math.rint(data.get(10).getNumericCellValue() * 100)));
            } catch (Exception e) {
                payDataImport.setIncome_amount("0");
                e.printStackTrace();
            }
            //退款入账账户
            payDataImport.setUser_account(data.get(11).getStringCellValue());
            //退款成功时间

            try {
                payDataImport.setPay_time(new Timestamp(data.get(4).getDateCellValue().getTime()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            //微信支付单号
            String str1 = data.get(7).getStringCellValue();

            try {
                payDataImport.setBusiness_order_sn(str1.substring(1, str1.length() - 1));
            } catch (Exception e) {
                payDataImport.setBusiness_order_sn("0");
                e.printStackTrace();
            }
            payDataImport.setBusiness(data.get(3).getStringCellValue());
            //退款金额
            try {
                payDataImport.setSpending_amount(String.valueOf((int) Math.rint(data.get(5).getNumericCellValue() * 100)));
            } catch (Exception e) {
                payDataImport.setSpending_amount("0");
                e.printStackTrace();
            }
            //退款状态
            payDataImport.setTrades_status(data.get(3).getStringCellValue());
            payDataImport.setTrading_scenario(data.get(11).getStringCellValue());
        }
        return payDataImport;
    }
    /**
     * 支付宝银行划账导入
     * @param data
     * @return
     */
    public static ArrayList<PayDataImport> wechat_account_buildList(ArrayList<ArrayList<Cell>> data){
        ArrayList<PayDataImport> result=new ArrayList<>();
        for(int i=5;i<data.size()-4;i++){
            result.add(weChatAccountbuild(data.get(i)));
        }
        return result;
    }

    public static PayDataImport weChatAccountbuild(ArrayList<Cell> data){

        PayDataImport payDataImport= new PayDataImport();
        payDataImport.setPay_style("ali");
        payDataImport.setBusiness_order_sn((data.get(1).getStringCellValue().trim()));  //业务流水号
        payDataImport.setAccount_checking_status(-1);
        String tradesId=data.get(2).getStringCellValue().trim();
        if(tradesId.length() <19){
            //setBusiness_order_sn关联订单表查找订单号
            Object order_sn=payDataImport.getBusiness_order_sn();
            String  trades_id =dataImportProcessService.tradesMapper.getTradesIdByPayNum(order_sn.toString());
            if(StringUtil.isEmpty(trades_id)){
                logger.info("业务流水号:{}未找到对应订单号",payDataImport.getBusiness_order_sn());
            }
            payDataImport.setTrades_id(tradesId);   //订单号
        }else {
            payDataImport.setTrades_id(tradesId);   //订单号
        }
//        payDataImport.setTrades_id((data.get(2).getStringCellValue().trim()));   //订单号
        payDataImport.setData_type(3);
        payDataImport.setUser_account((data.get(5).getStringCellValue().trim()));;   //付款账号（对方账号）
        payDataImport.setTrades_status((data.get(10).getStringCellValue().trim()));;   //交易状态（券名称）
        payDataImport.setPay_time(new java.sql.Timestamp(data.get(4).getDateCellValue().getTime()));   //创建时间
        payDataImport.setIncome_amount(String.valueOf(Math.round(data.get(6).getNumericCellValue()*100.0)));   //付款金额

        if (data.get(10).getStringCellValue().equals("交易退款")){
            payDataImport.setSpending_amount(String.valueOf(Math.round(data.get(7).getNumericCellValue()*100.0)));   //退款金额
        }else{
            payDataImport.setSpending_amount("0");
        }
        //payDataImport.setIncome_amount(String.valueOf(Math.round(data.get(12).getNumericCellValue()*100.0)));   //付款金额
        if (data.get(10).getStringCellValue().equals("收费")){
            payDataImport.setPlatform_payment_amount(String.valueOf(Math.round(data.get(7).getNumericCellValue()*100.0)));   //服务费
        }else{
            payDataImport.setPlatform_payment_amount("0");
        }
        payDataImport.setPlatform_service_fee("0.55%");//费率
        return payDataImport;
    }

    /**
     * 微信银行划账导入
     * @param data
     * @return
     */

    public static ArrayList<PayDataImport> wechat_refund_bank(ArrayList<ArrayList<Cell>> data){
        ArrayList<PayDataImport> result = new ArrayList<>();
        for (int i=1;i<data.size()-2;i++){
            result.add(wechatRefundBank(data.get(i)));
        }
        return result;
    }


    public static PayDataImport wechatRefundBank(ArrayList<Cell> data){
        PayDataImport payDataImport= new PayDataImport();

        payDataImport.setPay_style("wx");
        payDataImport.setBusiness_order_sn((data.get(5).getStringCellValue().substring(1)));   //微信订单号
        payDataImport.setAccount_checking_status(-1);
        payDataImport.setTrades_id(String.valueOf(data.get(6).getStringCellValue().substring(1))); //订单号
        payDataImport.setData_type(3);
        payDataImport.setUser_account((data.get(7).getStringCellValue().substring(1)));;   //付款账号（对方账号）
        payDataImport.setTrades_status((data.get(9).getStringCellValue().substring(1)));;   //交易状态（交易状态）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse(data.get(0).getStringCellValue().substring(1), formatter);
        payDataImport.setPay_time(Timestamp.valueOf(ldt));   //创建时间

        payDataImport.setPlatform_service_fee(String.valueOf(data.get(23).getStringCellValue().substring(1)));//费率


        payDataImport.setPay_time(Timestamp.valueOf(ldt));

        try {
            payDataImport.setIncome_amount(String.valueOf((int) Math.rint( Float.parseFloat( data.get(12).getStringCellValue().substring(1)) * 100)));//总金额
        } catch (Exception e) {
            payDataImport.setIncome_amount("0");
            e.printStackTrace();
        }

        try {
            payDataImport.setSpending_amount(String.valueOf((int) Math.rint( Float.parseFloat( data.get(16).getStringCellValue().substring(1)) * 100)));//退款金额
        } catch (Exception e) {
            payDataImport.setSpending_amount("0");
            e.printStackTrace();
        }

        try {
            payDataImport.setPlatform_payment_amount(String.valueOf((int) Math.rint( Float.parseFloat( data.get(22).getStringCellValue().substring(1)) * 100)));//手续费
        } catch (Exception e) {
            payDataImport.setPlatform_payment_amount("0");
            e.printStackTrace();
        }
        return payDataImport;
    }
}
