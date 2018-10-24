package com.jk51.modules.distribution.mapper;

import com.jk51.model.Goods;
import com.jk51.model.distribute.GoodsDistribute;
import com.jk51.model.distribute.QueryGoodsDistribute;
import com.jk51.model.goods.PageData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yeah
 * 创建日期: 2017-04-14
 * 修改记录:
 */
@Mapper
public interface GoodsDistributeMapper {

    GoodsDistribute selectByGoodsIdAndSiteId(@Param("siteId") Integer siteId, @Param("goodsId") int goodsId);

    GoodsDistribute queryByGoodsIdAndSiteId(@Param("siteId") Integer siteId, @Param("goodsId") int goodsId);

    List<PageData> findGoodsDistribute(QueryGoodsDistribute queryGoodsDistribute);

    int changeById(String id);

    int updateDistributionTemplate(QueryGoodsDistribute queryGoodsDistribute);

    List<PageData> queryGoodsDistributeBytempId(QueryGoodsDistribute queryGoodsDistribute);


    int updategoodsDistributeBymodelid(int modelid,int siteId);

    int insertgoodsDistribute(@Param("goods")Goods goods,@Param("tempid")int tempid);

    int updateDistributionTemplateBysiteIdAndGoodsId(int tempid,int siteId,int goodsId);
}
