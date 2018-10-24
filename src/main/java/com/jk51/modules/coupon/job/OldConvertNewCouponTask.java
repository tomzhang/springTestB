package com.jk51.modules.coupon.job;

import com.alibaba.fastjson.JSON;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.coupon.Coupon;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.UserCoupon;
import com.jk51.model.coupon.requestParams.GoodsRule;
import com.jk51.model.coupon.requestParams.LimitRule;
import com.jk51.model.coupon.requestParams.OrderRule;
import com.jk51.model.coupon.requestParams.TimeRule;
import com.jk51.model.order.Member;
import com.jk51.modules.coupon.mapper.CouponDetailMapper;
import com.jk51.modules.coupon.mapper.CouponMapper;
import com.jk51.modules.coupon.mapper.CouponRuleMapper;
import com.jk51.modules.coupon.mapper.UserCouponMapper;
import com.jk51.modules.goods.mapper.CategoryMapper;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.swing.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * filename :com.jk51.modules.coupon.job.
 * author   :zw
 * date     :2017/4/19
 * Update   :
 * 旧站点的优惠券转成新站优惠券
 */
@Component
public class OldConvertNewCouponTask {
    private static final Logger logger = LoggerFactory.getLogger(OldConvertNewCouponTask.class);

    @Autowired
    private UserCouponMapper userCouponMapper;
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private CouponDetailMapper couponDetailMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private GoodsMapper goodsMapper;

    private int siteId;

    private Jedis jedis;
    private Map<String, String> keyMap;

    public void setSiteId(final int siteId) {
        this.siteId = siteId;
    }


    public void setJedis(Jedis jedis) {

        try {
            jedis = new Jedis("172.20.12.14", 6379);
            jedis.select(0);
            logger.info("coupon:register:" + siteId);
            keyMap = jedis.hgetAll("coupon:register:" + siteId);
            logger.info("=================== redis 数据总数 {}", keyMap.size());
        } catch (Exception e) {
            this.jedis = null;
            logger.info("连接不到14的redis服务");
        }
    }

    /**
     * 获取旧的优惠券规则
     *
     * @return
     */
    List<Coupon> getCoupons() {
        return couponMapper.getOldCouponBySiteId(siteId);
       // return couponMapper.getOldCouponBySiteIdAndCouponId(siteId,52);
    }

    public void run(Integer site_id) {
        this.siteId = site_id;
        setJedis(jedis);
        //1.查询站点所有的老数据的优惠券 foreach
        //2.根据old优惠券id 找到所有 use_coupon 数据
        //3.判断会员是否存在，
        //4.存在的组装数据保存到新表的rule和detail表
        //5.不存在记录到日志表中
        List<Coupon> coupons = getCoupons();
        coupons.parallelStream().forEach(this::insert2NewRule);
    }

    public CouponRule conv2CouponRule(Coupon coupon) {

        CouponRule couponRule = new CouponRule();
        try {
            // 将b_shop_${site}.b_coupon的数据转换到 b_coupon_rule
            couponRule.setSiteId(siteId);
            couponRule.setRuleName(coupon.getCoupon_name());
            // 所有转成现金
            couponRule.setCouponType(100);
            // 新增字段  这里和优惠券名字一样
            couponRule.setMarkedWords(coupon.getCoupon_name());
            if (coupon.getCoupon_create_num() == -1) {
                couponRule.setAmount(-1);
            } else {
                couponRule.setAmount(coupon.getCoupon_available_num());
            }


            // coupon_limit_type 优惠券限制类别,0:无限制,1:类目限制,配合coupon_limit_vals使用,2:商品限制 3商品
            LimitRule limitRule = new LimitRule();
            limitRule.setApply_channel("100,101,102,103,104,105");
            limitRule.setOrder_type("100,200,300,400");
            limitRule.setApply_store(-1);
            limitRule.setIs_first_order(0);
            limitRule.setIs_share(0);
            couponRule.setLimitRule(JSON.toJSONString(limitRule));
            if(coupon.getCoupon_limit_type() != 0){
                //针对对象 0订单1商品
                couponRule.setAimAt(1);
                // 获取转换的商品规则  本期都转换为 九州在使用的都是全品类
                GoodsRule goodsRule = new GoodsRule();
                goodsRule = getGoodsIds(coupon.getCoupon_limit_type(), coupon.getCoupon_limit_vals(),goodsRule);
                if (goodsRule.getType() != null) {
                    goodsRule.setRule_type(0);
                    Map gRule = new HashMap<>();
                    Integer amount_min = coupon.getCoupon_amount_min() > 0 ? coupon.getCoupon_amount_min() : 1;
                    gRule.put("each_full_money", amount_min);
                    gRule.put("reduce_price", coupon.getCoupon_amount());
                    gRule.put("max_reduce", coupon.getCoupon_amount());
                    goodsRule.setRule(JacksonUtils.mapToJson(gRule));
                    couponRule.setGoodsRule(JSON.toJSONString(goodsRule));
                }
            }else {
                couponRule.setAimAt(0);
                // 针对订单全品类
                OrderRule orderRule = new OrderRule();
                orderRule.setRule_type(1);
                Map rule = new HashMap<>();
                Integer amount_min = coupon.getCoupon_amount_min() > 0 ? coupon.getCoupon_amount_min() : 1;
                rule.put("each_full_money", amount_min);
                rule.put("reduce_price", coupon.getCoupon_amount());
                rule.put("max_reduce", coupon.getCoupon_amount());
                orderRule.setRule(JacksonUtils.mapToJson(rule));
                couponRule.setOrderRule(JSON.toJSONString(orderRule));
            }
            TimeRule timeRule = new TimeRule();
            timeRule.setValidity_type(1);

            String format = "yyyy-MM-dd";
            if (coupon.getCoupon_start().getTime() > 0) {
                timeRule.setStartTime(dateFormat(coupon.getCoupon_start(), format));
            }
            long endTime = 0;
            try {
                endTime = coupon.getCoupon_end().getTime();
            } catch (Exception e) {
                endTime = 0;
            }

            if (endTime >= coupon.getCoupon_start().getTime()) {
                timeRule.setEndTime(dateFormat(coupon.getCoupon_end(), format));
            } else {
                if (couponRule.getStatus() == 0) { //正常的优惠券才给默认时间
                    Date date = coupon.getCoupon_start();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    cal.add(Calendar.YEAR, 10);
                    timeRule.setEndTime(dateFormat(cal.getTime(), format));
                } else {
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -1);
                    timeRule.setEndTime(dateFormat(cal.getTime(), format));
                }
            }

            // 规则状态0 正常 1结束 2撤销       优惠券状态 0 正常 1 已结束
            Integer status = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(timeRule.getEndTime());
            if (coupon.getCoupon_status() == 1) {
                status = 2;
            } else if (date.getTime() < new Date().getTime()) {
                status = 1;
            }
            couponRule.setStatus(status);

            couponRule.setTimeRule(JacksonUtils.obj2json(timeRule));

            // 可发放数量
            couponRule.setTotal(coupon.getCoupon_create_num());
            // 已发放次数
            couponRule.setSendNum(coupon.getCoupon_dispatch_num());
            // 使用数量
            couponRule.setUseAmount(coupon.getCoupon_use_num());
            // 分享数量
            couponRule.setShareNum(0);
            // 领取数量
            couponRule.setReceiveNum(0);
            couponRule.setOldCouponId(coupon.getCoupon_id());
            String formats = "yyyy-MM-dd HH:mm:ss";
            couponRule.setCreateTime(Timestamp.valueOf(dateFormat(coupon.getCreate_time(), formats)));
        } catch (Exception e) {
            logger.debug("转换错误 {}", e.getMessage());
        }

        return couponRule;
    }

    /**
     * 转换
     *
     * @param coupon
     */
    @Transactional
    public void insert2NewRule(Coupon coupon) {
        try {
            Map couponRules = couponMapper.getCouponRule(siteId, coupon.getCoupon_id());
            if (couponRules == null) {
                CouponRule couponRule = conv2CouponRule(coupon);
                couponMapper.insert(couponRule);

                Integer a = couponRule.getRuleId();
                System.out.println(a+"-----------");
                if (couponRule.getRuleId() != null) {
                    // 规则记录插入成功 处理优惠券数据
                    logger.info("插入一条记录 新的id:{} {}", couponRule.getRuleId(), couponRule);
                    Integer pageNum = 1000;
                    Integer total = couponMapper.getUserCouponCount(siteId, coupon.getCoupon_id());
                    logger.info("站点{},共找到数据{}条", siteId, total);
                    Integer pageTotal = (int) Math.ceil(total / (double) pageNum);
                    logger.info("站点{},共分页数{}，每页1000条数据", siteId, pageTotal);
                    List<UserCoupon> userCoupons = null;
                    for (int i = 0; i < pageTotal; i++) {
                        userCoupons = couponMapper.getUserCoupon(siteId, coupon.getCoupon_id(), pageNum, i * pageNum);
                        if (userCoupons != null && userCoupons.size() > 0) {
                            List<CouponDetail> couponDetails = userCoupons
                                    .stream()
                                    .map(userCoupon -> conv2CouponDetail(userCoupon, couponRule.getRuleId()))
                                    .collect(Collectors.toList());
                            couponMapper.insertDetails(couponDetails);
                            logger.info("{} 成功转换{}条优惠券记录 总共{}条", couponRule.getRuleId(),
                                        couponDetails.size(), userCoupons.size());
                        } else {
                            logger.info("{} 下面没有发放的优惠券", coupon.getCoupon_id());
                        }
                    }
                }
            } else {
                logger.info("数据已经导入过了");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 优惠券转换
     *
     * @param userCoupon
     * @param ruleId
     * @return
     */
    public CouponDetail conv2CouponDetail(UserCoupon userCoupon, int ruleId) {
        CouponDetail couponDetail = new CouponDetail();
        couponDetail.setSiteId(siteId);
        couponDetail.setRuleId(ruleId);
        String coupontNo = userCoupon.getUser_coupon_code();
        try {
            String userCouponCode = userCoupon.getUser_coupon_code();
            if (StringUtil.isNotEmpty(userCouponCode) && StringUtil.isNotEmpty(keyMap.get(userCouponCode))) {
                coupontNo = keyMap.get(userCouponCode);
            } else {
                logger.info("{} code ", userCoupon.getCoupon_id());
                String temp = jedis.hget("coupon:register:" + siteId, userCouponCode);
                if (StringUtil.isNotEmpty(temp)) {
                    logger.info("{}, {} 没有获取到值", "coupon:register:" + siteId, userCouponCode);
                    coupontNo = temp;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        couponDetail.setCouponNo(coupontNo);

        couponDetail.setMoney((double) userCoupon.getUser_coupon_amount());
        // 这里有个潜在的问题 如果buyerId为空需要用mobile新建一个用户
        Map member = couponMapper.getMember(siteId, userCoupon.getBuyer_id());
        if (member == null) {
            logger.info("{} 没有找到改会员", userCoupon.getBuyer_id());
            couponDetail.setUserId(userCoupon.getBuyer_id());
        } else {
            couponDetail.setUserId(Integer.parseInt(String.valueOf(member.get("memberId"))));
        }
        couponDetail.setOrderId("");
        // 优惠券状态: 0 正常, 1: 已经使用,2: 过期 , 3: 被删除（软删除） 	优惠券状态0:已使用1:待使用
        Map<Integer, Integer> stateMap = new HashMap();
        stateMap.put(0, 1);
        stateMap.put(1, 0);
        stateMap.put(2, 0);
        stateMap.put(3, 0);
        couponDetail.setCreateTime(new Timestamp(userCoupon.getCreate_time().getTime()));
        couponDetail.setUpdateTime(new Timestamp(userCoupon.getUpdate_time().getTime()));

        int state = userCoupon.getUser_coupon_state();
        couponDetail.setStatus(Optional.ofNullable(stateMap.get(state)).orElse(0));
        couponDetail.setSource(0 + "");
        couponDetail.setManagerId("");
        couponDetail.setIsCopy(0);
        couponDetail.setIsShare(0);
        couponDetail.setShareNum(0);
        couponDetail.setShareUrl("");
        couponDetail.setVersion(0);
        couponDetail.setStoreId(0);
        couponDetail.setSendOrderId("");

        return couponDetail;

    }

    /**
     * 日期格式化
     *
     * @param date
     * @param format
     * @return
     */
    private String dateFormat(final Date date, final String format) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), zoneId);

        return localDateTime.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * 获取旧数据分类下的商品id
     * 商品分类可能后面被删除 导致不存在
     *
     * @param limitType
     * @param limitVals
     * @return
     * @throws Exception
     */
    private GoodsRule getGoodsIds(int limitType, String limitVals ,GoodsRule goodsRule) throws Exception {
        switch (limitType) {
            case 1: {
                goodsRule.setType(1);
                String limitCateId = limitVals;
                Map mcateId = JacksonUtils.json2map(limitCateId);
                if (mcateId == null) {
                    return null;
                }

                String cateId = (String) mcateId.keySet().toArray()[0];
                if (StringUtil.isEmpty(cateId)) {
                    return null;
                }
                Map goodsIdsMap = couponMapper.getCateGoodsIds(Long.parseLong(cateId),siteId);

                String goodsIds = (String) goodsIdsMap.get("goodsIds");
                if (StringUtil.isEmpty(goodsIds)) {
                    // 这里没有找到商品说明分类被移除了
                    return null;
                }
                goodsRule.setPromotion_goods(goodsIds);

                return goodsRule;
            }
            case 3: {
                goodsRule.setType(2);
                goodsRule.setPromotion_goods(limitVals);
                return goodsRule;
            }
            default:
                return null;
        }
    }

    public void runRedis(Integer site_id) {
        setJedis(jedis);
        this.siteId = site_id;
        String coupontNo = null;
        try {
            coupontNo = jedis.hget("coupon:register:" + siteId, "Q729001869231887");
            logger.info("zhuwei:条形码" + coupontNo);
        } catch (Exception e) {
            logger.error("没有链接到redis服务器：" + e);
        }
        logger.info("zhuwei:info----------" + coupontNo);
    }


    public void testConnRedis() {
        try {
            Jedis jedis = new Jedis("172.20.12.14", 6379);
            jedis.hgetAll("coupon:register:" + siteId);
            keyMap = jedis.hgetAll("coupon:register:" + siteId);
            logger.info("=================== redis 数据总数 {}", keyMap.size());
        } catch (Exception e) {
            this.jedis = null;
            logger.info("redis2:连接不到14的redis服务");
        } finally {
            jedis.close();
        }
    }


}
