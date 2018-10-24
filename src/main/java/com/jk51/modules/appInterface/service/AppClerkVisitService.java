package com.jk51.modules.appInterface.service;

import com.alibaba.fastjson.JSON;
import com.github.binarywang.java.emoji.EmojiConverter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.http.HttpClient;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.clerkvisit.BClerkVisit;
import com.jk51.model.coupon.requestParams.CouponFilterParams;
import com.jk51.model.promotions.EasyToSee;
import com.jk51.model.promotions.EasyToSeeParam;
import com.jk51.model.promotions.PromotionsActivity;
import com.jk51.modules.appInterface.util.AuthToken;
import com.jk51.modules.clerkvisit.mapper.BClerkVisitMapper;
import com.jk51.modules.coupon.mapper.CouponActivityMapper;
import com.jk51.modules.coupon.service.CouponFilterService;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.order.mapper.OrdersMapper;
import com.jk51.modules.persistence.mapper.LabelSecondMapper;
import com.jk51.modules.persistence.mapper.RelationLabelMapper;
import com.jk51.modules.promotions.service.PromotionsFilterService;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.management.ObjectName;
import java.io.IOException;
import java.util.*;

/**
 * 回访
 *
 * @auhter zy
 * @create 2017-12-05 15:56
 */
@Service
public class AppClerkVisitService {


    @Autowired
    GoodsMapper goodsMapper;

    @Autowired
    MemberMapper memberMapper;

    @Autowired
    OrdersMapper ordersMapper;

    @Autowired
    PromotionsFilterService promotionsFilterService;

    @Autowired
    CouponActivityMapper couponActivityMapper;

    @Autowired
    RelationLabelMapper relationLabelMapper;

    @Autowired
    CouponFilterService couponFilterService;

    @Autowired
    BClerkVisitMapper bClerkVisitMapper;

    @Autowired
    LabelSecondMapper labelSecondMapper;


    private static EmojiConverter emojiConverter = EmojiConverter.getInstance();

    private static final Logger log = LoggerFactory.getLogger(AppClerkVisitService.class);

    //解析AuthToken
    public AuthToken parseAuthToken(String authToken) {
        String s = new String(Base64.decodeBase64(authToken.getBytes()));
        AuthToken result = JSON.parseObject(s, AuthToken.class);
        return result;
    }

    public String taskGoodsIdsList(int id) {
        return bClerkVisitMapper.taskGoodsIdsList(id);
    }

    //商品信息
    public List<Map<String, Object>> queryGoodsInfo(Integer siteId, String[] goodsId, Integer buyerId, Integer userId) {
        if (("0").equals(goodsId[0])) goodsId = null;
        List<Map<String, Object>> result = goodsMapper.queryVisitGoodsInfo(siteId, goodsId);
        result.stream().forEach(stringObjectMap -> {
            EasyToSeeParam easyToSeeParam = new EasyToSeeParam();
            Integer goods_Id = Integer.valueOf(stringObjectMap.get("goodsId").toString());
            easyToSeeParam.setSiteId(siteId);
            easyToSeeParam.setGoodsIds(goods_Id.toString());
            easyToSeeParam.setUserId(userId);
            List<EasyToSee> easyToSees = promotionsFilterService.activityGroupGoodsIds(easyToSeeParam);
            Map<String, Object> map = new HashMap<>();
            couponFilterService.addCoupons(siteId, easyToSees, goods_Id.toString(), map);
            if (easyToSees.size() > 0) {
                stringObjectMap.put("easyToSees", easyToSees.get(0).getProCouponEasyToSee());
            }
            stringObjectMap.put("buyThisLogMap", queryBuyThisLog(siteId, buyerId, goods_Id));
            //活动详情
            //stringObjectMap.put("goodsActivityList", queryGoodsActivity(siteId, goods_Id, userId));

        });
        return result;
    }

    //最后购买时间,购买次数
    public Map<String, Object> queryBuyThisLog(Integer siteId, Integer buyerId, Integer goodsId) {
        Map<String, Object> map = ordersMapper.queryshoppingLog(siteId, buyerId, goodsId); //第几次买 buyNum lastByTime
        return map;
    }

    //商品现有活动
    public List<PromotionsActivity> queryGoodsActivity(Integer siteId, Integer goodsId, Integer userId) {
        //获取当前商户所有活动
//        List<PromotionsRule> rules = goodsMapper.queryTotalActivitys(siteId);
        //将promotionsRule根据promotionsType转换成对应的实体类, 将timeRule转换成对应实体
        /*rules.stream().forEach(ptr -> {
            Integer promotionsType = ptr.getPromotionsType();
            String promotionsRule = ptr.getPromotionsRule();
            String timeRule = ptr.getTimeRule();
            TimeRuleForPromotionsRule timeRuleForPromotionsRule = JSON.parseObject(timeRule, TimeRuleForPromotionsRule.class);
            switch (promotionsType) {
                case 10 :   //满赠
                    GiftRule giftRule = JSON.parseObject(promotionsRule, GiftRule.class);
                    giftRule
                    break;
                case 20 :   //打折
                    DiscountRule discountRule = JSON.parseObject(promotionsRule, DiscountRule.class);
                    break;
                case 30 :   //包邮
                    FreePostageRule freePostageRule = JSON.parseObject(promotionsRule, FreePostageRule.class);
                    break;
                case 40 :   //满减
                    ReduceMoneyRule reduceMoneyRule = JSON.parseObject(promotionsRule, ReduceMoneyRule.class);
                    break;
                case 50 :   //限价
                    FixedPriceRule fixedPriceRule = JSON.parseObject(promotionsRule, FixedPriceRule.class);
                    break;
                default:
                    break;
            }
        });*/
        CouponFilterParams couponFilterParams = new CouponFilterParams(siteId, goodsId.toString(), userId);
        List<PromotionsActivity> promotionsActivityList = promotionsFilterService.filterByGoodsId(couponFilterParams);
        return promotionsActivityList;
    }

    //顾客信息
    public Map<String, Object> queryCustomerInfo(Integer buyerId, Integer siteId, Integer storeId) {
        //b_member,b_member_info
        Map<String, Object> memberInfo = memberMapper.queryMemberInfoByBuyerId(buyerId, siteId);
        //获取会员经纬度
        Map<String, Object> l = null;
        if (Objects.nonNull(memberInfo)) {
            l = memberMapper.getMembersLngAndLat(siteId, Integer.valueOf(memberInfo.get("member_id").toString()));
        }
        if (Objects.nonNull(l)) {
            //计算距离本店距离
            l.put("siteId", siteId);
            l.put("storeId", storeId);
            Map<String, Object> distance = memberMapper.calculateDistance(l);
            //请求高德接口根据坐标获取会员常活动区域,
            String url = "http://restapi.amap.com/v3/geocode/regeo?output=json&location=" + l.get("userGaodeLng") + "," + l.get("userGaodeLat") + "&key=137816363e19388eae9c693ebe9281ce&radius=1000";
            Map<String, Object> map = null;
            try {
                String result = HttpClient.doHttpGet(url);  //返回结果为json格式
                if (StringUtil.isNotEmpty(result)) {
                    map = JSON.parseObject(result, Map.class);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (Objects.nonNull(map)) {
                Map<String, Object> area = (Map<String, Object>) map.get("regeocode");
                memberInfo.put("alwaysStayArea", area.get("formatted_address"));
            } else {
                memberInfo.put("alwaysStayArea", "无常活动区域");
            }
            memberInfo.put("distance", distance.get("distance"));
        }

        //推测症状
        List<Map<String, Object>> goodsTagIds = goodsMapper.queryGoodsTagIds(siteId, buyerId);//查询用户所有订单中商品的标签ID
        goodsTagIds.stream().filter(stringObjectMap -> stringObjectMap.get("goods_tagsid") != "");
        //标签
        List<Map<String, Object>> memberTags = relationLabelMapper.getRelationLabelAllByMemberId(siteId, buyerId);
        if (memberTags.size() > 0) {
            memberInfo.put("memberTags", memberTags);
        }
        /**
         * 将标签ID作为key,个数作为value
         */
        Map<String, Integer> putTagId = new HashedMap();
        Set<String> set = new HashSet<>();
        goodsTagIds.stream().forEach(tagid -> {
            Object goods_tagsid = tagid.get("goods_tagsid");
            if (Objects.nonNull(goods_tagsid)) {
                String[] split = goods_tagsid.toString().split(",");
                for (int i = 0; i < split.length; i++) {
                    String key = split[i];
                    if (set.add(key)) {//第一次
                        putTagId.put(key, 1);
                    } else {
                        Integer integer = putTagId.get(key);
                        putTagId.put(key, integer++);
                    }
                }
            }
        });
        //查询标签ID对应的标签
        if (set.size() > 0) {
            List<Map<String, Object>> tags = goodsMapper.queryGoodsTags(siteId, set.toArray());
            if (Objects.nonNull(tags)) {
                tags.stream().forEach(tag -> {
                    //标签个数
                    tag.put("num", putTagId.get(tag.get("tagsId").toString()));
                });
            }
            memberInfo.put("speculateSymptom", tags);
        }


        return memberInfo;
    }

    //交易分析
    public Map<String, Object> queryDealAnalyze(Integer siteId, Integer buyerId) {
        Map<String, Object> map = ordersMapper.queryDealAnalyze(siteId, buyerId);
        /**
         * 购买次数 buyGoodsTimes
         * 支付总金额 totalMoney
         * 优惠金额  totalFee + postFee -totalMoney
         */
        if (Objects.nonNull(map) && Objects.nonNull(map.get("totalFee")) && Objects.nonNull(map.get("postFee")) && Objects.nonNull(map.get("totalMoney"))) {
            map.put("discountMoney", Integer.valueOf(map.get("totalFee").toString()) + Integer.valueOf(map.get("postFee").toString()) - Integer.valueOf(map.get("totalMoney").toString()));
            //平均购物时间间隔
            long allInterv = ordersMapper.queryIntervalTime(siteId, buyerId);
            long interv = allInterv / Integer.valueOf(map.get("buyGoodsTimes").toString());
            double intervTime = interv / 86400000; //单位天
            map.put("averageTime", intervTime);

            Map<String, Object> num = ordersMapper.queryshoppingNum(siteId, buyerId);
            Object goodsNum = num.get("goodsNum");
            map.put("shoppingNumMap", goodsNum);     //购物数量
            //客单价   历史总订单金额/购物次数.
            int i = Integer.valueOf(map.get("totalMoney").toString()) / Integer.valueOf(map.get("buyGoodsTimes").toString());
            map.put("perCustomerTransaction", i);
            return map;
        }
        return null;

    }

    //购物数量
    /*public Map<String,Object> queryShoppingNum(Integer siteId, Integer buyerId) {
        Map<String,Object> num = ordersMapper.queryshoppingNum(siteId,buyerId);
        return num;
    }*/

    //回访记录
    public List<Map<String, Object>> queryReturnVisitLog(Integer siteId, Integer buyerId) {
        List<Map<String, Object>> rvlog = ordersMapper.queryReVisitLog(siteId, buyerId);
        if (rvlog.size() > 0) {
            rvlog.stream().forEach(stringObjectMap -> {
                if (Objects.nonNull(stringObjectMap.get("content"))) {
                    stringObjectMap.put("content", emojiConverter.toUnicode(stringObjectMap.get("content").toString()));
                }
            });
        }
        return rvlog;
    }

    ////查询顾客信息
    public Map<String, Object> getCustomerInfo(Integer siteId, Integer buyerId) {
        Map<String, Object> memberInfo = memberMapper.queryMemberInfoByBuyerId(buyerId, siteId);
        return memberInfo;
    }

    /**
     * 查询会员标签
     *
     * @param siteId  100190
     * @param buyerId 769745
     * @return
     */
    public Map<String, Object> getCustomerLabels(Integer siteId, Integer buyerId) {
        Map<String, Object> map = new HashMap<>();
        try {
            Map<String, Object> memberLabelMap = new HashMap<>();
            List<String> memberLabelList = new ArrayList<>();
            //基础标签
            List<String> baseLabel = getBaseLabel(siteId, buyerId);
            memberLabelMap.put("baseLabel", baseLabel);

            //交易标签
            List<String> tradesLabel = getTradesLabel(siteId, buyerId);
            memberLabelMap.put("tradesLabel", tradesLabel);

            //距离标签
            memberLabelList.clear();
            memberLabelMap.put("distanceLabel", memberLabelList);

            //健康标签
            memberLabelList.clear();
            memberLabelMap.put("healthLabel", memberLabelList);

            //自定义标签
            memberLabelList.clear();
            memberLabelMap.put("customLabel", memberLabelList);

            map.put("memberLabelMap", memberLabelMap);
            map.put("msg", "查询会员标签成功");
            map.put("status", 0);
            return map;
        } catch (Exception e) {
            log.info("查询会员标签:{}", e);
            map.put("msg", "查询会员标签失败");
            map.put("status", -1);
            return map;
        }
    }

    //基础标签
    public List<String> getBaseLabel(Integer siteId, Integer buyerId) {
        List<String> list = new ArrayList<>();
        try {
            Map<String, Object> map = labelSecondMapper.getBaseLabel(siteId, buyerId);
            if (!StringUtil.isEmpty(map.get("birthday"))) {
                String birthday = String.valueOf(map.get("birthday"));
                if (!StringUtil.isEmpty(birthday) && !birthday.equals("0000-00-00")) {
                    list.add(birthday);//生日
                    Integer shengxiao = Integer.parseInt(birthday.substring(0, 4));//生肖
                    String xingzuo = labelSecondMapper.getXingzuo(siteId, buyerId);//星座
                    list.add(xingzuo);
                    Integer i = shengxiao - 1900;
                    if (i % 12 == 0) {
                        list.add("鼠");
                    } else if (i % 12 == 1) {
                        list.add("牛");
                    } else if (i % 12 == 2) {
                        list.add("虎");
                    } else if (i % 12 == 3) {
                        list.add("兔");
                    } else if (i % 12 == 4) {
                        list.add("龙");
                    } else if (i % 12 == 5) {
                        list.add("蛇");
                    } else if (i % 12 == 6) {
                        list.add("马");
                    } else if (i % 12 == 7) {
                        list.add("羊");
                    } else if (i % 12 == 8) {
                        list.add("猴");
                    } else if (i % 12 == 9) {
                        list.add("鸡");
                    } else if (i % 12 == 10) {
                        list.add("狗");
                    } else if (i % 12 == 11) {
                        list.add("猪");
                    }
                }
            }
            if (!StringUtil.isEmpty(map.get("create_time"))) {
                String create_time = String.valueOf(map.get("create_time"));
                list.add(create_time);
            }
            String areaArea = "";
            if (!StringUtil.isEmpty(map.get("province"))) {
                String province = String.valueOf(map.get("province"));
                areaArea = areaArea + province;
            }
            if (!StringUtil.isEmpty(map.get("city"))) {
                String city = String.valueOf(map.get("city"));
                areaArea = areaArea + city;
            }
            if (!StringUtil.isEmpty(map.get("area"))) {
                String area = String.valueOf(map.get("area"));
                areaArea = areaArea + area;
            }
            if (!StringUtil.isEmpty(areaArea)) {
                list.add("areaArea");
            }
            return list;
        } catch (Exception e) {
            log.info("查询基础标签:{}", e);
            return list;
        }
    }

    //交易标签
    public List<String> getTradesLabel(Integer siteId, Integer buyerId) {
        List<String> list = new ArrayList<>();
        try {
            //购买时段
            Date lastTime = labelSecondMapper.getLastTimeForDimensionality(siteId, buyerId);
            if (!StringUtil.isEmpty(lastTime)) {
                //获取当前时间
                Date date = new Date();
                int days = getDays(lastTime, date);
                //获取商户下的购买时段
                List<Map<String, Object>> strList = labelSecondMapper.getStrListForDimensionality(siteId, "ever_buy");
                if (strList.size() > 0) {
                    for (Map<String, Object> strMap : strList) {
                        Map<String, Object> label_attribute = JacksonUtils.json2map(String.valueOf(strMap.get("label_attribute")));
                        Integer min = Integer.parseInt(String.valueOf(label_attribute.get("timeMin")));
                        if (days < min) {
                            list.add(String.valueOf(strMap.get("label_name")));
                            break;
                        }
                    }
                }

            }

            //购买周期
            Map<String, Object> periodMap = labelSecondMapper.getPeriodLabelForDimensionality(siteId, buyerId);
            if (!StringUtil.isEmpty(periodMap)) {
                Date sh_time = (Date) periodMap.get("sh_time");//获取第一次订单时间
                Date ed_time = (Date) periodMap.get("ed_time");//获取最后一次订单时间
                Integer order_cnt = Integer.parseInt(String.valueOf(periodMap.get("order_cnt")));//获取订单数
                Integer days = getDays(sh_time, ed_time) / order_cnt;//购买周期

                //获取商户下的购买周期
                List<Map<String, Object>> strList = labelSecondMapper.getStrListForDimensionality(siteId, "buy_period");
                list.addAll(getTradesMrthod(strList, days, "day"));
            }

            //赚取积分
            Integer addIntegrate = labelSecondMapper.getAddIntegrateForDimensionality(siteId, buyerId);
            //获取商户下的赚取积分对象
            List<Map<String, Object>> addIntegrateList = labelSecondMapper.getStrListForDimensionality(siteId, "add_integral");
            list.addAll(getTradesMrthod(addIntegrateList, addIntegrate, "scope"));

            //消耗积分
            Integer consumeIntegrate = labelSecondMapper.getConsumeIntegrateForDimensionality(siteId, buyerId);
            //获取商户下的赚取积分对象
            List<Map<String, Object>> consumeIntegrateList = labelSecondMapper.getStrListForDimensionality(siteId, "consume_integral");
            list.addAll(getTradesMrthod(consumeIntegrateList, consumeIntegrate, "scope"));

            //剩余积分
            Integer residueIntegrate = addIntegrate - consumeIntegrate;
            //获取商户下的赚取积分对象
            List<Map<String, Object>> residueIntegrateList = labelSecondMapper.getStrListForDimensionality(siteId, "residue_integral");
            list.addAll(getTradesMrthod(residueIntegrateList, residueIntegrate, "day"));

        } catch (Exception e) {
            log.info("查询交易标签异常:{}", e);
        }
        return list;
    }

    //查询时间
    public int getDays(Date sh_time, Date ed_time) {
        int days = ((int) ((ed_time.getTime() - sh_time.getTime()) / (1000 * 3600 * 24)));
        return days;
    }

    //抽方法
    public List<String> getTradesMrthod(List<Map<String, Object>> strList, Integer days, String type) {
        List<String> list = new ArrayList<>();
        try {
            if (strList.size() > 0) {
                for (Map<String, Object> strMap : strList) {
                    Map<String, Object> label_attribute = JacksonUtils.json2map(String.valueOf(strMap.get("label_attribute")));
                    if ("1".equals(String.valueOf(label_attribute.get(type)))) {//小于
                        Integer max = Integer.parseInt(String.valueOf(label_attribute.get("max")));
                        if (days <= max) {
                            list.add(String.valueOf(strMap.get("label_name")));
                        }
                    } else if ("2".equals(String.valueOf(label_attribute.get(type)))) {//区间
                        Integer max = Integer.parseInt(String.valueOf(label_attribute.get("max")));
                        Integer min = Integer.parseInt(String.valueOf(label_attribute.get("min")));
                        if (days <= max && days > min) {
                            list.add(String.valueOf(strMap.get("label_name")));
                        }
                    } else if ("3".equals(String.valueOf(label_attribute.get(type)))) {//大于
                        Integer min = Integer.parseInt(String.valueOf(label_attribute.get("min")));
                        if (days > min) {
                            list.add(String.valueOf(strMap.get("label_name")));
                        }
                    }
                }
            }
            return list;
        } catch (Exception e) {
            log.info("处理交易标签异常:{}", e);
            return list;
        }
    }

    //会员排序
    public ReturnDto sortVip(Map<String, Object> map) {
        Integer pageNum = Integer.valueOf(map.get("pageNum").toString());
        Integer pageSize = Integer.valueOf(map.get("pageSize").toString());
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String, Object>> memberList = memberMapper.getCustomerByLabel(map);
        Map<String, Object> result = new HashedMap();
        PageInfo pageInfo = new PageInfo(memberList);
        result.put("memberCount", pageInfo.getTotal());//总记录数
        result.put("currentPage", pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());
        result.put("pageCount", pageInfo.getPages());//总页数
        result.put("members", memberList);
        return ReturnDto.buildSuccessReturnDto(result);
    }

    //查询会员标签
    public Map<String, Object> getCustomerTags(Integer siteId, Integer buyerId) {
        Map<String, Object> memberInfo = new HashedMap();
        List<Map<String, Object>> memberTags = relationLabelMapper.getRelationLabelAllByMemberId(siteId, buyerId);
        if (memberTags.size() > 0) {
            memberInfo.put("memberTags", memberTags);
        }
        return memberInfo;
    }
}
