package com.jk51.modules.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/17.
 */
@Mapper
public interface LabelSecondMapper {
    //查询年龄
    Integer selectAge(Map<String, Object> params);

    //添加标签
    Integer insertAllLabel(Map<String, Object> params);

    //修改标签
    Integer updateAllLabel(Map<String, Object> params);

    //删除标签
    Integer deleteAllLabel(Map<String, Object> params);

    //查询标签
    Map<String,Object> seleteAllLabel(Map<String, Object> params);

    //生日查询（天数）
    Integer selectBirthdayByDay(Map<String, Object> params);

    //生日查询（月）
    Integer selectBirthdayByMonth(Map<String, Object> params);

    //生日查询（区间）
    Integer selectBirthdayBySection(Map<String, Object> params);

    //查询改商家下所有的会员数
    Integer getMemberCount(Map<String, Object> params);

    //查询近多少天内的注册会员数
    Integer selectRegisterOne(Map<String, Object> params);

    //查询指定时间内的注册会员数
    Integer selectRegisterTwo(Map<String, Object> params);

    //查询交易金额
    Integer selectBargainMoney(Map<String, Object> params);

    //查询成功交易次数
    Integer selectBargainCount(Map<String, Object> params);

    //查询客单价
    Integer selectPreTransaction(Map<String, Object> params);

    //购买时段
    Integer selectEverBuy(Map<String, Object> params);

    //退款率
    Integer selecRefundProbability(Map<String, Object> params);

    //购买周期
    Integer selectBuyPeriod(Map<String, Object> params);

    //剩余积分
    Integer selectResidueIntegral(Map<String, Object> params);

    //赚取积分
    Integer selectAddIntegral(Map<String, Object> params);

    //消费积分
    Integer selectConsumeIntegral(Map<String, Object> params);

    //门店距离（活动）
    Integer selectDisStoreActivity(Map<String, Object> params);

    //查询商户下所有的竞店坐标
    List<Map<String,Object>> getStoreCoodinate(Map<String, Object> params);

    //竞店距离（活动）
    Integer selectDisContendStoreActivity(Map<String, Object> params);

    //查询所有商家siteId
    List<Integer> getSiteIdAll();

    //获取地址表信息
    List<Map<String,Object>> getAddressList(@Param("siteId") Integer siteId);

    //获取地址
    String getAdd(Map<String, Object> params);

    //修改地址表
    void updateAddressPoint(Map<String, Object> addressMap);

    //查所有门店的坐标
    List<Map<String,Object>> getStorePoint(Map<String, Object> params);

    //门店距离（收货地址）
    Integer selectDisStoreAddress(Map<String, Object> params);

    //竞店距离（收货地址）
    Integer selectDisContendStoreAddress(Map<String, Object> params);

    //获取商户下所有的下过单的buyerId
    List<Integer> getBuyerIdAll(@Param("siteId") Integer siteId);

    //查询这个会员最后一次下单的时间
    String getCreateTime(@Param("siteId")Integer siteId,@Param("buyerId") Integer buyerId);

    //修改会员最后一次下单时间
    void updateEndOrderTime(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId,@Param("createTime") String createTime);

    //查询未购买过
    Integer selectNotBuy(Map<String, Object> params);

    //性别：男
    Integer getSexByMan(Map<String, Object> params);

    //性别：女
    Integer getSexByWoman(Map<String, Object> params);

    //性别：未知
    Integer getSexByUnknow(Map<String, Object> params);

    //查询商户下所有的年龄标签
    List<Map<String,Object>> getAgeLabel(Map<String, Object> params);

    //查询商户下所有的生日标签
    List<Map<String,Object>> getBithdayLabel(Map<String, Object> params);

    //查询商户下所有的星座标签会员数
    Map<String,Object> getXingzuoLabel(Map<String, Object> params);

    //查询商户下所有的生肖标签会员数
    Map<String,Object> getShengxiaoLabel(Map<String, Object> params);

    //查询商户下所有的注册时间标签
    List<Map<String,Object>> getRegisterLabel(Map<String, Object> params);

    //查询商户下所有的成交金额标签
    List<Map<String,Object>> getBargainMoneyLabel(Map<String, Object> params);

    //查询商户下所有的成交次数标签
    List<Map<String,Object>> getBargainCountLabel(Map<String, Object> params);

    //查询商户下所有的客单价标签
    List<Map<String,Object>> getPreTransactionLabel(Map<String, Object> params);

    //查询商户下所有的退款率标签
    List<Map<String,Object>> getRefundProbabilityLabel(Map<String, Object> params);

    //查询商户下所有的购买过标签
    List<Map<String,Object>> getEverBuyLabel(Map<String, Object> params);

    //查询商户下所有的购买周期标签
    List<Map<String,Object>> getBuyPeriodLabel(Map<String, Object> params);

    //查询商户下所有的赚取积分标签
    List<Map<String,Object>> getaddIntegralLabel(Map<String, Object> params);

    //查询商户下所有的消耗积分标签
    List<Map<String,Object>> getConsumeIntegralLabel(Map<String, Object> params);

    //查询商户下所有的剩余积分标签
    List<Map<String,Object>> getResidueIntegralLabel(Map<String, Object> params);

    //查询商户下所有的门店距离(高频活动)标签
    List<Map<String,Object>> getDisStoreActivityLabel(Map<String, Object> params);

    //查询商户下所有的门店距离(收货地址)标签
    List<Map<String,Object>> getDisStoreAddressLabel(Map<String, Object> params);

    //查询商户下所有的竞店距离(高频活动)标签
    List<Map<String,Object>> getDisContendStoreActivityLabel(Map<String, Object> params);

    //查询商户下所有的竞店距离(收货地址)标签
    List<Map<String,Object>> getDisContendStoreAddressLabel(Map<String, Object> params);

    //健康标签
    Integer getHealthLabel(Map<String, Object> params);

    //自定义标签
    List<Map<String,Object>> getCustomLabel(Map<String, Object> params);

    //查询省市
    List<Map<Object,Object>> getCityLabelBySiteId(@Param("siteId") Integer siteId);

    //查询所有地区及人数
    List<Map<Object,Object>> getAreaLabelByCityId(@Param("city_id") Integer city_id,@Param("siteId") Integer siteId);

    //查询健康标签
    List<Map<String,Object>> getHealIllLabel(Map<String, Object> params);

    //查询疑似患病人数
    Integer selectHealthIll(Map<String, Object> params);

    //查询高血压患者
    List<Map<String,Object>> getHealIllLabelByGaoxueya(Map<String, Object> params);

    //查询高血脂患者
    List<Map<String,Object>> getHealIllLabelByGaoxuezhi(Map<String, Object> params);

    //查询糖尿病患者
    List<Map<String,Object>> getHealIllLabelByTangniaobing(Map<String, Object> params);

    //查询会员人数
    List<Integer> getMemberLabelCount(Map<String, Object> args);

    //查询会员ID
    List<Integer> getMemberIdsToInsert(Map<String, Object> args);

    //添加会员标签
    Integer insertMemberLabel(Map<String, Object> params);

    //修改标签
    Integer updateMemberLabel(Map<String, Object> params);

    //查询会员标签名称是否存在
    Integer getBooleanByName(Map<String, Object> params);

    //删除会员标签
    Integer deleteMemberLabel(Map<String, Object> params);

    //查询所有会员标签
    List<Map<String,Object>> selectAllMemberLabel(Map<String, Object> params);

    //查询标签表中所有的siteId
    List<Integer> getAllSiteId();

    //查询商户下所有的会员分组标签
    List<String> getStrList(Integer siteId);

    //查询商户下所有的会员注册时填写的地址
    List<Map<String,Object>> getInfoListMap(Map<String, Object> params);

    //查询商户下会员收货地址的区域
    List<Map<String,Object>> getAddrListMap(Map<String, Object> params);

    //查询指定类型下的标签是否有重复
    Integer getBooleanNameByClassType(Map<String, Object> params);

    //查询城市下的区域根据会员表
    List<Map<String,Object>> getInfoListMapArea(Map<String, Object> params);

    //查询城市下的区域根据地址表
    List<Map<String,Object>> getAddrListMapAddr(Map<String, Object> params);

    //未知年龄
    Integer getWeizhiAge(Map<String, Object> params);

    //成功交易金额为0
    Integer getbargainMoneyCount(Map<String, Object> params);

    //成交次数为0
    Integer getbargainMoneyCountCount(Map<String, Object> params);

    //生日
    Map<String,Object> getBithdayLabelByDay(Map<String, Object> params);

    //零退款
    Integer getZeroRefundCount(Map<String, Object> params);

    //根据ID查询标签名称
    String getNameById(Map<String, Object> params);

    //查询所有的会员标签
    List<Map<String,Object>> getListByType(Map<String, Object> params);

    //根据ID修改会员标签的标签组
    void updateParamsNameById(@Param("id") Integer id,@Param("labelsMap") String labelsMap);

    //查询门店距离（收货地址）
    List<Integer> getDisStoreAddressBuyerIdList(@Param("siteId")Integer siteId,@Param("disStoreAddressList") List<Map<String, Object>> disStoreAddressList);

    //查询竞店距离（高频活动）
    List<Integer> getDisContendStoreActivityBuyerIdList(@Param("siteId") Integer siteId,@Param("disContendStoreActivityList") List<Map<String, Object>> disContendStoreActivityList);

    //查询竞店距离（收货地址）
    List<Integer> getDisContendStoreAddressBuyerIdList(@Param("siteId")Integer siteId,@Param("disContendStoreAddressList") List<Map<String, Object>> disContendStoreAddressList);

    List<Map<String,Object>> getInfoListMap2(Map<String, Object> params);

    List<Map<String,Object>> getAddrListMap2(Map<String, Object> params);

    //查询所有的会员分组（只包含新数据）
    List<Map<String,Object>> getAllLabel(Map<String, Object> params);

    //获得标签属性
    List<String> getShuxing(@Param("siteId")Integer siteId, @Param("key")String key, @Param("labName")List<String> labName);

    //根据标签名称修改标签组
    void updateLabelGroup(Map<String, Object> params);

    //根据分组名称查询标签组
    Map<String,Object> getLabelGroupByName(Map<String, Object> params);

    //根据分组名称修改分组数量
    void updateCountByName(Map<String, Object> params);

    //查询成交金额的会员ID
    List<Integer> bargainMoneyBuyerId(@Param("bargainMoneyList")List<Map<String, Object>> bargainMoneyList,@Param("siteId")Integer siteId);

    //查询成交次数的会员ID
    List<Integer> bargainCountBuyerId(@Param("bargainCountList")List<Map<String, Object>> bargainCountList,@Param("siteId")Integer siteId);

    //查询客单价的会员ID
    List<Integer> preTransactionBuyerId(@Param("preTransactionList")List<Map<String, Object>> preTransactionList,@Param("siteId")Integer siteId);

    //查询退款率的会员ID
    List<Integer> refundProbabilityBuyerId(@Param("refundProbabilityList")List<Map<String, Object>> refundProbabilityList,@Param("siteId")Integer siteId);

    //查询获取积分的会员ID
    List<Integer> addIntegralBuyerId(@Param("addIntegralList")List<Map<String, Object>> addIntegralList,@Param("siteId")Integer siteId);

    //查询消耗积分的会员ID
    List<Integer> consumeIntegralBuyerId(@Param("consumeIntegralList")List<Map<String, Object>> consumeIntegralList,@Param("siteId")Integer siteId);

    //根据标签名称查询LabelAttribute
    String getLabelAttribute(@Param("siteId")Integer siteId, @Param("labelKey")String type, @Param("name")String name);

    //根据区域ID查区域名称
    List<String> getAreaName(@Param("areaList")List<String> areaList);

    //查询购买时段的会员ID
    List<Integer> getEverBuyBuyerIdList(@Param("siteId")Integer siteId, @Param("everBuyList")List<Map<String, Object>> everBuyList);

    //查询高血压
    List<Integer> getHealthGaoXueYaBuyerIdList(@Param("siteId")Integer siteId, @Param("healthGaoXueYaList")List<Map<String, Object>> healthGaoXueYaList);

    //高血脂
    List<Integer> getHealthGaoXueZhiBuyerIdList(@Param("siteId")Integer siteId, @Param("healthGaoXueZhiList")List<Map<String, Object>> healthGaoXueZhiList);

    //糖尿病
    List<Integer> getHealthTangniaobingBuyerIdList(@Param("siteId")Integer siteId, @Param("healthTangNiaoBingList")List<Map<String, Object>> healthTangNiaoBingList);

    //地址表查漏
    List<Map<String,Object>> getLou() ;

    //根据地址ID修改经纬度
    void updateAddressPointByLou(Map<String, Object> addressMap);

    //初始化商户标签
    void initInsertLabel(@Param("initList")List<Map<String, Object>> initList);

    //年龄已知会员数
    Integer getYizhiAge(Map<String, Object> params);

    //金额已知会员数
    Integer getyizhiBargainMoneyCount(Map<String, Object> params);

    //已知没有赚取积分
    Integer getWeizhiAddIntegrateCount(Map<String, Object> params);

    //已知有赚取积分
    Integer getyizhiAddIntegrateCount(Map<String, Object> params);

    //已知没有消耗积分
    Integer getWeizhiConsumeIntegrateCount(Map<String, Object> params);

    //已知有消耗积分
    Integer getyizhiConsumeIntegrateCount(Map<String, Object> params);

    //没有剩余积分
    Integer getWeizhiResidueIntegrateCount(Map<String, Object> params);

    //有剩余积分
    Integer getyizhiResidueIntegrateCount(Map<String, Object> params);

    //没有活动（高频活动）
    Integer getWeizhiDisStoreActivityCount(Map<String, Object> params);

    //有活动（高频活动）
    Integer getyizhiDisStoreActivityCount(Map<String, Object> params);

    //没有地址
    Integer getweizhiDisStoreAddressCount(Map<String, Object> params);

    //有地址
    Integer getyizhiDisStoreAddressCount(Map<String, Object> params);

    Integer getAreaYizhi(Map<String, Object> params);

    Integer getAreaWeizhi(Map<String, Object> params);

    //查询改商户下所有的会员数
    Integer getallCount(Map<String, Object> params);

    //查询部分成交金额为0 的情况
    Integer getBufenWeizhiBargainMoney(Map<String, Object> params);

    //历史赚取积分
    Integer selectAddIntegralTotal(Map<String, Object> params);

    //历史累计消耗积分
    Integer selectConsumeIntegralTotal(Map<String, Object> params);

    //页面加载查询购买周期
    Map<String,Object> getPeriodList(@Param("siteId")Integer siteId, @Param("chaList")List<Map<String, Object>> chaList);

    //页面加载查询成交金额
    Map<String,Object> getBargainMoneyList(@Param("siteId")Integer siteId,@Param("chaList") List<Map<String, Object>> chaList);

    //页面加载查询成交次数
    Map<String,Object> getBargainCountList(@Param("siteId")Integer siteId, @Param("chaList")List<Map<String, Object>> chaList);

    //页面加载查询客单价
    Map<String,Object> getTransactionList(@Param("siteId")Integer siteId,@Param("chaList") List<Map<String, Object>> chaList);

    //页面加载查询退款率
    Map<String,Object> getRefundProbabilityList(@Param("siteId")Integer siteId, @Param("chaList")List<Map<String, Object>> chaList);

    //页面加载查询购买过
    Map<String,Object> getEverBuyList(@Param("siteId")Integer siteId,@Param("chaList") List<Map<String, Object>> chaXunList);

    //页面加载查询年龄
    Map<String,Object> getAgeList(@Param("siteId")Integer siteId,@Param("chaList") List<Map<String, Object>> chaList);

    //页面加载查询年龄
    Map<String,Object> getRegisterList(@Param("siteId")Integer siteId,@Param("chaList") List<Map<String, Object>> chaList);

    //页面加载查询获取积分
    Map<String,Object> getaddIntegraList(@Param("siteId")Integer siteId, @Param("chaList")List<Map<String, Object>> chaList);

    //页面加载查询消耗积分
    Map<String,Object> getconsumeIntegralList(@Param("siteId")Integer siteId, @Param("chaList")List<Map<String, Object>> chaList);

    //页面加载查询剩余积分
    Map<String,Object> getresidueIntegralList(@Param("siteId")Integer siteId, @Param("chaList")List<Map<String, Object>> chaXunList);

    //页面加载查询门店距离（高频活动）
    Map<String,Object> getDisStoreActivityList(@Param("siteId")Integer siteId, @Param("chaList")List<Map<String, Object>> chaListAdd,@Param("timeNineten")String timeNineten,@Param("curentTime")String curentTime);

    //页面加载查询竞店距离（高频活动）
    Map<String,Object> getDisContendStoreActivityList(@Param("siteId") Integer siteId, @Param("chaList") List<Map<String, Object>> chaListAdd,@Param("timeNineten")String timeNineten,@Param("curentTime")String curentTime);

    //页面加载查询竞店距离（收货地址）
    Map<String,Object> getdDsContendStoreAddressList(@Param("siteId")Integer siteId, @Param("chaList")List<Map<String, Object>> chaList);

    //页面加载查询竞店距离（高频活动）
    Map<String,Object> getdDisStoreAddressList(@Param("siteId")Integer siteId,@Param("chaList") List<Map<String, Object>> chaList);

    //根据标签名称查询标签锁包含的会员ID
    String memberIdsByNameAndSiteId(@Param("siteId")Integer siteId,@Param("labelName") String labelName);

    //多维度：查询基础标签
    Map<String,Object> getBaseLabel(@Param("siteId")Integer siteId,@Param("buyerId") Integer buyerId);

    //多维度：查询星座
    String getXingzuo(@Param("siteId")Integer siteId,@Param("buyerId") Integer buyerId);

    //多维度：查询购买周期
    Map<String,Object> getPeriodLabelForDimensionality(@Param("siteId")Integer siteId,@Param("buyerId") Integer buyerId);

    //多维度：查询商户下所有的购买周期对象
    List<Map<String,Object>> getStrListForDimensionality(@Param("siteId") Integer siteId,@Param("label_type") String label_type);

    //多维度：查询会员最后一次购买时间
    Date getLastTimeForDimensionality(@Param("siteId")Integer siteId, @Param("buyerId") Integer buyerId);

    //多维度：查询会员获取积分
    Integer getAddIntegrateForDimensionality(@Param("siteId")Integer siteId, @Param("buyerId") Integer buyerId);

    //多维度：查询会员消耗积分
    Integer getConsumeIntegrateForDimensionality(@Param("siteId")Integer siteId, @Param("buyerId") Integer buyerId);

}
