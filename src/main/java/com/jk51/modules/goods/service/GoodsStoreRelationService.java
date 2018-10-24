package com.jk51.modules.goods.service;

import com.jk51.model.GoodsStoreRelation;
import com.jk51.modules.goods.mapper.GoodsStoreRelationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 商品门店关联
 * Created by Administrator on 2018/6/29.
 */
@Service
public class GoodsStoreRelationService {
    private static final Logger logger = LoggerFactory.getLogger(GoodsStoreRelationService.class);
    @Autowired
    private GoodsStoreRelationMapper goodsStoreRelationMapper;

    /**
     * 新增
     *
     * @param goodsStoreRelation
     */
    public void storeRelationAdd(GoodsStoreRelation goodsStoreRelation) {
        goodsStoreRelationMapper.storeRelationAdd(goodsStoreRelation);
    }

    /**
     * 修改
     *
     * @param goodsStoreRelation
     */
    public void storRelationUpdate(GoodsStoreRelation goodsStoreRelation) {
        goodsStoreRelationMapper.storRelationUpdate(goodsStoreRelation);
    }

    /**
     * 查询
     * @param siteId
     * @param goodsId
     * @return
     */
    public String goodsStoreRelationStoreIds(Integer siteId,Integer goodsId) {

        String store = goodsStoreRelationMapper.goodsStoreRelationStoreIds(siteId,goodsId);

        return store;

    }

}
