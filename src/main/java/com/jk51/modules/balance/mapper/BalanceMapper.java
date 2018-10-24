package com.jk51.modules.balance.mapper;

import com.jk51.model.balance.Balance;
import com.jk51.model.balance.BalanceAccount;
import com.jk51.model.balance.BalanceAccountMonth;
import com.jk51.model.balance.BalanceDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/26.
 */
@Mapper
public interface BalanceMapper {
    //根据ID查询商户下的余额配置信息
    Balance getBalanceBySiteId(@Param("siteId") Integer siteId);

    //根据siteId查询商家记录
    Integer getBalanceCountBySiteId(@Param("siteId") Integer siteId);

    //添加商家余额记录
    Integer insertBalanceBySiteId(Balance balance);

    //修改商家余额记录
    Integer updateBalanceBySiteId(Balance balance);

    //查询商户下的账户余额
    Integer getBalancePayForSiteId(@Param("siteId") Integer siteId);

    //添加余额操作日志
    Integer insertBalanceDetail(BalanceDetail balanceDetail);

    //获取日志表余额
    int getBalanceDetailBalancePay(@Param("siteId") Integer siteId);

    //查询商家名称
    Map<String,Object> getMechantNameBySiteId(@Param("siteId") Integer siteId);

    //判断商户是否是服务商
    Balance getBalanceBySiteIdForBool(@Param("siteId") Integer siteId);

    //查询余额明细
    List<Map<String,Object>> getBalanceDetail(Map<String, Object> params);

    //获取所有的商户记录
    List<Map<String,Object>> getAllBalanceObj();

    //获取预警
    Balance getWarning(@Param("siteId") Integer siteId);

    //报表专用
    List<Map<String,Object>> getAccountDetailReport (Map<String,Object> map);

    //导出报表（收支明细）
    List<Map<String,Object>> getIODetailReport (Map<String,Object> map);

    //获取启用服务商模式的商户ID
    List<Integer> getSiteIdList();

    //查询昨天数据
    BalanceAccount getYestodayDate(@Param("siteId")Integer siteId,@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("startTimeBefore") String startTimeBefore,@Param("endTimeBefore") String endTimeBefore);

    //插入记录数据
    void insertBalanceAccountBySiteId(BalanceAccount balanceAccount);

    //查询结算记录
    List<Map<String,Object>> getBalanceAccountBySiteId(Map<String, Object> params);

    //校验
    Integer boolIExist(@Param("siteId")Integer siteId,@Param("tradesId") Long tradesId,@Param("applyStatus") Integer applyStatus);

    //报表专用
    List<Map<String,Object>> getAccountBillReport (Map<String,Object> map);

    //获取前天最后的余额
    Integer getBeforeYestodayTime(@Param("siteId")Integer siteId,@Param("startTimeBefore") String startTimeBefore,@Param("startTimeBefore1") String startTimeBefore1);

    //查询收费短信状态码
    String getMsgStatus(@Param("siteId")Integer siteId);

    //查询短信规则
    List<Map<String,Object>> getMsgRole(@Param("siteId")Integer siteId);

    //51后台获取余额记录
    List<Map<String,Object>> getAccountBalance(Map<String,Object> map );

    //51后台获取收支详情
    List<Map<String,Object>> getAccountBalanceSiteDetail(Map<String, Object> params);

    //51后台获取短信收费列表
    List<Map<String,Object>> getBalanceSMSLst(Map<String, Object> params);

    //修改备注
    Integer updateHqRemark(Map<String, Object> params);

    //根据ID查询充值详情
    Map<String,Object> getUpBalanceById(Map<String, Object> params);

    List<Map<String,Object>> getBalanceAccountForSite(Map<String, Object> params);

    //结算弹框
    Map<String,Object> getAccountInfo(Map<String, Object> params);

    //编辑结算弹框
    Integer updateAccountInfo(Map<String, Object> params);

    //报表专用
    List<Map<String,Object>> getAccountBillAllReport (Map<String,Object> map);

    //修改详情表结算状态
    void updateBalanceDetialByTime(Map<String, Object> params);

    //51后台：交易佣金
    List<Map<String,Object>> getTradesBrokerage(Map<String, Object> params);

    //其他业务详情
    List<Map<String,Object>> getOtherDetail(Map<String, Object> params);

    //查询当天商户余额
    Map<String,Object> getTodayData(@Param("siteId")Integer siteId,@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("startTimeBefore") String startTimeBefore,@Param("endTimeBefore") String endTimeBefore);

    //将出账的数据状态改为已出账状态
    void updateOutbillStatus(@Param("siteId")Integer siteId, @Param("startTime") String startTime, @Param("endTime") String endTime,@Param("newDate") Date newDate);

    Map<String,Object> findDetails(@Param("tradesId")String tradesId, @Param("siteId")Integer siteId,@Param("apply_status")Integer apply_status);

    //根据订单ID，查询佣金
    Integer getMoneyByTradesId(@Param("tradesId")Long tradesId);

    //查询订单来源门店
    Map<String,Object> getStoreName(@Param("siteId") Integer siteId,@Param("tradesId")String tradesId);

    //获取订单的支付时间
    Date getPayTime(@Param("siteId")Integer siteId,@Param("tradesId") Long tradesId);

    //查询商户下所有的描述
    List<Map<String,Object>> getMsgContent(@Param("siteId") Integer siteId);

    //修改对应参数
    void updateHomologousData(Map<String, Object> map);

    Integer getBalanceDetailByTradesIdAndApplyStatus(@Param("tradesId") Long tradesId,@Param("applyStatus") Integer applyStatus);

    Map<String,Object> getLogisticsNameByTradesId(@Param("tradesId") Long tradesId);

    Integer insertBalanceDetail2(BalanceDetail balanceDetail);

    Integer boolYunfei(@Param("tradesId") Long tradesId);

    //月账单
    BalanceAccountMonth getYestodayDateMonth(@Param("siteId")Integer siteId,@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("startTimeBefore") String startTimeBefore,@Param("endTimeBefore") String endTimeBefore);

    //插入月账单
    void insertBalanceAccountMonthBySiteId(BalanceAccountMonth balanceAccountMonth);

    //查询余额账单
    List<Map<String,Object>> getBalanceAccountForSiteMoth(Map<String, Object> params);

    void updateAuditStatus(Map<String, Object> params);

    //报表专用
    List<Map<String,Object>> getAccountBillAllDayReport (Map<String,Object> map);

    List<Map<String,Object>> getBalanceDetailOrder(Map<String, Object> params);

    List<Map<String,Object>> getBalanceAccountBySiteIdMonth(Map<String, Object> params);

    void updateAuditStatusMonth(Map<String, Object> params);

    //报表专用
    List<Map<String,Object>> getAccountBillMonthReport (Map<String,Object> map);

    Integer getAuditStatusById(Map<String, Object> params);

    List<Map<String,Object>> getYunfeiMap(@Param("siteId")Integer siteId);

    void updateBalancePayChange(@Param("pay_change")Integer pay_change,@Param("id") Integer id,@Param("balance") Integer balance);

    //报表专用
    List<Map<String,Object>> getTradesBrokerageReport (Map<String,Object> map);

    void insertBalnceLog(Map<String, Object> map);

    List<Map<String,Object>> getFiveMap(@Param("siteId")Integer siteId);

    List<Map<String,Object>> getDingshiLou(@Param("startTime")String startTime,@Param("endTime") String endTime);

    Integer getId(@Param("siteId")Integer siteId);

    Balance getBalanceBySiteId2(@Param("id")Integer id);

    Map<String,Object> getBillMap(@Param("id")Integer id,@Param("siteId") Integer siteId);

    Map<String,Object> getAccountMap(@Param("siteId")Integer siteId);

    void updateTradesStatusBySuccess(@Param("tradesId")Long tradesId);

    BalanceDetail getBalanceDetailBySerialnum(@Param("siteId") Integer siteId,@Param("serialNum") String serialNum);

    Integer updBalanceDetail(BalanceDetail balanceDetail);

    List<Map> getTopupRecord(Integer siteId);
}
