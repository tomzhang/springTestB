package com.jk51.configuration;

/**
 *缓存key的常量
 */

public class CacheSetConfig {
    /**
     * 单个商品的缓存
     */
    public static final String GOODS_CACHE="'goods_one'.concat(#params['siteId']+#params['goodsId'])";

    /**
     * 全部商品分类缓存
     */
    public static final String CATEGERY_CACHE="'categories_list'.concat(#params['siteId'])";;


}
