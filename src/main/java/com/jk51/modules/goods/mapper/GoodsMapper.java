package com.jk51.modules.goods.mapper;


import com.jk51.model.Goods;
import com.jk51.model.GoodsParam;
import com.jk51.model.concession.GiftMsg;
import com.jk51.model.goods.BGoodsSynchroLog;
import com.jk51.model.goods.GoodsUpdateInfo;
import com.jk51.model.goods.PageData;
import com.jk51.model.goods.YbStoresGoodsPrice;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.modules.im.service.iMRecode.response.GoodsInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface GoodsMapper {
    Goods get (@Param("goods_id") String id);

    List<Goods> getList ();

    void update (@Param("goods_id") String id, @Param("drug_name") String drug_name);

    List<Goods> find (Map<String, Object> param);

    int add (Goods goods);

    Goods queryGoods (@Param("goods_id") String id);

    boolean updateByGoodsId (Goods goods);

    boolean changeStatus (@Param("param")Map<String, Object> param);
//    boolean changeStatus (@Param("goods_id") int goodsId, @Param("site_id") int siteId, @Param("goods_status") int goodsStatus);

    Map<String, String> getGoodsD (Map<String, Object> param);
    Map<String, Object> getGoodsD2 (Map<String, Object> param);
    Map<String, String> getGoodsDStatus (Map<String, Object> param);

    List<Map<String, String>> getGoodsDList (Map<String, Object> param);

    List<Map<String, String>> getGoodsImg (Map<String, Object> param);

    List<Map<String, String>> getGoodsList (Map<String, Object> param);

    List<Map<String, String>> getCorrelationGoodsList (Map<String, Object> param);

    List<Map<String, String>> getGoodsReportList (Map<String, Object> param);

    Integer updateBuyWay (Map<String, Object> param);

    /*Integer updateByWayDis(Map<String, Object> param);*/

    List<Goods> getListByUserCateId (String cate_id);

    List<Goods> getListBytradesId (int siteId, long tradesId);

    String getImageHash (int siteId, int goodsId);

    Goods getBySiteIdAndGoodsId (@Param("goodsId") int goodsId, @Param("siteId") int siteId);

    List<String> getGoodsCode (@Param("siteId") int siteId, @Param("goodsIds") List<Integer> goodsIds);

    List<Map<String, Object>> getByApprovalNumber (Map<String, Object> param);

    boolean updateByApprovalNumber (Goods goods);

    Map<String, String> queryByBarCodeSYS (@Param("bar_code") String bar_code, @Param("site_id") String site_id);

    Goods queryByBarCode (@Param("bar_code") String bar_code, @Param("site_id") String site_id, @Param("goods_id") String goods_id);

    Goods queryByGoodsCode (@Param("goods_code") String goods_code, @Param("site_id") String site_id);
    Goods queryByGoodsCodeSYS (@Param("goods_code") String goods_code, @Param("site_id") String site_id);
    Map<String, String> queryGoodsWithGoodsCode (@Param("goodsCode") String goodsCode, @Param("siteId") int siteId);

    int queryGoodsIdByCond (Map param);

    Map<String, Integer> queryGoodsCodeAndBarCodeCount (@Param("goodsCode") String goodsCode, @Param("barCode") String barCode, @Param("siteId") int siteId);

    int countBarCodeNotGoodsCode (@Param("goodsCode") String goodsCode, @Param("barCode") String barCode, @Param("siteId") int siteId);

    String getGoodsCodeID (@Param("siteId") int siteId, @Param("goodsId") Integer goodsId);

    //11
    Map queryGoodsDetailByGoodId (@Param("goods_id") Integer goods_id, @Param("site_id") Integer site_id);

    YbStoresGoodsPrice queryGoodStorePrice (@Param("goods_id") Integer goods_id, @Param("site_id") Integer site_id, @Param("store_id") Integer store_id);

    List<Map> queryGoodsListByConditions (Map map);

    List<Map> queryNullBarCode (Map map);

    Goods findByGoodsIdAndBarcode (@Param("siteId") Integer siteId, @Param("barcode") String barcode, @Param("goods_id") Integer goods_id);

    int updateGoodsBarCode (Map<String, Object> map);

    Integer updateBarCode (@Param("site_id") Integer site_id, @Param("goods_id") Integer goods_id, @Param("bar_code") String bar_code);


    List<Map> getRecommendGoodsByIds (@Param("siteId") Integer siteId, @Param("goodsIdLst") List<Integer> goodsIdLst);


    List<Map<String, Object>> queryGoodsInfoByIds (@Param("siteId") Integer siteId, @Param("goodsIds") List<Integer> goodsIds);

    List<Map<String, Object>> selectGoodsByBuyerId (@Param("siteId") String siteId, @Param("buyerId") String buyerId);

    Integer updatedisPrice (Map map);

    int updateErpPrice (Map map);

    List<String> getGoodCodeBySiteId (@Param("siteId") Integer siteId, @Param("status") Integer status);

    Integer batchDeleteGoods (@Param("param") Map<String, Object> param);

    GoodsUpdateInfo queryBGoodsBeforeUpdate (@Param("goodsId") Integer goodsId, @Param("siteId") Integer siteId);

    List<GoodsUpdateInfo> queryYbGoods (@Param("ybGoodsId") Integer ybGoodsId, @Param("approvalNumber") String approvalNumber, @Param("barCode") String barCode);

    String getDefaultImg (Map<String, Object> param);

    int updateGoodByPrice (@Param("siteId") String siteId, @Param("goodId") String goodId, @Param("price") Integer price);

    int insertGoodsSynchro (BGoodsSynchroLog bGoodsSynchroLog);

    /**
     * 根据商品id查询商品详情
     *
     * @param ids    商品id列表
     * @param siteId 商户id
     * @param fields b_goods表中的字段列表
     * @return
     */
    List<Map> findByIds (@Param("ids") int[] ids, @Param("siteId") int siteId, @Param("fields") String[] fields);

    int countGoodsOnSale (@Param("siteId") int siteId);

    List<Map<String, Object>> getTaskGoods (@Param("siteId") Integer siteId, @Param("goodIds") String[] goodIds);

    List<PageData> getGiftList (GoodsParam goodsParam);

    Map<String, Object> getGiftById (@Param("siteId") Integer siteId, @Param("goodsId") Integer goodsId);
    GiftMsg getGiftResult (@Param("siteId") Integer siteId, @Param("goodsId") Integer goodsId);
    int updateImg (Map m);

    int updateGoodByPriceOther (@Param("siteId") String siteId, @Param("goodId") String goodId, @Param("price") Integer price);

    List<Map<String, Object>> selectGoodsListBysiteId2(@Param("site_id") Integer siteId, @Param("goods_status") Integer goods_status);
    /**
     * 根据商品的主键ids查询商品编码
     *
     * @param siteId
     * @param goodIds
     * @return
     */
    String selectGcodefromGids (@Param("siteId") Integer siteId, @Param("gids") String goodIds);

    List<Map<String, Object>> getGoodsInfoByGoodsCodes (@Param("siteId") Integer siteId, @Param("goodsCodes") Set<String> goodsCodes);

    /**
     * 通过商编获取上下架状态的商品信息
     *
     * @param siteId
     * @param goodsCodes
     * @return
     */
    List<Map<String, Object>> getGoodsByGoodsCodesAndStatusIs1Or2 (@Param("siteId") Integer siteId, @Param("goodsCodes") Set<String> goodsCodes);

    List<Map<String, Object>> selectGoodsListBysiteId(@Param("site_id") Integer siteId, @Param("goods_status") Integer goods_status);

    Map<String,Object> getGoodsForVisit(@Param("siteId") Integer siteId, @Param("goodsId") Integer goodsId);

    List<Map<String,Object>> queryVisitGoodsInfo(@Param("siteId")Integer siteId, @Param("goodsId")String[] goodsId);

    List<PromotionsRule> queryTotalActivitys(@Param("siteId") Integer siteId);

    List<GoodsInfo> findGoodsInfo(@Param("ids")Set<Integer> ids, @Param("siteId")Integer siteId);

    List<Map<String,Object>> queryGoodsTagIds(@Param("siteId") Integer siteId, @Param("buyerId") Integer buyerId);

    List<Map<String,Object>> queryGoodsTags(@Param("siteId") Integer siteId, @Param("tagIds") Object[] tagIds);

    List<Map<String, Object>> queryGoodsInfoByIds2 (@Param("siteId") Integer siteId, @Param("goodsIds") List<Integer> goodsIds);

    List<String> queryGoodsIds (@Param("siteId") Integer siteId);

    //查询预约单及其商品信息
    Map<String,Object> getGoodsByPreNumber(@Param("siteId") String siteId,@Param("preNumber") String preNumber);

    List<String> queryNotInGoods(@Param("siteId")Integer siteId,@Param("list") List<String> discount_not);

    List<Map> selectGoodsAndImgInGoodsId(@Param("siteId") Integer siteId,@Param("goodsIdList") List<Integer> goodsIdList);

    List<String> getGidsByGcodes(@Param("siteId") Integer siteId, @Param("gCodeList") List<String> goodsIdList);

    Map<String, Object> getDefaultImgByGoodsCode(@Param("siteId") Integer siteId, @Param("goodsCode") String goodsCode);
    int addDefaultImage(@Param("siteId") Integer siteId, @Param("hash") String hash, @Param("size") int size);

    Integer getHasErpPriceOfMerchantExtBySiteId(@Param("siteId") Integer siteId);

    Integer updateGoodsBySiteIdAndAppGoodsStatus(@Param("para")Map<String, Object> para);

    List<String> getKaoHeBySiteIdAndStoreIdOrStoreAdminId(@Param("siteId")Integer siteId, @Param("storeId")Integer storeId, @Param("storeAdminId")String storeAdminId);

    List<Integer> getBuyerRecordBySiteIdAndStoreIdOrStoreAdminId(Map map);

    //查询关联分类
    Map<String,Object> queryRelevanceClassify(@Param("siteId") Integer siteId, @Param("userCateid") String userCateid);

    List<Map<String,Object>> selectGoodsByCateType(@Param("siteId") Integer siteId, @Param("cateName") String cateName);

    Map<String,Object> getDefaultImagesAndCate(@Param("siteId") Integer siteId, @Param("goodsCode") String goodsCode);
    List<Map<String, String>> ckByGoodsId(Map m);
    int ckUpdateByGoodsId(Map m);
}
