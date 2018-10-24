package com.jk51.modules.promotions.sequence.wechat;

import com.gexin.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.Goods;
import com.jk51.model.coupon.requestParams.SignMembers;
import com.jk51.model.merchant.MemberLabel;
import com.jk51.model.order.Member;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.rule.DiscountRule;
import com.jk51.model.promotions.rule.FixedPriceRule;
import com.jk51.model.promotions.rule.GroupBookingRule;
import com.jk51.model.promotions.sequence.*;
import com.jk51.model.promotions.sequence.wechat.WechatSequenceBlock;
import com.jk51.model.promotions.sequence.wechat.WechatSequenceGoods;
import com.jk51.model.promotions.sequence.wechat.WechatSequenceResult;
import com.jk51.modules.coupon.utils.CouponProcessUtils;
import com.jk51.modules.persistence.mapper.UserSpecificDiscountMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static com.jk51.modules.promotions.constants.PromotionsConstant.*;
import static java.util.stream.Collectors.toList;

/**
 * Created by javen73 on 2018/5/14.
 */
public class WechatSequenceHandlerImpl implements SequenceHandler {


    /**
     * 排序器的实现方法
     * @param sequence 此处与Sequence严重耦合，Sequence必须使用该类，该类必须依赖Sequence
     * @throws Exception
     * TODO 耦合优化
     */
    @Override
    public void sequence(SequenceInterface sequence) throws Exception {
        //将sequence强转为与handler一致的sequence
        WechatSequenceImpl wechatSequence = (WechatSequenceImpl) sequence;
        //判断结果集是否存在
        if (wechatSequence.result.isPresent()) {
            //活动Sequence结果集
            WechatSequenceResult sequenceResult = (WechatSequenceResult) wechatSequence.result.get();
            switch (wechatSequence.param.getPromotionType()) {
                case PROMOTIONS_RULE_TYPE_LIMIT_PRICE:
                case PROMOTIONS_RULE_TYPE_GROUP_BOOKING:
                    //处理拼团和限价
                    if (sequenceResult.endBlock.goodsIds.size() > 0) {
                        List<Map> goodsSequence = sequenceProcess(wechatSequence, sequenceResult.endBlock);
                        List<WechatSequenceGoods> goodsList = createSequenceGoods(goodsSequence);
                        sequenceResult.endBlock.setGoods(goodsList);
                    }
                    if (sequenceResult.startBlock.goodsIds.size() > 0) {
                        List<Map> goodsSequence = sequenceProcess(wechatSequence, sequenceResult.startBlock);
                        List<WechatSequenceGoods> goodsList = createSequenceGoods(goodsSequence);
                        sequenceResult.startBlock.setGoods(goodsList);
                    }
                    break;
                case PROMOTIONS_RULE_TYPE_MONEY_OFF:
                case PROMOTIONS_RULE_TYPE_FREE_POST:
                case PROMOTIONS_RULE_TYPE_DISCOUNT:
                case PROMOTIONS_RULE_TYPE_GIFT:
                    if (sequenceResult.block.size() > 0) {
                        for (WechatSequenceBlock block : sequenceResult.block) {
                            List<Map> goodsSequence = sequenceProcess(wechatSequence, block);
                            List<WechatSequenceGoods> goodsList = createSequenceGoods(goodsSequence);
                            block.setGoods(goodsList);
                        }
                    }
                    break;
                default:
                    throw new RuntimeException("未知类型,收集活动失败");
            }

        }
    }

    /**
     * 对商品进行封装
     * @param goodsSequence
     * @return
     */
    private List<WechatSequenceGoods> createSequenceGoods(List<Map> goodsSequence) {
        List<WechatSequenceGoods> goodsList = new ArrayList<>();
        for (int i = 0; i < goodsSequence.size(); i++) {
            goodsList.add(new WechatSequenceGoods(goodsSequence.get(i), i));
        }
        return goodsList;
    }

    /**
     * 排序处理
     * @param wechatSequence
     * @param block
     * @return
     * @throws Exception
     */
    private List<Map> sequenceProcess(WechatSequenceImpl wechatSequence, WechatSequenceBlock block) throws Exception {
        SequenceParam param = wechatSequence.param;

        if(Objects.isNull(block.goodsIds) || block.goodsIds.size()==0){
            throw new Exception("商品为空，排序异常");
        }
        //todo 进行商品过滤
        //1.根据siteId查出所有独立活动的信息 //先将独立活动查出
        List<PromotionsActivity> independentPromotionsActivityList = wechatSequence.activityMapper.getIndependentPromotionsActivityBySiteId(param.getSiteId());
        //如果当前活动为拼团活动，则不做活动处理操作
        if (PROMOTIONS_RULE_TYPE_GROUP_BOOKING != param.getPromotionType() && CollectionUtils.isNotEmpty(independentPromotionsActivityList)){

            //2.若指定活动 和 独立活动 都不为null 则遍历两个活动列表并进行goodsId 的比较，
            // 若两个活动中有相同的goodsId 则在指定活动中移除对应的goodsId
            //当前活动类型
            Integer promotionType = param.getPromotionType();
            //依次对goodsIds中的id进行审核，如果其中有一条活动匹配到了该商品，则将该商品移除
            List<PromotionsActivity> independentPromotions = null;
            independentPromotions = independentPromotionsActivityList.stream().filter(pa -> {
                Integer currentPaType = pa.getPromotionsRule().getPromotionsType();
                switch (promotionType) {//过滤比当前活动优惠等级高的，参加商品的id（若proType的级别比promotionType高，则将block.getGoodsIds()中的商品id过滤掉）
                    case PROMOTIONS_RULE_TYPE_GROUP_BOOKING:
                        return false;
                    case PROMOTIONS_RULE_TYPE_LIMIT_PRICE:
                        if (CALCULATE_SEQUENCE.get(PROMOTIONS_RULE_TYPE_LIMIT_PRICE).contains(currentPaType)) {
                            return true;
                        }
                        break;
                    case PROMOTIONS_RULE_TYPE_DISCOUNT:
                        if (CALCULATE_SEQUENCE.get(PROMOTIONS_RULE_TYPE_DISCOUNT).contains(currentPaType)) {
                            return true;
                        }
                        break;
                    case PROMOTIONS_RULE_TYPE_MONEY_OFF:
                        if (CALCULATE_SEQUENCE.get(PROMOTIONS_RULE_TYPE_MONEY_OFF).contains(currentPaType)){
                            return true;
                        }
                        break;
                    case PROMOTIONS_RULE_TYPE_GIFT:
                        if (CALCULATE_SEQUENCE.get(PROMOTIONS_RULE_TYPE_GIFT).contains(currentPaType)){
                            return true;
                        }
                        break;
                }
                return false;
            }).collect(toList());
            //对是否是专属会员进行处理
            List<PromotionsActivity> promotionsActivityList = handleMemberTag(param.getSiteId(), param.getMemberId(), independentPromotions);

            if (CollectionUtils.isNotEmpty(promotionsActivityList)){
                promotionsActivityList.stream().forEach(pa -> {
                    //独立活动的活动类型
                    Integer proType = pa.getPromotionsRule().getPromotionsType();
                    switch (promotionType){//过滤比当前活动优惠等级高的，参加商品的id（若proType的级别比promotionType高，则将block.getGoodsIds()中的商品id过滤掉）
                        case PROMOTIONS_RULE_TYPE_GROUP_BOOKING://当前活动是拼团活动
                            break;
                        case PROMOTIONS_RULE_TYPE_LIMIT_PRICE://当前活动是限价活动
                            if (CALCULATE_SEQUENCE.get(PROMOTIONS_RULE_TYPE_LIMIT_PRICE).contains(proType)){
                                //处理独立活动类型为拼团的活动
                                handleGroupBookingOrLimitPrice(block, pa, proType);
                                break;
                            }
                        case PROMOTIONS_RULE_TYPE_DISCOUNT://当前活动是打折活动
                            if (CALCULATE_SEQUENCE.get(PROMOTIONS_RULE_TYPE_DISCOUNT).contains(proType)){
                                //如果独立活动包含拼团活动 和 限价活动，则对当前活动进行商品id处理
                                handleGroupBookingOrLimitPrice(block, pa, proType);
//                                handleGroupOrLimitPrice(block, pa, proType);
                                break;
                            }
                        case PROMOTIONS_RULE_TYPE_MONEY_OFF://当前活动是满减活动
                            if (CALCULATE_SEQUENCE.get(PROMOTIONS_RULE_TYPE_MONEY_OFF).contains(proType)){
                                handleGroupBookingOrLimitPrice(block, pa, proType);
//                                handleGroupOrLimitPrice(block, pa, proType);
                                break;
                            }
                        case PROMOTIONS_RULE_TYPE_GIFT://当前活动是满赠活动
                            if (CALCULATE_SEQUENCE.get(PROMOTIONS_RULE_TYPE_GIFT).contains(proType)){
                                handleGroupBookingOrLimitPrice(block, pa, proType);
//                                handleGroupOrLimitPrice(block, pa, proType);
                                break;
                            }
                    }
                });
            }
        }

        if(Objects.isNull(block.goodsIds) || block.goodsIds.size()==0){
            throw new Exception("商品为空，排序异常");
        }
        PageHelper.startPage(param.getPage(), param.getPageSize());
        List<Map> goodsMap = wechatSequence.goodsMapper.getRecommendGoodsByIds(param.getSiteId(), new ArrayList<Integer>(block.goodsIds));
        PageInfo<Map> info = new PageInfo<Map>(goodsMap);
        List<Map> result = info.getList();
        block.setPages(info.getPages());
        block.setTotals(info.getTotal());
        block.setPage(info.getPageNum());
        //登录用户为空，不做历史购买排序，直接返回查询商品
        boolean memberIsNll = Objects.isNull(param.getMemberId());
        if(memberIsNll)
            return result;

        Member member = wechatSequence.memberMapper.getMemberByMemberId(param.getSiteId(), param.getMemberId());
        if (Objects.isNull(member))
            throw new Exception("未查询到该用户");
        //排序List
        List<Map> goodsSequence = new ArrayList<>();
        //收集完所有的活动商品ID
        List<Integer> historyGoods = wechatSequence.ordersMapper.findHistoryGoods(param.getSiteId(), member.getBuyerId());
        List<Map> residue = result.stream().filter(map -> {
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
        goodsSequence.addAll(residue);
        return goodsSequence;
    }
    //处理拼团或限价
   /* private void handleGroupOrLimitPrice(WechatSequenceBlock block, PromotionsActivity pa, Integer proType) {
        if (PROMOTIONS_RULE_TYPE_GROUP_BOOKING == proType)
            handleGroupBooking(block, pa, proType);
        else if (PROMOTIONS_RULE_TYPE_LIMIT_PRICE == proType)
            handleLimitPrice(block, pa, proType);
    }*/

    //处理独立活动类型为 拼团的活动 活动 限价活动
    private void handleGroupBookingOrLimitPrice(WechatSequenceBlock block, PromotionsActivity pa, Integer proType) {
        //若拼团活动不为null 处理；若拼团活动为null，限价活动不为null 处理 同样处理
        if (PROMOTIONS_RULE_TYPE_GROUP_BOOKING == proType || PROMOTIONS_RULE_TYPE_LIMIT_PRICE == proType){
            //处理独立活动类型为拼团的活动或限价的活动
            //过滤block.getGoodsIds()中的商品id
            String proRuleStr = pa.getPromotionsRule().getPromotionsRule();
            Set<Integer> goodsIdsSet = block.getGoodsIds();

            try {
                GroupBookingRule groupBookingRule = JacksonUtils.json2pojo(proRuleStr, GroupBookingRule.class);
                String goodsIds = groupBookingRule.getGoodsIds();
                Integer goodsIdsType = groupBookingRule.getGoodsIdsType();
                if (StringUtils.isNotBlank(goodsIds) && null != goodsIdsType){
                    //独立活动类型为： 全部商品参加
                    if (0 == goodsIdsType){
                        block.goodsIds.removeAll(goodsIdsSet);
                    }
                    //独立活动类型为： 指定商品参加
                    //独立活动中的商品id
                    List<String> goodsIdsList = Arrays.asList(goodsIds.split(","));
                    //block中的商品id
                    List<Integer> goodsIdlist = new ArrayList<Integer>(goodsIdsSet);
                    if (1 == goodsIdsType){
                        goodsIdlist.forEach(gsId ->{
                            if (null != gsId)
                                if (goodsIdsList.contains(gsId.toString()))
                                    block.goodsIds.remove(gsId);
                        });
                        /*goodsIdsList.retainAll(goodsIdlist);
                        if (null != goodsIdsList && goodsIdsList.size() > 0){
                            block.goodsIds.removeAll(goodsIdsList);
                        }*/
                    }
                    //独立活动类型为： 指定商品不参加  若指定活动的商品id 不在 参加独立活动的商品id中（说明 该商品id只在 指定活动中） 就删除，
                    if (2 == goodsIdsType){
                        goodsIdlist.forEach(gsId -> {
                            if (null != gsId)
                                if (!goodsIdsList.contains(gsId.toString()))
                                    block.goodsIds.remove(gsId);

                        });

                        /*List<Integer> goodsIds1 = goodsIdlist;
                        List<Integer> goodsIds2 = goodsIdlist;
                        goodsIds1.retainAll(goodsIdsList);//只剩下共同显示的id
                        if (CollectionUtils.isNotEmpty(goodsIdsList)){
//                                            goodsIds2.removeAll(goodsIds1);//剩下独立活动中没有的商品id
//                                            goodsIdsList.removeAll(goodsIds2);//将指定商品不参加的id 不在独立活动中的商品id移除
                            block.goodsIds = new HashSet<>(goodsIds1);//在指定商品不参加条件下，只剩下当前活动和独立活动共有的商品id
                        }*/
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Autowired
    private UserSpecificDiscountMapper userSpecificDiscountMapper;
    @Autowired
    private CouponProcessUtils couponProcessUtils;
    //处理会员标签
    private List<PromotionsActivity> handleMemberTag(Integer siteId, Integer memberId, List<PromotionsActivity> promotionsActivityList){
        //通用会员活动
        List<PromotionsActivity> commonPromotionsList = new ArrayList<>();
        //专属会员活动
        List<PromotionsActivity> speciPromotionsList = new ArrayList<>();
        promotionsActivityList.forEach(pa -> {
            String signMermbers = pa.getUseObject();
            if (StringUtils.isNotBlank(signMermbers) && (!signMermbers.equals("null")) && (!signMermbers.equals(""))){
                SignMembers signMembers = com.alibaba.fastjson.JSON.parseObject(signMermbers, SignMembers.class);
                //会员标签类型  0全部会员  1指定标签组会员 2 指定会员 3 指定标签会员
                Integer type = signMembers.getType();
                String promotionMembers = signMembers.getPromotion_members();
                //存放 会员id 或 标签组  或标签
                List<String> memberIdOrLabelList = null;
                if (StringUtils.isNotBlank(promotionMembers)){
                    memberIdOrLabelList = Arrays.asList(promotionMembers.split(","));
                }
                if (0 == type){//0全部会员
                    commonPromotionsList.add(pa);
                }else if (1 == type){//1指定标签组会员 {"type":1,"promotion_members":"1386"} 1386为该标签组
                    if (CollectionUtils.isNotEmpty(memberIdOrLabelList)){
                        //根据 site_id 和指定标签组 获取MemberLabel会员标签对象集合
                        List<MemberLabel> memberLabelList = userSpecificDiscountMapper.getMemberLabelBySiteIdAndLabelGroup(siteId, memberIdOrLabelList).stream()
                            .filter(ml -> StringUtils.isNotBlank(ml.getScene())).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(memberLabelList)){
                            for (MemberLabel ml : memberLabelList) {
                                String scene = ml.getScene();
                                if (StringUtils.isNotBlank(scene)){
                                    Map<String, Object> memberIdsMap = couponProcessUtils.String2Map(scene);
                                    //{"userIds":"15234420,15234436"}
                                    String memberIds = (String)memberIdsMap.get("userIds");
                                    if (StringUtils.isNotBlank(memberIds) && memberIds.equals("null")){
                                        List<String> memIdList = Arrays.asList(memberIds.split(","));
                                        if (CollectionUtils.isNotEmpty(memIdList)){
                                            for (String mId : memIdList) {
                                                if (memberId == Integer.parseInt(mId)){
                                                    speciPromotionsList.add(pa);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else if (2 == type){//2 指定会员{"type":2,"promotion_members":"656784"}
                    if (CollectionUtils.isNotEmpty(memberIdOrLabelList)){
                        for (String memberid : memberIdOrLabelList) {
                            if (memberId == Integer.parseInt(memberid))
                                speciPromotionsList.add(pa);
                        }
                    }
                }else if (3 == type){//3 指定标签会员{"type":3,"promotion_members":"标签库"}
                    if (CollectionUtils.isNotEmpty(memberIdOrLabelList)){
                        //根据site_id 和指定标签 获取指定标签
                        List<String> memberIdList = userSpecificDiscountMapper.getMemberLabelBySiteIdAndLabels(siteId, memberIdOrLabelList);
                        if (CollectionUtils.isNotEmpty(memberIdList)){
                            for (String mId: memberIdList) {
                                if (memberId == Integer.parseInt(mId))
                                    speciPromotionsList.add(pa);
                            }
                        }
                    }
                }
            }else {
                commonPromotionsList.add(pa);
            }
        });
        speciPromotionsList.addAll(commonPromotionsList);
        return speciPromotionsList;
    }
}
