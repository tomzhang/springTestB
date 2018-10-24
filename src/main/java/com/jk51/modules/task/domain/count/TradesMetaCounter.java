package com.jk51.modules.task.domain.count;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.task.TCounttype;
import com.jk51.modules.task.domain.StoreAndAdminCombId;
import com.jk51.modules.task.domain.TQuotaType;
import com.jk51.modules.task.domain.TradesSale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

import static com.jk51.modules.task.domain.TQuotaType.GOODS_SALE_NUM;
import static com.jk51.modules.task.domain.TQuotaType.TRADES_GOODS_PRICE_SUM;
import static java.util.stream.Collectors.*;

/**
 * 订单数据统计
 */
public class TradesMetaCounter extends AbstractCounter {
    public static final Logger logger = LoggerFactory.getLogger(TradesMetaCounter.class);

    private TQuotaType tQuotaType;

    private CountRangeTime rangeTime;

    private List<Integer> goodsIds;

    private Map<Integer, Integer> adminIdOfStoreId;

    private List<Integer> joinIds;

    public TradesMetaCounter(TQuotaType tQuotaType) {
        this.tQuotaType = tQuotaType;
    }

    public void init() {
        logger.info("统计订单时间 {}", rangeTime);
        goodsIds = getGoodsIds();
        // 获取商户下所有门店店员
        List<StoreAndAdminCombId> storeAndAdminCombIds = selectStoreAdmid(bTaskplan.getSiteId());
        // 店员id对应门店id
        adminIdOfStoreId = getAdminIdOfStoreId(storeAndAdminCombIds);
        joinIds = StringUtil.split(bTaskplan.getJoinIds(), ",", Integer::parseInt);
        if (bTaskplan.getJoinType() == 10) {
            // 计划参与对象如果是门店取出所有店员
            Map<Integer, List<Integer>> storeIdOfadminList = getStoreIdOfadminList(storeAndAdminCombIds);
            if (Objects.nonNull(storeIdOfadminList)) {
                joinIds = joinIds.stream().flatMap(storeId -> storeIdOfadminList.get(storeId).stream()).collect(toList());
            }
        }
    }

    @Override
    public Map<Integer, Long> count(List<TCounttype> tCounttypeList, CountRangeTime rangeTime) {
        this.rangeTime = rangeTime;
        init();
        Stream<TradesSale> tradesSalePipe = tCounttypeList.stream()
            .flatMap(this::packingBOrdersStream)
            .filter(this::filterNotRecommend)
            .filter(this::filterNotInRecommendUserId);

        if (!(tQuotaType.equals(GOODS_SALE_NUM) || tQuotaType.equals(TRADES_GOODS_PRICE_SUM))) {
            // 不是商品销售数量和商品金额的统计 进行订单数据的去重
            tradesSalePipe = tradesSalePipe.distinct();
        }

        // 根据奖励对象统计
        return tradesSalePipe.collect(groupingBy(this::getRewardId, reducing(0L, this::reducByType, Long::sum)));
    }

    public Long reducByType(TradesSale tradesSale) {
        switch (tQuotaType) {
            case GOODS_PRICE_SUM:
                return tradesSale.getTotalFee().longValue();
            case GOODS_REAL_SUM:
                return tradesSale.getRealPay().longValue();
            case GOODS_SALE_NUM:
                return tradesSale.getGoodsNum().longValue();
            case TRADES_GOODS_PRICE_SUM:
                return tradesSale.getGoodsPrice().longValue() * tradesSale.getGoodsNum();
            case TRADES_REAL_PRICE_SUM:
                return tradesSale.getRealPay().longValue();
            case TRADES_NUM:
                return 1L;
            default:
                return 1L;
        }
    }

    public List<TradesSale> selectTradesByCountType(TCounttype tCounttype) {
        return taskPlanCountMapper.selectBTradesBySiteIdAndCreateTime(task.getSiteId(),
                tCounttype.getFilterCondition(),
                goodsIds,
                rangeTime.getStartTime(),
                rangeTime.getEndTime());
    }

    /**
     * 过滤非推荐订单
     * @param tradesSale
     * @return
     */
    public boolean filterNotRecommend(TradesSale tradesSale) {
        return tradesSale != null && tradesSale.getStoreUserId() != null && tradesSale.getStoreUserId() != 0;
    }

    /**
     * 过滤销售店员不在计划参与人列表内的订单
     * @param tradesSale
     * @return
     */
    private boolean filterNotInRecommendUserId(TradesSale tradesSale) {
        return joinIds.contains(tradesSale.getStoreUserId());
    }

    public Stream<TradesSale> packingBOrdersStream(TCounttype tCounttype) {
        List<TradesSale> tradesSales = selectTradesByCountType(tCounttype);
        if (tradesSales == null) {
            tradesSales = new ArrayList<>();
        }

        return tradesSales.stream();
    }

    public Integer getRewardId(TradesSale tradesSale) {
        int id;
        if (task.getObject() == 10) {
            id = Optional.ofNullable(adminIdOfStoreId.get(tradesSale.getStoreUserId())).orElse(0);
        } else {
            id = Optional.ofNullable(tradesSale.getStoreUserId()).orElse(0);
        }

        return id;
    }
}
