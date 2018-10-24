package com.jk51.modules.promotions.service;

import com.jk51.modules.promotions.request.OrderCombinationsParam;
import com.jk51.modules.promotions.request.OrderDeductionDto;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;

import javax.validation.constraints.AssertFalse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * filename :com.jk51.modules.promotions.service.
 * author   :zw
 * date     :2017/11/2
 * Update   :
 * 分组优惠
 */
@Service
public class OrderCombinationService {

    /**
     * 组合计算价格
     *
     * @param orderDeductionDto 入参
     * @return list
     */
    public List<OrderCombinationsParam> countPrice(OrderDeductionDto orderDeductionDto) {
        return null;
    }

    /**
     * 拆分分组 针对折扣
     */
    public ArrayList divideIntoGroupsByDiscount(List<OrderCombinationsParam> ocpl) {
        ArrayList arrayList = new ArrayList();
        ocpl.stream()
                .filter(ocp -> {
                    if (ocp.getCheckPrice() > 0 || ocp.isExistDiscountPromotions() || ocp.getDiscountPrice() > 0) {
                        return false;
                    }
                    return true;
                })
                .forEach(ocp -> {
                    arrayList.add(ocp.getGoodsId());
                });
        ArrayList discountArray = new ArrayList();
        for (int i = 0; i < arrayList.size(); i++) {
            discountArray.addAll(recursionGroup(i, arrayList));
        }
        return discountArray;
    }

    /**
     * 所有分组情况
     *
     * @param index_     开始下标
     * @param goodsArray 原数据
     * @return
     */
    private ArrayList recursionGroup(Integer index_, ArrayList goodsArray) {
        ArrayList groupArray = new ArrayList();
        String startGoodsId = "";
        for (int i = index_; i < goodsArray.size(); i++) {
            startGoodsId += goodsArray.get(i) + ",";
            for (int j = i + 1; j < goodsArray.size(); j++) {
                groupArray.add(startGoodsId + goodsArray.get(j));
            }
        }
        return groupArray;
    }


}
