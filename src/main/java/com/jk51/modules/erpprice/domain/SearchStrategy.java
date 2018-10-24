package com.jk51.modules.erpprice.domain;

import com.jk51.model.erpprice.BGoodsErp;
import com.jk51.modules.erpprice.mapper.BGoodsErpMapper;

import java.util.List;
import java.util.Map;

/**
 * @author
 * erp商品价格查询策略接口
 */
public interface SearchStrategy {
    /**
     * 查询
     * @param mapper
     * @return
     */
    List<BGoodsErp> search(BGoodsErpMapper mapper);

    /**
     * 转换成map
     * @return
     */
    Map<Integer, BGoodsErp> convertMap(List<BGoodsErp> erpGoods);
}
