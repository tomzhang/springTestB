package com.jk51.modules.goods.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/6/29.
 */
@Mapper
public interface GoodsRecommendMapper {

    Integer addGoodsRecommend(@Param("paramMap") Map<String, Object> paramMap);

    List<Map<String,Object>> getGoodsRecommendList(@Param("paramMap") Map<String, Object> paramMap);

    Map<String,Object> getGoodsRecommendDetail(@Param("paramMap")Map<String, Object> paramMap);

    Integer updateGoodsRecommend(@Param("paramMap")Map<String, Object> paramMap);

    Integer updateAgainGoodsRecommend(@Param("paramMap")Map<String, Object> paramMap);

    Map<String,Object> getGoodsRecommendByGoodsId(@Param("paramMap")Map<String, Object> paramMap);

    Integer deleteGoodsRecommendImg(@Param("paramMap")Map<String, Object> paramMap);
    /*------APP下单页面------*/
    List<Map<String,Object>> getStartOfGoodsRecommendList(@Param("siteId")Integer siteId, @Param("storeId")Object storeIdObj);
    List<Map<String,Object>> getStartOfGoodsRecommendList2(@Param("siteId")Integer siteId);

    Integer updateDailyBrowseOfAppGoodsRecommend(@Param("paramMap")Map<String, Object> paramMap);

    Integer insertGoodsRecommendRecords(@Param("paramMap")Map<String, Object> paramMap);

    List<Map<String, Object>> getGoodsRecommendRecords(@Param("paramMap")Map<String, Object> paramMap);

    Map<String,Object> getGoodsNameBySiteIdAndGoodsIdAndStoreId(@Param("siteId")Integer siteId, @Param("goodsId")Integer goodsId, @Param("storeId")Integer storeId);

    Map<String,Object> getGoodsNameBySiteIdAndGoodsId(@Param("siteId")Integer siteId, @Param("goodsId")Integer goodsId);

    Integer updateBTradesBySiteIdAndTradesId(@Param("paramMap")Map<String, Object> paramMap);

    Integer updateRecommend(@Param("paramMap")Map<String, Object> paramMap);

    List<Map<String,Object>> getGoodsRecommendBySiteIdOrStoreId(@Param("paramMap")Map<String, Object> paramMap);

}
