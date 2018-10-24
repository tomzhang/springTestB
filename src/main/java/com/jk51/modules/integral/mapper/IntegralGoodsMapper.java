package com.jk51.modules.integral.mapper;

import com.jk51.model.CityHasStores;
import com.jk51.model.order.Store;
import com.jk51.modules.integral.model.IntegralGoodsDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by guosheng on 2017/6/2.
 */
@Mapper
public interface IntegralGoodsMapper {
    List<Map<String, Object>> queryIntegralGoods(Map parameterMap);

    int deleteIntegralGoods(Integer id);

    int updateIntegralGoods(Map parameterMap);

    int updateIntegralGoodsNum(Map parameterMap);

    IntegralGoodsDetail getIntegralGoodsByGoodsId(@Param("site_id") Integer siteId, @Param("goods_id") Integer goodsId);

    Map<String,Object> queryIntegralGoodsByGoodsId(@Param("site_id") Integer siteId, @Param("goods_id") Integer goodsId);

    List<Store> getStoresBySiteId(@Param("siteId") Integer siteId,@Param("storeIds")String [] storeIds);

    List<CityHasStores> GroupStoresByCityAndServiceSupport(@Param("site_id") Integer siteId, @Param("serviceSupport") String servicesupport, @Param("storeIds")String [] storeIds);

    List<Store> selectStoresByCityAndServiceSupport(@Param("site_id") Integer siteId, @Param("cityId") Integer cityId, @Param("serviceSupport") String serviceSupport
            , @Param("storeIds")String [] storeIds);
    Integer getConvertibility (@Param("buyerId")Integer buyerId,@Param("siteId")Integer siteId,@Param("goodsId") Integer goodsId);

    List<Store> getStores(@Param("siteId")Integer siteId);

    List<CityHasStores> GroupStoresByCity(@Param("site_id")Integer siteId,@Param("serviceSupport")String servicesupport);

    List<Store> selectStoresByCity(@Param("site_id")Integer siteId,@Param("cityId")Integer cityId,@Param("serviceSupport") String serviceSupport);
}
