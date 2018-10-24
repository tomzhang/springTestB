package com.jk51.modules.persistence.mapper;


import com.jk51.model.Goods;
import com.jk51.model.goods.YbStoresGoodsPrice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SGoodsMapper {

    public Goods get(@Param("goods_id") String id);

    public List<Goods> getList();

    public void update(@Param("goods_id") String id, @Param("drug_name") String drug_name);

    Map queryGoodsDetailByGoodId(@Param("goods_id") Integer goods_id, @Param("site_id") Integer site_id);

    List<Map> queryGoodsListByConditions(Map map);

    Integer updateBarCode(@Param("site_id") Integer site_id, @Param("goods_id") Integer goods_id, @Param("bar_code") String bar_code);

    Map queryGoodsNameByGoodsId(@Param("siteId") int siteId, @Param("goodId") String goodId);

    List<Map<String,Object>> getGoodsInfoByGoodIds(@Param("siteId") int siteId, @Param("goodIds") List<Integer> goodIds);

    YbStoresGoodsPrice queryGoodStorePrice(@Param("goods_id") Integer goods_id, @Param("site_id") Integer site_id, @Param("store_id") Integer store_id);

    Goods findByGoodsIdAndBarcode(@Param("siteId") Integer siteId, @Param("barcode") String barcode, @Param("goods_id") Integer goods_id);
}
