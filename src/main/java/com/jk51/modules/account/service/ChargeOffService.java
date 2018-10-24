package com.jk51.modules.account.service;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.interceptor.LoginInterceptor;
import com.jk51.model.account.models.*;
import com.jk51.model.account.models.Finances;
import com.jk51.model.account.models.SettlementDetailAndTrades;
import com.jk51.model.order.Trades;
import com.jk51.model.order.TradesFinanceLog;
import com.jk51.modules.account.constants.AccountConstants;
import com.jk51.modules.account.mapper.*;
import com.jk51.modules.trades.mapper.RefundMapper;
import com.jk51.modules.trades.mapper.TradesFinanceLogMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * filename :com.jk51.modules.account.goodsService.
 * author   :zw
 * date     :2017/2/21
 * Update   :
 */

@Service
public class ChargeOffService {
    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
    @Autowired
    private SettlementDetailAndTradesMapper settlementDetailAndTradesMapper;
    @Autowired
    private ClassifiedStatisticMapper classifiedStatisticMapper;
    @Autowired
    private FinancesMapper financesMapper;
    @Autowired
    private SettlementDetailMapper settlementDetailMapper;
    @Autowired
    private SettlementDetailLogService settlementDetailLogService;
    @Autowired
    private FinanceAuditLogService financeAuditLogService;
    @Autowired
    private SettlementdayConfigMapper settlementdayConfigMapper;

    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    private RefundMapper refundMapper;
    @Autowired
    private TradesFinanceLogMapper tradesFinanceLogMapper;

    /**系统出账
     * @param seller_id
     * @param operator
     * @return
     */
    public ReturnDto classifiedAccount(Integer seller_id,String operator) {
        logger.info("系统开始出账，商家id===>{},操作人===>{}",seller_id,operator);
        try {
            SettlementdayConfig config = settlementdayConfigMapper.selectSettlementDay(seller_id);
            logger.info("开始出账单，商家id====>{},开始时间====>{},结束时间====>{}", seller_id, config.getStart_date(),
                config.getEnd_date());
//            config.setEnd_date(Timestamp.valueOf("2017-11-29 23:59:59"));//测试设置，用完去掉

            //老结算
            if (config.getFinance_type()==0){
                    List<SettlementDetailAndTrades> sdat = settlementDetailAndTradesMapper.getSettlementListByTradesId(
                    seller_id, config.getStart_date(), config.getEnd_date())
                    .stream().filter(pdi -> {
                        return Arrays.asList(AccountConstants.FILTER_PAY_TYPE).contains(pdi.getPay_style());
                    }).collect(Collectors.toList());
                if (!sdat.isEmpty() && null != sdat) {
                    sdat.get(0).setStart_date(config.getStart_date());
                    sdat.get(0).setEnd_date(config.getEnd_date());
                    processClassifiedAccount(sdat, seller_id,operator);
                } else {
                    logger.error("error message:商家{},暂无需要结算的记录", seller_id);
                    return ReturnDto.buildFailedReturnDto("error message:商家" + seller_id + ",暂无需要结算的记录");
                }

            }else{
                //新结算
                logger.info("新的结算类型开始！商家id====>{},结算类型====>{}",config.getSite_id(),config.getFinance_type());
                List<SettlementDetailAndTrades> sdat = settlementDetailAndTradesMapper.getTradesForFinances(
                    seller_id, config.getStart_date(), config.getEnd_date())
                    .stream().filter(pdi -> {
                        return Arrays.asList(AccountConstants.FILTER_PAY_TYPE).contains(pdi.getPay_style());
                    }).collect(Collectors.toList());
                /*Map<String,Re fund> refunds=refundMapper.getRefunds(seller_id,config.getStart_date(),
                                            config.getEnd_date()).stream().collect(Collectors.toMap(Refund::getTradeId, (o)->o,(k1,k2)->k1));*/
                //查询结算周期外所有未结算订单
                List<SettlementDetailAndTrades> noSettlementTrades = settlementDetailAndTradesMapper.getNoSettlementTrades(seller_id, config.getStart_date())
                    .stream().filter(pdi -> { return Arrays.asList(AccountConstants.FILTER_PAY_TYPE).contains(pdi.getPay_style());
                                            }).filter(item->item.getReal_pay()>0||item.getO2O_freight()>0).collect(Collectors.toList());
                logger.info("{}结算之前未结算订单数量====>{}",config.getStart_date(),noSettlementTrades.size());
                if (null!=noSettlementTrades&&noSettlementTrades.size()>0){
                    sdat.addAll(noSettlementTrades);
                }

                List<SettlementDetailAndTrades> refunds=refundMapper.getRefunds(seller_id,config.getStart_date(),
                    config.getEnd_date());


                List<SettlementDetailAndTrades> unionRefunds = refunds.stream().filter(r->{//获取上次没有结算付款和退款的订单（存在,订单退款在此周期，付款不在此周期，而且付款为结算过，所以一起结算）
                    return sdat.stream().filter(t->t.getTrades_id()==r.getTrades_id()).count()<=0;
                }).collect(Collectors.toList());
                if (null!=unionRefunds&&unionRefunds.size()>0){
                    sdat.addAll(unionRefunds);
                }

                List<SettlementDetailAndTrades> postList=settlementDetailAndTradesMapper.getNoSettlementPostfee(seller_id);//获取所有没有结算配送费的订单
                List<SettlementDetailAndTrades> postfees = postList.stream().filter(p->{//获取上次没有结算配送费的订单
                    return sdat.stream().filter(t->(t.getTrades_id()+"").equals(p.getTrades_id())).count()<=0&&p.getO2O_freight()>0;
                }).collect(Collectors.toList());

                if (null!=postfees&&postfees.size()>0){
                    sdat.addAll(postfees);//把上次没结算的配送订单  添加到结算集合
                }

                if (!sdat.isEmpty() && null != sdat) {
                    sdat.get(0).setStart_date(config.getStart_date());
                    sdat.get(0).setEnd_date(config.getEnd_date());
                    newProcessClassified(sdat, seller_id,operator);
                } else {
                    logger.error("error message:商家{},暂无需要结算的记录", seller_id);
                    return ReturnDto.buildFailedReturnDto("error message:商家" + seller_id + ",暂无需要结算的记录");
                }


            }

        } catch (Exception e) {
            logger.error("出账单发生错误：{}", e);
            return ReturnDto.buildFailedReturnDto("error message:" + e);
        }
        return ReturnDto.buildSuccessReturnDto("商家站点：" + seller_id + "，汇总统计成功");
    }

    /**
     * 手动出账
     * @param seller_id
     * @param endtTime
     * @param operator
     * @return
     */
    public ReturnDto classifiedAccount(Integer seller_id,Timestamp endtTime,String operator) {
        logger.info("人工开始出账，商家id===>{},操作人===>{}",seller_id,operator);
        try {
            SettlementdayConfig config = settlementdayConfigMapper.selectSettlementDay(seller_id);
            logger.info("开始出账单，商家id====>{},开始时间====>{},结束时间====>{}", seller_id, config.getStart_date(), endtTime);

            List<SettlementDetailAndTrades> sdat = settlementDetailAndTradesMapper.getSettlementListByTradesId(
                seller_id, config.getStart_date(), endtTime)
                .stream().filter(pdi -> {
                    return Arrays.asList(AccountConstants.FILTER_PAY_TYPE).contains(pdi.getPay_style());
                }).collect(Collectors.toList());

            if (!sdat.isEmpty() && null != sdat) {
                sdat.get(0).setStart_date(config.getStart_date());
                sdat.get(0).setEnd_date(endtTime);
                processClassifiedAccount(sdat, seller_id,operator);
            } else {
                logger.info("商家:{},暂无需要结算的记录", seller_id);
                return ReturnDto.buildSuccessReturnDto("商家:" + seller_id + ",暂无需要结算的记录!");
            }

        } catch (Exception e) {
            logger.error("出账单发生错误：{}", e);
            return ReturnDto.buildFailedReturnDto("商家:" +seller_id+"出账异常！");
        }

        return ReturnDto.buildSuccessReturnDto("商家站点：" + seller_id + "，出账成功！");
    }



  /*  public int  updateStatus(){
        List<SettlementDetail> list = settlementDetailMapper.selectRefunCheckStatus();
        int i=0;
        for (SettlementDetail settlementDetail:list){
           i= settlementDetailMapper.updateRefunCheckStatus(settlementDetail.getTrades_id());
        }

        return i;
    }*/


    /**
     * LocalDate时间格式转成date
     *
     * @param localDate
     * @return
     */
    private Date convertDate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant start = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(start);
    }

    private void processClassifiedAccount(List<SettlementDetailAndTrades> sdat, Integer seller_id,String operator) {

        Map<String, ClassifiedStatistic> sortMap = new HashMap<String, ClassifiedStatistic>();
        Date start_time = sdat.get(0).getStart_date();
        java.util.Date pay_data = convertDate(LocalDate.now()); //结算日
        java.util.Date end_time = sdat.get(0).getEnd_date();
        String collect_no = createCollectNo(seller_id, start_time, end_time);
        // 判断开始时间和结束时间是否存在，存在则return
        Finances finances = null;
        try {
            finances = financesMapper.getStartDayAndEndDay(seller_id, start_time, end_time);
        } catch (Exception e) {
            logger.error("processClassifiedAccount 报错：参数：seller_id：{}，start_time：{}，end_time：{}，报错信息：{}",seller_id,start_time,end_time,ExceptionUtil.exceptionDetail(e));
        }
        if (finances != null) {
            logger.error("站点:" + seller_id + ",此此商家已出账");
            return;
        }

        sortMap.put("wx", sumClassifiedAccount(sdat, "wx", seller_id, start_time, end_time, collect_no));
        sortMap.put("ali", sumClassifiedAccount(sdat, "ali", seller_id, start_time, end_time, collect_no));
        sortMap.put("health_insurance", sumClassifiedAccount(sdat, "health_insurance",
            seller_id, start_time, end_time, collect_no));
        sortMap.put("cash", sumClassifiedAccount(sdat, "cash", seller_id, start_time, end_time, collect_no));

        Integer needPay = 0;
        for (String in : sortMap.keySet()) {
            ClassifiedStatistic str = sortMap.get(in);
            if (str != null) {
                classifiedStatisticMapper.addClassifiedStatistic(str);
            }
        }


        processFinances(sdat, seller_id, start_time, end_time, pay_data, collect_no,operator);
    }

    /**
     * 生成出账总记录
     *
     * @param sdat
     * @param seller_id
     * @param start_time
     * @param end_time
     */
    @Transactional
    private void processFinances(List<SettlementDetailAndTrades> sdat, Integer seller_id,
                                 Date start_time, Date end_time, Date pay_date, String collect_no,String operator) {
        int is_charge_off = 1;//出账异常
        try {
            List<SettlementDetailAndTrades> sdatList = sdat.stream().filter(pdi -> pdi.getAccount_checking_status() != 1 || pdi.getAccount_checking_status() == null ||
                (pdi.getRefund_checking_status() != 1 && pdi.getRefund_fee() > 0)).collect(Collectors.toList());
            if (sdatList.isEmpty() || null == sdatList) {
                is_charge_off = 0;//出账正常
            }
        } catch (Exception e) {
            is_charge_off = 1;
            logger.error("processFinances 报错：参数：sdat：{}，seller_id：{}，报错信息：{}",sdat,seller_id,ExceptionUtil.exceptionDetail(e));
        }

        Finances finances = new Finances();
        finances.setFinance_no(collect_no);
        finances.setSeller_id(seller_id);
        finances.setSettlement_start_date(start_time); //获取结算开始时间
        finances.setSettlement_end_date(end_time);
        finances.setPay_day(pay_date);
        finances.setTotal_pay(sdat.stream().filter(a -> !"health_insurance".equals(a.getPay_style()) && !"cash".equals(a.getPay_style())).collect(Collectors.toList()).stream().mapToInt(c -> c.getReal_pay()).sum());
        finances.setFinance_checking_total(sdat.stream().mapToInt(c -> c.getPlatform_payment_amount()).sum());
        finances.setPlatform_total(sdat.stream().mapToInt(c -> c.getPlat_split()).sum());
        finances.setCommission_total(sdat.stream().mapToInt(c -> c.getTrades_split()).sum());
        finances.setRefund_total(sdat.stream().filter(a -> !"health_insurance".equals(a.getPay_style()) && !"cash".equals(a.getPay_style())).collect(Collectors.toList()).stream().mapToInt(c -> c.getRefund_fee()).sum());
        finances.setRefund_checking_total(sdat.stream().mapToInt(c -> c.getPlatform_refund_fee()).sum());
        finances.setPost_total(sdat.stream().filter(c -> c.getO2O_freight() != null).mapToInt(c -> c.getO2O_freight()).sum());
        finances.setIs_charge_off(is_charge_off);

        finances.setNeed_pay(finances.getTotal_pay() - ((finances.getPlatform_total() == null ? 0 : finances.getPlatform_total()) + (finances.getCommission_total() == null ? 0 : finances.getCommission_total())
            + (finances.getPost_total() == null ? 0 : finances.getPost_total()) + (finances.getRefund_total() == null ? 0 : finances.getRefund_total())));//这里未加上期结转，上期结转页面去算
        finances.setReal_pay(0);
        finances.setInvoice_value(((finances.getPlatform_total() == null ? 0 : finances.getPlatform_total()) +
            (finances.getCommission_total() == null ? 0 : finances.getCommission_total())
            + (finances.getPost_total() == null ? 0 : finances.getPost_total())));//发票金额
        int finance_id = financesMapper.addFinances(finances);
        if (finance_id > 0) {
            updateSettlementDetailStatus(sdat, collect_no);

            sdat.stream().forEach(l -> {//更新账单编号和已结算状态
               /* //插入结算数据日志
                TradesFinanceLog tradesLog=new TradesFinanceLog();//日志
                tradesLog.setFinanceNo(collect_no);
                tradesLog.setFinanceNoRefund(collect_no);
                logBuild(l,tradesLog,0);//封装日志信息
                tradesFinanceLogMapper.insertSelective(tradesLog);//添加日志
                tradesMapper.updateFinancesNoByTradesId(collect_no, l.getTrades_id() + "");*/

               Trades trades=new Trades();
                TradesFinanceLog tradesLog=new TradesFinanceLog();//日志
                trades.setTradesId(l.getTrades_id());
                trades.setSettlementStatus(250);
                if(null==l.getFinance_no()||"".equals(l.getFinance_no())){
                    trades.setFinanceNo(collect_no);
                    tradesLog.setFinanceNo(collect_no);
                }else{
                    trades.setFinanceNo(l.getFinance_no());
                    tradesLog.setFinanceNo(l.getFinance_no());
                }
                if (l.getRefund_fee()>0||l.getO2O_freight()>0||l.getDeal_finish_status()==1){
                    trades.setFinanceNoRefund(collect_no);
                    tradesLog.setFinanceNoRefund(collect_no);
                }else{
                    trades.setFinanceNoRefund(l.getFinance_no());
                    tradesLog.setFinanceNoRefund(l.getFinance_no());
                }

                tradesMapper.updateTradesForFinances(trades);

                logBuild(l,tradesLog,0);//封装日志信息
                tradesFinanceLogMapper.insertSelective(tradesLog);//添加日志
            });
            List<SettlementDetail> list = settlementDetailMapper.findSettlementDetailsByFinancesId(collect_no);
            if (!list.isEmpty() && null != list) {
                list.stream().forEach(l -> {
                    //修改订单为已结
                    tradesMapper.updateSettlementStatusByTradesId(l.getTrades_id() + "");
                });
            }
            //更新最后结算日
            SettlementdayConfig config = new SettlementdayConfig();
            config.setSite_id(seller_id);
            config.setThelast_time(sdat.get(0).getEnd_date());//获取最后结算日期
            settlementdayConfigMapper.updateConfig(config);
            //添加日志
            financeAuditLogService.addFinanceAuditLog(0, operator, collect_no, "", finance_id);
        }
    }

    private TradesFinanceLog logBuild(SettlementDetailAndTrades l, TradesFinanceLog tradesLog, Integer financeType) {
        tradesLog.setSiteId(l.getSeller_id());
        tradesLog.setTradesId(l.getTrades_id()+"");
        tradesLog.setEndTime(l.getEnd_time());
        tradesLog.setPayTime(l.getPay_time());
        tradesLog.setSettlementType(l.getSettlement_type());
        tradesLog.setTradesStatus(l.getTrades_status());
        tradesLog.setIsPayment(l.getIs_payment());

        tradesLog.setCreateTime(l.getCreate_time());
        tradesLog.setDealFinishStatus(l.getDeal_finish_status());
        tradesLog.setEndTime(l.getEnd_time());
        tradesLog.setFinanceType(financeType);
        tradesLog.setIsRefund(l.getIs_refund());
        tradesLog.setPayStyle(l.getPay_style());
        tradesLog.setRealPay(l.getReal_pay());
        tradesLog.setRefundFee(l.getRefund_fee());
        tradesLog.setTradesSplit(l.getTrades_split());
        tradesLog.setPlatSplit(l.getPlat_split());
        tradesLog.setO2oFreight(l.getO2O_freight());
//        tradesLog.setUpdateTime();
        return tradesLog;
    }

    /**
     * 实时结算账单汇总
     * @param sdat
     * @param seller_id
     * @param operator
     */
    private void newProcessClassified(List<SettlementDetailAndTrades> sdat, Integer seller_id,String operator) {

        Map<String, ClassifiedStatistic> sortMap = new HashMap<String, ClassifiedStatistic>();
        Date start_time = sdat.get(0).getStart_date();
        java.util.Date pay_data = convertDate(LocalDate.now()); //结算日
        java.util.Date end_time = sdat.get(0).getEnd_date();
        String collect_no = createCollectNo(seller_id, start_time, end_time);
        logger.info("账单编号是======>{}",collect_no);
        // 判断开始时间和结束时间是否存在，存在则return
        Finances finances = null;
        try {
            finances = financesMapper.getStartDayAndEndDay(seller_id, start_time, end_time);
        } catch (Exception e) {
            logger.error("newProcessClassified 报错：参数：seller_id：{}，start_time：{}，end_time：{}，报错信息：{}",seller_id,start_time,end_time,ExceptionUtil.exceptionDetail(e));
        }
        if (finances != null) {
            logger.error("站点:" + seller_id + ",此此商家已出账");
            return;
        }

        sortMap.put("wx", sumClassifiedAccount(sdat, "wx", seller_id, start_time, end_time, collect_no));
        sortMap.put("ali", sumClassifiedAccount(sdat, "ali", seller_id, start_time, end_time, collect_no));
        sortMap.put("health_insurance", sumClassifiedAccount(sdat, "health_insurance",
            seller_id, start_time, end_time, collect_no));
        sortMap.put("cash", sumClassifiedAccount(sdat, "cash", seller_id, start_time, end_time, collect_no));

        Integer needPay = 0;
        for (String in : sortMap.keySet()) {
            ClassifiedStatistic str = sortMap.get(in);
            if (str != null) {
                classifiedStatisticMapper.addClassifiedStatistic(str);
            }
        }


        newProcessFinances(sdat, seller_id, start_time, end_time, pay_data, collect_no,operator);
    }

    /**
     * 新结算
     * @param sdat
     * @param seller_id
     * @param start_time
     * @param end_time
     * @param pay_date
     * @param collect_no
     * @param operator
     */
    @Transactional
    private void newProcessFinances(List<SettlementDetailAndTrades> sdat, Integer seller_id,
                                 Date start_time, Date end_time, Date pay_date, String collect_no,String operator) {
        int is_charge_off = 1;//出账异常
        try {
            List<SettlementDetailAndTrades> sdatList = sdat.stream().filter(pdi -> pdi.getAccount_checking_status() != 1 || pdi.getAccount_checking_status() == null ||
                (pdi.getRefund_checking_status() != 1 && pdi.getRefund_fee() > 0)).collect(Collectors.toList());
            if (sdatList.isEmpty() || null == sdatList) {
                is_charge_off = 0;//出账正常
            }
        } catch (Exception e) {
            is_charge_off = 1;
            logger.error("newProcessFinances 报错：参数：sdat：{}，seller_id：{}，start_time：{}，end_time {}，pay_date：{}，collect_no：{}，operator：{}，报错信息：{}",sdat,seller_id,start_time,end_time,pay_date,collect_no,operator,ExceptionUtil.exceptionDetail(e));
        }

        Finances finances = new Finances();
        finances.setFinance_no(collect_no);
        finances.setSeller_id(seller_id);
        finances.setSettlement_start_date(start_time); //获取结算开始时间
        finances.setSettlement_end_date(end_time);
        finances.setPay_day(pay_date);
        finances.setTotal_pay(sdat.stream().filter(a -> !"health_insurance".equals(a.getPay_style()) && !"cash".equals(a.getPay_style())).collect(Collectors.toList()).stream().mapToInt(c -> c.getReal_pay()).sum());
        finances.setFinance_checking_total(sdat.stream().mapToInt(c -> c.getPlatform_payment_amount()).sum());
        finances.setPlatform_total(sdat.stream().mapToInt(c -> c.getPlat_split()).sum());
        finances.setCommission_total(sdat.stream().mapToInt(c -> c.getTrades_split()).sum());
        finances.setRefund_total(sdat.stream().filter(a -> !"health_insurance".equals(a.getPay_style()) && !"cash".equals(a.getPay_style())).collect(Collectors.toList()).stream().mapToInt(c -> c.getRefund_fee()).sum());
        finances.setRefund_checking_total(sdat.stream().mapToInt(c -> c.getPlatform_refund_fee()).sum());
        finances.setPost_total(sdat.stream().filter(c -> c.getO2O_freight() != null).mapToInt(c -> c.getO2O_freight()).sum());
        finances.setIs_charge_off(is_charge_off);

        finances.setNeed_pay(finances.getTotal_pay() - ((finances.getPlatform_total() == null ? 0 : finances.getPlatform_total()) + (finances.getCommission_total() == null ? 0 : finances.getCommission_total())
            + (finances.getPost_total() == null ? 0 : finances.getPost_total()) + (finances.getRefund_total() == null ? 0 : finances.getRefund_total())));//这里未加上期结转，上期结转页面去算
        finances.setReal_pay(0);
        finances.setInvoice_value(((finances.getPlatform_total() == null ? 0 : finances.getPlatform_total()) +
            (finances.getCommission_total() == null ? 0 : finances.getCommission_total())
            + (finances.getPost_total() == null ? 0 : finances.getPost_total())));//发票金额
        int finance_id = financesMapper.addFinances(finances);
        if (finance_id > 0) {
            updateSettlementDetailStatus(sdat, collect_no);

            sdat.stream().forEach(l -> {
                TradesFinanceLog tradesLog=new TradesFinanceLog();//日志
                // 优化（可以直接写更新操作）
                Trades trades=new Trades();
                trades.setTradesId(l.getTrades_id());
                if (("".equals(l.getFinance_no())||null==l.getFinance_no())){//无退款类型的订单，更新付款账单号
                    trades.setFinanceNo(collect_no);
                    tradesLog.setFinanceNo(collect_no);
                }else{
                    trades.setFinanceNo(l.getFinance_no());
                    tradesLog.setFinanceNo(l.getFinance_no());
                }

                //如果是退款订单,订单结束的，则更新FinanceNoRefund字段，没结束的订单，什么时候结束什么时候算
                if ((null!=l.getIs_refund()&&l.getIs_refund()==120)||l.getDeal_finish_status()==1){
                    trades.setFinanceNoRefund(collect_no);
                    trades.setSettlementStatus(250);
                    tradesLog.setFinanceNoRefund(collect_no);
                }
                tradesMapper.updateTradesForFinances(trades);

                logBuild(l,tradesLog,1);//封装日志信息
                tradesFinanceLogMapper.insertSelective(tradesLog);//添加日志

            });
            /*List<SettlementDetail> list = settlementDetailMapper.findSettlementDetailsByFinancesId(collect_no);
            if (!list.isEmpty() && null != list) {
                list.stream().forEach(l -> {
                    //修改订单为已结
                    tradesMapper.updateSettlementStatusByTradesId(l.getTrades_id() + "");
                });
            }*/
            //更新最后结算日
            SettlementdayConfig config = new SettlementdayConfig();
            config.setSite_id(seller_id);
            config.setThelast_time(sdat.get(0).getEnd_date());//获取最后结算日期
            settlementdayConfigMapper.updateConfig(config);
            //添加日志
            financeAuditLogService.addFinanceAuditLog(0, operator, collect_no, "", finance_id);
        }
    }

    private String createCollectNo(Integer seller_id, Date start_time, Date end_time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String str = sdf.format(start_time);
        SimpleDateFormat sdf1 = new SimpleDateFormat("MMdd");
        String str1 = sdf1.format(end_time);
        int random_number = (int) (Math.random() * 900) + 100;
        return seller_id + str + str1 + random_number;
    }

    /**
     * 生成分类统计数据
     *
     * @param sdat
     * @param pay_style
     * @param seller_id
     * @param start_time
     * @param end_time
     * @return
     */
    private ClassifiedStatistic sumClassifiedAccount(List<SettlementDetailAndTrades> sdat, String pay_style,
                                                     Integer seller_id, Date start_time, Date end_time, String collect_no) {
        ClassifiedStatistic classifiedStatistic = new ClassifiedStatistic();
        classifiedStatistic.setSeller_id(seller_id);
        classifiedStatistic.setSettlement_start_date(start_time);
        classifiedStatistic.setSettlement_end_date(end_time);
        classifiedStatistic.setFinance_no(collect_no);
        classifiedStatistic.setPay_style(pay_style);
        try {
            classifiedStatistic.setTotal_pay(sdat.stream().filter(c -> c.getPay_style().equals(pay_style))
                .mapToInt(c -> c.getReal_pay()).sum());

            classifiedStatistic.setFinance_checking_total(sdat.stream().filter(c -> c.getPay_style().equals(pay_style))
                .mapToInt(c -> c.getPlatform_payment_amount()).sum());

            classifiedStatistic.setPlatform_total(sdat.stream().filter(c -> c.getPay_style().equals(pay_style))
                .mapToInt(c -> c.getPlat_split()).sum());

            classifiedStatistic.setCommission_total(sdat.stream().filter(c -> c.getPay_style().equals(pay_style))
                .mapToInt(c -> c.getTrades_split()).sum());

            classifiedStatistic.setRefund_total(sdat.stream().filter(c -> c.getPay_style().equals(pay_style))
                .mapToInt(c -> c.getRefund_fee()).sum());


            classifiedStatistic.setRefund_checking_total(sdat.stream().filter(c -> c.getPay_style().equals(pay_style))
                .mapToInt(c -> c.getPlatform_refund_fee()).sum());

            classifiedStatistic.setPost_total(sdat.stream().filter(c -> c.getO2O_freight() != null && c.getPay_style().equals(pay_style))
                .mapToInt(c -> c.getO2O_freight()).sum());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("商家{},生成分类统计数据异常：{}", seller_id, e);
        }

        return classifiedStatistic;

    }

    /**
     * 修改明细表已汇总
     *
     * @param sdat
     * @param finance_no
     */
    private void updateSettlementDetailStatus(List<SettlementDetailAndTrades> sdat, String finance_no) {
        sdat.stream().forEach(p -> {
            settlementDetailMapper.updateSettlementByTradeId(p.getTrades_id(), finance_no);
            settlementDetailLogService.addSettlementDetailLog(5, p.getTrades_id());
        });
    }

    /**
     * 重新核算账单
     * @param financeNo
     */
    public Finances financesRecalculate(String financeNo){
        return tradesMapper.getFinancesRecalculate(financeNo);
    }

    /** 查询和修改汇总表和账单表数据
     * 修改账单：
     * @param financeNo
     */
    public Finances updateFinances(String financeNo){
        return tradesMapper.getFinancesRecalculate(financeNo);
    }
}
