package com.jk51.modules.persistence.mapper;

import com.jk51.model.goods.YbStoresGoodsPrice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SYbStoresGoodsPriceMapper {

    List<Map<String, String>> findByGoodsListPrice(Map map);

    YbStoresGoodsPrice findByGoodsId(@Param("storeId") Integer storeId, @Param("siteId") Integer siteId, @Param("goodsId") Integer goodsId);

    YbStoresGoodsPrice findByGoodsId2(@Param("storeId") Integer storeId, @Param("siteId") Integer siteId, @Param("goodsId") Integer goodsId, @Param("delTag") Integer delTag);

    List<Map<String, String>> findList(@Param("storeId") Integer storeId, @Param("siteId") Integer siteId, @Param("goodsId") Integer goodsId, @Param("delTag") Integer delTag);

    int delYBPriceAll(@Param("storeId") Integer storeId, @Param("siteId") Integer siteId, @Param("goodsId") Integer goodsId);

    int delYBPriceAll(Integer id);

    int updateYBPrice(@Param("goodsPrice") Integer goodsPrice, @Param("goodsId") Integer goodsId, @Param("siteId") Integer siteId, @Param("storeId") Integer storeId);

    int insertYBPrice(YbStoresGoodsPrice ybStoresGoodsPrice);


    int refreshYBTime(Map map);

    int updateYBPrice2(@Param("goodsPrice") Integer goodsPrice, @Param("goodsId") Integer goodsId, @Param("siteId") Integer siteId,
                       @Param("storeId") Integer storeId);

    int updateFlag(@Param("delTag") Integer delTag, @Param("price") Integer price, @Param("siteId") Integer siteId,
                   @Param("goodIds") List<String> goodIds, @Param("storeIds") List<String> storeIds, @Param("selfFlag") Integer selfFlag);

    int updateFlag2(@Param("price") Integer price, @Param("siteId") Integer siteId, @Param("goodIds") List<String> goodIds,
                    @Param("storeIds") List<String> storeIds, @Param("selfFlag") Integer selfFlag);

    int updateSelfFlag(@Param("delTag") Integer delTag, @Param("selfFlag") Integer selfFlag, @Param("siteId") Integer siteId,
                       @Param("goodIds") List<String> goodIds, @Param("storeIds") List<String> storeIds);

    int batchupdateYBPrice(List<Map<String, Object>> mapList);

    int batchInsertYBPrice(List<YbStoresGoodsPrice> maplist);
}
