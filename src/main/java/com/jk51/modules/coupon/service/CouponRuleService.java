package com.jk51.modules.coupon.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.date.DateUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.java8datetime.ParseAndFormat;
import com.jk51.commons.java8datetime.Transform;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.ParamErrorException;
import com.jk51.model.Stores;
import com.jk51.model.coupon.CouponDetail;
import com.jk51.model.coupon.CouponExportLog;
import com.jk51.model.coupon.CouponLog;
import com.jk51.model.coupon.CouponRule;
import com.jk51.model.coupon.requestParams.*;
import com.jk51.model.exception.DBDataErrorException;
import com.jk51.model.goods.PageData;
import com.jk51.model.order.Member;
import com.jk51.model.promotions.PromotionsRule;
import com.jk51.model.promotions.rule.*;
import com.jk51.modules.coupon.constants.CouponConstant;
import com.jk51.modules.coupon.mapper.*;
import com.jk51.modules.coupon.request.CouponGoods;
import com.jk51.modules.coupon.utils.CouponProcessUtils;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.index.service.StoresService;
import com.jk51.modules.promotions.mapper.PromotionsRuleMapper;
import com.jk51.modules.promotions.utils.AmountUtils;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.UnsupportedDataTypeException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.jk51.commons.date.DateUtils.formatDate;
import static com.jk51.modules.coupon.constants.CouponConstant.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-03-02
 * 修改记录:
 */
@SuppressWarnings("Duplicates")
@Service
public class CouponRuleService {
    private static final Logger log = LoggerFactory.getLogger(CouponRuleService.class);

    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private CouponExportLogMapper couponExportMapper;
    @Autowired
    private CouponDetailMapper couponDetailMapper;
    @Autowired
    private CouponLogMapper couponLogMapper;
    @Autowired
    private CouponRuleMapper ruleMapper;
    @Autowired
    private ParsingCouponRuleService parsingCouponRuleService;
    @Autowired
    private CouponActivityService couponActivityService;
    @Autowired
    private CouponNoEncodingService couponNoEncodingService;
    @Autowired
    private CouponProcessUtils couponProcessUtils;
    @Autowired
    private CouponRuleActivityMapper couponRuleActivityMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private StoresService storesService;
    @Autowired
    private CouponRuleMapper couponRuleMapper;
    @Autowired
    private PromotionsRuleMapper promotionsRuleMapper;
    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 获取正在发放的优惠券列表
     * @param siteId
     * @param page 第几页
     * @param pageSize 页显示量
     * @return map
     */
    public Map<String,Object> CouponCanUseToolList(int siteId,int page,int pageSize){
        Map<String,Object> resultMap = new HashMap<String,Object>();
        try {
            PageHelper.startPage(page,pageSize);
            List<Map<String, Object>> couponRuleBySiteIdTool = couponRuleMapper.findCouponRuleBySiteIdTool(siteId);
            PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(couponRuleBySiteIdTool);
            //处理券
            List<Map<String, Object>> rows = pageInfo.getList().stream().filter(map -> {
                try {
                    Integer coupon_type = Integer.parseInt(map.get("coupon_type").toString());
                    String couponType = CouponConstant.COUPON_TYPE_MAP_STRING.get(coupon_type);
                    map.put("coupon_type", couponType);
                    String time_rule = (String) map.get("time_rule");
                    String effectiveTimeForCouponDetail = CouponDetailService.getEffectiveTimeForCouponDetail(time_rule);
                    map.put("time_rule",effectiveTimeForCouponDetail);
                } catch (Exception e) {
                    log.error("优惠券小工具，处理券异常:{}",e);
                    return false;
                }
                return true;
            }).collect(toList());

            resultMap.put("rows",rows);
            //总记录数
            resultMap.put("total",pageInfo.getTotal());
            //总页数
            resultMap.put("pages",pageInfo.getPages());
            return resultMap;
        } catch (Exception e) {
            log.error("查询优惠券可用小工具失败:{}",e);
            return null;
        }
    }

    private PageInfo<Map<String,Object>> couponNotFull2Add(int siteId,int page , int pageSize){
        PageHelper.startPage(page,pageSize);
        List<Map<String, Object>> couponRuleBySiteIdTool = couponRuleMapper.findCouponRuleBySiteIdTool(siteId);
        return new PageInfo<Map<String, Object>>(couponRuleBySiteIdTool);
    }

    /**
     * 改变赠品规则中的赠品数量.
     *
     * @param siteId
     * @param couponRuleId
     * @param map          以giftId为key，改变量为value组装的map.
     * @throws Exception
     */
    public void changeGiftNums(int siteId, int couponRuleId, Map<Integer, Integer> map) {
        CouponRule rule = ruleMapper.findCouponRuleById(couponRuleId, siteId);

        changeGiftNums(rule, map);
    }

    /**
     * 重载方法，具体看com.jk51.modules.coupon.service.CouponRuleService#changeGiftNums(int, int, java.util.Map)
     *
     * @param rule
     * @param map
     */
    public void changeGiftNums(CouponRule rule, Map<Integer, Integer> map) {
        if (rule == null || rule.getCouponType() != 500) {
            throw new ParamErrorException("传入的couponRule有问题:" + JSON.toJSONString(rule));
        }

        GoodsRule goodsRule = JSON.parseObject(rule.getGoodsRule(), GoodsRule.class);
        goodsRule.getGift_storage().stream()
            .filter(giftStorage -> map.get(giftStorage.getGiftId()) != null)
            .forEach(giftStorage -> {
                giftStorage.setSendNum(giftStorage.getSendNum() + map.get(giftStorage.getGiftId()));

                if (giftStorage.getSendNum() < 0) {
                    throw new RuntimeException("赠品库存不能小于0");
                }

                if (giftStorage.getSendNum() > giftStorage.getTotal()) {
                    throw new RuntimeException("赠品库存不能大于总库存");
                }
            });

        ruleMapper.updateOneFiled(rule.getSiteId(), rule.getRuleId(), "goods_rule", JSON.toJSONString(goodsRule));
    }

    /**
     * 优惠券编码(用户领取以后每张优惠券对应的编码)
     *
     * @param ruleId
     * @param siteId
     * @return
     */
    public String getCouponDownDetailNum(Integer ruleId, Integer siteId) {

        Integer integer = 0;
        synchronized (integer) {
            CouponDetail detail = null;
            HashOperations<String, String, String> ops = stringRedisTemplate.opsForHash();

            String theNum = ops.get("getcouponNo", siteId.toString() + ruleId.toString());

            String couponNo = null;

            Long num = null;

            //redis没有保存过 先去数据库里查
            if (StringUtils.isEmpty(theNum)) {
                detail = couponDetailMapper.getMAxCouponDetail(ruleId, siteId);
                if (detail != null && null != detail.getCouponNo()) {
                    couponNo = detail.getCouponNo();
                    if (StringUtils.equalsIgnoreCase(couponNo.substring(0, ruleId.toString().length()), ruleId.toString())) {
                        num = Long.parseLong(couponNo.substring(ruleId.toString().length(), couponNo.length()));
                        ops.put("getcouponNo", siteId.toString() + ruleId.toString(), num.toString());
                    } else {
                        ops.put("getcouponNo", siteId.toString() + ruleId.toString(), "0");
                    }
                } else {
                    ops.put("getcouponNo", siteId.toString() + ruleId.toString(), "0");
                }
            }

            num = ops.increment("getcouponNo", siteId.toString() + ruleId.toString(), 1);

            return ruleId.toString() + String.format("%08d", num);
        }
    }

    /**
     * 检查和更新优惠券的状态，同时检查并修正包含该优惠券的活动的状态
     *
     * @param siteId
     * @param ruleId
     * @return
     */
    public boolean checkRuleAndActivityStatusByRuleId(Integer siteId, Integer ruleId) {
        List<Integer> activityIds = couponRuleActivityMapper.selectActiveIdByRuleIdAndSiteId(siteId, ruleId);
        return activityIds.stream()
            .map(activityId -> couponActivityService.checkStatus(activityId, siteId))
            .reduce(true, (b1, b2) -> b1 && b2);
    }

    /**
     * 检查和更新优惠券的状态<br/>
     * 新规则状态 0可发放 1已发完 2手动停发 3已过期 4手动作废 10待发放 <br/>
     *
     * @param ruleId
     * @param siteId
     * @return true 状态正确或已经恢复正确, false 状态错误且不可恢复，一般都会抛出异常
     * @throws Exception 可能会抛出异常，记得处理
     */
    @Transactional
    public boolean checkStatus(Integer siteId, Integer ruleId) {
        if (ruleId == null) return false;
        if (siteId == null) return false;

        CouponRule couponRule = couponRuleMapper.findCouponRuleById(ruleId, siteId);
        if (couponRule == null) return false;
        try {
            return checkStatus(couponRule.getSiteId(),
                couponRule.getRuleId(),
                couponRule.getTimeRule(),
                couponRule.getAmount(),
                couponRule.getStatus());
        } catch (Exception e) {
            log.error("改变优惠券状态出错, {}", e);
            return false;
        }
    }

    /**
     * 检查优惠券的状态，如果错误就修复 <br/>
     * 新规则状态 0可发放 1已发完 2手动停发 3已过期 4手动作废 10待发放 <br/>
     *
     * @param siteId
     * @param ruleId
     * @param timeRule
     * @param amount
     * @param status
     * @return true 状态正确或已经恢复正确, false 状态错误且不可恢复，一般都会抛出异常
     */
    @Transactional
    public boolean checkStatus(Integer siteId, Integer ruleId, String timeRule, int amount, int status) {
        try {
            CouponRule couponRule = new CouponRule();
            couponRule.setSiteId(siteId);
            couponRule.setRuleId(ruleId);
            couponRule.setTimeRule(timeRule);
            couponRule.setAmount(amount);
            couponRule.setStatus(status);
            switch (couponRule.getStatus()) {
                case COUPON_RULE_STATUS_DRAFT:
                    return true; // 该状态的操作都是手动的，没有自动判断的存在
                case COUPON_RULE_STATUS_RELEASE:
                case COUPON_RULE_STATUS_NO_INVENTORY:
                case COUPON_RULE_STATUS_OVERDUE:
                    return checkStatusByTimeAndAmount(couponRule);
                case COUPON_RULE_STATUS_STOP:
                case COUPON_RULE_STATUS_INVALID:
                    return true; // 上述状态不可逆
            }
        } catch (Exception e) {
            log.error(e.getMessage() + ": {}", e);
        }

        return false;
    }

    /**
     * 根据数量和时间判断状态，如果状态不符合判断，则自动改变状态<br/>
     * ps: 状态只可能被改变成可发放，已发完，已过期
     *
     * @param couponRule
     * @return true 状态正确或修复成功，false 状态不符合预期且改变失败
     * @throws Exception
     */
    private boolean checkStatusByTimeAndAmount(CouponRule couponRule) throws Exception {
        TimeRule timeRule = JacksonUtils.json2pojo(couponRule.getTimeRule(), TimeRule.class);

        switch (timeRule.getValidity_type()) {
            case TIME_RULE_STATUS_ABSOLUTE:
            case TIME_RULE_STATUS_SECKILL:
                if (isOverdue(couponRule.getTimeRule())) {
                    return ifNotThenChangeForStatus(couponRule, COUPON_RULE_STATUS_OVERDUE);
                } else {
                    return checkStatusByAmountAfterTime(couponRule);
                }
            case TIME_RULE_STATUS_RELATIVE:
                return checkStatusByAmountAfterTime(couponRule);
            default:
                throw new RuntimeException("未知状态");
        }
    }

    /**
     * 在检查时间后检查数量，来修复状态
     *
     * @param couponRule
     * @return
     */
    private boolean checkStatusByAmountAfterTime(CouponRule couponRule) {
        switch (couponRule.getAmount()) {
            case -1:
                return ifNotThenChangeForStatus(couponRule, COUPON_RULE_STATUS_RELEASE);
            case 0:
                return ifNotThenChangeForStatus(couponRule, COUPON_RULE_STATUS_NO_INVENTORY);
            default:
                return ifNotThenChangeForStatus(couponRule, COUPON_RULE_STATUS_RELEASE);
        }
    }

    /**
     * 如果优惠券规则的状态不是规定的状态，则改变这个状态
     *
     * @param couponRule
     * @return
     */
    private boolean ifNotThenChangeForStatus(CouponRule couponRule, int status) {
        if (couponRule.getStatus() == status) {
            return true;
        } else {
            int i = couponRuleMapper.revampCouponRuleStatus(couponRule.getRuleId(), couponRule.getSiteId(), status);
            return i == 1;
        }
    }


    public Boolean judgeLimitTime(String time_rule){
        TimeRule timeRule = com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON.parseObject(time_rule, TimeRule.class);
        LocalDateTime now = LocalDateTime.now();
        switch (timeRule.getValidity_type()) {
            case TimeRule.VALIDITY_TYPE_ABSOLUTE_TIME:
                LocalDate absolute_date = LocalDate.parse(timeRule.getEndTime(), ParseAndFormat.dateTimeFormatter_3);
                LocalTime absolute_time = LocalTime.MAX;
                LocalDateTime absolute = LocalDateTime.of(absolute_date, absolute_time);
                if(absolute.isBefore(now)){
                    //过期
                    return false;
                }
                break;
            // 秒杀时间
            case TimeRule.VALIDITY_TYPE_MONTH_SEPARATE:
                LocalDateTime second = LocalDateTime.parse(timeRule.getEndTime(), ParseAndFormat.longFormatter);
                if(second.isBefore(second)){
                    //过期
                    return false;
                }
                break;
            case TimeRule.VALIDITY_TYPE_RELATIVE_TIME:
                //忽略相对时间
                return true;
            default:
                return false;
        }
        return true;
    }
    /**
     * 对优惠券规则判断时间是否过期
     *
     * @param timeRuleStr @return
     * @return true: 过期, false:不过期
     * @throws Exception
     */
    public boolean isOverdue(String timeRuleStr) throws Exception {
        TimeRule timeRule = JacksonUtils.json2pojo(timeRuleStr, TimeRule.class);

        SimpleDateFormat longFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd");
        String endTime = null;
        Date end = null;
        Date now = new Date();


        switch (timeRule.getValidity_type()) {
            case TIME_RULE_STATUS_ABSOLUTE:
                endTime = timeRule.getEndTime() + " 23:59:59";
                end = longFormat.parse(endTime);

                return now.after(end);
            case TIME_RULE_STATUS_SECKILL:
                endTime = timeRule.getEndTime();
                end = endTime.length() == 10 ? shortFormat.parse(endTime) : longFormat.parse(endTime);

                return now.after(end);
            case TIME_RULE_STATUS_RELATIVE:
                return false;
            default:
                throw new RuntimeException("未知情况");
        }
    }

    /**
     * 检查时间
     *
     * @param map
     * @return
     */
    public boolean checkTimeRuleOverdue(Map<String, Object> map) {
        try {
            String timeRuleJson = map.get("time_rule").toString();
            Date createDate = (Date) map.get("create_time");
            TimeRule timeRule = JSON.parseObject(timeRuleJson, TimeRule.class);
            switch (timeRule.getValidity_type()) {
                case 1:
                    //绝对时间
                    LocalDateTime startTime = LocalDate.parse(timeRule.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .atTime(0, 0, 0);
                    LocalDateTime endTime = LocalDate.parse(timeRule.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .atTime(0, 0, 0);

                    if (endTime.isBefore(LocalDateTime.now().minusDays(1)))
                        return false;
                    break;
                  /*  String format="yyyy-MM-dd hh:mm:ss";
                    Date startTime=DateUtils.parseDate(timeRule.getStartTime(),format);
                    Date endTime=DateUtils.parseDate(timeRule.getEndTime(),format);
                    if(!(startTime.before(new Date())&&endTime.after(new Date())))
                        return false;
                    break;*/
                case 2:
                    //相对时间
                    Date startDate = DateUtils.getBeforeOrAfterDate(createDate, timeRule.getDraw_day());
                    Date endDate = DateUtils.getBeforeOrAfterDate(startDate, timeRule.getHow_day());
                    if (endDate.before(new Date()))
                        return false;
                    break;
                case 3:
                    if (timeRule.getAssign_type().equals(1)) {
                        String assign_rule = timeRule.getAssign_rule();
                        String[] aRules = assign_rule.split(",");
                        for (int i = 0; i < aRules.length; ++i) {
                            if (LocalDateTime.now().getDayOfMonth() == checkNullReturnInt(aRules[i]))
                                return true;
                        }
                        return false;
                    } else if (timeRule.getAssign_type().equals(2)) {
                        String assign_rule = timeRule.getAssign_rule();
                        String[] aRules = assign_rule.split(",");
                        for (int i = 0; i < aRules.length; ++i) {
                            if (LocalDateTime.now().getDayOfWeek().getValue() == checkNullReturnInt(aRules[i]))
                                return true;
                        }
                        return false;
                    }
                    break;
                //秒杀时间
                case 4:
                    SimpleDateFormat formt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date endTimeag = formt.parse(timeRule.getEndTime());

                    if (endTimeag.before(new Date()))
                        return false;
                    break;
            }
        } catch (Exception e) {
            log.error("Exception", e);
            map.put("errorMsg", "不可使用");
            return false;
        }
        return true;
    }

    /**
     * 检查优惠券时间
     *
     * @param map
     * @return
     */
    public boolean checkTimeRule(Map<String, Object> map) {
        try {
            String timeRuleJson = map.get("time_rule").toString();
            Date createDate = (Date) map.get("create_time");
            TimeRule timeRule = JSON.parseObject(timeRuleJson, TimeRule.class);
            LocalDateTime localDateTimeNow = LocalDateTime.now();
            Date dateNow = Transform.localDateTimeToUDate(localDateTimeNow);

            switch (timeRule.getValidity_type()) {
                case 1:
                    //绝对时间
                    LocalDateTime startTime = LocalDate.parse(timeRule.getStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .atTime(0, 0, 0);
                    LocalDateTime endTime = LocalDate.parse(timeRule.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .atTime(0, 0, 0);

                    if (!(startTime.isBefore(localDateTimeNow) && endTime.isAfter(localDateTimeNow.minusDays(1)))) {
                        if (startTime.isAfter(localDateTimeNow)) {
                            map.put("errorMsg", "优惠活动还未开始，不可用");
                            return false;
                        }

                        if (endTime.isBefore(localDateTimeNow.minusDays(1))) {
                            map.put("errorMsg", "优惠券已过期，不可用");
                            return false;
                        }
                    }
                    break;
                  /*  String format="yyyy-MM-dd hh:mm:ss";
                    Date startTime=DateUtils.parseDate(timeRule.getStartTime(),format);
                    Date endTime=DateUtils.parseDate(timeRule.getEndTime(),format);
                    if(!(startTime.before(new Date())&&endTime.after(new Date())))
                        return false;
                    break;*/
                case 2:
                    // 相对时间
                    Date startDate = DateUtils.getBeforeOrAfterDate(createDate, timeRule.getDraw_day());
                    Date endDate = DateUtils.getBeforeOrAfterDate(startDate, timeRule.getHow_day());
                    if (!(startDate.before(dateNow) && endDate.after(dateNow))) {
                        map.put("errorMsg", "不在有效使用时间范围内");
                        return false;
                    }
                    break;
                case 3:
                    if (timeRule.getAssign_type().equals(1)) {
                        String assign_rule = timeRule.getAssign_rule();
                        String[] aRules = assign_rule.split(",");
                        for (int i = 0; i < aRules.length; ++i) {
                            if (LocalDateTime.now().getDayOfMonth() == checkNullReturnInt(aRules[i]))
                                return true;
                        }
                        return false;
                    } else if (timeRule.getAssign_type().equals(2)) {
                        String assign_rule = timeRule.getAssign_rule();
                        String[] aRules = assign_rule.split(",");
                        for (int i = 0; i < aRules.length; ++i) {
                            if (LocalDateTime.now().getDayOfWeek().getValue() == checkNullReturnInt(aRules[i]))
                                return true;
                        }
                        return false;
                    }
                    break;
                //秒杀时间
                case 4:
                    SimpleDateFormat formt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date strtTimeag = formt.parse(timeRule.getStartTime());

                    Date endTimeag = formt.parse(timeRule.getEndTime());

                    if (!(strtTimeag.before(dateNow) && endTimeag.after(dateNow))) {
                        if (strtTimeag.after(dateNow)) {
                            map.put("errorMsg", "优惠活动还未开始，不可用");
                            return false;
                        }

                        if (endTimeag.before(dateNow)) {
                            map.put("errorMsg", "优惠券已过期，不可用");
                            return false;
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            log.error("Exception", e);
            map.put("errorMsg", "不可使用");
            return false;
        }
        return true;
    }

    /**
     * 检查优惠券时间
     *
     * @param map
     * @return
     */
    public boolean checkTimeRuleType2(Map<String, Object> map) {
        try {
            String timeRuleJson = map.get("time_rule").toString();
            Date createDate = (Date) map.get("create_time");
            TimeRule timeRule = JSON.parseObject(timeRuleJson, TimeRule.class);
            LocalDateTime localDateTimeNow = LocalDateTime.now();
            Date dateNow = Transform.localDateTimeToUDate(localDateTimeNow);

            switch (timeRule.getValidity_type()) {
                case 2:
                    // 相对时间
                    Date startDate = DateUtils.getBeforeOrAfterDate(createDate, timeRule.getDraw_day());
                    Date endDate = DateUtils.getBeforeOrAfterDate(startDate, timeRule.getHow_day());
                    if (endDate.before(dateNow)) {
                        return false;
                    }
            }
        } catch (Exception e) {
            log.error("Exception", e);
            map.put("errorMsg", "不可使用");
            return false;
        }
        return true;
    }


    /**
     * 限价优惠券 判断件数限制 商品列表中超过限制要求的就去除
     *
     * @param couponMap
     * @param goodsInfo
     * @return
     */
/*    public  boolean checkPriceFixIng(Map<String, Object> couponMap,List<Map<String, Integer>> goodsInfo){
        if(!goodsInfo.isEmpty()){
            if(null!=couponMap.get("goods_rule")){
                JSONObject json=new JSONObject(couponMap);
                Object obj=json.get("goods_rule");
                JSONObject json2= JSONObject.parseObject(obj.toString());
                JSONObject json3= JSONObject.parseObject(json2.get("rule").toString());
                for(Map<String, Integer> map :goodsInfo){
                    Integer buy_max_num=Integer.parseInt(json3.getString("buy_num_max"));
                    Integer goods_num=map.get("num");
                    if(goods_num>buy_max_num){
                        return false;
                    }

                }
            }
        }
        return true;
    }*/


    /**
     * 解析限制条件
     *
     * @param map
     * @param orderMessageParams
     * @return
     */
    public boolean checkLimitRule(Map<String, Object> map, OrderMessageParams orderMessageParams) {
        try {
            String limitRuleJson = map.get("limit_rule").toString();
            boolean isFirstOrder = orderMessageParams.isFirstOrder();
            Integer orderType = orderMessageParams.getOrderType();
            Integer applyChannel = orderMessageParams.getApplyChannel();
            Integer storeId = orderMessageParams.getStoreId();

            LimitRule limitRule = JacksonUtils.json2pojo(limitRuleJson, LimitRule.class);
            if (!isFirstOrder && limitRule.getIs_first_order().equals(1)) {
                map.put("errorMsg", "仅限首单使用");
                return false;
            }

            String[] orderTypes = limitRule.getOrder_type().split(",");// 判断适用订单
            if (!new HashSet<String>(Arrays.asList(orderTypes)).contains(orderType.toString())) {
                if (orderType == 100)
                    map.put("errorMsg", "仅限送货上门订单使用");
                else if (orderType == 200)
                    map.put("errorMsg", "仅限门店自提订单使用");
                return false;
            }


            String[] applyChannels = limitRule.getApply_channel().split(",");// 判断适用渠道
            if (!new HashSet<String>(Arrays.asList(applyChannels)).contains(applyChannel.toString())) {
                if (applyChannel == 103)
                    map.put("errorMsg", "仅限线下渠道使用");
                else if (applyChannel == 105)
                    map.put("errorMsg", "仅限线上渠道使用");
                return false;
            }


            if (limitRule.getApply_store().equals(1)) { // 判断适用门店
                if (storeId == null) {
                    map.put("errorMsg", "仅限指定门店使用");
                    return false;
                }

                String useStore = limitRule.getUse_stores();
                String[] stores = useStore.split(",");
                if (!new HashSet<String>(Arrays.asList(stores)).contains(storeId.toString())) {
                    map.put("errorMsg", "仅限指定门店使用");
                    return false;
                }

            } else if (limitRule.getApply_store().equals(2)) {
                //区域内优惠判断
                String theareaId = null;
                if (orderType == 200)
                    theareaId = orderMessageParams.getAreaId().toString();
                else if (orderType == 100) {
                    Stores store = storesService.getStore(storeId, orderMessageParams.getSiteId());
                    theareaId = store != null ? store.getCity_id() + "" : "";
                }

                String useStore = limitRule.getUse_stores();
                String[] stores = useStore.split(",");
                if (!new HashSet<String>(Arrays.asList(stores)).contains(theareaId)) {
                    map.put("errorMsg", "仅限指定区域使用");
                    return false;
                }
            }

        } catch (Exception e) {
            map.put("errorMsg", "不能使用");
            log.error("Exception", e);
            return false;
        }
        return true;
    }


    public boolean checkCouponsForGift(Map<String, Object> map, OrderMessageParams orderMessageParams) {
        try {
            Integer contentType = (Integer) map.get("coupon_type");

            if (null != contentType && contentType == 500)
                return checkGiftRule(map, orderMessageParams);
            else
                return true;

        } catch (Exception e) {
            map.put("errorMsg", "不能使用");
            log.error("Exception", e);
            return false;
        }
    }

    public boolean checkGiftRule(Map<String, Object> map, OrderMessageParams orderMessageParams) {
        try {
            String goodsRuleJson = (String) map.get("goods_rule");
            GoodsRule goodsRule = JacksonUtils.json2pojo(goodsRuleJson, GoodsRule.class);
            switch (goodsRule.getRule_type()) {

                case 1:
                    if (!orderMessageParams.getGoodsInfo().stream().anyMatch(goodsMap -> new HashSet<String>(Arrays.asList(goodsRule.getPromotion_goods().split(",")))
                        .contains(goodsMap.get("goodsId").toString()))) {
                        map.put("errorMsg", "无指定商品，不可用");
                    } else if (!checkGiftRuleForContainsNum(goodsRule, orderMessageParams) && goodsRule.getGift_calculate_base() == 1) {
                        map.put("errorMsg", "指定商品未满" + couponProcessUtils.String2List(goodsRule.getRule()).get(0).get("meetNum") + "件，不可用");
                    } else if (!checkGiftRuleForContainsNum(goodsRule, orderMessageParams) && goodsRule.getGift_calculate_base() == 2) {
                        map.put("errorMsg", "指定商品未满" + couponProcessUtils.String2List(goodsRule.getRule()).get(0).get("meetNum") + "件，不可用");
                    }

                    return checkGiftRuleForContainsNum(goodsRule, orderMessageParams);
                case 2:
                    if (!orderMessageParams.getGoodsInfo().stream().anyMatch(goodsMap -> new HashSet<String>(Arrays.asList(goodsRule.getPromotion_goods().split(",")))
                        .contains(goodsMap.get("goodsId").toString()))) {
                        map.put("errorMsg", "无指定商品，不可用");
                    } else if (!checkGiftRuleForContainsMoney(goodsRule, orderMessageParams) && goodsRule.getGift_calculate_base() == 1) {
                        map.put("errorMsg", "指定商品金额未满" + AmountUtils.changeF2Y(couponProcessUtils.String2List(goodsRule.getRule()).get(0).get("meetMoney")) + "元，不可用");
                    } else if (!checkGiftRuleForContainsMoney(goodsRule, orderMessageParams) && goodsRule.getGift_calculate_base() == 2) {
                        map.put("errorMsg", "指定商品金额未满" + AmountUtils.changeF2Y(couponProcessUtils.String2List(goodsRule.getRule()).get(0).get("meetMoney")) + "元，不可用");
                    }


                    return checkGiftRuleForContainsMoney(goodsRule, orderMessageParams);
            }
        } catch (Exception e) {
            log.info("满赠券过滤解析异常");
            map.put("errorMsg", "不能使用");
            return false;
        }
        return false;
    }

    private boolean checkGiftRuleForContainsNum(GoodsRule goodsRule, OrderMessageParams orderMessageParams) {
        try {
            List<Map<String, Integer>> goodsInfo = orderMessageParams.getGoodsInfo();
            //单种商品满足条件才可
            if (goodsRule.getGift_calculate_base() == 1) {
                return goodsInfo.stream().anyMatch(map -> new HashSet<String>(Arrays.asList(goodsRule.getPromotion_goods().split(",")))
                    .contains(map.get("goodsId").toString()) && map.get("num") >= Integer.parseInt(couponProcessUtils.String2List(goodsRule.getRule()).get(0).get("meetNum")));
                //多种商品组合满足条件即可
            } else if (goodsRule.getGift_calculate_base() == 2) {
                int totalNum = goodsInfo.stream().filter(map -> new HashSet<String>(Arrays.asList(goodsRule.getPromotion_goods().split(",")))
                    .contains(map.get("goodsId").toString())).mapToInt(map -> map.get("num")).sum();
                if (totalNum >= Integer.parseInt(couponProcessUtils.String2List(goodsRule.getRule()).get(0).get("meetNum")))
                    return true;

            }
        } catch (Exception e) {
            log.info("满赠优惠券满件判断异常:" + e);
            return false;
        }

        return false;
    }

    private boolean checkGiftRuleForContainsMoney(GoodsRule goodsRule, OrderMessageParams orderMessageParams) {
        try {
            List<Map<String, Integer>> goodsInfo = orderMessageParams.getGoodsInfo();
            //单种商品满足条件才可
            if (goodsRule.getGift_calculate_base() == 1) {
                return goodsInfo.stream().anyMatch(map -> new HashSet<String>(Arrays.asList(goodsRule.getPromotion_goods().split(",")))
                    .contains(map.get("goodsId").toString()) && map.get("num") * map.get("goodsPrice") >= Integer.parseInt(couponProcessUtils.String2List(goodsRule.getRule()).get(0).get("meetMoney")));
                //组合商品满足条件即可
            } else if (goodsRule.getGift_calculate_base() == 2) {
                int totalMoney = goodsInfo.stream().filter(map -> new HashSet<String>(Arrays.asList(goodsRule.getPromotion_goods().split(",")))
                    .contains(map.get("goodsId").toString())).mapToInt(map -> map.get("num") * map.get("goodsPrice")).sum();
                if (totalMoney >= Integer.parseInt(couponProcessUtils.String2List(goodsRule.getRule()).get(0).get("meetMoney")))
                    return true;

            }
        } catch (Exception e) {
            log.info("满赠活动满元解析异常:" + e);
            return false;
        }

        return false;
    }


    public boolean checkGoodsRule(Map<String, Object> mapParam, OrderMessageParams orderMessageParams) {

        String goodsRuleJson = (String) mapParam.get("goods_rule");
        List<Map<String, Integer>> goodsInfo = orderMessageParams.getGoodsInfo();
        Integer siteId = orderMessageParams.getSiteId();
        Integer userId = orderMessageParams.getUserId();
        Integer ruleId = (Integer) mapParam.get("rule_id");
        Integer postFee = orderMessageParams.getPostFee();
        Integer CouponType = (Integer) mapParam.get("coupon_type");
        try {
            GoodsRule goodsRule = JSON.parseObject(goodsRuleJson, GoodsRule.class);
            for (Map<String, Integer> map : goodsInfo) {
                Integer goodsId = map.get("goodsId");
                switch (goodsRule.getType()) {
                    case 0:
                        //全部
                        break;
                    case 1:
                        //指定类目
                        break;
                    case 2:
                        //指定商品
                        if (!checkGoodsByShopCar(goodsRule, goodsInfo)) {
                            mapParam.put("errorMsg", "无指定商品，不可用");
                            return false;
                        }

                        break;
                    case 3:
                        //指定商品不参加
                        if (!checkGoodsUnByShopCar(goodsRule, goodsInfo)) {
                            mapParam.put("errorMsg", "无指定商品，不可用");
                            return false;
                        }
                        break;
                }
                Integer num = map.get("num");
                int price = map.get("goodsPrice");
                Map<String, Object> stringMap = null;
                List<Map<String, String>> mapList = null;

                switch (goodsRule.getRule_type()) {
                    case 0:
                        //每满
                        if (!checkGoodsIfContainsMoney(goodsRuleJson, goodsInfo, postFee, mapParam)) {
                            return false;
                        }
                        break;
                    case 1:
                        //满元
                        if ((CouponType == 100 || CouponType == 200) && !checkGoodsIfContainsMoney2(goodsRuleJson, goodsInfo, postFee, mapParam)) {
                            return false;
                        }

                        break;
                    case 2:
                        //满件
                        if ((CouponType == 100 || CouponType == 200) && !checkGoodsContainsNum(goodsRuleJson, goodsInfo, mapParam)) {
                            return false;
                        }
                        break;
                    case 3:
                        //限价
                        stringMap = couponProcessUtils.String2Map(goodsRule.getRule());
                        Member member = memberMapper.getMemberByMemberId(siteId, userId);
                        String buy_num_max = stringMap.get("buy_num_max").toString();
                        String each_goods_max_buy_num = stringMap.get("each_goods_max_buy_num").toString();
                        String[] goods = goodsRule.getPromotion_goods().split(",");
                        Set<String> set = new HashSet<String>(Arrays.asList(goods));
                        //a: 对于指定商品参加 只对指定的商品进行购买量的校验
                        if (goodsRule.getType() == 2 && set.contains(goodsId.toString())
                            && !(checkNullReturnInt(buy_num_max).intValue() >= num.intValue() &&
                            checkNullReturnInt(each_goods_max_buy_num).intValue()
                                >= (num.intValue() + couponDetailMapper.getUseBuyedGoodsNum(siteId, goodsId, member.getBuyerId(), ruleId).intValue()))) {
                            if (checkNullReturnInt(buy_num_max).intValue() < num.intValue()) {
                                mapParam.put("errorMsg", "指定商品最多可买" + buy_num_max + "件");
                                return false;
                            } else if (checkNullReturnInt(each_goods_max_buy_num).intValue()
                                < (num.intValue() + couponDetailMapper.getUseBuyedGoodsNum(siteId, goodsId, member.getBuyerId(), ruleId).intValue())) {
                                mapParam.put("errorMsg", "指定商品最多买" + each_goods_max_buy_num + "件，超出不可用");
                                return false;
                            }

                            return false;
                        }
                        //b:对于指定商品不参加 需要对所有购买的非指定不参加的商品进行购买量的校验
                        else if (goodsRule.getType() == 3 && !set.contains(goodsId.toString()) && !(checkNullReturnInt(buy_num_max).intValue() >= num.intValue() &&
                            checkNullReturnInt(each_goods_max_buy_num).intValue()
                                >= (num.intValue() + couponDetailMapper.getUseBuyedGoodsNum(siteId, goodsId, member.getBuyerId(), ruleId).intValue()))) {
                            if (num.intValue() > checkNullReturnInt(buy_num_max).intValue()) {
                                mapParam.put("errorMsg", "指定商品最多可买" + buy_num_max + "件");
                                return false;
                            } else if (checkNullReturnInt(each_goods_max_buy_num).intValue()
                                < (num.intValue() + couponDetailMapper.getUseBuyedGoodsNum(siteId, goodsId, member.getBuyerId(), ruleId).intValue())) {
                                mapParam.put("errorMsg", "指定商品最多买" + each_goods_max_buy_num + "件，超出不可用");
                                return false;
                            }
                            return false;
                        }
                        break;
                    case 4:
                        //直减(按商品 与满减类似) 直折
                        break;
                    case 5:
                        //第二件半价
                        if (CouponType == 200 && !checkMoreGoodsDirectCount(goodsRuleJson, goodsInfo)) {
                            mapParam.put("errorMsg", "商品未满2件，不可用");
                            return false;
                        }

                        break;
                }


            }
        } catch (Exception e) {
            mapParam.put("errorMsg", "不可使用");
            log.error("Exception", e);
            return false;
        }
        return true;
    }


    //第几件半折规则校验
    private boolean checkMoreGoodsDirectCount(String goodsRuleJson, List<Map<String, Integer>> goodsInfo) {
        try {
            GoodsRule goodsRule = JSON.parseObject(goodsRuleJson, GoodsRule.class);
            Map<String, Object> stringMap = couponProcessUtils.String2Map(goodsRule.getRule());
            switch (goodsRule.getType()) {
                case 0:
                    return goodsInfo.stream().anyMatch(goods -> (checkNullReturnInt(goods.get("num").toString()).intValue() >= checkNullReturnInt(stringMap.get("how_piece").toString()).intValue()));
                case 2:
                    return goodsInfo.stream().anyMatch(goods -> new HashSet<String>(Arrays.asList(goodsRule.getPromotion_goods().split(","))).contains(goods.get("goodsId").toString()) && (checkNullReturnInt(goods.get("num").toString()).intValue() >= checkNullReturnInt(stringMap.get("how_piece").toString()).intValue()));
                case 3:
                    return goodsInfo.stream().anyMatch(goods -> (!new HashSet<String>(Arrays.asList(goodsRule.getPromotion_goods().split(","))).contains(goods.get("goodsId").toString())) && (checkNullReturnInt(goods.get("num").toString()).intValue() >= checkNullReturnInt(stringMap.get("how_piece").toString()).intValue()));
            }
        } catch (Exception e) {
            log.info("查询多件半折解析判断异常:" + e);
            return false;
        }
        return false;
    }

    //折扣券验证满件规则
    private boolean checkGoodsContainsNum(String goodsRuleJson, List<Map<String, Integer>> goodsInfo, Map<String, Object> paramMap) {
        GoodsRule goodsRule = JSON.parseObject(goodsRuleJson, GoodsRule.class);
        boolean result = false;
        //全部商品满足满减只需一条数据满足即可
        if (goodsRule.getType() == 0) {
            List<Map<String, String>> mapList = null;
            int totalNum = 0;
            for (Map<String, Integer> map : goodsInfo) {
                totalNum += map.get("num");
            }
            mapList = couponProcessUtils.String2List(goodsRule.getRule());
            mapList = mapList.stream().filter(map1 -> map1.get("ladder").equals("1"))
                .collect(Collectors.toList());
            String num = mapList.get(0).get("meet_num");

            if ((checkNullReturnInt(num) <= totalNum)) {
                return true;
            } else {
                paramMap.put("errorMsg", "商品未满" + num + "件，不可用");
            }
            //指定商品参加
        } else if (goodsRule.getType() == 2) {
            List<Map<String, String>> mapList = null;
            int totalNum = 0;
            for (Map<String, Integer> map : goodsInfo) {
                Integer goodsId = map.get("goodsId");
                if (goodsRule.getPromotion_goods().contains(goodsId.toString())) {
                    totalNum += map.get("num");
                }
            }
            mapList = couponProcessUtils.String2List(goodsRule.getRule());
            mapList = mapList.stream().filter(map1 -> map1.get("ladder").equals("1"))
                .collect(Collectors.toList());

            String num = mapList.get(0).get("meet_num");
            if ((checkNullReturnInt(num) <= totalNum)) {
                return true;
            } else {
                paramMap.put("errorMsg", "指定商品未满" + num + "件，不可用");
            }

            //指定商品不参加
        } else if (goodsRule.getType() == 3) {
            List<Map<String, String>> mapList = null;
            int totalNum = 0;
            for (Map<String, Integer> map : goodsInfo) {
                Integer goodsId = map.get("goodsId");
                if (!goodsRule.getPromotion_goods().contains(goodsId.toString())) {
                    totalNum += map.get("num");
                }
            }

            mapList = couponProcessUtils.String2List(goodsRule.getRule());
            mapList = mapList.stream().filter(map1 -> map1.get("ladder").equals("1"))
                .collect(Collectors.toList());

            String num = mapList.get(0).get("meet_num");
            if ((checkNullReturnInt(num) <= totalNum)) {
                return true;
            } else {
                paramMap.put("errorMsg", "指定商品未满" + num + "件，不可用");
            }

        }
        return result;
    }


    //现金券满元递归层级验证
    private boolean checkGoodsIfContainsMoney2(String goodsRuleJson, List<Map<String, Integer>> goodsInfo, Integer postFee, Map<String, Object> paramMap) {
        GoodsRule goodsRule = JSON.parseObject(goodsRuleJson, GoodsRule.class);
        boolean result = false;
        //全部商品满足满减只需一条数据满足即可
        if (goodsRule.getType() == 0) {
            List<Map<String, String>> mapList = null;
            int totalPrice = 0;
            totalPrice = goodsInfo.stream().mapToInt(map -> map.get("num") * map.get("goodsPrice")).sum();
            mapList = couponProcessUtils.String2List(goodsRule.getRule());
            mapList = mapList.stream().filter(map1 -> map1.get("ladder").equals("1"))
                .collect(Collectors.toList());
            String eachFullMoney = mapList.get(0).get("meet_money");
            if (goodsRule.getIs_post() == 1) {
                totalPrice = totalPrice + postFee;
            }

            if ((checkNullReturnInt(eachFullMoney) <= totalPrice)) {
                return true;
            } else {
                paramMap.put("errorMsg", "金额未满" + AmountUtils.changeF2Y(eachFullMoney) + "元，不可用");
            }
            //指定商品参加
        } else if (goodsRule.getType() == 2) {
            List<Map<String, String>> mapList = null;
            int totalPrice = 0;

            totalPrice = goodsInfo.stream().filter(map -> new HashSet<String>(Arrays.asList(goodsRule.getPromotion_goods().
                split(","))).contains(map.get("goodsId").toString())).mapToInt(
                map -> map.get("num") * map.get("goodsPrice")
            ).sum();

            mapList = couponProcessUtils.String2List(goodsRule.getRule());
            mapList = mapList.stream().filter(map1 -> map1.get("ladder").equals("1"))
                .collect(Collectors.toList());
            String eachFullMoney = mapList.get(0).get("meet_money");

            if (goodsRule.getIs_post() == 1)
                totalPrice = totalPrice + postFee;

            if (((checkNullReturnInt(eachFullMoney) <= totalPrice)))
                return true;
            else
                paramMap.put("errorMsg", "金额未满" + AmountUtils.changeF2Y(eachFullMoney) + "元，不可用");


            //指定商品不参加
        } else if (goodsRule.getType() == 3) {
            List<Map<String, String>> mapList = null;
            int totalPrice = 0;

            totalPrice = goodsInfo.stream().filter(map ->
                !new HashSet<String>(Arrays.asList(goodsRule.getPromotion_goods().split(","))).
                    contains(map.get("goodsId").toString()))
                .mapToInt(map -> map.get("num") * map.get("goodsPrice")).sum();

            mapList = couponProcessUtils.String2List(goodsRule.getRule());
            mapList = mapList.stream().filter(map1 -> map1.get("ladder").equals("1"))
                .collect(Collectors.toList());
            String eachFullMoney = mapList.get(0).get("meet_money");

            if (goodsRule.getIs_post() == 1)
                totalPrice = totalPrice + postFee;

            if (((checkNullReturnInt(eachFullMoney) <= totalPrice))) {
                return true;
            } else {
                paramMap.put("errorMsg", "金额未满" + AmountUtils.changeF2Y(eachFullMoney) + "元，不可用");
            }
        }
        return result;
    }

    //现金券满元验证
    private boolean checkGoodsIfContainsMoney(String goodsRuleJson, List<Map<String, Integer>> goodsInfo, Integer postFee, Map<String, Object> parammap) {
        log.info("现金券每满减规则校验：。。。。。");
        GoodsRule goodsRule = JSON.parseObject(goodsRuleJson, GoodsRule.class);
        boolean result = false;
        //全部商品满足满减只需一条数据满足即可
        if (goodsRule.getType() == 0) {
            Map<String, Object> stringMap = null;
            int totalPrice = 0;
            totalPrice = goodsInfo.stream().mapToInt(map -> map.get("num") * map.get("goodsPrice")).sum();
            stringMap = couponProcessUtils.String2Map(goodsRule.getRule());
            String eachFullMoney = stringMap.get("each_full_money").toString();

            if (goodsRule.getIs_post() == 1)
                totalPrice = totalPrice + postFee;

            if ((checkNullReturnInt(eachFullMoney) <= totalPrice)) {
                log.info("现金券每满减规则校验结果：" + true);
                return true;
            } else {
                parammap.put("errorMsg", "金额未满" + AmountUtils.changeF2Y(eachFullMoney) + "元，不可用");
            }
            //指定商品参加
        } else if (goodsRule.getType() == 2) {
            Map<String, Object> stringMap = null;
            int totalPrice = 0;

            totalPrice = goodsInfo.stream().filter(map -> new HashSet<String>(Arrays.asList(goodsRule.getPromotion_goods().
                split(","))).contains(map.get("goodsId").toString())).mapToInt(
                map -> map.get("num") * map.get("goodsPrice")
            ).sum();

            stringMap = couponProcessUtils.String2Map(goodsRule.getRule());
            String eachFullMoney = stringMap.get("each_full_money").toString();
            if (goodsRule.getIs_post() == 1)
                totalPrice = totalPrice + postFee;

            if (((checkNullReturnInt(eachFullMoney) <= totalPrice))) {
                log.info("现金券每满减规则校验结果：" + true);
                return true;
            } else {
                parammap.put("errorMsg", "金额未满" + AmountUtils.changeF2Y(eachFullMoney) + "元，不可用");
            }
            //指定商品不参加
        } else if (goodsRule.getType() == 3) {
            Map<String, Object> stringMap = null;
            int totalPrice = 0;

            totalPrice = goodsInfo.stream().filter(map ->
                !new HashSet<String>(Arrays.asList(goodsRule.getPromotion_goods().split(","))).
                    contains(map.get("goodsId").toString()))
                .mapToInt(map -> map.get("num") * map.get("goodsPrice")).sum();

            stringMap = couponProcessUtils.String2Map(goodsRule.getRule());
            String eachFullMoney = stringMap.get("each_full_money").toString();

            if (goodsRule.getIs_post() == 1)
                totalPrice = totalPrice + postFee;

            if (((checkNullReturnInt(eachFullMoney) <= totalPrice))) {
                log.info("现金券每满减规则校验结果：" + true);
                return true;
            } else {
                parammap.put("errorMsg", "金额未满" + AmountUtils.changeF2Y(eachFullMoney) + "元，不可用");
            }
        }
        log.info("现金券每满减规则校验结果：" + result);
        return result;
    }


    private boolean checkGoodsByShopCar(GoodsRule goodsRule, List<Map<String, Integer>> goodsInfo) {
        //对于限制性商品  只要购物车中的商品有一条是在限制性商品列表里的该购物券就可用
        log.info("优惠券指定商品校验:.......");
        String[] goods = goodsRule.getPromotion_goods().split(",");
        Set<String> set = new HashSet<String>(Arrays.asList(goods));
        boolean included = false;
        included = goodsInfo.stream().anyMatch(
            map -> set.contains(map.get("goodsId").toString()));
        log.info("优惠券指定商品校验返回结果:" + included);
        return included;
    }


    private boolean checkGoodsUnByShopCar(GoodsRule goodsRule, List<Map<String, Integer>> goodsInfo) {
        //对于限制性指定不参加商品
        log.info("优惠券指定商品不参加校验:.......");
        String[] goods = goodsRule.getPromotion_goods().split(",");
        Set<String> set = new HashSet<String>(Arrays.asList(goods));
        boolean included = false;
        included = goodsInfo.stream().allMatch(map -> set.contains(map.get("goodsId").toString()));
        log.info("优惠券指定商品不参加校验结果:" + included);
        return !included;
    }

    public boolean checkOrderRule(Map<String, Object> paramMap, OrderMessageParams orderMessageParams) {
        try {
            String orderRuleJson = (String) paramMap.get("order_rule");
            String areaRuleJson = (String) paramMap.get("area_rule");
            List<Map<String, Integer>> goodsInfo = orderMessageParams.getGoodsInfo();
            int orderFee = orderMessageParams.getOrderFee();
            Integer areaId = orderMessageParams.getAreaId();

            OrderRule orderRule = JSON.parseObject(orderRuleJson, OrderRule.class);
            Iterator<Map<String, Integer>> iterator = goodsInfo.iterator();
            int orderNum = 0;
            while (iterator.hasNext()) {
                Map<String, Integer> map = iterator.next();
                orderNum += map.get("num");
                if (orderRule.getGoods_num_max() != null && orderRule.getGoods_num_max() < map.get("num").intValue()) {
                    return false;
                }
            }
            if (orderRule.getOrder_money_max() != null && orderFee > orderRule.getOrder_money_max()) {
                return false;
            }

            Map<String, Object> stringMap = couponProcessUtils.String2Map(orderRule.getRule());
            switch (orderRule.getRule_type()) {
                case 0:
                    //立减
                    return true;
                case 1:
                    //每满
                    String eachFullMoney = stringMap.get("each_full_money").toString();
                    if (checkNullReturnInt(eachFullMoney) <= orderFee) {
                        return true;
                    }
                    break;
                case 2:
                    //满元
                    String money = stringMap.get("meet_money_first").toString();
                    if (checkNullReturnInt(money) <= orderFee) {
                        return true;
                    }
                    break;
                case 3:
                    //满件
                    String num = stringMap.get("meet_num_first").toString();
                    if (checkNullReturnInt(num) <= orderNum) {
                        return true;
                    }
                    break;
                case 4:
                    //包邮
                    //检测地址
                    if (checkPostCondition(orderRule, orderFee, orderNum) && checkAddr(areaRuleJson, areaId))
                        return true;
                    break;
            }
        } catch (Exception e) {
            log.error("Exception", e);
            return false;
        }
        return false;
    }

    /**
     * 检测包邮条件
     *
     * @param orderRule
     * @param orderFee
     * @param orderNum
     * @return
     */
    private boolean checkPostCondition(OrderRule orderRule, int orderFee, int orderNum) {

        Map<String, Object> stringMap = couponProcessUtils.String2Map(orderRule.getRule());

        String meetMoney = stringMap.get("order_full_money").toString();

        if (checkNullReturnInt(meetMoney) <= orderFee) {
            return true;
        }
        String meetNum = stringMap.get("order_full_num").toString();
        if (checkNullReturnInt(meetNum) <= orderNum) {
            return true;
        }
        return false;
    }

    /**
     * 检测地址
     *
     * @param areaRuleJson
     * @param areaId
     * @return
     * @throws Exception
     */
    private boolean checkAddr(String areaRuleJson, Integer areaId) throws Exception {
        if (areaId != null)
            return false;
        if (!StringUtil.isEmpty(areaRuleJson)) {
            AreaRule areaRule = JacksonUtils.json2pojo(areaRuleJson, AreaRule.class);
            boolean included = areaRule.getPost_area().equals(0);
            String provinccIds = areaRule.getRule().get("province_ids");
            if (!StringUtil.isEmpty(provinccIds)) {
                String[] pIds = provinccIds.split(",");
                for (int i = 0; i < pIds.length; ++i) {
                    if (areaId.intValue() == checkNullReturnInt(pIds[i])) {
                        return included;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 创建优惠券
     *
     * @param basisParams
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ReturnDto createCouponRule(BasisParams basisParams) {
        String str = checkParam(basisParams);
        if (!StringUtils.isBlank(str)) {
            return ReturnDto.buildFailedReturnDto(str);
        }

        try {
            couponRuleMapper.addCouponRule(buildCouponRule(basisParams));
        } catch (Exception e) {
            log.error("站点" + basisParams.getSiteId() + "优惠券创建失败，原因：" + e);
            return ReturnDto.buildFailedReturnDto("站点" + basisParams.getSiteId() + "优惠券创建失败，原因：" + e);
        }

        log.info("coupon rule create success");
        return ReturnDto.buildSuccessReturnDto("coupon rule create success");
    }

    /**
     * 模板创建优惠券
     *
     * @param basisParams
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ReturnDto createReleasedCouponRuleReturnKey(BasisParams basisParams) {
        String str = checkParam(basisParams);
        if (!StringUtils.isBlank(str)) {
            return ReturnDto.buildFailedReturnDto(str);
        }

        try {
            CouponRule couponRule = buildCouponRule(basisParams);
            //可发布状态
            couponRule.setStatus(0);
            couponRuleMapper.addCouponRuleAndGetId(couponRule);

            log.info("coupon rule create success");
            return ReturnDto.buildSuccessReturnDto(couponRule.getRuleId());
        } catch (Exception e) {
            log.error("站点" + basisParams.getSiteId() + "优惠券创建失败，原因：" + e);
            return ReturnDto.buildFailedReturnDto("站点" + basisParams.getSiteId() + "优惠券创建失败，原因：" + e);
        }
    }


    /**
     * 编辑待发放的优惠券
     *
     * @param basisParams
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ReturnDto editCouponRule(BasisParams basisParams) {
        if (basisParams.getRuleId() == null || basisParams.getStatus() != 10) {
            return ReturnDto.buildFailedReturnDto("该优惠券不可编辑");
        }

        String str = checkParam(basisParams);
        if (!StringUtils.isBlank(str)) {
            return ReturnDto.buildFailedReturnDto(str);
        }

        try {
            CouponRule couponRule = buildCouponRule(basisParams);
            couponRule.setRuleId(basisParams.getRuleId());
            int i = couponRuleMapper.updateCouponRuleById(couponRule);
            if (i != 1) throw new RuntimeException("不符合编辑条件");

        } catch (Exception e) {
            log.error("站点" + basisParams.getSiteId() + "优惠券编辑失败，原因：" + e);
            return ReturnDto.buildFailedReturnDto("站点" + basisParams.getSiteId() + "优惠券编辑失败，原因：" + e);
        }

        log.info("coupon rule edit success");
        return ReturnDto.buildSuccessReturnDto(basisParams.getRuleId());
    }


    /**
     * 编辑待发放的优惠券并发放
     *
     * @param basisParams
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ReturnDto editCouponRuleAndRelease(BasisParams basisParams) {
        if (basisParams.getRuleId() == null || basisParams.getStatus() != 10) {
            return ReturnDto.buildFailedReturnDto("该优惠券不可编辑");
        }

        String str = checkParam(basisParams);
        if (!StringUtils.isBlank(str)) {
            return ReturnDto.buildFailedReturnDto(str);
        }

        try {
            CouponRule couponRule = buildCouponRule(basisParams);
            couponRule.setRuleId(basisParams.getRuleId());
            //修改优惠券状态
            couponRule.setStatus(0);
            int i = couponRuleMapper.updateCouponRuleById(couponRule);
            if (i != 1) throw new RuntimeException("不符合编辑条件");

        } catch (Exception e) {
            log.error("站点" + basisParams.getSiteId() + "优惠券编辑失败，原因：" + e);
            return ReturnDto.buildFailedReturnDto("站点" + basisParams.getSiteId() + "优惠券编辑失败，原因：" + e);
        }

        log.info("coupon rule edit success");
        return ReturnDto.buildSuccessReturnDto(basisParams.getRuleId());
    }

    public ReturnDto editCouponRuleOneField(Integer siteId, Integer ruleId, String field, String value) {
        try {
            if (field.equals("goods")) {
                com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(value);
                Integer goodsIdsType = (Integer) jsonObject.get("goodsIdsType");
                String goodsIds = (String) jsonObject.get("goodsIds");
                if (goodsIdsType == null || goodsIds == null) {
                    return ReturnDto.buildFailedReturnDto("商品信息不足，无法修改");
                }
                CouponRule couponRule = couponRuleMapper.findCouponRuleById(ruleId, siteId);
                GoodsRule goodsRule = com.alibaba.fastjson.JSON.parseObject(couponRule.getGoodsRule(), GoodsRule.class);
                goodsRule.setType(goodsIdsType);
                goodsRule.setPromotion_goods(goodsIds);
                couponRuleMapper.updateOneFiled(siteId, ruleId, "goods_rule", com.alibaba.fastjson.JSON.toJSONString(goodsRule));
                return ReturnDto.buildSuccessReturnDto();
            } else {
                return ReturnDto.buildFailedReturnDto("field " + field + " 无法识别");
            }
        } catch (Exception e) {
            log.error("系统出错, {}", e);
            return ReturnDto.buildFailedReturnDto("系统出错");
        }
    }

    //优惠券信息相关商品过滤
    public PageInfo<?> queryCouponGoodsList(CouponGoods couponGoods) {

        try {
            if (null == couponGoods.getType()) {
                CouponRule couponRule = this.couponRuleMapper.findCouponRuleById(Integer.parseInt(couponGoods.getRuleId()),
                    Integer.parseInt(couponGoods.getSiteId()));

                String goodsRuleJson = couponRule.getGoodsRule();
                String orderRuleJson = couponRule.getOrderRule();

                if (StringUtils.isNotBlank(goodsRuleJson) && !goodsRuleJson.equalsIgnoreCase("null")) {
                    GoodsRule goodsRule = JSON.parseObject(goodsRuleJson, GoodsRule.class);
                    if (null != goodsRule.getType() && 0 == goodsRule.getType()) {
                        couponGoods.setType(goodsRule.getType());
                    } else if (null != goodsRule.getType() && (2 == goodsRule.getType() || 3 == goodsRule.getType())) {
                        couponGoods.setType(goodsRule.getType());
                        if (!StringUtils.isBlank(goodsRule.getPromotion_goods())) {
                            couponGoods.setList(goodsRule.getPromotion_goods().split(","));
                        } else {
                            couponGoods.setList(null);
                        }
                    } else {
                        couponGoods.setType(0);
                    }
                } else if (StringUtils.isNotBlank(orderRuleJson)) {
                    couponGoods.setType(0);
                }
            } else if (couponGoods.getType() == -1) {

                PromotionsRule promotionsRule = promotionsRuleMapper.getPromotionsRuleByIdAndSiteId(Integer.parseInt(couponGoods.getSiteId()), Integer.parseInt(couponGoods.getRuleId()));
                if (promotionsRule.getPromotionsType() == 10) {
                    GiftRule giftRule = JacksonUtils.json2pojo(promotionsRule.getPromotionsRule(), GiftRule.class);
                    couponGoods.setType(2);
                    if (!StringUtils.isBlank(giftRule.getGoodsIds())) {
                        couponGoods.setList(giftRule.getGoodsIds().split(","));
                    } else {
                        couponGoods.setList(null);
                    }
                } else if (promotionsRule.getPromotionsType() == 20) {

                    DiscountRule discountRule = JacksonUtils.json2pojo(promotionsRule.getPromotionsRule(), DiscountRule.class);

                    if (null != discountRule.getGoodsIdsType() && 0 == discountRule.getGoodsIdsType())
                        couponGoods.setType(discountRule.getGoodsIdsType());
                    else if (null != discountRule.getGoodsIdsType() && (2 == discountRule.getGoodsIdsType() + 1 || 3 == discountRule.getGoodsIdsType() + 1)) {

                        couponGoods.setType(discountRule.getGoodsIdsType() + 1);
                        if (!StringUtils.isBlank(discountRule.getGoodsIds()))
                            couponGoods.setList(discountRule.getGoodsIds().split(","));
                        else
                            couponGoods.setList(null);


                    } else {
                        couponGoods.setType(0);
                    }
                } else if (promotionsRule.getPromotionsType() == 30) {
                    FreePostageRule freePostageRule = JacksonUtils.json2pojo(promotionsRule.getPromotionsRule(), FreePostageRule.class);
                    if (null != freePostageRule.getGoodsIdsType() && 0 == freePostageRule.getGoodsIdsType())
                        couponGoods.setType(freePostageRule.getGoodsIdsType());
                    else if (null != freePostageRule.getGoodsIdsType() && (2 == freePostageRule.getGoodsIdsType() + 1 || 3 == freePostageRule.getGoodsIdsType() + 1)) {

                        couponGoods.setType(freePostageRule.getGoodsIdsType() + 1);
                        if (!StringUtils.isBlank(freePostageRule.getGoodsIds())) {
                            couponGoods.setList(freePostageRule.getGoodsIds().split(","));
                        } else {
                            couponGoods.setList(null);
                        }
                    } else {
                        couponGoods.setType(0);
                    }

                } else if (promotionsRule.getPromotionsType() == 40) {
                    ReduceMoneyRule reduceMoneyRule = JacksonUtils.json2pojo(promotionsRule.getPromotionsRule(), ReduceMoneyRule.class);
                    if (null != reduceMoneyRule.getGoodsIdsType() && 0 == reduceMoneyRule.getGoodsIdsType())
                        couponGoods.setType(reduceMoneyRule.getGoodsIdsType());
                    else if (null != reduceMoneyRule.getGoodsIdsType() && (2 == reduceMoneyRule.getGoodsIdsType() + 1 || 3 == reduceMoneyRule.getGoodsIdsType() + 1)) {

                        couponGoods.setType(reduceMoneyRule.getGoodsIdsType() + 1);
                        if (!StringUtils.isBlank(reduceMoneyRule.getGoodsIds())) {
                            couponGoods.setList(reduceMoneyRule.getGoodsIds().split(","));
                        } else {
                            couponGoods.setList(null);
                        }
                    } else {
                        couponGoods.setType(0);
                    }

                } else if (promotionsRule.getPromotionsType() == 50) {
                    FixedPriceRule fixedPriceRule = JacksonUtils.json2pojo(promotionsRule.getPromotionsRule(), FixedPriceRule.class);
                    if (null != fixedPriceRule.getGoodsIdsType() && 0 == fixedPriceRule.getGoodsIdsType())
                        couponGoods.setType(fixedPriceRule.getGoodsIdsType());
                    else if (null != fixedPriceRule.getGoodsIdsType() && (2 == fixedPriceRule.getGoodsIdsType() + 1 || 3 == fixedPriceRule.getGoodsIdsType() + 1)) {

                        couponGoods.setType(fixedPriceRule.getGoodsIdsType() + 1);
                        if (!StringUtils.isBlank(fixedPriceRule.getGoodsIds())) {
                            couponGoods.setList(fixedPriceRule.getGoodsIds().split(","));
                        } else {
                            couponGoods.setList(null);
                        }
                    } else {
                        couponGoods.setType(0);
                    }

                } else if (promotionsRule.getPromotionsType() == 60) {
                    GroupBookingRule groupBookingRule = JacksonUtils.json2pojo(promotionsRule.getPromotionsRule(), GroupBookingRule.class);
                    if (null != groupBookingRule.getGoodsIdsType() && groupBookingRule.getGoodsIdsType().intValue() == 0) {
                        couponGoods.setType(groupBookingRule.getGoodsIdsType());
                    } else if (null != groupBookingRule.getGoodsIdsType() && (2 == groupBookingRule.getGoodsIdsType() + 1 || 3 == groupBookingRule.getGoodsIdsType() + 1)) {
                        couponGoods.setType(groupBookingRule.getGoodsIdsType() + 1);
                        if (StringUtils.isNotEmpty(groupBookingRule.getGoodsIds())) {
                            couponGoods.setList(groupBookingRule.getGoodsIds().split(","));
                        } else {
                            couponGoods.setList(null);
                        }
                    } else {
                        couponGoods.setType(0);
                    }
                }
            }
        } catch (Exception e) {
            log.info("微信商城读取商品列表异常:" + e);
            return new PageInfo<>(null);
        }
        PageHelper.startPage(couponGoods.getPageNum(), couponGoods.getPageSize());
        List<PageData> list = couponRuleMapper.queryCouponGoodsForCouponRule(couponGoods);
        return new PageInfo<>(list);
    }


    /**
     * 导出优惠券规则校验
     *
     * @param basisParams
     * @return
     */
    public ReturnDto couponDownDetail(BasisParams basisParams) {
        if (null == basisParams.getRuleId()) {
            return ReturnDto.buildFailedReturnDto("参数错误，优惠券主键不能为空");
        }
        if (basisParams.getSiteId() == null) {
            return ReturnDto.buildFailedReturnDto("参数错误，站点主键不能为空");
        }

        CouponExportLog logParam = new CouponExportLog();
        logParam.setSiteId(basisParams.getSiteId());
        logParam.setRuleId(basisParams.getRuleId());

        CouponRule resultRule = couponRuleMapper.findMemberNumByIdforExport(basisParams.getRuleId(), basisParams.getSiteId());
        Integer sendAmount = couponDetailMapper.findSendAmount(basisParams.getSiteId(), null, basisParams.getRuleId());
        resultRule.setSendNum(sendAmount);
        Map<String, Object> resultMap = new HashMap<String, Object>();

        //查询操作过的日志记录
        resultMap.put("couponRuleLogList", couponExportMapper.findCouponExportLog(logParam));

        //已发送的张数
        resultMap.put("sendNum", resultRule.getSendNum() == null ? 0 : resultRule.getSendNum());

        //不限类型的优惠券 创建总数，还可以导出，已导出
        if (resultRule.getTotal() == -1) {
            resultMap.put("createTotalNum", "不限");
            resultMap.put("surpluse", "不限");
        } else {
            resultMap.put("createTotalNum", resultRule.getTotal());
            resultMap.put("canExportNum", resultRule.getTotal() - resultRule.getSendNum());
            resultMap.put("surpluse", resultRule.getTotal());
        }

        if (couponExportMapper.findCouponExportLog(logParam).isEmpty()) {
            resultMap.put("canExportNum", 0);
            resultMap.put("startNum", 1);
            resultMap.put("endNum", 10000);
        } else {
            resultMap.put("canExportNum", couponExportMapper.findCouponExportLog(logParam).size() * 10000);
            resultMap.put("startNum", couponExportMapper.findCouponExportLog(logParam).
                get(couponExportMapper.findCouponExportLog(logParam).size() - 1).getStartCouponNo() + 10000);
            resultMap.put("endNum", couponExportMapper.findCouponExportLog(logParam).
                get(couponExportMapper.findCouponExportLog(logParam).size() - 1).getEndCouponNo() + 10000);
        }

        return ReturnDto.buildSuccessReturnDto(resultMap);
    }

    /**
     * 参数校验
     *
     * @param basisParams
     * @return 返回值为null，代表校验成功，否则返回的是失败内容
     */
    private String checkParam(BasisParams basisParams) {
        if (basisParams == null) {
            return "请求参数不能为空";
        }
        if (basisParams.getSiteId() == null) {
            return "站点siteId 不能为空";
        }
        if (StringUtils.isBlank(basisParams.getRuleName())) {
            return "优惠券名不能为空";
        }
        if (StringUtils.isBlank(basisParams.getMarkedWords())) {
            return "前台提示语不能为空";
        }
        if (basisParams.getCouponType() == null) {
            return "优惠券类型不能为空";
        }
        if (basisParams.getAmount() == null) {
            return "优惠券数量不能为空";
        }
        if (basisParams.getAimAt() == null) {
            return "针对订单还是商品不能为空";
        }
        if (basisParams.getTimeRule() == null) {
            return "有效期规则不能为空";
        }
        if (basisParams.getLimitRule() == null) {
            return "限制规则不能为空";
        }
        if (basisParams.getLimitRule().getIs_first_order() == null) {
            return "是否首单不能为空";
        }
        if (StringUtils.isBlank(basisParams.getLimitRule().getOrder_type())) {
            return "订单类型不能为空";
        }
        if (StringUtils.isBlank(basisParams.getLimitRule().getApply_channel())) {
            return "适用渠道不能为空";
        }
        if (basisParams.getLimitRule().getApply_store() == null) {
            return "适用门店类型不能为空";
        }
        if (basisParams.getLimitRule().getIs_share() == null) {
            return "是否可分享不能为空";
        }
        if (basisParams.getOrderRule() == null && basisParams.getGoodsRule() == null) {
            return "订单规则和商品规则不能同时为空";
        }
        return null;
    }


    /**
     * 修改优惠券
     *
     * @param basisParams
     * @return
     */
    public ReturnDto updateCouponRule(BasisParams basisParams) {
        CouponRule couponRule = couponRuleMapper.findCouponRuleById(basisParams.getRuleId(), basisParams.getSiteId());
        if (couponRule == null) {
            log.info("站点" + basisParams.getSiteId() + "优惠券修改失败，原因：没有找到此优惠券");
            return ReturnDto.buildFailedReturnDto("站点" + basisParams.getSiteId() + "优惠券修改失败，原因：没有找到此优惠券");
        }
        try {
            if (basisParams.getDayNum() == null) {
                basisParams.setDayNum(0);
            }
            if (basisParams.getAmount() == null) {
                basisParams.setAmount(0);
            }

            if (checkLimitRule(couponRule, basisParams.getDayNum(), basisParams.getAmount())) {
                couponRuleMapper.updateCouponRule(builUpdateCouponRule(basisParams, couponRule));

            } else {
                return ReturnDto.buildFailedReturnDto("站点" + basisParams.getSiteId() + "优惠券修改失败,当前数量为不限制，" +
                    "不能追加数量，或者有效期不为绝对时间，不能追加时间");
            }
        } catch (Exception e) {
            log.info("站点" + basisParams.getSiteId() + "优惠券修改失败，原因：" + e);
            return ReturnDto.buildFailedReturnDto("站点" + basisParams.getSiteId() + "优惠券修改失败，原因：" + e);
        }

        checkRuleAndActivityStatusByRuleId(basisParams.getSiteId(), basisParams.getRuleId());

        log.info("coupon rule update success");
        return ReturnDto.buildSuccessReturnDto("coupon rule update success");
    }

    // 判断追加的天数或者优惠券的数量是否大于当前时间或者大于0 则修改优惠券的状态
    private boolean judgeAddDay(Integer dayNum, CouponRule couponRule, Integer amount) {
        TimeRule timeRules = JSON.parseObject(couponRule.getTimeRule(), TimeRule.class);
        // 判断时间类型
        if (timeRules.getValidity_type() == 2 && couponRule.getStatus() != 2 && (couponRule.getAmount() + amount.intValue()) > 0) {
            return true;
        } else if (timeRules.getValidity_type() == 1) {
            String end_time = timeRules.getEndTime();
            Integer num = couponRule.getAmount();
            LocalDate localDate = LocalDate.parse(end_time, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate s = localDate.plusDays(dayNum);
            if ((num == -1 || (num + amount) > 0) && s.getYear() >= LocalDate.now().getYear() && s.getDayOfYear() >= LocalDate.now().getDayOfYear()) {
                return true;
            }
        }
        return false;
    }

    private CouponRule builUpdateCouponRule(BasisParams basisParams, CouponRule couponRule) throws Exception {
        CouponRule updateCouponRule = new CouponRule();
        updateCouponRule.setSiteId(basisParams.getSiteId());
        updateCouponRule.setRuleId(basisParams.getRuleId());
        updateCouponRule.setAmount(basisParams.getAmount() + couponRule.getAmount());
        updateCouponRule.setTotal(basisParams.getAmount() + couponRule.getTotal());
        if (basisParams.getDayNum() > 0) {//追加时间大于0天才处理
            TimeRule timeRule = JSON.parseObject(couponRule.getTimeRule(), TimeRule.class);
            try {
                String end_time = timeRule.getEndTime();
                LocalDate localDate = LocalDate.parse(end_time, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                timeRule.setEndTime(String.valueOf(localDate.plusDays(basisParams.getDayNum())));
                updateCouponRule.setTimeRule(JacksonUtils.obj2json(timeRule));
            } catch (Exception e) {
                throw e;
            }
        } else {
            updateCouponRule.setTimeRule(couponRule.getTimeRule());
        }
        return updateCouponRule;
    }

    private TimeRule resolveTimeJson(String timeRuleJson) {
        ObjectMapper jsonStu = new ObjectMapper();
        TimeRule timeRule = new TimeRule();
        try {
            timeRule = jsonStu.readValue(timeRuleJson, TimeRule.class);
        } catch (Exception e) {
            log.info("转换异常，错误信息：" + e);
        }
        return timeRule;
    }

    private boolean checkLimitRule(CouponRule couponRule, int dayNum, int amount) {
        TimeRule timeRule = JSON.parseObject(couponRule.getTimeRule(), TimeRule.class);
        if (amount > 0 && couponRule.getAmount() > -1 && dayNum > 0 && timeRule.getValidity_type() == 1) {
            return true;
        } else if (amount > 0 && couponRule.getAmount() > -1 && dayNum == 0) {
            return true;
        } else if (timeRule.getValidity_type() == 1 && dayNum > 0) {
            return true;
        }

        return false;
    }

    /**
     * 把参数封装入CouponRule对象中
     *
     * @param basisParams
     * @return 封装完数据的CouponRule对象
     * @throws Exception
     */
    private CouponRule buildCouponRule(BasisParams basisParams) throws Exception {
        CouponRule couponRule = new CouponRule();

        couponRule.setSiteId(basisParams.getSiteId());
        couponRule.setRuleName(basisParams.getRuleName());
        couponRule.setMarkedWords(basisParams.getMarkedWords());
        couponRule.setCouponType(basisParams.getCouponType());
        couponRule.setAmount(basisParams.getAmount());
        couponRule.setTimeRule(JacksonUtils.obj2json(basisParams.getTimeRule()));
        couponRule.setLimitRule(JacksonUtils.obj2json(basisParams.getLimitRule()));
        couponRule.setLimitState(basisParams.getLimitState());
        couponRule.setLimitRemark(basisParams.getLimitRemark());
        couponRule.setAimAt(basisParams.getAimAt());

        couponRule.setStatus(10);
        couponRule.setOrderRule(JSON.toJSONString(basisParams.getOrderRule()));
        couponRule.setAreaRule(JSON.toJSONString(basisParams.getAreaRule()));
        couponRule.setGoodsRule(JSON.toJSONString(basisParams.getGoodsRule()));
        couponRule.setTotal(basisParams.getAmount());

        return couponRule;
    }

    public String ObjectConvertJson(Object rule) {
        ObjectMapper jsonStu = new ObjectMapper();
        String str = "";
        try {
            str = jsonStu.writeValueAsString(rule);
        } catch (Exception e) {
            log.info("转换异常，错误信息：" + e);
            return "";
        }
        return str;
    }

    private Integer checkNullReturnInt(String str) {
        if (StringUtils.isBlank(str)) {
            return -1;
        } else {
            return Integer.parseInt(str);
        }
    }

    //优惠券使用情况
    public void updateRuleStatus(Integer siteId, Integer ruleId, Integer sendNum,
                                 Integer useAmount, Integer orderPrice, Integer goodsNum,
                                 Integer receiveNum) {
        CouponRule couponRule = new CouponRule();
        couponRule.setSiteId(siteId);
        couponRule.setRuleId(ruleId);
        couponRule.setUseAmount(useAmount);
        couponRule.setGoodsNum(goodsNum);
        couponRule.setReceiveNum(receiveNum);
        couponRule.setOrderPrice(orderPrice);
        couponRule.setSendNum(sendNum);
        couponRuleMapper.updateCouponRulesById(couponRule);
    }

    //优惠券分享次数
    public void updateRuleStatus(Integer siteId, Integer ruleId, Integer shareNum) {
        CouponRule couponRule = new CouponRule();
        couponRule.setSiteId(siteId);
        couponRule.setRuleId(ruleId);
        couponRule.setShareNum(shareNum);
        couponRuleMapper.updateCouponRulesById(couponRule);
    }

    //导出报表
    public List<Map<String, Object>> findCouponListExcel(Map<String, Object> params) {
        //创建日志装载对象
        CouponLog couponLog = new CouponLog();
        //获取前台传递的各种参数
        int siteId = Integer.parseInt(params.get("siteId").toString());
        int ruleId = Integer.parseInt(params.get("ruleId").toString());
        int startTime = Integer.parseInt(params.get("startNum").toString());
        int endTime = Integer.parseInt(params.get("endNum").toString());
        couponLog.setSiteId(siteId);
        couponLog.setRuleId(ruleId);
        couponLog.setStartCouponNo(startTime);
        couponLog.setEndCouponNo(endTime);
        //查询coupon_rule 表，得到有效期和金额的数据
        CouponRule couponRuleById = ruleMapper.findCouponRuleById(ruleId, siteId);
        String timeRule = couponRuleById.getTimeRule();
        int aimAt = couponRuleById.getAimAt();
        String goodsRule = couponRuleById.getGoodsRule();
        int couponType = couponRuleById.getCouponType();
        String orderRule = couponRuleById.getOrderRule();
        CouponView view = parsingCouponRuleService.accountCoupon(aimAt, couponType, orderRule, goodsRule);
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = startTime; i <= endTime; i++) {
            String num = ruleId + String.format("%08d", i);

            Map<String, Object> map = new HashMap<String, Object>();
            try {
                Map<String, Object> ruleMap = JacksonUtils.json2map(timeRule);
                //如果是绝对时间才显示，如果是相对时间显示--

                Integer validity_type = ruleMap.get("validity_type") == null ? 0 : Integer.parseInt(ruleMap.get("validity_type").toString());
                if (validity_type == 1 || validity_type == 4) {
                    String MaxTime = ruleMap.get("startTime").toString() + "-" + ruleMap.get("endTime");
                    map.put("status", MaxTime);
                } else {
                    map.put("status", "--");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            map.put("rule_name", params.get("type"));
            //如果在页面URL上没有拼接这个字段，则直接在mapper返回数据中获取
            if (map.get("rule_name") == null || "undefined".equals(map.get("rule_name"))) {
                map.put("rule_name", couponRuleById.getRuleName());
            }
            //将获取到的优惠券编码加密
            map.put("num", couponNoEncodingService.encryptionCouponNo(num));
            map.put("pay", view.getRuleDetail());
            list.add(map);
        }
        CouponLog couponLog1 = couponLogMapper.queryLog(couponLog);
        if (couponLog1 != null) {
            couponLogMapper.updateLog(couponLog);
        } else {
            couponLogMapper.insertLog(couponLog);
        }
        return list;
    }

    //优惠券使用详情
    public Object findCouponListTable(Map<String, Object> params) throws Exception {

        //获取前台传递的各种参数
        int siteId = Integer.parseInt(params.get("siteId").toString());
        int ruleId = Integer.parseInt(params.get("ruleId").toString());
        int startNum = params.get("page") == null ? 1 : Integer.parseInt(params.get("page").toString());
        int pageSize = params.get("pageSize") == null ? 2000 : Integer.parseInt(params.get("pageSize").toString());
        Object exportFlag = params.get("export");
        Map<String, Object> itemPre = new HashMap<String, Object>();
        //查询coupon_rule 表，得到有效期和金额的数据
        CouponRule couponRuleById = ruleMapper.findCouponRuleById(ruleId, siteId);
        String timeRule = couponRuleById.getTimeRule();
        int aimAt = couponRuleById.getAimAt();
        String goodsRule = couponRuleById.getGoodsRule();
        int couponType = couponRuleById.getCouponType();
        String orderRule = couponRuleById.getOrderRule();
        Date create_time = new Date(couponRuleById.getCreateTime().getTime());
        int status = couponRuleById.getStatus();
        CouponView view = parsingCouponRuleService.accountCoupon(aimAt, couponType, orderRule, goodsRule);
        switch (couponType) {
            case 100:
                itemPre.put("coupon_type", "现金券");
                break;
            case 200:
                itemPre.put("coupon_type", "打折券");
                break;
            case 300:
                itemPre.put("coupon_type", "现价券");
                break;
            case 400:
                itemPre.put("coupon_type", "包邮券");
                break;
            default:
                itemPre.put("coupon_type", "");
                break;
        }


        JSONObject rule = new JSONObject(timeRule);
        switch (rule.getInt("validity_type")) {

            case 1:
                itemPre.put("valid_period", "绝对时间:" + rule.getString("startTime"));
                break;
            case 2:
                itemPre.put("valid_period", "相对时间:" + rule.getInt("how_day") + "天内使用");
                break;
            case 3: {
                if (rule.getInt("assign_type") == 1) {
                    itemPre.put("valid_period", "指定时间：按日期设置，每月" + rule.getInt("assign_rule") + "号");
                } else if (rule.getInt("assign_type") == 2) {
                    itemPre.put("valid_period", "指定时间：按日期设置，每周" + rule.getInt("assign_rule"));
                }
            }
            break;
            case 4:
                itemPre.put("valid_period", rule.getString("startTime") + "~" + rule.getString("endTime"));
                break;
            default:
                itemPre.put("valid_period", null);
                break;
        }
        itemPre.put("create_time", formatDate(create_time, "yyyy-MM-dd HH:mm:ss"));

        itemPre.put("rule_name", params.get("type"));
        //如果在页面URL上没有拼接这个字段，则直接在mapper返回数据中获取
        if (itemPre.get("rule_name") == null) {
            itemPre.put("rule_name", couponRuleById.getRuleName());
        }
        itemPre.put("amount_money", view.getRuleDetail());
        PageHelper.startPage(startNum, pageSize);
        if (params.get("no") != null) {
            String prarmNum = params.get("no").toString();
            if (!prarmNum.contains("Q")) {
                params.put("no", couponNoEncodingService.decryptionCouponNo(prarmNum));
            }
        }
        List<Map<String, Object>> list = couponDetailMapper.findCouponDetailList(params);
        PageInfo<Map<String, Object>> info = new PageInfo<Map<String, Object>>(list);
        List<Map<String, Object>> result = info.getList();
        for (Map<String, Object> temp : result) {
            String num = temp.get("coupon_no") == null ? "" : temp.get("coupon_no").toString();
            if (!num.contains("Q")) {
                temp.put("coupon_no", couponNoEncodingService.encryptionCouponNo(num));
            }
            temp.putAll(itemPre);
        }
        if (exportFlag != null) {
            return result;
        }
        Map<String, Object> pages = new HashMap<String, Object>();
        pages.put("rows", result);
        pages.put("pages", info.getPages());
        pages.put("pageNum", info.getPageNum());
        pages.put("total", info.getTotal());
//        int use = couponDetailMapper.findCouponDetailStatusBySiteIdAndRuleId(siteId, ruleId, params, 0);
//        int unused = couponDetailMapper.findCouponDetailStatusBySiteIdAndRuleId(siteId, ruleId, params, 1);
//        pages.put("use",use);
//        pages.put("unused",unused);
        return pages;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ReturnDto updateCouponRuleStatus(CouponRuleUpdateStatus couponRuleUpdateStatus) {

        try {
            switch (couponRuleUpdateStatus.getPreStatus()) {
                case 0:
                    if (!(couponRuleUpdateStatus.getToUpdateStatus() == 1 || couponRuleUpdateStatus.getToUpdateStatus() == 2 ||
                        couponRuleUpdateStatus.getToUpdateStatus() == 3 ||
                        couponRuleUpdateStatus.getToUpdateStatus() == 4))
                        return ReturnDto.buildFailedReturnDto("可发布优惠券只能手动修改为手动停发或手动作废状态");
                    break;
                case 1:
                    return ReturnDto.buildFailedReturnDto("过期优惠券不可手动修改状态");
                case 2:
                    if (!(couponRuleUpdateStatus.getToUpdateStatus() == 3 || couponRuleUpdateStatus.getToUpdateStatus() == 4))
                        return ReturnDto.buildFailedReturnDto("手动停发优惠券只能手动修改为过期或手动作废状态");
                    break;
                case 3:
                    if (!(couponRuleUpdateStatus.getToUpdateStatus() == 1 || couponRuleUpdateStatus.getToUpdateStatus() == 4))
                        return ReturnDto.buildFailedReturnDto("已发完优惠券只能改为过期或手动作废状态");
                    break;
                case 4:
                    return ReturnDto.buildFailedReturnDto("作废优惠券不可手动修改状态");
                case 10:
                    if (!(couponRuleUpdateStatus.getToUpdateStatus() == 0 || couponRuleUpdateStatus.getToUpdateStatus() == 4))
                        return ReturnDto.buildFailedReturnDto("待发布优惠券只能修改为可发布或手动作废状态");
            }

        } catch (Exception e) {
            return ReturnDto.buildFailedReturnDto(e.getMessage());
        }

        int result = couponRuleMapper.revampCouponRuleStatus(couponRuleUpdateStatus.getRuleId(), couponRuleUpdateStatus.getSiteId(),
            couponRuleUpdateStatus.getToUpdateStatus());

        //停发,作废 优惠券修改活动
        if (couponRuleUpdateStatus.getToUpdateStatus() == 2 || couponRuleUpdateStatus.getToUpdateStatus() == 4) {
            //修改活动状态
            checkRuleAndActivityStatusByRuleId(couponRuleUpdateStatus.getSiteId(), couponRuleUpdateStatus.getRuleId());
        }

        if (result == 1)
            return ReturnDto.buildSuccessReturnDto("success");
        else
            return ReturnDto.buildFailedReturnDto("faile");
    }

    public Integer getSendCouponDetailCount(Integer siteId, Integer ruleId) {
        return couponRuleMapper.getSendCouponDetailCount(siteId, ruleId);
    }

    public List<Map<String, Object>> getSendCouponDetailList(Map<String, Object> params) {

        //创建日志装载对象
        CouponLog couponLog = new CouponLog();
        //获取前台传递的各种参数
        int siteId = Integer.parseInt(params.get("siteId").toString());
        int ruleId = Integer.parseInt(params.get("ruleId").toString());

        List<Map<String, Object>> sendCouponDatailList = couponRuleMapper.getSendCouponDetailList(siteId, ruleId);

        sendCouponDatailList.forEach(sendCouponDatail -> {

            int status = Integer.valueOf(sendCouponDatail.get("status").toString());
            int coupon_type = Integer.valueOf(sendCouponDatail.get("coupon_type").toString());
            String timeRule = sendCouponDatail.get("time_rule").toString();
            int aimAt = Integer.valueOf(sendCouponDatail.get("aim_at").toString());
            String goodsRule = sendCouponDatail.get("goods_rule").toString();
            int couponType = Integer.valueOf(sendCouponDatail.get("coupon_type").toString());
            String orderRule = sendCouponDatail.get("order_rule").toString();

            CouponView view = parsingCouponRuleService.accountCoupon(aimAt, couponType, orderRule, goodsRule);

            sendCouponDatail.put("amount_money", view.getRuleDetail());

            switch (coupon_type) {
                case 100:
                    sendCouponDatail.put("coupon_type", "现金券");
                    break;
                case 200:
                    sendCouponDatail.put("coupon_type", "打折券");
                    break;
                case 300:
                    sendCouponDatail.put("coupon_type", "现价券");
                    break;
                case 400:
                    sendCouponDatail.put("coupon_type", "包邮券");
                    break;
                default:
                    sendCouponDatail.put("coupon_type", "");
                    break;
            }

            switch (status) {
                case 0:
                    sendCouponDatail.put("status", "已使用");
                    break;
                case 1:
                    sendCouponDatail.put("status", "待使用");
                    break;
                default:
                    sendCouponDatail.put("status", "");
                    break;
            }

            try {
                JSONObject rule = new JSONObject(timeRule);
                switch (rule.getInt("validity_type")) {

                    case 1:
                        sendCouponDatail.put("valid_period", "绝对时间:" + rule.getString("startTime"));
                        break;
                    case 2:
                        sendCouponDatail.put("valid_period", "相对时间:" + rule.getInt("how_day") + "天内使用");
                        break;
                    case 3: {
                        if (rule.getInt("assign_type") == 1) {
                            sendCouponDatail.put("valid_period", "指定时间：按日期设置，每月" + rule.getInt("assign_rule") + "号");
                        } else if (rule.getInt("assign_type") == 2) {
                            sendCouponDatail.put("valid_period", "指定时间：按日期设置，每周" + rule.getInt("assign_rule"));
                        }
                    }
                    break;
                    case 4:
                        sendCouponDatail.put("valid_period", rule.getString("startTime") + "~" + rule.getString("endTime"));
                        break;
                    default:
                        sendCouponDatail.put("valid_period", null);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return sendCouponDatailList;
    }

    public ReturnDto getReleaseCouponNumAndGoodsNum(List<Map<String, Object>> params, Integer siteId) {
        // 查数据
        String[] fields = new String[]{"rule_id", "coupon_type", "goods_rule"};
        List<Map<String, Object>> couponRules = couponRuleMapper.findReleaseCouponRules(siteId, fields);
        int goodsNumOnSale = goodsMapper.countGoodsOnSale(siteId);

        // 分组
        Map<String, Map<String, List<Map<String, Object>>>> result = couponRules.stream()
            .map(map -> {
                GoodsRule goods_rule = JSON.parseObject(map.get("goods_rule").toString(), GoodsRule.class);

                // 页面满赠券的类型分类是针对的 gift_send_type ，而其他的券针对的是rule_type, 所以这里用 gift_send_type 覆盖 rule_type
                if (map.get("coupon_type").toString().equals("500")) {
                    goods_rule.setRule_type(goods_rule.getGift_send_type());
                }

                map.put("goods_rule", goods_rule);
                return map;
            })
            .collect(groupingBy((Map map) -> map.get("coupon_type").toString(),
                groupingBy((Map map2) -> ((GoodsRule) map2.get("goods_rule")).getRule_type().toString())));

        // 分析数据
        try {
            analyzeGroupedData(params, result, goodsNumOnSale);
        } catch (DBDataErrorException e) {
            log.error("数据库数据有误: {}", e);
            return ReturnDto.buildFailedReturnDto("数据库数据有误");
        } catch (UnsupportedDataTypeException e) {
            log.error("不支持的数据类型: {}", e);
            return ReturnDto.buildFailedReturnDto("不支持的数据类型");
        }

        // 出结果
        return ReturnDto.buildSuccessReturnDto(params);
    }

    @SuppressWarnings("Duplicates")
    private void analyzeGroupedData(List<Map<String, Object>> params, Map<String, Map<String, List<Map<String, Object>>>> result, int goodsNumOnSale) throws DBDataErrorException, UnsupportedDataTypeException {
        for (int i = 0; i < params.size(); i++) {
            Map<String, Object> map = params.get(i);
            Integer couponType = Integer.parseInt(map.get("couponType").toString());
            Integer ruleType = Integer.parseInt(map.get("ruleType").toString());

            List<Map<String, Object>> list;
            Map<String, List<Map<String, Object>>> result_1 = result.get(couponType.toString());
            if (result_1 != null)
                list = Optional.ofNullable(result_1.get(ruleType.toString())).orElseGet(ArrayList::new);
            else
                list = new ArrayList<>();

            Set<String> goodsInRule = new HashSet<>();
            Set<String> goodsNotInRule = new HashSet<>();
            boolean isAll = false;
            map.put("releaseRule", list.size());
            map.put("goodsInRule", 0);

            if (list.size() == 0) {
                continue;
            }

            for (int j = 0; j < list.size(); j++) {
                GoodsRule goodsRule = (GoodsRule) list.get(j).get("goods_rule");

                if (goodsRule.getType() == 0) {
                    isAll = true;
                    break;
                } else if (goodsRule.getType() == 1) {
                    throw new UnsupportedDataTypeException();
                } else if (goodsRule.getType() == 2) {
                    goodsInRule.addAll(Arrays.asList(goodsRule.getPromotion_goods().split(",")));
                } else if (goodsRule.getType() == 3) {
                    goodsNotInRule.addAll(Arrays.asList(goodsRule.getPromotion_goods().split(",")));
                } else {
                    throw new DBDataErrorException();
                }
            }

            if (isAll) {
                map.put("goodsInRule", goodsNumOnSale);
                continue;
            }

            if (goodsNotInRule.size() == 0) {
                map.put("goodsInRule", goodsInRule.size());
            } else {
                goodsNotInRule.removeAll(goodsInRule);
                int goodsInRuleNum = goodsInRule.size();
                int goodsInRuleNumByNot = goodsNumOnSale - goodsNotInRule.size();
                map.put("goodsInRule", goodsInRuleNum >= goodsInRuleNumByNot ? goodsInRuleNum : goodsInRuleNumByNot);
            }
        }
    }


}
