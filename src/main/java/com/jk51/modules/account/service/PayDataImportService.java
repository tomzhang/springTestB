package com.jk51.modules.account.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.excel.ExcelObject;
import com.jk51.model.account.models.Finances;
import com.jk51.model.account.models.PayDataImport;
import com.jk51.model.account.models.SettlementDetail;
import com.jk51.modules.account.controller.PayDataImportController;
import com.jk51.modules.account.mapper.ClassifiedStatisticMapper;
import com.jk51.modules.account.mapper.FinancesMapper;
import com.jk51.modules.account.mapper.PayDataImportMapper;
import com.jk51.modules.account.mapper.SettlementDetailMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class PayDataImportService {
    private static final Logger logger = LoggerFactory.getLogger(PayDataImportController.class);

    @Autowired
    public PayDataImportMapper payDataImportMapper;

    @Autowired
    private DataImportProcessService dataImportProcessService;

    @Autowired
    private SettlementDetailService settlementDetailService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FinancesMapper financesMapper;

    @Autowired
    private ClassifiedStatisticMapper classifiedStatisticMapper;
    /**
     * 解析支付宝收款数据 @param xlspath
     */
    public void parsePayFile(MultipartFile file, Integer siteId) throws IOException {
        ExcelObject eo = new ExcelObject(file);
        dataImportProcessService.zf_pay_buildList(eo.read()).forEach(c -> {
            saveAliPayData(c);
        });
        runQueue(siteId);
    }

    /**
     * 解析支付宝退款数据 @param xlspath
     */
    public void parseAliRefundFile(MultipartFile file, Integer siteId) throws IOException {
        ExcelObject eo = new ExcelObject(file);
        dataImportProcessService.zf_refund_buildList(eo.read()).forEach(c -> {
            savePayData(c);
        });
        runQueue(siteId);
    }

    /**
     * 解析微信收款数据 @param xlspath
     */
    public void parseWechatPayFile(MultipartFile file, Integer siteId) throws IOException {
        ExcelObject eo = new ExcelObject(file);
        dataImportProcessService.wechat_pay_buildList(eo.read()).forEach(c -> {
            savePayData(c);
        });
        runQueue(siteId);
    }

    /**
     * 解析微信退款数据 @param xlspath
     */
    public void parseWechatRefundFile(MultipartFile file, Integer siteId) throws IOException {
        ExcelObject eo = new ExcelObject(file);
        dataImportProcessService.wechat_refund_buildList(eo.read()).forEach(c -> {
            savePayData(c);
        });
        runQueue(siteId);
    }

    @Autowired
    private SettlementDetailMapper settlementDetailMapper;

    /**
     * 解析支付宝付款划账
     */
    public void parseIsAccountile(MultipartFile file) throws IOException {
        ExcelObject eo = new ExcelObject(file);
        dataImportProcessService.wechat_account_buildList(eo.read()).forEach(c -> {
            savePayData(c);
        });
        //payDataImportMapper.updateLostTradesId();
        runRemitQueue();
    }
    /**
     * 解析微信付款划账
     */
    public void paresWechatAccountBank(MultipartFile file) throws IOException {
        ExcelObject eo = new ExcelObject(file);
        dataImportProcessService.wechat_refund_bank(eo.read()).forEach(c -> {
            savePayData(c);
        });
        runRemitQueue();
    }


    /**
     * 划账添加数据
     * @param payDataImportList
     * @return
     */
    public void savePayData(PayDataImport payDataImportList) {
        PayDataImport payDataImport = payDataImportMapper.getPayDataImportByTradesIdAndStatus(payDataImportList.getTrades_id(),
                payDataImportList.getTrades_status());

        if (payDataImport == null) {
            payDataImportMapper.savePayDataImport(payDataImportList);
        } else {
            payDataImportList.setId(payDataImport.getId());
            payDataImportMapper.updateImportList(payDataImportList);
        }

//        AccountCouponMq accountCouponMq = new AccountCouponMq();
//        /* 默认值*/settlementDetailService.batchAccountChecking()
//        accountCouponMq.setSiteId(1);
//        accountCouponMq.setType(1);
//        AccountEvent accountEvent = new AccountEvent(this, accountCouponMq);
//        applicationContext.publishEvent(accountEvent);
    }

    /**
     * 支付宝导入数据
     * @param order
     * @return
     */
    public void saveAliPayData(PayDataImport order) {
        PayDataImport payDataImport = payDataImportMapper.selectPayDataByTradesIdAndAccountBalance(order.getTrades_id(), order.getTrades_status(),order.getAccount_balance());
        if (payDataImport == null) {
            payDataImportMapper.savePayDataImport(order);
        } else {
            order.setId(payDataImport.getId());
            payDataImportMapper.updateImportList(order);
        }
    }


// 划账执行程序
    private void runRemitQueue(){
        Thread sendCouponDirect = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                settlementDetailService.remitProcess();
            }
        });
        sendCouponDirect.start();
    }



    private void runQueue(Integer siteId){
        Thread sendCouponDirect = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                settlementDetailService.batchAccountChecking(siteId);
            }
        });
        sendCouponDirect.start();
    }

    public List<PayDataImport> getPayDataImportListByCheckStatus() {
        return payDataImportMapper.getPayDataImportListByCheckStatus();
    }


    public PageInfo<?> queryPayDataImport(Map<String, Object> params) {
        String financeNos = (params.get("finance_no")+"").replaceAll("，",",").replaceAll("；",",").replaceAll(";",",").replaceAll(" ","").replaceAll("\n","");
        List<String> financesNos= Arrays.stream(financeNos.split(",")).collect(Collectors.toList());
        params.put("financesNos","".equals(financeNos)||null==financeNos?null:financesNos);
        boolean isCount = true;
        if (params.containsKey("isCount") && params.get("isCount") != null) {
            isCount = ((Boolean) params.get("isCount")).booleanValue();
        }
        if (params.get("payAmountStart")!=null&&params.get("payAmountStart")!=""){
            Double   f3   =    (new  BigDecimal(Double.parseDouble( params.get("payAmountStart").toString()) * 100).setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue());
            int payAmountStart = f3.intValue();
            params.put("payAmountStart",payAmountStart);
        }
        if (params.get("payAmountEnd")!=null&&params.get("payAmountEnd")!=""){
            Double   f3   =    (new  BigDecimal(Double.parseDouble( params.get("payAmountEnd").toString()) * 100).setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue());
            int payAmountStart = f3.intValue();
            params.put("payAmountEnd",payAmountStart);
        }
        if (params.get("refundAmountStart")!=null&&params.get("refundAmountStart")!=""){
            Double   f3   =    (new  BigDecimal(Double.parseDouble( params.get("refundAmountStart").toString()) * 100).setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue());
            int payAmountStart = f3.intValue();
            params.put("refundAmountStart",payAmountStart);
        }
        if (params.get("refundAmountEnd")!=null&&params.get("refundAmountEnd")!=""){
            Double   f3   =    (new  BigDecimal(Double.parseDouble( params.get("refundAmountEnd").toString()) * 100).setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue());
            int payAmountStart = f3.intValue();
            params.put("refundAmountEnd",payAmountStart);
        }
        Map<String,Double> stastic = payDataImportMapper.getPayDataImportStastic(params);  //  汇总
        PageHelper.startPage((int) params.get("pageNum"), (int) params.get("pageSize"), isCount);
        List<Map<String,Object>> list = payDataImportMapper.getPayDataImportList(params);

//        List<Map<String,Object>> listStatic = payDataImportMapper.getPayDataImportList(params);
        if (null!=list&&!list.isEmpty()){
            list.get(0).put("income_sum",stastic==null?0:stastic.get("income_amount")); //付款对账表的汇总
            list.get(0).put("spende_sum",stastic==null?0:stastic.get("spending_amount")); //退款对账表的汇总

           /* list.get(0).setIncome_sum(stastic==null? 0.00:stastic.get("income_amount")); //付款对账表的汇总
            list.get(0).setSpende_sum(stastic==null ? 0.00:stastic.get("spende_amount")); //退款对账表的汇总*/
        }
        return new PageInfo<>(list);
    }

    public int updateCheckStatus(Map<String,Object> param){

        int i=settlementDetailMapper.updateStatusByTradesId(param);

        SettlementDetail detail= settlementDetailMapper.getSettlementListByTradesId(Long.valueOf(param.get("trades_id").toString()));
        //查询账单总状态
        updateIsChargeOffByFinanceNo(detail.getFinance_no());
        return i;
    }

    public void updateIsChargeOffByFinanceNo(String financeNo){

        List<SettlementDetail> checkResult=settlementDetailMapper.findTradesByFinance(financeNo);

        boolean temp=true;

        if (null!=checkResult&&!checkResult.isEmpty()){
            for (SettlementDetail item:checkResult) {
                if ("异常".equals(item.getIsChargeOff())){
                    temp=false;
                }
            }
        }

        //修改账单状态
        if (temp&&null!=checkResult&&!checkResult.isEmpty()){
            Finances finances=new Finances();
            finances.setFinance_no(financeNo);
            finances.setIs_charge_off(0);//正常
            financesMapper.updateFinanceByNo(finances);
        }

    }
}

