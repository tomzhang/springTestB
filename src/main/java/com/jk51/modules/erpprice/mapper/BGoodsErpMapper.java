package com.jk51.modules.erpprice.mapper;

import com.jk51.model.erpprice.BGoodsErp;
import com.jk51.model.erpprice.BGoodsErpExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BGoodsErpMapper {
    long countByExample(BGoodsErpExample example);

    int deleteByExample(BGoodsErpExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BGoodsErp record);

    int insertSelective(BGoodsErp record);

    List<BGoodsErp> selectByExample(BGoodsErpExample example);

    BGoodsErp selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BGoodsErp record, @Param("example") BGoodsErpExample example);

    int updateByExample(@Param("record") BGoodsErp record, @Param("example") BGoodsErpExample example);

    int updateByPrimaryKeySelective(BGoodsErp record);

    int updateByPrimaryKey(BGoodsErp record);

    /**
     * 增加门店ERP价格记录
     *
     * @param siteId     商户编号
     * @param storeId    门店编号
     * @param goodsId    商品编号
     * @param goodsPrice 门店商品ERP价格 单位分
     * @return
     */
    int insertERPPrice(@Param("siteId") Integer siteId,
                       @Param("storeId") Integer storeId,
                       @Param("goodsId") Integer goodsId,
                       @Param("goodsCode") String goodsCode,
                       @Param("goodsPrice") Integer goodsPrice,
                       @Param("storeNumber") String storeNumber);

    /**
     * 增加门店ERP价格记录
     *
     * @param siteId     商户编号
     * @param storeId    门店编号
     * @param goodsPrice 门店商品ERP价格 单位分
     * @return
     */
    int updateERPPrice(@Param("siteId") Integer siteId,
                       @Param("storeId") Integer storeId,
                       @Param("goodsCode") String goodsCode,
                       @Param("goodsPrice") Integer goodsPrice,
                       @Param("storeNumber") String storeNumber);

    /**
     * 增加门店ERP价格记录前让之前的价格失效
     *
     * @param siteId
     * @param storeId
     * @param goodsCode
     * @return
     */
    int abateERPPrice(@Param("siteId") Integer siteId,
                      @Param("storeId") Integer storeId,
                      @Param("goodsCode") String goodsCode);

    List<Map<String, Object>> erpPriceList(Map<String, Object> param);

    List<Map<String, Object>> erpPriceList2(Map<String, Object> param);

    int batchChangePrice(@Param("ids") String[] ids, @Param("price") Integer price);

    List<BGoodsErp> selectChangePriceInfos(@Param("ids") String[] ids);

    int ChangePrice(@Param("id") String id, @Param("price") Integer price);

    BGoodsErp selectByStroeidAndGoodsid(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("goodsId") Integer goodsId);

    int updateByStroeidAndGoodsid(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId, @Param("goodsId") Integer goodsId, @Param("erpPrice") Integer erpPrice);

    List<Map<String, Object>> selectErpPriceList(Map map);

    List<Map<String, Object>> selectErpPriceListBysiteId(@Param("siteId") Integer siteId);

    int batchUpdateErpPrice(List updateList);

    int batchInsertErpPrice(List insertList);
}
