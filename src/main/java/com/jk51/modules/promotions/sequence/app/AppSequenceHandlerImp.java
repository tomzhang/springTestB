package com.jk51.modules.promotions.sequence.app;

import com.gexin.fastjson.JSONObject;
import com.gexin.fastjson.TypeReference;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.model.order.Member;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.sequence.SequenceHandler;
import com.jk51.model.promotions.sequence.SequenceInterface;
import com.jk51.model.promotions.sequence.SequenceParam;
import com.jk51.model.promotions.sequence.SequenceResult;
import com.jk51.model.promotions.sequence.app.AppSequenceBlock;
import com.jk51.model.promotions.sequence.app.AppSequenceGoods;
import com.jk51.model.promotions.sequence.app.AppSequenceResult;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.promotions.mapper.PromotionsActivityMapper;
import com.jk51.modules.promotions.mapper.PromotionsRuleMapper;
import com.jk51.modules.promotions.service.PromotionsFilterService;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

/**
 * Created by javen73 on 2018/5/10.
 * 排序器实现，及排序块处理
 */
public class AppSequenceHandlerImp implements SequenceHandler {
    private SequenceParam param;
    private Set<Integer> goodsCollection;
    private List<PromotionsActivity> collection;

    private MemberMapper memberMapper;
    private GoodsMapper goodsMapper;
    private OrdersMapper ordersMapper;
    private PromotionsFilterService promotionsFilterService;
    private Integer counter = 0;

    private List<Map> sequenceGoodsBefore(SequenceResult srs) throws Exception {
        //将Sequence强转为与handler类型一致的
        AppSequenceResult sequenceResult = (AppSequenceResult) srs;
        Member member = memberMapper.getMemberByMemberId(param.getSiteId(), param.getMemberId());
        if (Objects.isNull(member))
            throw new Exception("未查询到该用户");
        if (Objects.isNull(goodsCollection) || goodsCollection.size() == 0)
            throw new Exception("未查询活动的商品");
        //商品根据历史的购买优先排序，并对参加展示的商品进行分页处理
        PageHelper.startPage(param.getPage(), param.getPageSize());
        List<Map> goodsMap = goodsMapper.selectGoodsAndImgInGoodsId(param.getSiteId(), new ArrayList<Integer>(goodsCollection));
        PageInfo<Map> info = new PageInfo<Map>(goodsMap);
        List<Map> result = info.getList();
        sequenceResult.setPages(info.getPages());
        sequenceResult.setTotals(info.getTotal());
        sequenceResult.setPage(info.getPageNum());

        //排序List
        List<Map> goodsSequence = new ArrayList<>();
        //收集完所有的活动商品ID
        List<Integer> historyGoods = ordersMapper.findHistoryGoods(param.getSiteId(), member.getBuyerId());
/*        List<Map> residue = result.stream().filter(map -> {
            Integer goodsId = (Integer) map.get("goodsId");
            for (Integer hid : historyGoods) {
                if (Objects.equals(goodsId, hid)) {
                    goodsSequence.add(map);
                    return false;
                }
            }
            return true;
        }).collect(toList());
        //将剩余的加入
        goodsSequence.addAll(residue);*/
        //将排序完的商品进行重新归纳到活动中去
        for (Integer historyGood : historyGoods) {
            for (Map map : result) {
                Integer goodsId = (Integer) map.get("goodsId");
                if (Objects.equals(goodsId, historyGood)) {
                    goodsSequence.add(map);
                    result.remove(map);
                    break;
                }
            }
        }
        goodsSequence.addAll(result);

        return goodsSequence;
    }

    @Override
    public void sequence(SequenceInterface sequence) throws Exception {
        AppSequenceImpl appSequence = checkedAndBefore(sequence);
        if (Objects.isNull(collection) || collection.size() < 1)
            return;
        SequenceResult result = appSequence.result.get();
        List<Map> sequenceGoodsBefore = sequenceGoodsBefore(result);
        //与活动进行匹配
        sequenceGoodsBefore.forEach(goodsMap -> {
            for (PromotionsActivity pa : collection) {
                boolean convertSequence = convertSequence(goodsMap, pa, result);
                if (convertSequence) {
                    //已匹配成功，无需再次匹配
                    break;
                }
            }
        });
    }

    private AppSequenceImpl checkedAndBefore(SequenceInterface sequence) {
        checkNotNull(sequence);
        AppSequenceImpl appSequence = (AppSequenceImpl) sequence;
        getServiceBean(appSequence.servletContext);
        this.param = checkNotNull(appSequence.param);
        this.collection = checkNotNull(appSequence.collection);
        this.goodsCollection = checkNotNull(appSequence.goodsCollection);
        return appSequence;
    }

    private void getServiceBean(ServletContext servletContext) {
        BeanFactory beanFactory = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        this.ordersMapper = beanFactory.getBean(OrdersMapper.class);
        this.memberMapper = beanFactory.getBean(MemberMapper.class);
        this.goodsMapper = beanFactory.getBean(GoodsMapper.class);
        this.promotionsFilterService = beanFactory.getBean(PromotionsFilterService.class);
    }

    private boolean convertSequence(Map goodsMap, PromotionsActivity pa, SequenceResult result) {
        PromotionsRule promotionsRule = pa.getPromotionsRule();
        String goodsId = goodsMap.get("goodsId").toString();

        boolean ifExcuseGoods = promotionsFilterService.isIfExcuseGoods(promotionsRule, goodsId);
        if (ifExcuseGoods) {
            //校验，该商品是否已加入结果集
            if (result.block.size() > 0) {
                long count = result.block.stream().filter(asb -> {
                    String asb_goods = asb.sequenceGoods.getGoods().get("goodsId").toString();
                    if (Objects.equals(goodsId, asb_goods))
                        return true;
                    return false;
                }).count();
                //结果集中已有数据，不再添加，直接返回false
                if (count > 0)
                    return false;
            }
            AppSequenceGoods goods = new AppSequenceGoods(goodsMap, counter++);
            AppSequenceBlock appSequenceBlock = new AppSequenceBlock();
            appSequenceBlock.sequenceGoods = goods;
            appSequenceBlock.setActivity(pa);
            result.block.add(appSequenceBlock);
            return true;
        }
        return false;
    }

}
