package com.jk51.modules.goods.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface GoodsmMapper {

    List<Map<String, Object>> goodsSearch(Map m);

    List<Map> getByGoodsId(Map m);

    int insert(Map m);

    int insert2(Map m);

    int del(Map m);

    int delImg(Map m);

    int setDefaultImg(Map m);

    int delDefaultImg(Map m);

    int getGoodsCountByCateCode(Map m);

    int getByGoodsIdAndHash(Map map);

    int getByGoodsIdAndHashflag(Map map);

    int updateByGoodsIdAndHashFlag(Map map);

    boolean saveImgMulti(@Param("goods_id_old") int goods_id_old, @Param("goods_id") int goods_id, @Param("site_id") int site_id);

    boolean copy51jkImgByYbGoodsId(@Param("siteId") int siteId,
                                   @Param("goodsId") int goodsId,
                                   @Param("ybGoodsId") int ybGoodsId);

    Map<String, Integer> isUpListShopMainImg(Map map);

    int insertGoodsImgBatch( @Param("img") Map img , @Param("imgMap") Map imgMap);

    int insertGoodsImgBatch2( @Param("img") Map img , @Param("imgMap") Map imgMap,@Param("picSize") Map size);


    Map<String, Object> selectImgById(@Param("siteId")Integer siteId,@Param("goodsId") Integer goodsId);
}
