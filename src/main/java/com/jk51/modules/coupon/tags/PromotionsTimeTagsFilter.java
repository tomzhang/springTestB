package com.jk51.modules.coupon.tags;

import com.gexin.fastjson.JSON;
import com.jk51.model.coupon.tags.TagsGoodsPromotions;
import com.jk51.model.coupon.tags.TagsParam;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.rule.FreePostageRule;
import com.jk51.modules.promotions.constants.PromotionsConstant;

import javax.servlet.ServletContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.jk51.modules.promotions.constants.PromotionsConstant.PROMOTIONS_RULE_TYPE_FREE_POST;
import static com.jk51.modules.promotions.constants.PromotionsConstant.PROMOTIONS_RULE_TYPE_GIFT;
import static com.jk51.modules.promotions.constants.PromotionsConstant.PROMOTIONS_RULE_TYPE_GROUP_BOOKING;
import static java.util.stream.Collectors.toList;

/**
 * Created by javen73 on 2018/5/12.
 */
public class PromotionsTimeTagsFilter extends PromotionsTagsFilter {
    private LocalDateTime now = LocalDateTime.now();

    /**
     * 根据时间快结束的进行排序
     *
     * @return
     */
    public PromotionsTimeTagsFilter sorted() {
        tags.forEach(tg -> {
            List<PromotionsActivity> rules = tg.getRules();
            List<PromotionsActivity> list = rules.stream()
                .filter(pa -> {
                    //过滤活动是拼团的，因为app内无拼团活动
                    return pa.getPromotionsRule().getPromotionsType() != PROMOTIONS_RULE_TYPE_GROUP_BOOKING;

                })
/*                .filter(pa->{
                    return pa.getPromotionsRule().getPromotionsType()!=PROMOTIONS_RULE_TYPE_GIFT;//暂时隐藏赠品
                })*/
                .filter(pa -> {
                    long between = ChronoUnit.HOURS.between(now, pa.getEndTime());
                    if (between >= 0) {
                        return true;
                    }
                    return false;
                })
                .sorted(Comparator.comparing(PromotionsActivity::getEndTime)).collect(toList());
            tg.setRules(list);
        });
        return this;
    }

    @Override
    public PromotionsTagsFilter resolve() throws Exception {
        for (TagsGoodsPromotions tg : tags) {
            for (PromotionsActivity pa : tg.getRules()) {
                if(pa.getPromotionsRule().getPromotionsType() == PROMOTIONS_RULE_TYPE_GROUP_BOOKING)
                    continue;
                List<PromotionsActivity> list = new ArrayList<PromotionsActivity>() {{
                    add(pa);
                }};
                //设置当前活动标签解析   --假数据，防止空指针
                super.currentTags = TagsGoodsPromotions.buildTagsGoodsPromotions(tg.getGoodsId());
                //当前解析的活动类型
                super.TYPE_ENV = pa.getPromotionsRule().getPromotionsType();
                String tag = resolveRouter(list, pa.getPromotionsRule().getPromotionsType());
                String resolveTime = this.promotionsActivityService.resolveTime(pa.getEndTime(), now);
                if (Objects.nonNull(resolveTime)) {
                    //包邮商品判断
                    if (Objects.isNull(tag) && TYPE_ENV == PROMOTIONS_RULE_TYPE_FREE_POST)
                        tag = resolveFreePost(pa);
                    String s = tag /*+ "(" + resolveTime + ")"*/;
                    if (tg.getTags().size() < super.tagsParam.getTagsNum())
                        tg.getTags().add(s);
                }
            }
        }
        return this;
    }

    private String resolveFreePost(PromotionsActivity pa) {
        PromotionsRule promotionsRule = pa.getPromotionsRule();
        FreePostageRule freePostageRule = JSON.parseObject(promotionsRule.getPromotionsRule(), FreePostageRule.class);
        return "满" + promotionsFilterService.save2Digit(freePostageRule.getMeetMoney() / 100.00d) +
            "元包邮，最多免" + promotionsFilterService.save2Digit(freePostageRule.getReducePostageLimit() / 100.00d) + "元运费";
    }

    private void resolveTime() {
    }

    public PromotionsTimeTagsFilter(ServletContext servletContext, TagsParam tagsParam) {
        super(servletContext, tagsParam);
    }
}
