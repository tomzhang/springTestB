package com.jk51.modules.goods.mapper;

import com.jk51.model.GoodsExtd;
import com.jk51.model.GoodsExtdWithBLOBs;


public interface GoodsExtdMapper {
    int deleteByPrimaryKey(Integer goodsextdId);

    int insert(GoodsExtdWithBLOBs record);

    int insertSelective(GoodsExtdWithBLOBs record);

    GoodsExtdWithBLOBs selectByPrimaryKey(Integer goodsextdId);

    int updateByPrimaryKeySelective(GoodsExtdWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(GoodsExtdWithBLOBs record);

    int updateByPrimaryKey(GoodsExtd record);

    int add(GoodsExtdWithBLOBs record);

    boolean updateByGoodsId(GoodsExtdWithBLOBs record);
}