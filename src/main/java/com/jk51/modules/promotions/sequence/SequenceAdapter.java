package com.jk51.modules.promotions.sequence;


import com.gexin.fastjson.JSON;
import com.jk51.model.promotions.rule.DiscountRule;
import com.jk51.model.promotions.rule.GiftRule;
import com.jk51.model.promotions.sequence.SequenceHandler;
import com.jk51.model.promotions.sequence.SequenceInterface;
import com.jk51.model.promotions.sequence.SequenceParam;
import com.jk51.model.promotions.sequence.SequenceResult;
import com.jk51.modules.coupon.service.CouponActiveForMemberService;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.grouppurchase.mapper.GroupPurChaseMapper;
import com.jk51.modules.grouppurchase.service.GroupPurChaseService;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.promotions.mapper.PromotionsActivityMapper;
import com.jk51.modules.promotions.mapper.PromotionsRuleMapper;
import com.jk51.modules.promotions.service.PromotionsFilterService;
import com.jk51.modules.promotions.utils.OrderDeductionUtils;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.util.*;

import static com.jk51.modules.promotions.constants.PromotionsConstant.*;
import static java.util.stream.Collectors.toSet;

/**
 * Created by javen73 on 2018/5/8.
 */

/**
 * @param <T> 泛型T为自己实现的 SequenceBlock
 */
public abstract class SequenceAdapter<T> implements SequenceInterface<T> {

    public PromotionsRuleMapper promotionsRuleMapper;
    public PromotionsActivityMapper activityMapper;
    public PromotionsFilterService promotionsFilterService;
    public GroupPurChaseService groupPurChaseService;
    public GroupPurChaseMapper groupPurChaseMapper;
    public CouponActiveForMemberService couponActiveForMemberService;
    public OrderDeductionUtils orderDeductionUtils;
    public OrdersMapper ordersMapper;
    public MemberMapper memberMapper;
    public GoodsMapper goodsMapper;
    public SequenceParam param;

    public Optional<? extends SequenceResult> result;

    /**
     * 对商品进行排序，具体解析规则要根据具体实现类
     *
     * @param handler 排序器接口
     * @return
     */
    @Override
    public T processSequence(SequenceHandler handler) throws Exception {
        if (!result.isPresent())
            throw new Exception("未设置返回结果类");
        //执行排序
        handler.sequence(this);
        return (T) this;
    }

    /**
     * 获得活动所参加的商品ID
     *
     * @param promotionsType
     * @param rule
     * @return
     */
    public Set<Integer> converseRuleTypeFindGoodsId(Integer promotionsType, String rule) {
        Set<String> goodsIds = new HashSet<>();
        switch (promotionsType) {
            case PROMOTIONS_RULE_TYPE_GIFT:
                GiftRule giftRule = JSON.parseObject(rule, GiftRule.class);
                List<String> goodsList = Arrays.asList(giftRule.getGoodsIds().split(","));
                goodsIds.addAll(goodsList);
                break;
            case PROMOTIONS_RULE_TYPE_FREE_POST:
            case PROMOTIONS_RULE_TYPE_MONEY_OFF:
            case PROMOTIONS_RULE_TYPE_LIMIT_PRICE:
            case PROMOTIONS_RULE_TYPE_GROUP_BOOKING:
            case PROMOTIONS_RULE_TYPE_DISCOUNT:
                DiscountRule discountRule = JSON.parseObject(rule, DiscountRule.class);
                Integer goodsIdsType = discountRule.getGoodsIdsType();
                if (goodsIdsType == 0) {
                    List<String> discount_all = goodsMapper.queryGoodsIds(param.getSiteId());
                    goodsIds.addAll(discount_all);
                } else if (goodsIdsType == 1) {
                    List<String> discount_in = Arrays.asList(discountRule.getGoodsIds().split(","));
                    goodsIds.addAll(discount_in);
                } else if (goodsIdsType == 2) {
                    List<String> discount_not = Arrays.asList(discountRule.getGoodsIds().split(","));
                    List<String> discount_list = goodsMapper.queryNotInGoods(param.getSiteId(), discount_not);
                    goodsIds.addAll(discount_list);
                }
                break;
        }
        return goodsIds.stream().map(Integer::valueOf).collect(toSet());
    }

    public SequenceAdapter(ServletContext servletContext, SequenceParam param) {
        this.param = param;
        getServiceBean(servletContext);
    }

    private void getServiceBean(ServletContext servletContext) {
        BeanFactory beanFactory = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        this.promotionsRuleMapper = beanFactory.getBean(PromotionsRuleMapper.class);
        this.activityMapper = beanFactory.getBean(PromotionsActivityMapper.class);
        this.promotionsFilterService = beanFactory.getBean(PromotionsFilterService.class);
        this.ordersMapper = beanFactory.getBean(OrdersMapper.class);
        this.memberMapper = beanFactory.getBean(MemberMapper.class);
        this.goodsMapper = beanFactory.getBean(GoodsMapper.class);
        this.groupPurChaseService = beanFactory.getBean(GroupPurChaseService.class);
        this.groupPurChaseMapper = beanFactory.getBean(GroupPurChaseMapper.class);
        this.couponActiveForMemberService = beanFactory.getBean(CouponActiveForMemberService.class);
        this.orderDeductionUtils = beanFactory.getBean(OrderDeductionUtils.class);
    }
}
