package com.jk51.modules.promotions.sequence.app;

import com.gexin.fastjson.JSON;
import com.gexin.fastjson.JSONObject;
import com.gexin.fastjson.TypeReference;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.coupon.tags.TagsGoodsPromotions;
import com.jk51.model.coupon.tags.TagsParam;
import com.jk51.model.order.Member;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.rule.DiscountRule;
import com.jk51.model.promotions.rule.FreePostageRule;
import com.jk51.model.promotions.rule.GiftRule;
import com.jk51.model.promotions.sequence.SequenceParam;
import com.jk51.model.promotions.sequence.SequenceResult;
import com.jk51.model.promotions.sequence.app.AppSequenceBlock;
import com.jk51.model.promotions.sequence.app.AppSequenceGoods;
import com.jk51.model.promotions.sequence.app.AppSequenceResult;
import com.jk51.modules.coupon.tags.PromotionsTagsFilter;
import com.jk51.modules.promotions.constants.PromotionsConstant;
import com.jk51.modules.promotions.sequence.SequenceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.util.*;

import static com.dyuproject.protostuff.MapSchema.MessageFactories.HashMap;
import static com.jk51.modules.promotions.constants.PromotionsConstant.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Created by javen73 on 2018/5/8.
 */
public class AppSequenceImpl extends SequenceAdapter<AppSequenceImpl> {
    public ServletContext servletContext;

    public List<PromotionsActivity> collection;
    public Set<Integer> goodsCollection = new HashSet<Integer>();
    private final Logger log = LoggerFactory.getLogger(AppSequenceImpl.class);

    String GOODS_URL = null;

    /**
     * step.1 收集活动，并筛选掉不符合的活动
     *
     * @return 返回对象本身，链式编程
     * @throws Exception
     */
    @Override
    public AppSequenceImpl collection() throws Exception {
        //查询出所有的广告显示的活动
        List<PromotionsActivity> showAd2App = activityMapper.findShowAd2App(param.getSiteId());
        showAd2App = showAd2App.stream()
            .filter(pa -> {
                return Objects.nonNull(pa.getPromotionsRule());
            })
            .filter(pa->{
                //TODO 需求暂时不显示满赠活动
                return pa.getPromotionsRule().getPromotionsType()!=PROMOTIONS_RULE_TYPE_GIFT;
            })
            .collect(toList());
        //过滤活动
        collection = promotionsFilterService.getPromotionsActivitiesByCheckedNotGoods(showAd2App, param.getSiteId(), param.getMemberId());
        return this;
    }

    /**
     * step.2 对参加活动的商品进行转化，包装成能够统一处理的商品类
     *
     * @return 返回对象本身，链式编程
     * @throws Exception
     */
    @Override
    public AppSequenceImpl processGoods() throws Exception {
        collection.forEach(pa -> {
            PromotionsRule promotionsRule = pa.getPromotionsRule();
            String rule = promotionsRule.getPromotionsRule();
            if (Objects.nonNull(rule)) {
                Set<Integer> goodsIds = converseRuleTypeFindGoodsId(promotionsRule.getPromotionsType(), rule);
                goodsCollection.addAll(goodsIds);
            }
        });
        return this;
    }



    /**
     * step.4 对排序商品block 中的活动进行标签解析
     * step.3 排序解析过程在抽象类中实现，解析器实现类需要使用者提供
     *
     * @return 返回对象本身，链式编程
     * @throws Exception
     */
    public AppSequenceImpl processTags() throws Exception {
        //标签解析类需要参数，但goodsIds和tagsNum并不是必须
        TagsParam tags = new TagsParam(param.getSiteId(), param.getMemberId(), "0", Integer.MAX_VALUE);
        //创建标签解析类
        PromotionsTagsFilter tagsFilter = new PromotionsTagsFilter(servletContext, tags);
        tagsFilter.currentTags = TagsGoodsPromotions.buildTagsGoodsPromotions("0");
        tagsFilter.TYPE_ENV = 10; //假数据 都是假数据，假的都是假的 我在自己骗自己
        //获取Block
        AppSequenceResult sequenceResult = (AppSequenceResult) result.get();
        List<AppSequenceBlock> block = sequenceResult.block;

        block.forEach(asb -> {
            PromotionsActivity activity = asb.getActivity();
            List<PromotionsActivity> promotionsActivities = new ArrayList<PromotionsActivity>() {{
                add(activity);
            }};
            try {
                //------添加goodsId对标签进行匹配------start
                Object goodsIdObj = asb.sequenceGoods.getGoods().get("goodsId");
                if (Objects.nonNull(goodsIdObj)){
                    tagsFilter.currentTags = TagsGoodsPromotions.buildTagsGoodsPromotions(goodsIdObj.toString());
                }
                //------添加goodsId对标签进行匹配------end
                String tag = tagsFilter.resolveRouter(promotionsActivities, activity.getPromotionsRule().getPromotionsType());
                if(activity.getPromotionsRule().getPromotionsType() == PROMOTIONS_RULE_TYPE_FREE_POST)
                    tag  = resolveFreePost(activity);
                if (StringUtil.isNotBlank(tag)) {
                    asb.sequenceGoods.setTag(tag);
                }
                Object goodsId = asb.sequenceGoods.getGoods().get("goodsId");
                if(Objects.nonNull(goodsId)){
                    int id = Integer.parseInt(goodsId.toString());
                    //设置qr码
                    asb.sequenceGoods.setQrURL(this.GOODS_URL+id);

                }
            } catch (Exception e) {
                log.error("解析活动标签失败:{}", e);
            }
        });
        return this;
    }
    private String resolveFreePost(PromotionsActivity pa) {
        PromotionsRule promotionsRule = pa.getPromotionsRule();
        FreePostageRule freePostageRule = JSON.parseObject(promotionsRule.getPromotionsRule(), FreePostageRule.class);
        return "满" + promotionsFilterService.save2Digit(freePostageRule.getMeetMoney() / 100.00d) +
            "元包邮，最多免" + promotionsFilterService.save2Digit(freePostageRule.getReducePostageLimit() / 100.00d) + "元运费";
    }


    public AppSequenceImpl(ServletContext servletContext, SequenceParam param) {
        super(servletContext, param);
        this.servletContext = servletContext;
        this.result = Optional.of(new AppSequenceResult());
        this.GOODS_URL = "http://"+param.getSiteId()+".weixin-run.51jk.com/single/product?goodsId=";
    }
}
