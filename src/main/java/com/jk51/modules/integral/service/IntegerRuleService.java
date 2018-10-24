package com.jk51.modules.integral.service;

import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.BIMService;
import com.jk51.model.integral.IntegralLog;
import com.jk51.model.integral.IntegralRule;
import com.jk51.model.order.Trades;
import com.jk51.model.order.TradesExt;
import com.jk51.modules.checkin.mapper.CheckinMapper;
import com.jk51.modules.im.mapper.BIMServiceMapper;
import com.jk51.modules.integral.domain.IntegralRuleEntity;
import com.jk51.modules.integral.mapper.IntegralMapper;
import com.jk51.modules.integral.mapper.IntegralRuleMapper;
import com.jk51.modules.integral.mapper.IntegrallogMapper;
import com.jk51.modules.offline.service.ErpToolsService;
import com.jk51.modules.merchant.mapper.MerchantExtMapper;
import com.jk51.modules.trades.mapper.TradesExtMapper;
import com.jk51.modules.trades.mapper.TradesMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 积分规则
 *
 * @auhter zy
 * @create 2017-06-01 14:50
 */
@Service
public class IntegerRuleService {

    private static final Logger logger = LoggerFactory.getLogger(IntegerRuleService.class);

    @Autowired
    private IntegralRuleMapper integralRuleMapper;
    @Autowired
    private IntegrallogMapper integrallogMapper;
    @Autowired
    private TradesMapper tradesMapper;
    @Autowired
    MerchantExtMapper merchantExtMapper;
    @Autowired
    private IntegralMapper mapper;
    @Autowired
    CheckinMapper checkinMapper;
    @Autowired
    TradesExtMapper tradesExtMapper;
    @Autowired
    private BIMServiceMapper bimServiceMapper;
    @Autowired
    private ErpToolsService erpToolsService;


    //分页查询积分规则记录
    public List<IntegralRuleEntity> queryIntegralRules(String siteId) {
        return integralRuleMapper.queryIntegralRules(siteId);
    }


    //设置注册积分规则
    public Integer insertRegisterRule(IntegralRuleEntity ruleEntity) {
        return integralRuleMapper.insertRegisterRule(ruleEntity);
    }

    //查询单个注册积分规则对象
    public IntegralRuleEntity queryRegisterRule(Integer integer) {
        return integralRuleMapper.queryRegisterRule(integer);
    }

    public Integer updateRegisterRule(IntegralRuleEntity ruleEntity) {
        return integralRuleMapper.updateRegisterRule(ruleEntity);
    }

    public int insertRule(Map<String, Object> param) {
        return integralRuleMapper.insertRule(param);
    }

    public int updateRule(Map<String, Object> param) {
        Map<String, Object> log = new HashMap<String, Object>();
        Map<String, Object> integral = queryIntegral(param);
        int result = 0;
        log.put("ruleId", param.get("id"));
        log.put("ruleName", param.get("name").toString());
        log.put("beforeStatus", integral.get("status").toString());
        log.put("afterStatus", param.get("status").toString());
        log.put("opaterorId", param.get("vip_id").toString());
        log.put("operatorName", param.get("vip_name").toString());
        int lg = integralRuleMapper.insertRuleLog(log);
        if (lg > 0) {
            result = integralRuleMapper.updateRule(param);
        }
        return result;
        //return integralRuleMapper.updateRule(param);
    }

    public Map<String, Object> queryIntegral(Map<String, Object> param) {
        return integralRuleMapper.queryIntegral(param);
    }

    public List<Map<String, Object>> getIntegralRuleLog(Map<String, Object> param) {
        return integralRuleMapper.getIntegralRuleLog(param);
    }

    public String getIntegralByShopping(Map<String, Object> param) {
        String msg = CommonConstant.MSG_STATUS_BUY;
        Map<String, Object> ruleParam = new HashMap<>();
        param.put("type", CommonConstant.TYPE_BUY);
        param.put("integralDesc", CommonConstant.LOG_DESC_BUY);
        param.put("integralDiff", 0);
        IntegralRule integralRule = integralRuleMapper.getIntegralRule(param);

        if (integralRule == null || integralRule.getStatus() != 1) {
            System.out.println(integralRule);
            return CommonConstant.MSG_STATUS_BUY;
        }

        int siteId = Integer.valueOf(param.get("siteId").toString());
        ruleParam.put("siteId", siteId);
        ruleParam.put("tradesId", param.get("tradesId"));
        ruleParam.put("type", CommonConstant.TYPE_BUY);
        Map<String, Object> isSend = integrallogMapper.getIntegralLogByTradesId(ruleParam);
        if (!StringUtil.isEmpty(isSend)) {
            System.out.println(isSend);
            return CommonConstant.MSG_STATUS_REPEAT;
        }

        Map<String, Object> memberInfo = integralRuleMapper.memberInfo(param);
        if (StringUtil.isEmpty(memberInfo)) {
            System.out.println(memberInfo);
            return CommonConstant.MSG_MEMBER_NOTEXISTS;
        }

        if (!"".equals(integralRule.getRule())) {
            int addIntegral = 0;

            try {
                Map<String, Object> rule = JacksonUtils.json2map(integralRule.getRule());
                int realPay = Integer.valueOf(param.get("realPay").toString()) - Integer.valueOf(param.get("postFee").toString());
                System.out.println(rule);
                if (Integer.valueOf(rule.get("type").toString()) == 1) {  //固定金额
                    Object rulesObject = rule.get("payLevel");

                    List<LinkedHashMap<String, Object>> rules = JacksonUtils.json2listMap(JacksonUtils.obj2json(rulesObject));
                    System.out.println(rules.stream());

                    OptionalInt maxTag = rules.stream().filter(x -> Integer.valueOf(x.get("payMoney").toString()) <= realPay).map(x -> x.get("integral")).mapToInt(x -> Integer.valueOf(x.toString())).max();
                    if (maxTag.isPresent()) {
                        addIntegral = maxTag.getAsInt();
                    }

                } else if (Integer.valueOf(rule.get("type").toString()) == 2) {  //累计
                    Map<String, Object> rules = JacksonUtils.json2map(JacksonUtils.obj2json(rule.get("meetLevel")));

                    addIntegral = (int) Math.floor(realPay / Integer.valueOf(rules.get("eachMoney").toString())) * Integer.valueOf(rules.get("eachIntegral").toString());

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (addIntegral <= 0) {
                return "实付金额不满足积分赠送";
            }


            SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd");
            param.put("startTime", data.format(new Date()) + " 00:00:00");
            param.put("endTime", data.format(new Date()) + " 23:59:59");
            Map<String, Object> getIntegralByShopping = integrallogMapper.getIntegralByShopping(param);//当天交易成功获得的积分数量
            param.put("type", CommonConstant.TYPE_ORDER_BACK);
            Map<String, Object> getIntegralByreFree = integrallogMapper.getIntegralSumRefree(param);//当天交易成功订单退款退还的积分数量

            int getShoppingNum = 0;
            if (getIntegralByShopping != null) {
                int reFreeNum = (getIntegralByreFree != null) ? Integer.valueOf(getIntegralByreFree.get("integralDiff").toString()) : 0;
                getShoppingNum = Integer.valueOf(getIntegralByShopping.get("shoppingIntegralSum").toString()) - reFreeNum;
            }
            int realAddIntegral = 0;
            System.out.println(integralRule.getLimit());

            if (integralRule.getLimit() == null || integralRule.getLimit() == 0 || integralRule.getLimit() - getShoppingNum >= addIntegral) {
                realAddIntegral = addIntegral;
            } else if (integralRule.getLimit() > getShoppingNum) {
                realAddIntegral = integralRule.getLimit() - getShoppingNum;
            }

            if (realAddIntegral > 0) {
                param.put("type", CommonConstant.TYPE_BUY);
                int checkinNum = Integer.valueOf(memberInfo.get("checkin_num").toString());
                int checkinSum = Integer.valueOf(memberInfo.get("checkin_sum").toString());
                int total = realAddIntegral + Integer.valueOf(memberInfo.get("integrate").toString());
                boolean result = checkinAndGiveIntegral(checkinNum, checkinSum, realAddIntegral, memberInfo, param);

                if (result) {
                    msg = "购物送积分成功";
                } else {
                    msg = "购物送积分失败";
                }
            } else {
                msg = "赠送以达到上限";
            }
        }

        return msg;

    }

    public JSONObject getIntegralByRegister(Map<String, Object> param) {
        String msg = CommonConstant.MSG_STATUS_REGISTER;

        JSONObject res = new JSONObject();
        param.put("type", CommonConstant.TYPE_REGIST);
        param.put("integralDesc", CommonConstant.LOG_DESC_REGIST);
        param.put("integralDiff", 0);
        logger.info("IntegerRuleService.getIntegralByRegister.param:" + param);

        IntegralRule integralRule = integralRuleMapper.getIntegralRule(param);

        if (integralRule == null || integralRule.getStatus() != 1) {
            logger.info(param.get("siteId") + "." + param.get("buyerId") + CommonConstant.MSG_STATUS_REGISTER);
            System.out.println(integralRule);
            res.put("status", "error");
            res.put("msg", CommonConstant.MSG_STATUS_REGISTER);
            return res;
        }

        Map<String, Object> memberInfo = integralRuleMapper.memberInfo(param);
        logger.info(JacksonUtils.mapToJson(memberInfo));
        if (StringUtil.isEmpty(memberInfo)) {
            logger.info(param.get("siteId") + "." + param.get("buyerId") + CommonConstant.MSG_MEMBER_NOTEXISTS);
            res.put("status", "error");
            res.put("msg", CommonConstant.MSG_MEMBER_NOTEXISTS);
            return res;
        }

        IntegralLog integralLog = integrallogMapper.getIntegralLog(param);
        if (integralLog != null) {
            logger.info(param.get("siteId") + "." + param.get("buyerId") + CommonConstant.MSG_EXIST);
            res.put("status", "error");
            res.put("msg", CommonConstant.MSG_EXIST);
            return res;
        }
        try {
            Map rule = JacksonUtils.json2map(integralRule.getRule());
            int addIntegral = Integer.valueOf(rule.get("firstRegister").toString());
            boolean result = checkinAndGiveIntegral(Integer.valueOf(memberInfo.get("checkin_num").toString()), Integer.valueOf(memberInfo.get("checkin_sum").toString()), addIntegral, memberInfo, param);

            if (result) {
                logger.info(param.get("siteId") + "." + param.get("buyerId") + CommonConstant.LOG_DESC_REGIST + "成功");
                res.put("status", "success");
                res.put("msg", CommonConstant.LOG_DESC_REGIST + "成功");
                return res;
            } else {
                logger.info(param.get("siteId") + "." + param.get("buyerId") + CommonConstant.LOG_DESC_REGIST + "失败");
                res.put("status", "error");
                res.put("msg", CommonConstant.LOG_DESC_REGIST + "失败");
                return res;
            }
        } catch (Exception e) {
            logger.info(param.get("siteId") + "." + param.get("buyerId") + e.getMessage());
            res.put("status", "error");
            res.put("msg", e.getMessage());
            return res;
        }
    }

    public int insertRuleLog(Map<String, Object> param) {
        return integralRuleMapper.insertRuleLog(param);
    }

    //微信签到查询
    //param ：siteId ，buyerId
    public ReturnDto checkinCheck(Map<String, Object> param) {

        ReturnDto returnDto;
        //返回的结果集
        Map<String, Object> map = new HashMap<String, Object>();

        //签到类型
        param.put("type", CommonConstant.TYPE_CHECKIN);
        //login类型
        param.put("integralDesc", CommonConstant.LOG_DESC_CHECKIN);
        //消费积分
        param.put("integralDiff", 0);
        //判断当天是否已送过积分
        param.put("createTime", "today");


        Map<String, Object> ruleMap = integralRuleMapper.queryIntegralRule(param);//是否开启送积分，查b_integral_rule积分规则

        if (StringUtil.isEmpty(ruleMap) || (ruleMap.get("status") != null && 0 == Integer.parseInt(ruleMap.get("status").toString()))) {
            //如果没有查到签到送积分规则
            returnDto = resultHelper(false, CommonConstant.MSG_STATUS_CHECKIN_T);
            returnDto.setCode("102");
            return returnDto;
        }

        Map<String, Object> today = integralRuleMapper.isGiveIntegral(param); //返回的列表为空就是没送过，查b_integrallog积分使用记录表
        Integer todayCheckin = checkinMapper.checkinTodayIs(Integer.valueOf(param.get("siteId").toString()), Integer.parseInt(param.get("buyerId").toString()));//是否签到过

        if (!StringUtil.isEmpty(today) || todayCheckin > 0) { //如果今天送过积分或签到过（因为签到就送积分）
            returnDto = resultHelper(false, CommonConstant.MSG_CHECKIN_HASACTION);
            //获取今日获取的积分
            map.put("addVal", today.get("integral_add"));
            returnDto.setValue(map);
            return returnDto;
        }

        JSONObject rulesTemp = JSONObject.parseObject(ruleMap.get("rule").toString());

        //签到赠送积分值
        Integer dayIntegral = rulesTemp.getInteger("value");
        //Integer dayIntegral = 10;
        //累计天数
        Integer dayLoop = rulesTemp.getInteger("max_num");
        //Integer dayLoop = 3;
        //额外赠送积分
        Integer addIntegral = rulesTemp.getInteger("add_value");

        Map checkinContinue = integralRuleMapper.checkinNumber(param);
        Integer checkinNum = Integer.parseInt(checkinContinue.get("checkin_num").toString());//连续签到天数
        Integer checkinSum = Integer.parseInt(checkinContinue.get("checkin_sum").toString()); //签到总天数

        //判断昨天是否有签到
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String lasttime = "";
        String yesterday = checkinContinue.get("yesterday").toString();

        if (checkinContinue.get("checkin_lasttime") != null) {
            lasttime = simpleDateFormat.format((Timestamp) checkinContinue.get("checkin_lasttime"));
        }

        //不支持连续签到时
        if (dayLoop == null || dayLoop < 2 || addIntegral == null || addIntegral < 1) {

            map.put("addVal", dayIntegral);

        } else {
            if (checkinNum == 0 || !lasttime.equals(yesterday)) {
                //连续签到断了。从头开始记，member表要的连续签到要变1,操作积分操作积分
                map.put("addVal", dayIntegral);

            } else if ((checkinNum + 1) % dayLoop == 0) {
                //满足多送积分,member签到相关+1，操作积分（还有member_info表）
                map.put("addVal", dayIntegral + addIntegral);
            } else {
                //赠送普通积分，其他同上
                map.put("addVal", dayIntegral);
            }
        }

        returnDto = resultHelper(true, "查询成功");

        returnDto.setValue(map);

        return returnDto;

    }

    //微信签到送积分
    //param ：siteId ，buyerId
    public ReturnDto integralAddForChicken(Map<String, Object> param) {

        ReturnDto returnDto;

        //签到类型
        param.put("type", CommonConstant.TYPE_CHECKIN);
        //login类型
        param.put("integralDesc", CommonConstant.LOG_DESC_CHECKIN);
        //消费积分
        param.put("integralDiff", 0);
        logger.info("签到送积分接口，参数为：{}", JacksonUtils.mapToJson(param));

        //Map today = checkinToday(param);
        param.put("createTime", "today");//判断当天是否已送过积分

        Map<String, Object> ruleMap = integralRuleMapper.queryIntegralRule(param);//是否开启送积分，查b_integral_rule积分规则

        if (StringUtil.isEmpty(ruleMap) || (ruleMap.get("status") != null && 0 == Integer.parseInt(ruleMap.get("status").toString()))) {

            //如果没有查到签到送积分规则
            returnDto = resultHelper(false, CommonConstant.MSG_STATUS_CHECKIN_T);
            returnDto.setCode("102");
            return returnDto;
        }

        Map<String, Object> memberIntegral = integralRuleMapper.memberIntegral(param);//查b_member中用户的积分、获得积分、使用积分、mobile
        if (StringUtil.isEmpty(memberIntegral)) {
            //如果没有查到这个会员
            returnDto = resultHelper(false, CommonConstant.MSG_MEMBER_NOTEXISTS);
            returnDto.setCode("103");
            return returnDto;
        }

        Map<String, Object> today = integralRuleMapper.isGiveIntegral(param); //返回的列表为空就是没送过，查b_integrallog积分使用记录表
        Integer todayCheckin = checkinMapper.checkinTodayIs(Integer.valueOf(param.get("siteId").toString()), Integer.parseInt(param.get("buyerId").toString()));//是否签到过
        if (!StringUtil.isEmpty(today) || todayCheckin > 0) {
            //如果今天送过积分
            return resultHelper(false, CommonConstant.MSG_CHECKIN_HASACTION);
        }

        JSONObject rulesTemp = JSONObject.parseObject(ruleMap.get("rule").toString());

        //签到赠送积分值
        Integer dayIntegral = rulesTemp.getInteger("value");
        //Integer dayIntegral = 10;
        //累计天数
        Integer dayLoop = rulesTemp.getInteger("max_num");
        //Integer dayLoop = 3;
        //额外赠送积分
        Integer addIntegral = rulesTemp.getInteger("add_value");
        //Integer addIntegral = 10;

        //获取签到天数
        Map checkinContinue = integralRuleMapper.checkinNumber(param);
        Integer checkinNum = Integer.parseInt(checkinContinue.get("checkin_num").toString());//连续签到天数
        Integer checkinSum = Integer.parseInt(checkinContinue.get("checkin_sum").toString()); //签到总天数

        //判断昨天是否有签到
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String lasttime = "";
        String yesterday = checkinContinue.get("yesterday").toString();

        if (checkinContinue.get("checkin_lasttime") != null) {
            lasttime = simpleDateFormat.format((Timestamp) checkinContinue.get("checkin_lasttime"));
        }

        boolean res = true;

        //不支持连续签到时
        if (dayLoop == null || dayLoop < 2 || addIntegral == null || addIntegral < 1) {

            if (checkinNum == 0 || !lasttime.equals(yesterday)) {
                //连续签到断了。从头开始记，member表要的连续签到要变1,操作积分操作积分（还有member_info表），记录进b_integrallog
                res = checkinAndGiveIntegral(1, checkinSum + 1, dayIntegral, memberIntegral, param);
            } else {
                //赠送普通积分，其他同上
                res = checkinAndGiveIntegral(checkinNum + 1, checkinSum + 1, dayIntegral, memberIntegral, param);
            }

        } else {

            if (checkinNum == 0 || !lasttime.equals(yesterday)) {
                //连续签到断了。从头开始记，member表要的连续签到要变1,操作积分操作积分（还有member_info表），记录进b_integrallog
                res = checkinAndGiveIntegral(1, checkinSum + 1, dayIntegral, memberIntegral, param);
            } else if ((checkinNum + 1) % dayLoop == 0) {
                //满足多送积分,member签到相关+1，操作积分（还有member_info表），记录进b_integrallog
                res = checkinAndGiveIntegral(checkinNum + 1, checkinSum + 1, dayIntegral + addIntegral, memberIntegral, param);
            } else {
                //赠送普通积分，其他同上
                res = checkinAndGiveIntegral(checkinNum + 1, checkinSum + 1, dayIntegral, memberIntegral, param);
            }

        }

        if (!res) return resultHelper(false, "今日签到送积分失败");

        return resultHelper(true, "今日签到送积分成功");
    }

    @Transactional
    public boolean checkinAndGiveIntegral(Integer checkinNum, Integer checkinSum, Integer addIntegral,
                                          Map<String, Object> memberIntegral, Map<String, Object> param) {

        //修改b_member中的积分
        int total = Integer.parseInt(memberIntegral.get("integrate").toString()) + addIntegral;
        Map<String, Object> updateMember = new HashMap();
        updateMember.put("siteId", param.get("siteId"));
        updateMember.put("buyerId", param.get("buyerId"));
        updateMember.put("integrate", total);
        updateMember.put("totalGetIntegrate", Long.parseLong(memberIntegral.get("total_get_integrate").toString()) + addIntegral);
        updateMember.put("checkinNum", checkinNum);
        updateMember.put("checkinSum", checkinSum);
        logger.info("修改商户:{}下{}会员积分",param.get("siteId"),param.get("buyerId"));

        if (integralRuleMapper.updateMemberInfo(updateMember) != 1){
            logger.info("integralRuleMapper.updateMemberInfo:操作失败");
            return false;
        }
        if (integralRuleMapper.updateMemberIntegral(updateMember) != 1){
            logger.info("integralRuleMapper.updateMemberIntegral:操作失败");
            return false;
        }

        Map<String, Object> map = new HashMap<>();
        if (param.containsKey("tradesId")) {
            map.put("tradesId", param.get("tradesId"));
            long tradesId = Long.valueOf(param.get("tradesId").toString());
            map.put("goodsNo", erpToolsService.getGoodsCodebyTradesId(Integer.parseInt(param.get("siteId").toString()), tradesId));
            TradesExt tradesExt = tradesExtMapper.getByTradesId(tradesId);
            if (tradesExt != null) {
                tradesExt.setIntegralFinalAward(addIntegral);
                int tradesExtUpdate = tradesExtMapper.updateTradesExt(tradesExt);
                if (tradesExtUpdate <= 0) {
                    return false;
                }
            } else {
                logger.info("checkinAndGiveIntegral:tradesExt null");
            }

        }
        String mark = param.containsKey("mark") ? param.get("mark").toString() : "";
        //插入b_integrallog表记录
        Map<String, Object> insertIntegralLog = new HashMap();
        insertIntegralLog.put("siteId", param.get("siteId"));
        insertIntegralLog.put("buyerId", param.get("buyerId"));
        insertIntegralLog.put("buyerNick", memberIntegral.get("mobile"));
        insertIntegralLog.put("integralDesc", param.get("integralDesc"));
        insertIntegralLog.put("type", param.get("type"));
        insertIntegralLog.put("integralAdd", addIntegral);
        insertIntegralLog.put("integralDiff", param.get("integralDiff"));
        insertIntegralLog.put("integralOverplus", updateMember.get("integrate"));
        insertIntegralLog.put("mark", mark);
        insertIntegralLog.put("type", param.get("type"));
        if (integralRuleMapper.insertIntegralLog(insertIntegralLog) != 1) {
            logger.info("integralRuleMapper.insertIntegralLog:操作失败");
            return false;
        }

        if (Integer.valueOf(param.get("type").toString()) == CommonConstant.TYPE_CHECKIN) {
            if (checkinMapper.checkinAction(Integer.valueOf(param.get("siteId").toString()), Integer.parseInt(param.get("buyerId").toString())) != 1)
                return false;
        }

        //推送积分变动情况，修改线上积分
        map.put("mobile", memberIntegral.get("mobile"));
        map.put("type", 1);
        map.put("sumScore", addIntegral);
        map.put("on_costScore", +addIntegral);
        map.put("off_costScore", 0);
        map.put("on_integral", total);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        map.put("create_time", sd.format(new Date()));
        map.put("desc", param.get("integralDesc"));
        erpToolsService.integralChange(Integer.valueOf(param.get("siteId").toString()), map);

        return true;
    }

    public ReturnDto resultHelper(boolean flag, String str) {
        if (flag) {
            return ReturnDto.buildSuccessReturnDto(str);
        } else {
            return ReturnDto.buildFailedReturnDto(str);
        }
    }

    /**
     * 确认收货赠送积分
     * com.jk51.modules.trades.service.TradesService
     * updateConfirmStatus
     * updateYiZiTi
     * systemDelivery
     *
     * @param trades
     * @param tradesStatus
     */
    public void integralByShopping(Trades trades, int tradesStatus) {
        logger.info("IntegerRuleService.integralByShopping.trades：" + trades);
        logger.info("IntegerRuleService.integralByShopping.tradesStatus：" + tradesStatus);
        List<Integer> tadesStatusList = Arrays.asList(210, 220, 230, 800);

        if (tadesStatusList.stream().anyMatch(s -> s.equals(tradesStatus))) {
            Map<String, Object> param = new HashMap<>();

            param.put("siteId", trades.getSiteId());
            param.put("buyerId", trades.getBuyerId());
            param.put("tradesId", trades.getTradesId());
            param.put("realPay", trades.getRealPay());
            Object postFee = trades.getPostFee() == null ? 0 : trades.getPostFee();
            param.put("postFee", postFee);
            logger.info("IntegerRuleService.integralByShopping参数：" + JacksonUtils.mapToJson(param));

            String msg = getIntegralByShopping(param);
            logger.info("购物送积分:[" + trades.getTradesId() + ']' + msg);
        }
    }

    /**
     * 付款成功/订单评价成功送积分
     *
     * @param trades
     */
    public void integralByOrderMulti(Trades trades) {
        logger.info("IntegerRuleService.integralByOrderMulti.trades：" + trades);
        List<Integer> tadesStatusList = Arrays.asList(210, 220, 230, 800);

            Map<String, Object> param = new HashMap<>();
            param.put("siteId", trades.getSiteId());
            param.put("buyerId", trades.getBuyerId());
            param.put("tradesId", trades.getTradesId());
            param.put("realPay", trades.getRealPay());
            Object postFee = trades.getPostFee() == null ? 0 : trades.getPostFee();
            logger.info("IntegerRuleService.integralByOrderMulti参数：" + JacksonUtils.mapToJson(param));

            param.put("postFee", postFee);
            //门店直购的状态为210
            if (trades.getTradesStatus() == 120 || trades.getTradesStatus() == 210) {  //订单付款成功
                param.put("mark", "购物送积分，订单号：" + trades.getTradesId());
                String msg = getIntegralByShopping(param);
                logger.info("购物送积分:[" + trades.getTradesId() + ']' + msg);
            } else if (tadesStatusList.stream().anyMatch(s -> s.equals(trades.getTradesStatus()))) { //订单评价成功
                param.put("mark", "评价送积分，订单号：" + trades.getTradesId());
                String msg = getIntegralByComment(param);
                logger.info("订单评价送积分:[" + trades.getTradesId() + ']' + msg);
            }

    }

    public String getIntegralByComment(Map<String, Object> param) {
        String msg = "";
        Map<String, Object> ruleParam = new HashMap<>();
        param.put("type", CommonConstant.TYPE_ORDER_ASSESS);
        param.put("integralDesc", CommonConstant.LOG_DESC_ORDER_ASSESS);
        param.put("integralDiff", 0);
        IntegralRule integralRule = integralRuleMapper.getIntegralRule(param);

        if (integralRule == null || integralRule.getStatus() != 1) {
            System.out.println(integralRule);
            return CommonConstant.MSG_STATUS_ORDER_ASSESS;
        }

        Map<String, Object> isSend = integrallogMapper.getIntegralLogByTradesId(param);
        if (!StringUtil.isEmpty(isSend)) {
            System.out.println(isSend);
            return CommonConstant.MSG_STATUS_REPEAT;
        }

        Map<String, Object> memberInfo = integralRuleMapper.memberInfo(param);
        if (StringUtil.isEmpty(memberInfo)) {
            System.out.println(memberInfo);
            return CommonConstant.MSG_MEMBER_NOTEXISTS;
        }

        if (!"".equals(integralRule.getRule())) {
            try {
                Map<String, Object> rule = JacksonUtils.json2map(integralRule.getRule());
                int addIntegral = Integer.valueOf(rule.get("orderEvaluate").toString());
                if (addIntegral <= 0) {
                    return "订单评价积分赠送规则设置有误:" + integralRule.getRule();
                }


                SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd");
                param.put("startTime", data.format(new Date()) + " 00:00:00");
                param.put("endTime", data.format(new Date()) + " 23:59:59");
                Map<String, Object> getIntegralByShopping = integrallogMapper.getIntegralByShopping(param);//当天订单评价获得的积分数量


                int getShoppingNum = 0;
                if (getIntegralByShopping != null) {
                    getShoppingNum = Integer.valueOf(getIntegralByShopping.get("shoppingIntegralSum").toString());
                }
                int realAddIntegral = 0;
                System.out.println(integralRule.getLimit());

                if (integralRule.getLimit() == null || integralRule.getLimit() == 0 || integralRule.getLimit() - getShoppingNum >= addIntegral) {
                    realAddIntegral = addIntegral;
                } else if (integralRule.getLimit() > getShoppingNum) {
                    realAddIntegral = integralRule.getLimit() - getShoppingNum;
                }

                if (realAddIntegral > 0) {
                    boolean result = checkinAndGiveIntegral(Integer.valueOf(memberInfo.get("checkin_num").toString()), Integer.valueOf(memberInfo.get("checkin_sum").toString()), realAddIntegral, memberInfo, param);

                    if (result) {
                        msg = "订单评价送积分成功";
                    } else {
                        msg = "订单评价送积分失败";
                    }
                } else {
                    msg = "赠送以达到上限";
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return msg;
    }

    /**
     * 咨询送积分
     *
     * @param imServiceId
     */
    public String getIntegralByConsult(int siteId, int imServiceId) {
        String msg = "";

        logger.info("IntegerRuleService.getIntegralByConsult.imServiceId：" + imServiceId);

            Map<String, Object> param = new HashMap<>();

            //是否开启评价送积分
            Map<String, Object> ruleParam = new HashMap<>();
            param.put("type", CommonConstant.TYPE_CONSULT_ASSESS);
            param.put("integralDesc", CommonConstant.LOG_DESC_CONSULT_ASSESS);
            param.put("integralDiff", 0);
            param.put("siteId", siteId);
            IntegralRule integralRule = integralRuleMapper.getIntegralRule(param);

            if (integralRule == null || integralRule.getStatus() != 1) {
                System.out.println(integralRule);
                return CommonConstant.MSG_STATUS_CONSULT_ASSESS;
            }

            //根据imServiceId获取评价的用户与店员
            BIMService bimService = bimServiceMapper.selectByPrimaryKey(imServiceId);
            String[] senderArray = bimService.getSender().split("_");
            String sender = senderArray[2];
            String[] receiveArray = bimService.getReceiver().split("_");
            String receive = receiveArray[2];

            SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd");
            param.put("mark", "咨询评价送积分，用户：" + sender + "店员：" + receive + "serviceId" + imServiceId);
            param.put("startTime", data.format(new Date()) + " 00:00:00");
            param.put("endTime", data.format(new Date()) + " 23:59:59");
            Map<String, Object> getIntegralLogByMark = integrallogMapper.getIntegralLogByMark(param);  //sender 24小时 内对receive的评鉴记录
            if (getIntegralLogByMark != null) {
                logger.info("{}对{}已评价", sender, receive);
                return sender + "对" + receive + "已评价，此次不送积分";
            }

            param.put("buyerId", sender);
            Map<String, Object> memberInfo = integralRuleMapper.memberInfo(param);
            if (StringUtil.isEmpty(memberInfo)) {
                System.out.println(memberInfo);
                return CommonConstant.MSG_MEMBER_NOTEXISTS;
            }

            if (!"".equals(integralRule.getRule())) {
                try {
                    Map<String, Object> rule = JacksonUtils.json2map(integralRule.getRule());
                    int addIntegral = Integer.valueOf(rule.get("evaluate").toString());
                    if (addIntegral <= 0) {
                        return "咨询评价积分赠送规则设置有误:" + integralRule.getRule();
                    }

                    Map<String, Object> getIntegralByConsult = integrallogMapper.getIntegralByShopping(param);//当天咨询评价获得的积分数量
                    int getConsultNum = 0;
                    if (getIntegralByConsult != null) {
                        getConsultNum = Integer.valueOf(getIntegralByConsult.get("shoppingIntegralSum").toString());
                    }
                    int realAddIntegral = 0;
                    System.out.println(integralRule.getLimit());

                    if (integralRule.getLimit() == null || integralRule.getLimit() == 0 || integralRule.getLimit() - getConsultNum >= addIntegral) {
                        realAddIntegral = addIntegral;
                    } else if (integralRule.getLimit() > getConsultNum) {
                        realAddIntegral = integralRule.getLimit() - getConsultNum;
                    }

                    if (realAddIntegral > 0) {
                        boolean result = checkinAndGiveIntegral(Integer.valueOf(memberInfo.get("checkin_num").toString()), Integer.valueOf(memberInfo.get("checkin_sum").toString()), realAddIntegral, memberInfo, param);

                        if (result) {
                            msg = "咨询评价送积分成功";
                        } else {
                            msg = "咨询评价送积分失败";
                        }
                    } else {
                        msg = "赠送以达到上限";
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }

        return msg;
    }

    public String getIntegralByGame(Map<String,Object> param){
        String msg = "";
        Integer siteId= Integer.valueOf(param.get("siteId").toString());
        Integer addIntegral= Integer.valueOf(param.get("addIntegral").toString());
        param.put("type", CommonConstant.TYPE_GAME_ASSESS);
        param.put("integralDesc", CommonConstant.LOG_DESC_OTHER_ASSESS);
        param.put("integralDiff", 0);
        param.put("siteId", siteId);
        Map<String, Object> memberInfo = integralRuleMapper.memberInfo(param);
        boolean result = checkinAndGiveIntegral(Integer.valueOf(memberInfo.get("checkin_num").toString()), Integer.valueOf(memberInfo.get("checkin_sum").toString()), addIntegral, memberInfo, param);
        if(result){
            msg="送积分成功";
        }else{
            msg="送积分失败";
            }
        return msg;
    }
}
