package com.jk51.modules.order.mapper;

import com.jk51.model.order.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:分单
 * 作者: baixiongfei
 * 创建日期: 2017/2/16
 * 修改记录:
 */
@Mapper
public interface DistributeOrderMapper {

    public List<Store> getStores(@Param("site_id") Integer siteId);

    public Store getStoresById(Integer siteId,String storesNumber);

    public GoodsInfo getGoodsInfo(Integer siteId,Integer goodsId);

    public Merchant getMerchant(@Param("merchant_id") Integer merchant);

    public Meta getMeta(Integer siteId,String metaKey);

    public DeliveryMethod getExpressRule(@Param("site_id") Integer site_id,@Param("post_style_id") Integer postStyleId);
    public DeliveryMethod getExpressRuledu(@Param("site_id") Integer site_id);


    /**
     * 通过goodIds和siteId查询Good信息
     *
     * @param site_id
     * @param goodIds
     * @return
     */
    List<GoodsInfo> getGoodsInfoByGoodIds(@Param("siteId") int site_id, @Param("goodIds") List<Integer> goodIds);
    
    Map<String,Integer> getGoodsInfoByPrice(@Param("siteId") int site_id, @Param("storeId") String storeId, @Param("goodsId") int goodsId);

    List<Map<String,Object>> selectRecommendOrderList(Map<String,Object> param);

    Long selectRecommendOrderListCount(Map<String,Object> param);

    List<GoodsInfo> selectGoodsByGoodsIds(@Param("siteId") Integer siteId, @Param("goodsIds") List<Integer> goodsIds);
}
