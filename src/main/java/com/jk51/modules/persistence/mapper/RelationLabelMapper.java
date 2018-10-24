package com.jk51.modules.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/9/20.
 */
@Mapper
public interface RelationLabelMapper {
    //获取该会员下所有的关系标签
    List<Map<String,Object>> getRelationLabelAllByMemberId(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId);

    //根据标签名称获取店员ID集合
    String getStoreadminIdsByLbelName(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId, @Param("labelName") String labelName);

    //修改关系标签的数量和店员ID集合
    Integer deleteCountAndStoreadminId(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId, @Param("labelName") String labelName, @Param("newStoreadminIds") String newStoreadminIds, @Param("count") Integer count);

    //添加关系标签
    Integer insertRelationLabel(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId, @Param("labelName") String labelName, @Param("storeadminIds") String storeadminId, @Param("storeadminCount") Integer xCount, @Param("labelType") Integer labelType);

    //门店后台获取会员自定义标签
    List<Map<String,Object>> getRelationLabelForCustom(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId);

    //门店后台获取会员慢病标签
    List<Map<String,Object>> getRelationLabelForSlow(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId);

    //门店后台查询给该会员贴指定标签的所有店员
    String getStoreadminIdsById(@Param("siteId") Integer siteId, @Param("id") Integer id);

    //根据店员ID查询店员姓名和手机号
    Map<String,Object> getStoreadminNameAndMobile(@Param("siteId") Integer siteId, @Param("storeadminId") Integer storeadminId);

    //根据标签ID获取店员ID集合和店员数量
    Map<String,Object> getStoreadminIdsByIdAndCount(@Param("siteId") Integer siteId, @Param("id") Integer id);

    //修改标签的店员数量和店员的ID集合
    Integer updateCountAndIds(@Param("siteId") Integer siteId, @Param("id") Integer id, @Param("newStoreadminIds") String newStoreadminIds, @Param("count") Integer count);

    //获取该会员下包含该店员的所有标签名称
    List<String> getStoreadminToMember(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId, @Param("storeadminId") Integer storeadminId,@Param("type")  Integer type);

    //根据标签名称查找该会员下的店员ID集合和店员数量
    Map<String,Object> getStoreadminIdsAndByLbelName(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId, @Param("labelName") String labelName);

    //根据标签名称查询改标签下又多少会员
    List<Integer> getmemberIds(@Param("siteId") Integer siteId, @Param("labelName") String labelName,@Param("type") Integer type);

    //修改自定义标签名称
    Integer updateLabelName(@Param("siteId") Integer siteId, @Param("labelName") String labelName, @Param("customName") String customName);

    //删除自定义标签
    Integer deleteCustemLabelName(@Param("siteId") Integer siteId, @Param("customName") String customName);

    //根据标签名称查询改标签下又多少会员
    Integer getMemberCount(@Param("siteId") Integer siteId, @Param("labelName") String labelName);

    //根据标签名称查询buyerId
    List<Integer> getBuyerIdByLabelName(Map<String, Object> customLabel);

    //根据buyerId查询openId
    List<String> getOpernIdByBuyerId(@Param("siteId") Integer siteId, @Param("buyerIds") List<Integer> buyerIds);

    //根据buyerId查询memberId
    List<Integer> getMemberIdBybuyerId(@Param("siteId") Integer siteId, @Param("buyerIds") List<Integer> buyerIds);

    //根据手机号获取buyerId
    Integer getBuyerIdByMobile(@Param("siteId") Integer siteId, @Param("mobile") String mobile);

    //判断该标签下是否有该会员
    Integer booleanBuyerId(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId, @Param("labelName") String labelName);

    //根据标签名称获取贴该标签的店员的数量
    Integer getStoreadminCountByLabelName(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId, @Param("labelName") String labelName);

    //根据标签名称和会员ID修改店员数量
    Integer updateStoreadminCount(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId, @Param("labelName") String labelName, @Param("count") Integer count,@Param("ids") String ids);

    //根据会员ID获取该会员下所有的标签名称
    List<String> getLabelNameByBuyerId(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId,@Param("type") Integer type);

    //根据会员ID获取该店员ID集合和数量
    Map<String,Object> getIdsByLabelName(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId,@Param("type") Integer type, @Param("labelName") String labelName);

    //根据标签名称修改店员数量和店员集合
    Integer updateCountAndIdsByLabelName(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId, @Param("labelName") String labelName, @Param("newIds") String newIds, @Param("count") Integer count, @Param("labelType") Integer labelType);

    //查询商户下所有的自定义标签名称
    List<String> getCustomNameAll(@Param("siteId") Integer siteId);

    //根据自定义标签名称获取所有会员的buyerId
    List<Integer> getCustomOpenIdAll(@Param("siteId") Integer siteId, @Param("labelName") String labelName);

    //根据自定义标签名称获取所有会员的memberId
    Set<String> getCustomMemberIdAll(@Param("siteId") Integer siteId, @Param("labelName") String[] labelName);

    //根据自定义标签名称获取所有会员的memberId
    List<String> getCustomMemberIdAll2(@Param("siteId") Integer siteId, @Param("labelName") String[] labelName);
    //根据buyerId获取所有会员的openId
    List<Map<String,Object>> getOpenIdByBuyerId(@Param("siteId") Integer siteId, @Param("idsList") List<Integer> idsList);

    //获取商户下所有会员的openId
    List<Map<String,Object>> getAllMemberOpenId(Integer siteId);

    //初始化：获取商户下慢病标签不为空的会员信息
    List<Map<String,Object>> getMemberData(@Param("siteId") Integer siteId);

    //获取该店员给指定会员添加的自定义标签
    List<String> getLabelCustomByStoreAsminIdAndMemberId(Map<String, Object> params);

    //获取会员的buyerId
    Integer getBuyerId(Map<String, Object> params);

    //获取所有的自定义标签
    List<Map<String,Object>> getAllLabelByCustom(Map<String, Object> params);

    //查询出人数与店员ID不匹配的记录
    List<Map<String,Object>> getCuoJiluList();

    //修改不匹配记录
    void updateCuoJilu(@Param("id")Integer id,@Param("str") String str);

    //获取店员类型：普通店员或管理员
    Integer getType(@Param("siteId")Integer siteId,@Param("storeadminId") String storeadminId);

    //获取门店名称
    String getStoreName(@Param("siteId")Integer siteId,@Param("storeadminId") String storeadminId);

    //获取所有含有慢病标签的会员
    List<Map<String,Object>> getMemberMapList();

    //获取会员的慢病标签（不包含商户后台贴标的）
    List<String> getlabelList(@Param("siteId")Integer siteId,@Param("buyerId") Integer buyerId);

    //修改慢病标签的店员数量和店员ID
    void updateSlowCountAndIds(@Param("siteId")Integer siteId,@Param("buyerId") Integer buyerId,@Param("label") String label);

    //添加慢病标签的店员数量和店员ID
    void insertSlowCountAndIds(@Param("siteId")Integer siteId,@Param("buyerId") Integer buyerId,@Param("label") String label);
}
