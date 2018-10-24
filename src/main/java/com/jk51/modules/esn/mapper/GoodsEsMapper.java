package com.jk51.modules.esn.mapper;

import com.jk51.modules.es.entity.GoodsInfosAdminReq;
import com.jk51.modules.esn.entity.GoodsInfo;
import com.jk51.modules.esn.entity.ImageInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface GoodsEsMapper {

    List<GoodsInfo> queryGoodsDetail(@Param("siteId") String siteId, @Param("goodsId") String goodsId);
    List<GoodsInfo> queryGoodsDetail4erp(@Param("siteId") String siteId, @Param("goodsId") String goodsId);
    int queryDistribute(@Param("siteId") String siteId, @Param("goodsId") String goodsId);
    String queryGoodsPrimaryImage(@Param("siteId") String siteId, @Param("goodsId") String goodsId);

    List<ImageInfo> queryGoodsImageBySiteId(@Param("siteId") String siteId);
    List<GoodsInfo> queryDistributeBySiteId(@Param("siteId") String siteId);
    List<GoodsInfo> querySuggestGoods(@Param("siteId") String siteId);

    //查询促销活动
    List<Map<String,Object>> queryActivityCoupon(@Param("siteId") String siteId);
    //查询所有商家id
    List<String> queryMerchantIds();

    int insertLog(@Param("siteId")String siteId, @Param("param")String param, @Param("result")String result);

    //查询分类编码
    String queryCategoryCode(@Param("siteId")String siteId, @Param("userCateid")String user_cateid);

    //根据分类查询商品
    List<GoodsInfo> queryGoodsDetailByCateId(@Param("siteId") String siteId, @Param("cateCodes") String[] cateCodes);

    //根据分类查询商品
    List<GoodsInfo> querySuggestGoodsByCateId(@Param("siteId") String shopid,@Param("cateCodes") String[] cateCodes);

    //根据分类查询cate_code
    String[] queryCateCodeByCateId(@Param("siteId") String siteId, @Param("cateIds") String[] cateIds);

    //查询商品价格
    List<Map<String,Object>> queryAppGoodsPrice(@Param("siteId") String brand, @Param("storeId") Integer storeId, @Param("goodsIds") String[] split);

    List<Integer> queryGoodsIdsByUserPay(GoodsInfosAdminReq goodsInfosAdminReq);
}
