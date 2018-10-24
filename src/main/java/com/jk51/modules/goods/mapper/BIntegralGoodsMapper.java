package com.jk51.modules.goods.mapper;

import com.jk51.model.goods.BIntegralGoods;
import com.jk51.modules.integral.model.IntegralGoodsTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-05-27 11:11
 * 修改记录:
 */
@Mapper
public interface BIntegralGoodsMapper {

    Integer insertIntegralGoods(BIntegralGoods integralGoods);

    Integer updateByGoodsId(BIntegralGoods integralGoods);

    BIntegralGoods getBIntegralGoodsByGoodsId(@Param("siteId")Integer siteId,@Param("goodsId")Integer goodsId);

    List<IntegralGoodsTask> listIntegralGoodsTimingOpenTask();

    Integer updateIntegralGoodsStartStatus(@Param("goodsIds") List<String> goodsIds);

    List<IntegralGoodsTask> listIntegralGoodsTimingCloseTask();

    Integer updateIntegralGoodsEndStatus(@Param("goodsIds") List<String> goodsIds);

    Integer deleteIntegralGoods(@Param("id")Long id,@Param("is_del") Integer isDel);


}
