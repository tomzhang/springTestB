package com.jk51.modules.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/4/3.
 */
@Mapper
public interface DiscountMapper {
    //获取商家支付情况
    Map<String,Object> getPayAliWx(Map<String, Object> params);

    //添加订单
    Integer insertTradesLine(Map<String, Object> params);

    //修改订单
    Integer updateTradesLine(Map<String, Object> params);
    //查询订单
    Map<String,Object> getTradesLine(Map<String, Object> params);
    //查询订单金额
    int getTradesLineSum(Map<String, Object> params);

    //查询打折记录
    Map<String,Object> getDiscountRuleLine(Map<String, Object> params);

    //修改打折记录
    Integer updateDiscountRuleLine(Map<String, Object> params);

    //添加打折记录
    Integer insertDiscountRuleLine(Map<String, Object> params);

    //根据siteId查询打折记录
    Map<String,Object> getDiscountRuleLineBySiteId(Map<String, Object> params);

    //获取红包记录
    Integer insertRedpacketLine(Map<String, Object> params);

    //获取指定会员所有未使用红包金额
    Integer getRedpacketTotalMoneyByUnused(Map<String, Object> params);

    //修改指定会员  所有未使用红包金额  为  已使用红包
    Integer updateRedpacketType(Map<String, Object> params);

    //获取优惠总金额
    Integer getTotalMoneyByActivite(Map<String, Object> params);

    //获取支付优惠订单列表
    List<Map<String,Object>> getDiscountOrderList(Map<String, Object> params);

    //查询手机号是否是已经注册过的
    Integer boolPhone(Map<String, Object> params);

    //根据buyer_id查询member_id
    Integer getMemberId(Map<String, Object> params);

    //线下支付设置查询
    Map<String,Object> getOfflineBySiteId(Map<String, Object> params);

    //查询商户线下支付设置是否存在
    Integer boolOfflineBySiteId(Map<String, Object> params);

    //编辑商户线下支付设置
    Integer editOfflineBySiteId(Map<String, Object> params);

    //添加商户线下支付设置
    Integer insertOfflineBySiteId(Map<String, Object> params);

    Integer insertTicketBlag(Map<String, Object> params);

    List<Map<String,Object>> getTicketBlag(Map<String, Object> params);

    List<Map<String,Object>> getRedPacket(Map<String, Object> params);

    Map<String,Object> boolMoneyByTradesId(Map<String, Object> params);

    String getTradesIdByMemberId(Map<String, Object> params);

    Integer boolGetRedBao(Map<String, Object> params);

    Integer updateStatusById(Map<String, Object> params);

    List<Map<String,Object>> getDiscountExtractList(Map<String, Object> params);

    Integer getHongbaoByTradesId(Map<String, Object> params);

    String getAliUserId(Map<String, Object> params);

    String getMechantName(@Param("siteId") Integer siteId);

    Integer getMerchantBack(Map<String, Object> params);

    void updateMechantExt(Map<String, Object> params);

    void updateMechant(Map<String, Object> params);

    String getOpenId(Map<String, Object> params);

    void insertRedpacketLog(@Param("siteId")String siteId,@Param("member_id") String member_id,@Param("trades_id") String trades_id,
                            @Param("money") Integer money,@Param("open_id") String open_id,@Param("return_log") String return_log,@Param("status") Integer status);

    Map<String,Object> getDeviceNumMap(Map<String, Object> params);


    Map<String,Object> getBoolDataIsRepetition(@Param("siteId")Integer siteId, @Param("data")String data);
}
