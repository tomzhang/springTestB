package com.jk51.modules.task.domain.count;

import com.jk51.model.task.TCounttype;
import com.jk51.modules.task.domain.TQuotaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CounterFactory {
    public static AbstractCounter create(Byte type) {
        switch (type) {
            case 10:
                // 订单数量
//                return new TradesNumCounter();
                return new TradesMetaCounter(TQuotaType.TRADES_NUM);
            case 20:
                // 商品销售数量
//                return new GoodsSaleNumCounter();
                return new TradesMetaCounter(TQuotaType.GOODS_SALE_NUM);
            case 30:
                // 商品总价
                return new TradesMetaCounter(TQuotaType.TRADES_GOODS_PRICE_SUM);
            case 40:
                return new TradesMetaCounter(TQuotaType.GOODS_PRICE_SUM);
            case 50:
                return new TradesMetaCounter(TQuotaType.TRADES_REAL_PRICE_SUM);
            case 60:
                return new TradesMetaCounter(TQuotaType.GOODS_REAL_SUM);
            case 70:
                // 会员注册
                return new RegisterMemberNumCounter();
            default:
                // 答题统计 不按时间统计数据但是需要结束时间
                return new AbstractCounter() {
                    @Override
                    public Map<Integer, Long> count(List<TCounttype> tCounttypeList, CountRangeTime countRangeTime) {
                        return new HashMap<>();
                    }
                };
        }
    }
}
