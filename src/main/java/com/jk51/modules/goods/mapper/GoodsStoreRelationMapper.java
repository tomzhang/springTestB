package com.jk51.modules.goods.mapper;

import com.jk51.model.GoodsStoreRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 商品门店关联
 * Created by Administrator on 2018/6/29.
 */
@Mapper
public interface GoodsStoreRelationMapper {

    /*新增*/
    void storeRelationAdd (GoodsStoreRelation goodsStoreRelation);
    /*修改*/
    void storRelationUpdate(GoodsStoreRelation goodsStoreRelation);
    /*查询过滤*/
    List<GoodsStoreRelation> goodsStoreRelationList(@Param("siteId") Integer siteId,@Param("goodsIds") List<Integer> goodsIds);
    /*查询门店*/
    String goodsStoreRelationStoreIds(@Param("siteId") Integer siteId,@Param("goodsId") Integer goodsId);

}
