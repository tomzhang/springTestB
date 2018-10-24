package com.jk51.cache;

import com.jk51.model.Goods;
import com.jk51.modules.goods.mapper.GoodsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 文件名:com.jk51.cache.CacheService
 * 描述: 演示缓存的使用方式
 * 作者: wangzhengfei
 * 创建日期: 2017-01-16
 * 修改记录:
 */
@Service
public class CacheService {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);

    @Autowired
    private GoodsMapper goodsMapper;


    /**
     *
     * @param goods
     * @return
     */
    @CachePut(value = "goods", key = "'goods_'.concat(#goods.goods_id.toString())")
    public Goods updateGoods(Goods goods){
        goodsMapper.update(goods.getGoodsId().toString(),goods.getDrugName());
        logger.info("数据库修改商品drugName属性完成,新值"+goods.getDrugName());
        return goods;
    }

    /**
     * 查询商品，先查询缓存
     * @param id
     * @return
     */
    @Cacheable(value="goods",key = "'goods_'.concat(#id)")
    public Goods queryGoods(String id){
        logger.info("通过数据库查询商品ID[{}]信息",id);
        return goodsMapper.get(id);
    }

    /**
     * 淘汰缓存
     * @param id
     */
    @CacheEvict(value = "goods",key = "'goods_'+#id")
    public void delGoods(String id){
        logger.info("通过数据库删除掉商品ID[{}]信息",id);
    //模拟删除一个商品,演示用就不做真正删除
}



}
