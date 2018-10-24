package com.jk51.modules.account.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.excel.ExcelObject;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.map.MapUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.interceptor.LoginInterceptor;
import com.jk51.model.account.models.*;
import com.jk51.model.account.requestParams.AccountRemit;
import com.jk51.model.account.requestParams.QueryOrderBill;
import com.jk51.model.account.requestParams.QuerySettlementLog;
import com.jk51.model.account.requestParams.QueryStatement;
import com.jk51.model.goods.PageData;
import com.jk51.model.order.Refund;
import com.jk51.model.order.Trades;
import com.jk51.model.settle.FinancialSettleDetail;
import com.jk51.modules.account.constants.AccountConstants;
import com.jk51.modules.account.mapper.*;
import com.jk51.modules.trades.mapper.RefundMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * filename :com.jk51.modules.account.goodsService.
 * author   :zw
 * date     :2017/2/16
 * Update   :
 */
@Service
public class SettlementDetailService {
    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
    @Autowired
    private SettlementDetailMapper settlementDetailMapper;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private PayDataImportMapper dataImportMapper;
    @Autowired
    private RefundMapper refundMapper;
    @Autowired
    private SettlementDetailLogService settlementDetailLogService;
    @Autowired
    private SettlementDetailAndTradesMapper settlementDetailAndTradesMapper;

    @Autowired
    private FinancesMapper financesMapper;

    @Autowired
    private PayDataImportService payDataImportService;

    @Autowired
    private ClassifiedStatisticMapper classifiedStatisticMapper;


    public void batchAccountChecking(Integer siteId) {
        List<Trades> tradesListObj = tradesMapper.getTradesListByAccountCheckingStatus(siteId);//支付,佣金,分账。条件：已付款，对账状态为待处理
        List<Refund> refundListObj = refundMapper.getRefundListByAccountCheckingStatus(siteId);//退款  获取refund表未对帐数据

        try {
            //现金和医保卡支付的直接添加到对账表
            processCashAndHealth(tradesListObj.stream().filter(pdi -> {
                return Arrays.asList("health_insurance", "cash").contains(pdi.getPayStyle());
            }).collect(Collectors.toList()));
        } catch (Exception e) {
            logger.error("error message:class->SettlementDetailService,function->processCashAndHealth" + e);
            e.printStackTrace();
        }

        try {
            processBill(tradesListObj);
        } catch (Exception e) {
            logger.error("error message:class->SettlementDetailService,function->processBill" + e);
            e.printStackTrace();
        }

        try {
            //现金和医保卡的退款记录直接修改对账表
            processRefundCashAndHealth(refundListObj.stream().filter(pdi -> {
                return Arrays.asList("health_insurance", "cash").contains(pdi.getPayStyle());
            }).collect(Collectors.toList()));
        } catch (Exception e) {
            logger.error("error message:class->SettlementDetailService,function->processRefundCashAndHealth" + e);
            e.printStackTrace();
        }
        try {
            processRefund(refundListObj);
        } catch (Exception e) {
            logger.error("error message:class->SettlementDetailService,function->processRefund" + e);
            e.printStackTrace();
        }

    }

    private void processCashAndHealth(List<Trades> tradesListObj) {
        tradesListObj.stream().forEach(trades -> {
            addSettlementDetailS(ProcessService.buildCashAndHealthInsurance(trades));
            tradesMapper.updateAccountStatus(trades.getTradesId()); //修改对账订单状态
            settlementDetailLogService.addSettlementDetailLog(1, trades.getTradesId());
        });
    }


    private void processBill(List<Trades> tradesListObj) {
        /*Map<String, PayDataImport> payDataListMap = dataImportMapper.getPayDataImportListByCheckStatus()
                .stream().filter(payDataImport -> payDataImport.getData_type().equals(0))
                .collect(Collectors.toMap(PayDataImport::getMapKey, (o) -> o));
        tradesListObj.parallelStream().forEach(trades -> {
            if (payDataListMap.get(trades.getTradesId() + "||买家已支付") != null) {
                switch (trades.getPayStyle()) {
                    case "ali":
                        Map<String, PayDataImport> assemblyMap = assembly(payDataListMap, String.valueOf(trades.getTradesId()));
                        SettlementDetail settlementDetail = ProcessService.buildByAli(trades, assemblyMap);
                        addSettlementDetailS(settlementDetail);
                        tradesMapper.updateAccountStatus(trades.getTradesId()); //修改对账订单状态
                        updatePayDataImport(String.valueOf(trades.getTradesId()), settlementDetail.getAccount_checking_status(), 0);
                        break;
                    case "wx":
                        SettlementDetail settlementDetail1 = ProcessService.buildByWx(trades, payDataListMap.get(trades.getTradesId() + "||买家已支付"));
                        addSettlementDetailS(settlementDetail1);
                        tradesMapper.updateAccountStatus(trades.getTradesId()); //修改对账订单状态
                        updatePayDataImport(String.valueOf(trades.getTradesId()), settlementDetail1.getAccount_checking_status(), 0);
                        break;
                }
            }
        });*/

        //获取第三方付款数据
        Map<String,PayDataImport> dataMap=dataImportMapper.selectPayDataForCheck().stream().collect(Collectors.toMap(PayDataImport::getTrades_id, (o) -> o));
        logger.info("获取第三方数据，开始对账！");
        tradesListObj.stream().forEach(trades -> {
            if (dataMap.get(trades.getTradesId()+"") != null) {
                switch (trades.getPayStyle()) {
                    case "ali":
                        SettlementDetail settlementDetail = ProcessService.buildAli(trades, dataMap.get(trades.getTradesId()+""));
                        addSettlementDetailS(settlementDetail);
                        if (settlementDetail.getAccount_checking_status()==1) {
                            tradesMapper.updateAccountStatus(trades.getTradesId()); //修改对账订单状态
                            updatePayDataImport(String.valueOf(trades.getTradesId()), settlementDetail.getAccount_checking_status(), 0);
                        }
                        break;
                    case "wx":
                        SettlementDetail settlementDetail1 = ProcessService.buildByWx(trades, dataMap.get(trades.getTradesId()+""));
                        addSettlementDetailS(settlementDetail1);
                        if (settlementDetail1.getAccount_checking_status()==1) {
                            tradesMapper.updateAccountStatus(trades.getTradesId()); //修改对账订单状态
                            updatePayDataImport(String.valueOf(trades.getTradesId()), settlementDetail1.getAccount_checking_status(), 0);
                        }
                        break;
                }
            }
        });

    }

    private void updatePayDataImport(String trades_id, int account_status, int type) {

        int data_type = 0;
        switch (type) {
            case 0:
                //失败
                if (account_status == 2) {
                    data_type = 1;
                } else {
                    data_type = 0;
                }
                break;
            case 1:
                //成功
                if (account_status == 1) {
                    data_type = 0;
                } else {
                    data_type = 1;
                }
                break;
        }
        dataImportMapper.updateImportStatus(trades_id, type, data_type);//修改dataImport表

    }

    private Map<String, PayDataImport> assembly(Map<String, PayDataImport> payDataListMap, String trade_id) {
        Map<String, PayDataImport> assemblyMap = new HashMap<>();
        assemblyMap.put("买家已支付", payDataListMap.get(trade_id + "||买家已支付"));
        assemblyMap.put("服务费", payDataListMap.get(trade_id + "||服务费"));
        assemblyMap.put("交易分账", payDataListMap.get(trade_id + "||交易分账"));
        assemblyMap.put("蚂蚁优惠集分宝补贴", payDataListMap.get(trade_id + "||蚂蚁优惠集分宝补贴"));
        assemblyMap.put("红包", payDataListMap.get(trade_id + "||红包"));

        return assemblyMap;

    }


    private void processRefundCashAndHealth(List<Refund> refundsListObj) {
        refundsListObj.stream().forEach(refund -> {
            int i = settlementDetailMapper.updateSettlementByCh(ProcessService.buildRefundCashAndHealthInsurance(refund));
            if (i > 0) {
                refundMapper.updateAccountStatus(Long.parseLong(refund.getTradeId())); //修改对账订单状态
            }

        });
    }


    private void processRefund(List<Refund> refundListObj) {
        Map<String, PayDataImport> payDataListMap = dataImportMapper.getPayDataImportListByCheckStatus()
                .stream().filter(payDataImport -> payDataImport.getData_type().equals(1))
                .collect(Collectors.toMap(PayDataImport::getTrades_id, (o) -> o));

        refundListObj.stream().forEach(refund -> {

            if (payDataListMap.get(refund.getTradeId()) != null) {
                SettlementDetail settlementListByTradesId = settlementDetailMapper.getSettlementListByTradesId(
                        Long.parseLong(refund.getTradeId()));
                if (settlementListByTradesId != null) {
                    SettlementDetail settlementDetail = ProcessService.refund(refund, payDataListMap.get(refund.getTradeId()));
                    settlementDetail.setId(settlementListByTradesId.getId());
                    settlementDetailMapper.updateSettlementRefundById(settlementDetail);
                    refundMapper.updateAccountStatus(Long.parseLong(refund.getTradeId())); //修改对账订单状态

                    updatePayDataImport(String.valueOf(refund.getTradeId()), settlementDetail.getRefund_checking_status(), 1);
                } else {
                    Trades trades = tradesMapper.getTradesByTradesId(Long.parseLong(refund.getTradeId()));
                    logger.info("退款对账查询once" + trades);
                    if (trades != null) {
                        SettlementDetail settlementDetail = ProcessService.refundByWx(trades,
                                payDataListMap.get(trades.getTradesId().toString()));

                        addSettlementDetailS(settlementDetail);
                        settlementDetailMapper.updateSettlementById(ProcessService.refund(refund, payDataListMap.get(refund.getTradeId())));
                        tradesMapper.updateAccountStatus(trades.getTradesId()); //修改对账订单状态

                        updatePayDataImport(String.valueOf(trades.getTradesId()), settlementDetail.getAccount_checking_status(), 1);
                    }

                }
            }
        });
    }


    //划账支付金额对账

    public  void remitProcess(){
        List<Trades> remitTradesListObj = tradesMapper.getTradesListByRemitAccountStatus();//支付金额  条件：已付款，划账状态为待处理
        try {
            processRemitPay(remitTradesListObj);    //支付金额 划账对账
        } catch (Exception e) {
            logger.error("error message:class->SettlementDetailService,function->remitProcess" + e);
            e.printStackTrace();
        }
    }


    public void processRemitPay(List<Trades> remitTradesListObj) {
        Map<String, PayDataImport> payDataListMap = dataImportMapper.getPayDataImportListByRemitAccountStatus()
                .stream().filter(payDataImport -> payDataImport.getData_type().equals(3))
                .collect(Collectors.toMap(PayDataImport::getTrades_id, (o) -> o));
        remitTradesListObj.parallelStream().forEach(trades -> {
            if (payDataListMap.get(trades.getTradesId().toString()) != null) {
                switch (trades.getPayStyle()) {
                    case "ali":
                        SettlementDetail settlementDetail = ProcessService.buildByRemit(trades, payDataListMap.get(trades.getTradesId().toString()));
                        updateRemit(settlementDetail);
                        tradesMapper.updateRemitAccountStatus(trades.getTradesId()); //修改划账订单状态
                        break;
                    case "wx":
                        SettlementDetail settlementDetail2 = ProcessService.buildByRemit(trades, payDataListMap.get(trades.getTradesId().toString()));
                        updateRemit(settlementDetail2);
                        tradesMapper.updateRemitAccountStatus(trades.getTradesId()); //修改划账订单状态
                        break;
                }
            }
        });
    }

    //    更新表 yb_settlement_detail 中划账数据
    private int updateRemit(SettlementDetail settlementDetail){
        int i =0;
        i=settlementDetailMapper.updateSettlementByRemitData(settlementDetail);
        return  i ;
    }


    /**
     * 支付宝银行数据 @param xlspath
     */
    public void parseAliBankFile(MultipartFile file) throws IOException {
        ExcelObject eo = new ExcelObject(file);
        SettlementDetail.ali_bank_buildList(eo.read()).forEach(c -> {
            saveSettlementDetails(c);
        });
    }

    /**
     * 微信银行数据 @param xlspath
     */
    public void parseWechatBankFile(MultipartFile file) throws IOException {
        ExcelObject eo = new ExcelObject(file);
        SettlementDetail.wechat_bank_buildList(eo.read()).forEach(c -> {
            saveSettlementDetails(c);
        });
    }

    //添加数据 计算明细
    private int addSettlementDetailS(SettlementDetail settlementDetail) {
        SettlementDetail sd = settlementDetailMapper.getSettlementListByTradesId(settlementDetail.getTrades_id());
        int i = 0;
        if (null != sd) {
            settlementDetail.setId(sd.getId());
            i = settlementDetailMapper.updateSettlementById(settlementDetail);
        } else {
            try {
                i = settlementDetailMapper.addSettlementDetail(settlementDetail);
            } catch (Exception e) {
                logger.error("已存在对账信息");
                e.printStackTrace();
            }
        }
        if (settlementDetail.getAccount_checking_status() == 1) {
            settlementDetailLogService.addSettlementDetailLog(1, settlementDetail.getTrades_id());
        } else if (settlementDetail.getAccount_checking_status() == 2) {
            settlementDetailLogService.addSettlementDetailLog(2, settlementDetail.getTrades_id());
        } else if (settlementDetail.getRefund_checking_status() == 0) {
            settlementDetailLogService.addSettlementDetailLog(4, settlementDetail.getTrades_id());
        } else if (settlementDetail.getRefund_checking_status() == 1) {
            settlementDetailLogService.addSettlementDetailLog(3, settlementDetail.getTrades_id());
        }

        return i;
    }


    /**
     * 添加数据
     *
     * @param
     * @return
     */
    public void saveSettlementDetails(SettlementDetail settlementDetailList) {
        settlementDetailMapper.addSettlementDetail(settlementDetailList);
    }


    public PageInfo<?> querySettlementDetail(QueryStatement queryStatement) {
        PageHelper.startPage(queryStatement.getPageNum(), queryStatement.getPageSize());

        List<PageData> list = settlementDetailMapper.findSettlementDetail(queryStatement);

        /*for (int i = 0; i < list.size(); i++) {

            Map<String, Object> data = financesMapper.findBeforeData(Integer.parseInt(list.get(i).get("seller_id") + ""), list.get(i).get("create_time") + "");
            if (data == null) {

                list.get(i).put("shangqijiezhuan", 0);
            } else {
                list.get(i).put("shangqijiezhuan", Double.parseDouble(data.get("need_pay").toString()) - Double.parseDouble(data.get("real_pay").toString()));
            }
            if ("1".equals(list.get(i).get("is_charge_off") + "")) {
                payDataImportService.updateIsChargeOffByFinanceNo(list.get(i).get("finance_no") + "");
            } else {
                //补分类汇总的付款，退款对账金额和账单的退款金额
                List<ClassifiedStatistic> classifieds = settlementDetailMapper.findCheckedMoney(list.get(i).get("finance_no") + "");
                if (null != classifieds && !classifieds.isEmpty()) {
                    classifieds.stream().forEach(item -> {
                        classifiedStatisticMapper.updateCheckedMoney(item);
                    });
                    Finances finances = new Finances();
                    finances.setFinance_no(list.get(i).get("finance_no") + "");
                    finances.setFinance_checking_total(classifieds.stream().mapToInt(l -> l.getFinance_checking_total()).sum());
//                finances.setRefund_total(classifieds.stream().mapToInt(l->l.getRefund_total()).sum());
                    finances.setRefund_checking_total(classifieds.stream().mapToInt(l -> l.getRefund_checking_total()).sum());
                    financesMapper.updateFinanceByNo(finances);
                }
            }

        }*/

        return new PageInfo<>(list);
    }

    @Transactional
    public PageInfo<?> updateChargeOff(QueryStatement queryStatement) {
        PageHelper.startPage(queryStatement.getPageNum(), queryStatement.getPageSize());

        List<PageData> list = settlementDetailMapper.findSettlementDetail(queryStatement);
        logger.info("核对并修改账单的状态，同时核对并修改结算汇总的对账数据及账单的对账数据！");
        for (int i = 0; i < list.size(); i++) {

//            Map<String, Object> data = financesMapper.findBeforeData(Integer.parseInt(list.get(i).get("seller_id") + ""), list.get(i).get("create_time") + "");
//            if (data == null) {
//
//                list.get(i).put("shangqijiezhuan", 0);
//            } else {
//                list.get(i).put("shangqijiezhuan", Double.parseDouble(data.get("need_pay").toString()) - Double.parseDouble(data.get("real_pay").toString()));
//            }
            logger.info("核对并修改账单号【{}】");
            if ("1".equals(list.get(i).get("is_charge_off") + "")) {
                payDataImportService.updateIsChargeOffByFinanceNo(list.get(i).get("finance_no") + "");
            }
                //补分类汇总的付款，退款对账金额和账单的退款金额
                List<ClassifiedStatistic> classifieds = settlementDetailMapper.findCheckedMoney(list.get(i).get("finance_no") + "");
                if (null != classifieds && !classifieds.isEmpty()) {
                    classifieds.stream().forEach(item -> {
                        classifiedStatisticMapper.updateCheckedMoney(item);
                    });
                    Finances finances = new Finances();
                    finances.setFinance_no(list.get(i).get("finance_no") + "");
                    finances.setFinance_checking_total(classifieds.stream().mapToInt(l -> l.getFinance_checking_total()).sum());
//                finances.setRefund_total(classifieds.stream().mapToInt(l->l.getRefund_total()).sum());
                    finances.setRefund_checking_total(classifieds.stream().mapToInt(l -> l.getRefund_checking_total()).sum());
                    financesMapper.updateFinanceByNo(finances);
                }

        }

        return new PageInfo<>(list);
    }


    public PageInfo<?> querySettlementDetailMerchant(QueryStatement queryStatement) {
        PageHelper.startPage(queryStatement.getPageNum(), queryStatement.getPageSize());
        List<PageData> list = settlementDetailMapper.findSettlementDetail(queryStatement);
        return new PageInfo<>(list);
    }

    public PageInfo<?> querySettlementList(QueryOrderBill queryOrderBill) {
        PageHelper.startPage(queryOrderBill.getPageNum(), queryOrderBill.getPageSize());

        List<PageData> list = settlementDetailMapper.findSettlementList(queryOrderBill);

        return new PageInfo<>(list);
    }

    public PageInfo<?> querySettlementLog(QuerySettlementLog log) {
        PageHelper.startPage(log.getPageNum(), log.getPageSize());

        List<Map<String, Object>> list = settlementDetailMapper.findSettlementLog(log);
        // 这里根据账单号分别查出对账成功数量和金额，失败数量和金额
        /*list.stream().forEach(l->{
           settlementDetailMapper.findDetailByFinancesNo(l.get("finance_no"));
        });*/
        return new PageInfo<>(list);
    }

    @Autowired
    private PayDataImportMapper payDataImportMapper;

    public PageInfo<?> queryOrderSettleDetail(Map<String, Object> params) {
//        List<FinancialSettleDetail> list1 = settlementDetailMapper.queryOrderSettleDetail(params);
//        将 params 中的 financeNo 转化成 List 数据
        List<String> financesNos= StringUtil.convertToList(params.get("financeNo")+"");
        params.put("financesNos","".equals(params.get("financeNo")+"")||null==params.get("financeNo")+""?null:financesNos);    // 将 List 数据再放回 params 参数中
        Long total = settlementDetailMapper.queryTotalOrderSettleDetail(params);
        Page<Object> page = PageHelper.startPage((int) params.get("pageNum"), (int) params.get("pageSize"), false);//开启分页
//        page.setTotal(total);
        List<FinancialSettleDetail> list = settlementDetailMapper.queryOrderSettleDetail(params);
        Map<String,Object> stastic = settlementDetailMapper.queryOrderSettleDetailStastic(params);  // //统计订单对账情况的支付汇总
        Map<String,Object> stastic1 = settlementDetailMapper.queryImportSettleDataStastic(params);  // //统计订单对账情况的导入支付金额汇总
        Map<String,Object> stastic2 = settlementDetailMapper.queryImportDataStastic(params);  // //统计订单对账情况的导入退款金额汇总
        ((Page) list).setTotal(total);

        if (null!=list&&!list.isEmpty()){
            list.get(0).setRealPaySum(null==stastic?0.00:Double.parseDouble(stastic.get("real_pay")+"")); //订单对账表订单金额的汇总
        }
        if (stastic!=null&&stastic.get("platform_payment_amount")!=null){
            list.get(0).setIncomeSum(Double.parseDouble(stastic.get("platform_payment_amount")+"")); //订单对账表支付平台付款金额的汇总
        }
        if(stastic!=null&&stastic.get("platform_refund_fee")!=null){
            list.get(0).setSpendeSum(Double.parseDouble(stastic.get("platform_refund_fee")+"")); //退款对账表支付平台退款金额的汇总
        }

        /*DoubleSummaryStatistics doubleSummaryStatistics = list1.parallelStream().mapToDouble(FinancialSettleDetail ->
            Double.valueOf(FinancialSettleDetail.getReal_pay())).summaryStatistics();
        if (list.size()>0){
            list.get(0).setSummaryStatistics(doubleSummaryStatistics.getSum());
        }
        DoubleSummaryStatistics intSummaryStatisticss = list1.parallelStream().mapToDouble(FinancialSettleDetail ->
            Double.valueOf(FinancialSettleDetail.getRefund_fee())).summaryStatistics();
        if (list.size()>0){
            list.get(0).setSummaryStatisticss(intSummaryStatisticss.getSum());
        }*/
        /*list1.stream().forEach(x ->{
            List<PayDataImport> importList = payDataImportMapper.selectPayDataByTradesId(x.getTrades_id()+"");
            Double sum =0.00;
            for(int i =0;importList.size()>i;i++){
                sum +=Double.valueOf(importList.get(i).getIncome_amount()+"");
            }
            x.setIncomeSum(sum);
        });
        DoubleSummaryStatistics doubleSummaryStatistics1 = list1.parallelStream().filter().mapToDouble(item -> Double.valueOf(item.getIncomeSum())).summaryStatistics();
        if (list.size()>0){
            list.get(0).setIncomeSum(doubleSummaryStatistics1.getSum());
        }*/

        list.stream().forEach(l -> {
            //查询退款表退款金额
            Refund refund = refundMapper.getRefundByTradeId(Integer.parseInt(l.getSite_id()), l.getTrades_id());
            if (refund != null) {
                switch (refund.getStatus()) {
                    case 100:
                        l.setRefund_status("申请退款");
                        break;
                    case 110:
                        l.setRefund_status("拒绝退款");
                        break;
                    case 120:
                        l.setRefund_status("退款成功");
                        break;
                    default:
                        l.setRefund_status("" + refund.getStatus());
                }
                try {
                    l.setPlatform_refund_time(DateUtils.formatDate(refund.getRefundTime(), "yyyy-MM-dd,HH:mm:ss"));
                    l.setAccount_checking_status(refund.getAccountCheckingStatus());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

                List<PayDataImport> importList = payDataImportMapper.selectPayDataByTradesId(l.getTrades_id()+"");
                for (PayDataImport payDataImport : importList) {
                    l.setTrades_ids(payDataImport.getTrades_id());
                    if (payDataImport.getData_type() == 0) {
                        l.setPay_times(DateUtils.formatDate(payDataImport.getPay_time(), "yyyy-MM-dd,HH:mm:ss"));
                        l.setPay_timess(DateUtils.formatDate(payDataImport.getPay_time(), "yyyy-MM-dd,HH:mm:ss"));
                        try {
                            l.setIncome_amount(String.valueOf(Integer.parseInt(payDataImport.getIncome_amount())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        l.setBusiness(payDataImport.getBusiness());
                    }
                    if (payDataImport.getData_type() == 1) {
                        l.setPay_times(DateUtils.formatDate(payDataImport.getPay_time(), "yyyy-MM-dd,HH:mm:ss"));
                        l.setPay_timess(DateUtils.formatDate(payDataImport.getPay_time(), "yyyy-MM-dd,HH:mm:ss"));
                        l.setIncome_amounts(String.valueOf(Integer.parseInt(payDataImport.getIncome_amount())));
                        l.setSpending_amount(String.valueOf(Integer.parseInt(payDataImport.getSpending_amount())));
                        l.setTrades_statuss(payDataImport.getTrades_status());
                        l.setBusiness(payDataImport.getBusiness());
                    }
                }
//                sum += importList.get()

       /*
        format(f.spending_amount / 100, 2) spending_amount,
,*/

        });
        return new PageInfo<>(list);
    }


    public PageInfo<?> queryPaySettleDetail(Map<String, Object> params) {
        PageHelper.startPage((int) params.get("pageNum"), (int) params.get("pageSize"));
        List<FinancialSettleDetail> list = settlementDetailMapper.queryOrderSettleDetail(params);
        return new PageInfo<>(list);
    }

    public PageInfo<?> queryRefundSettleDetail(Map<String, Object> params) {
        PageHelper.startPage((int) params.get("pageNum"), (int) params.get("pageSize"));
        List<FinancialSettleDetail> list = settlementDetailMapper.queryOrderSettleDetail(params);
        return new PageInfo<>(list);
    }


//    public List<Map<String, Object>> getSettlementListByObjByexport(AccountParams accountParams) throws Exception {
//        return settlementDetailAndTradesMapper.getSettlementListByObjByexport(JacksonUtils.json2map(JacksonUtils.obj2json(accountParams)));
//    }



//    public List<Map<String, Object>> queryOrderSettleDetailss(Map<String, Object> params) {
//        AccountParams accountParams = new AccountParams();
//        try {
//            accountParams = JacksonUtils.map2pojo(params, AccountParams.class);
//        } catch (Exception e) {
//            logger.error("导出结算明细" + e);
//        }
//        List<Map<FinancialSettleDetail, Object>> list = settlementDetailMapper.queryOrderSettleDetailForExport(accountParams);
//
//        list.stream().forEach(l -> {
//            //查询退款表退款金额
//                l.setPlatform_refund_time(DateUtils.formatDate(refund.getRefundTime(), "yyyy-MM-dd,HH:mm:ss"));
//                l.setAccount_checking_status(refund.getAccountCheckingStatus());
//                List<PayDataImport> importList = payDataImportMapper.selectPayDataByTradesId(l.getTrades_id());
//                for (PayDataImport payDataImport : importList) {
//                    l.setTrades_ids(payDataImport.getTrades_id());
//                    if (payDataImport.getData_type() == 0) {
//                        l.setPay_times(DateUtils.formatDate(payDataImport.getPay_time(), "yyyy-MM-dd,HH:mm:ss"));
//                        l.setPay_timess(DateUtils.formatDate(payDataImport.getPay_time(), "yyyy-MM-dd,HH:mm:ss"));
//                        try {
//                            l.setIncome_amount(String.valueOf(Integer.parseInt(payDataImport.getIncome_amount())));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        l.setBusiness(payDataImport.getBusiness());
//                    }
//                    if (payDataImport.getData_type() == 1) {
//                        l.setPay_times(DateUtils.formatDate(payDataImport.getPay_time(), "yyyy-MM-dd,HH:mm:ss"));
//                        l.setPay_timess(DateUtils.formatDate(payDataImport.getPay_time(), "yyyy-MM-dd,HH:mm:ss"));
//                        l.setIncome_amounts(String.valueOf(Integer.parseInt(payDataImport.getIncome_amount())));
//                        l.setSpending_amount(String.valueOf(Integer.parseInt(payDataImport.getSpending_amount())));
//                        l.setTrades_statuss(payDataImport.getTrades_status());
//                        l.setBusiness(payDataImport.getBusiness());
//                    }
//                }
//       /*
//        format(f.spending_amount / 100, 2) spending_amount,
//,*/
//
//        });
//        return list;
//    }


    private List<Map<String, Object>> getYingshouXiaoji(List<Map<String, Object>> paramMap) {
        if (null != paramMap && paramMap.size() > 0) {
            paramMap.stream().forEach(l -> {
                Map<String, Object> m = (Map<String, Object>) l;
                BigDecimal yingshouxiaoji = null;
                DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                dfs.setDecimalSeparator('.');
                dfs.setGroupingSeparator(',');
                dfs.setMonetaryDecimalSeparator('.');
                DecimalFormat df = new DecimalFormat("###,###.##", dfs);
                String lastRealPay = "---";
                Object trades_id = m.get("trades_id");
                Object daishoujine = m.get("real_payss");
                Object daituijine=m.get("refund_feess");
                Object daishoushouxufei=m.get("shoufei");
                Object jiaoyiyongjin=m.get("jiaoyiyongjin");
                Object daishoupeisongfei=m.get("daishoupeisongfei");
                Number parse = 0;
                Number parse1 = 0;
                Number parse2 = 0;
                Number parse3 = 0;
                Number parse4 = 0;
                try {
                    parse = df.parse(daishoujine.toString());
                    parse1 = df.parse(daituijine.toString());
                    parse2 = df.parse(daishoushouxufei.toString());
                    parse3 = df.parse(jiaoyiyongjin.toString());
                    parse4 = df.parse(daishoupeisongfei.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(null!=daishoujine&& StringUtils.equalsIgnoreCase((parse==null?0:parse).toString(),"---")){
                    lastRealPay="---";
                }
                if(null!=daituijine&&!StringUtils.equalsIgnoreCase((daituijine==null?0:daituijine).toString(),"---")){
                    yingshouxiaoji=new BigDecimal(parse.toString()).subtract(new BigDecimal((parse1==null?0:parse1).toString()));
                    lastRealPay=yingshouxiaoji.toString();
                }

                if(null!=daishoushouxufei&&!StringUtils.equalsIgnoreCase( (daishoushouxufei==null?0:daishoujine).toString(),"---")
                        &&!StringUtils.equalsIgnoreCase(lastRealPay.toString(),"---")){
                    yingshouxiaoji=new BigDecimal(lastRealPay.toString()).subtract(new BigDecimal((parse2==null?0:daishoushouxufei).toString()));
                    lastRealPay=yingshouxiaoji.toString();
                }

                if(null!=jiaoyiyongjin&&!StringUtils.equalsIgnoreCase((jiaoyiyongjin==null?0:jiaoyiyongjin).toString(),"---")
                        &&!StringUtils.equalsIgnoreCase(lastRealPay.toString(),"---")){
                    yingshouxiaoji=new BigDecimal(lastRealPay.toString()).subtract(new BigDecimal((parse3==null?0:jiaoyiyongjin).toString()));
                    lastRealPay=yingshouxiaoji.toString();
                }

                if(null!=daishoupeisongfei&&!StringUtils.equalsIgnoreCase((daishoupeisongfei==null?0:daishoupeisongfei).toString(),"---")
                        &&!StringUtils.equalsIgnoreCase(lastRealPay.toString(),"---")){
                    yingshouxiaoji=new BigDecimal(lastRealPay.toString()).subtract(new BigDecimal((parse4==null?0:daishoupeisongfei).toString()));
                    lastRealPay=yingshouxiaoji.toString();
                }
                m.put("trades_id", trades_id + "");
                m.put("lastrealpays", lastRealPay + "");

            });
        }
        return paramMap;
    }

    public List<Map<String, Object>> findSettlementListExcel(Map<String, Object> params) {

        QueryStatement queryStatement = new QueryStatement();
        try {
            queryStatement = JacksonUtils.map2pojo(params, QueryStatement.class);
        } catch (Exception e) {
            logger.error("商家导出账单出错，错误信息====>{}",e);
        }
        List<Map<String, Object>> list = settlementDetailMapper.findsSettlementListExcel(queryStatement);
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> data = financesMapper.findBeforeData(Integer.parseInt(list.get(i).get("seller_id") + ""), list.get(i).get("create_time") + "");
            if (data == null) {
                list.get(i).put("shangqijiezhuan", 0);
            } else {
                list.get(i).put("shangqijiezhuan", 0);
            }
        }
        return list;
    }

    @Transactional
    public void updateFinance(Finances finances) {
        financesMapper.updateFinanceByNo(finances);
        //已结清，部分结算,延期支付
        if (null != finances.getStatus() && finances.getStatus() > 100) {
            //修改b_trades的settlement_status
            List<Trades> trades = tradesMapper.selectTradesByFinanceNo(finances.getFinance_no());
            trades.stream().forEach(l -> {
                tradesMapper.updateSettlementStatusByTradesId(l.getTradesId().toString());
            });
        }
    }

    public List<Map<String, Object>> get_trades_list(Trades trades) {
        return tradesMapper.get_trades_list(trades);
    }

    /**
     * 获取小计
     *
     * @param realPay     应付金额
     * @param refund      退款
     * @param tradesSplit 佣金
     * @param platSplit   手续费
     * @param O2OFreight  配送费
     * @return
     */
    public String getSubtotal(Double realPay, Double refund, Double tradesSplit, Double platSplit, Double O2OFreight) {
        if (realPay == null) realPay = 0.00;
        if (refund == null) refund = 0.00;
        if (tradesSplit == null) tradesSplit = 0.00;
        if (platSplit == null) platSplit = 0.00;
        if (O2OFreight == null) O2OFreight = 0.00;

        BigDecimal subtotal = BigDecimal.valueOf(realPay).subtract(BigDecimal.valueOf(refund)).subtract(BigDecimal.valueOf(tradesSplit)).subtract(BigDecimal.valueOf(platSplit)).subtract(BigDecimal.valueOf(O2OFreight));
        return new DecimalFormat("#.00").format(subtotal);
    }


    public void AccountException() {
        List<Trades> tradesList = tradesMapper.selectTradesByMigrate();
        for (Trades trades: tradesList){
            if (trades.getSettlementType()==null){
                AccountCheckException accountCheckException = checkBuild(trades);
                accountCheckException.setError_code(AccountConstants.ERROR_TYPE);
                addAccountException(accountCheckException);
            }
            if ((trades.getIsPayment()==1)&&(trades.getPayStyle()==null))
            {
                AccountCheckException accountCheckException = checkBuild(trades);
                accountCheckException.setError_code(AccountConstants.ERROR_STYLE);
                addAccountException(accountCheckException);
            }
            if ((trades.getIsPayment()==0&&(trades.getTradesStatus()==140))||(trades.getIsPayment()==0&&(trades.getTradesStatus()==150))
                    ||(trades.getIsPayment()==0&&(trades.getTradesStatus()==210))||(trades.getIsPayment()==0&&(trades.getTradesStatus()==220))
                    ||(trades.getIsPayment()==0&&(trades.getTradesStatus()==230))||(trades.getIsPayment()==0&&(trades.getTradesStatus()==240))
                    ||(trades.getIsPayment()==0&&(trades.getTradesStatus()==800))||(trades.getIsPayment()==0&&(trades.getTradesStatus()==900))){
                AccountCheckException accountCheckException = checkBuild(trades);
                accountCheckException.setError_code(AccountConstants.ERROR_PAYMENT);
                addAccountException(accountCheckException);
            }
            if (trades.getPayStyle()!=null){
                if(((trades.getPayStyle().equals("cash"))&&(trades.getPlatSplit()!=0)
                        ||((trades.getPayStyle().equals("health_insurance"))&&(trades.getPlatSplit()!=0))
                        ||(trades.getPayStyle().equals("wx")&&trades.getRealPay()>50&&trades.getPlatSplit()==0)
                        ||(trades.getPayStyle().equals("ali")&&trades.getRealPay()>50&&trades.getPlatSplit()==0)
                        ||(trades.getPayStyle().equals("wx")&&trades.getRealPay()<50&&trades.getPlatSplit()!=0)
                        ||(trades.getPayStyle().equals("ali")&&trades.getRealPay()<50&&trades.getPlatSplit()!=0)
                )){
                    AccountCheckException accountCheckException = checkBuild(trades);
                    accountCheckException.setError_code(AccountConstants.ERROR_PLATSPLIT);
                    addAccountException(accountCheckException);
                }
            }
            if (trades.getPayStyle()!=null){
                if ((trades.getPayStyle().equals("wx")&&trades.getRealPay()>50&&trades.getTradesSplit()==0)
                        ||(trades.getPayStyle().equals("al")&&trades.getRealPay()>50&&trades.getTradesSplit()==0)
                        ||(trades.getPayStyle().equals("cash")&&trades.getRealPay()>50&&trades.getTradesSplit()==0)
                        ||(trades.getPayStyle().equals("health_insurance")&&trades.getRealPay()>50&&trades.getTradesSplit()==0)
                        ||(trades.getPayStyle().equals("wx")&&trades.getRealPay()<50&&trades.getTradesSplit()!=0)
                        ||(trades.getPayStyle().equals("al")&&trades.getRealPay()<50&&trades.getTradesSplit()!=0)
                        ||(trades.getPayStyle().equals("cash")&&trades.getRealPay()<50&&trades.getTradesSplit()!=0)
                        ||(trades.getPayStyle().equals("health_insurance")&&trades.getRealPay()<50&&trades.getTradesSplit()!=0)
                        ){
                    AccountCheckException accountCheckException = checkBuild(trades);
                    accountCheckException.setError_code(AccountConstants.ERROR_TRADESSPLIT);
                    addAccountException(accountCheckException);
                }
            }
            if ((trades.getO2OFreight()!=null&&trades.getO2OFreight()!=0)){
                if ((trades.getOrderNumber()!=null)&&(trades.getPeisongstatus()==5)){
                    AccountCheckException accountCheckException = checkBuild(trades);
                    accountCheckException.setError_code(AccountConstants.ERROR_FREIGHT);
                    addAccountException(accountCheckException);
                    if((trades.getOrderNumber()!=null&&trades.getO2OFreight()==0)){
                        accountCheckException.setError_code(AccountConstants.ERROR_FREIGHT);
                        addAccountException(accountCheckException);
                    }
                }
            }
            if ((trades.getDealFinishStatus()==1&&trades.getTradesStatus()==120)||(trades.getDealFinishStatus()==1&&trades.getTradesStatus()==130)
                    ||(trades.getDealFinishStatus()==1&&trades.getTradesStatus()==200)||(trades.getDealFinishStatus()==1&&trades.getTradesStatus()==220)
                    ){
                AccountCheckException accountCheckException = checkBuild(trades);
                accountCheckException.setError_code(AccountConstants.ERROR_TRADESSTATUS);
                addAccountException(accountCheckException);
            }
            if (trades.getConfirmGoodsTime()!=null&&trades.getEndTime()!=null){
                SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Timestamp time = Timestamp.valueOf(df.format(new Date(trades.getConfirmGoodsTime().getTime() + 3 * 24 * 60 * 60 * 1000)));
                if ((time.after(trades.getEndTime())&&trades.getDealFinishStatus()==1&&trades.getIsRefund()!=120)
                        ||(time.before(trades.getEndTime())&&trades.getDealFinishStatus()==0&&trades.getIsRefund()!=120)
                        ){
                    AccountCheckException accountCheckException = checkBuild(trades);
                    accountCheckException.setError_code(AccountConstants.ERROR_DEALSTATUS);
                    addAccountException(accountCheckException);
                }
            }
        }

//        List<Trades> settlementTypeIsNullList = tradesList.parallelStream()
//                .filter(trades -> {
//                    if (trades.getSettlementType() ==null) {
//                        return false;
//                    } else {
//                        return true;
//                    }
//                }).collect(Collectors.toList());
//        settlementTypeIsNullList.stream().forEach(trades -> {
//            AccountCheckException accountCheckException = checkBuild(trades);
//            accountCheckException.setError_code(AccountConstants.ERROR_TYPE);
//            addAccountException(accountCheckException);
//        });
    }

    public static AccountCheckException checkBuild(Trades trades) {
        AccountCheckException accountCheckException = new AccountCheckException();
        accountCheckException.setSite_id(trades.getSiteId());
        accountCheckException.setTrades_id(trades.getTradesId());
        accountCheckException.setReal_pay(trades.getRealPay());
        accountCheckException.setPay_style(trades.getPayStyle());
        accountCheckException.setSettlement_type(trades.getSettlementType());
        accountCheckException.setIs_payment(trades.getIsPayment());
        accountCheckException.setOrder_time(trades.getCreateTime());
        accountCheckException.setConsign_time(trades.getConfirmGoodsTime());
        accountCheckException.setPay_time(trades.getPayTime());
        accountCheckException.setEnd_time(trades.getEndTime());
        accountCheckException.setRefund_fee(trades.getRefundFee());
        accountCheckException.setPlat_split(trades.getPlatSplit());
        accountCheckException.setTrades_split(trades.getTradesSplit());
        accountCheckException.setO2O_freight(trades.getO2OFreight());
        accountCheckException.setTrades_status(trades.getTradesStatus());
        accountCheckException.setDeal_finish_status(trades.getDealFinishStatus());
        return accountCheckException;
    }

    private int addAccountException(AccountCheckException accountcheckException) {
        int i = 0;
        try {
            i = settlementDetailMapper.addAccountException(accountcheckException);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * 导出报表
     * @param params
     * @return
     */
    public List<Map<String, Object>> queryOrderSettleDetailByobjs(Map<String, Object> params) {

        String finances=(params.get("financeNo")+"").replaceAll("，",",").replaceAll("；",",").replaceAll(";",",").replaceAll(" ","").replaceAll("\n","");
        List<String> financesNos= Arrays.stream(finances.split(",")).collect(Collectors.toList());
        params.put("financesNos","".equals(finances)||null==finances?null:financesNos);    // 将 List 数据再放回 params 参数中
        List<Map<String, Object>> queryOrderSettleDetailByobj = settlementDetailMapper.queryOrderSettleDetailByobj(params);

       /* queryOrderSettleDetailByobj.stream().forEach(l -> {
            List<PayDataImport> importList = payDataImportMapper.selectPayDataByTradesId(l.get("trades_id")+"");
            for (PayDataImport payDataImport : importList) {
                l.put("trades_id",payDataImport.getTrades_id());

                if (payDataImport.getData_type() == 0) {
                    l.put("pay_times",DateUtils.formatDate(payDataImport.getPay_time(), "yyyy-MM-dd,HH:mm:ss"));
                    try {
                        float v = Float.valueOf(payDataImport.getIncome_amount()) / 100;
                        BigDecimal   b  =   new BigDecimal(v);
                        float   f1   =  b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                        l.put("income_amount",f1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    l.put("trades_statuss",payDataImport.getBusiness());
                }
                if (payDataImport.getData_type() == 1) {
                    l.put("refund_times",DateUtils.formatDate(payDataImport.getPay_time(), "yyyy-MM-dd,HH:mm:ss"));
                    float v = Float.valueOf(payDataImport.getIncome_amount()) / 100;
                    BigDecimal   b  =   new BigDecimal(v);
                    float   f1   =  b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    l.put("income_amount",f1);
                    float v1 = Float.valueOf(payDataImport.getSpending_amount()) / 100;
                    BigDecimal   b1  =   new BigDecimal(v1);
                    float   f2   =  b1.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    l.put("spending_amount",f2);
                    l.put("refund_statuss",payDataImport.getTrades_status());
                }
            }


       *//*
        format(f.spending_amount / 100, 2) spending_amount,
,*//*

        });*/
        return queryOrderSettleDetailByobj;
    }

    /**
     * 划账汇总表导出
     * @param params
     * @return
     */
    public List<Map<String,Object>> exportAccountRemitList(Map params){
        List<Map<String, Object>> list = settlementDetailAndTradesMapper.exportAccountRemitList(params); //划账汇总sql语句
        return list;
    }

}

