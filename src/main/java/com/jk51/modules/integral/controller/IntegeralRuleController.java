package com.jk51.modules.integral.controller;

/**
 * 查询积分规则列表
 *
 * @auhter zy
 * @create 2017-06-01 14:47
 */

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.CommonConstant;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.HomeDeliveryAndStoresInvite;
import com.jk51.model.order.Member;
import com.jk51.model.order.OrderGoods;
import com.jk51.model.order.response.OrderResponse;
import com.jk51.modules.integral.domain.*;
import com.jk51.modules.integral.mapper.IntegralRuleMapper;
import com.jk51.modules.integral.service.IntegerRuleService;
import com.jk51.modules.integral.service.IntegralLogService;
import com.jk51.modules.integral.service.IntegralService;
import com.jk51.modules.member.service.MemberService;
import com.jk51.modules.order.service.OrderService;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.apache.commons.collections.map.HashedMap;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(value="/Integeral")
public class IntegeralRuleController {

    private static final Logger LOOGER = LoggerFactory.getLogger(IntegeralRuleController.class);
    //注入service
    @Autowired
    private IntegerRuleService integerRuleService;
    @Autowired
    private IntegralService integralService;

    @Autowired
    private IntegralLogService integralLogService;

    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private OrderService orderService;
    /**
     *  积分规则列表查询
     * @return
     */
    @RequestMapping(value="/ruleList",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> queryIntegralRules(HttpServletRequest request) {

        //获取商家ID
//        String siteId = request.getParameter("siteId");
//        String siteId = request.getSession().getAttribute("siteId").toString();
        //获取请求中的map集合
        Map param = ParameterUtil.getParameterMap(request);
        String siteId = (String) param.get("siteId");
        Map<String, Object> resultMap = new HashedMap();
        //判断是否为空
        if(siteId == null || "".equals(siteId)) {
            LOOGER.warn("积分规则列表查询的siteId为空");
            return null;
        }
        //定义一ap集合用于封装数据
//        Map<String, Object> resultMap = new HashMap<>();
        List<IntegralRuleEntity> list = integerRuleService.queryIntegralRules(siteId);
        List<IntegralRuleList> list2 = new ArrayList<>();
        IntegralRuleList irl1 = new IntegralRuleList("注册送积分","完成注册任务获得积分","100","/","关闭","无");
        IntegralRuleList irl2 = new IntegralRuleList("签到送积分","完成每日签到和连续签到任务获得积分","按规则赠送","/","关闭","无");
        IntegralRuleList irl3 = new IntegralRuleList("购物送积分","订单金额满足条件交易成功后赠送积分","按规则赠送","/","关闭","无");
        IntegralRuleList irl4 = new IntegralRuleList("订单评价送积分","成功交易后完成订单评价即赠送积分","按规则赠送","/","关闭","无");
        IntegralRuleList irl5 = new IntegralRuleList("咨询评价送积分","在微信端完成咨询后评价即赠送积分","按规则赠送","/","关闭","无");
        if(list == null || list.size() == 0) {
            //如果查出的数据是空的手动设置默认的三条
            List<IntegralRuleList> list3 = new ArrayList<>();
            list3.add(irl1);
            list3.add(irl2);
            list3.add(irl3);
            list3.add(irl4);
            list3.add(irl5);
            resultMap.put("items",list3);
        }else {
//            if(list.size() < 3) {
//                String s = "";
                StringBuffer sb = new StringBuffer();
                for(IntegralRuleEntity ire : list) {
                    Integer type = ire.getType();
//                    s = s + type + " ";
                    sb.append(String.valueOf(type)+" ");
                }
//                String s ;
//                for(int i= 0; i < list.size(); i++) {
                    int i = 0;
                    while(list2.size() < 5) {
//                        s = sb.toString();
//                        if(!s.contains("10")) {
                        if(sb.indexOf("10") == -1) {
                            list2.add(irl1);
                            sb.append(" 10");
//                            s = sb.toString();
                        }
//                        if(!s.contains("20") && s.contains("10") && list2.size() == 1) {
                        if(sb.indexOf("20") == -1 && sb.indexOf("10") != -1 && list2.size() == 1) {
                            list2.add(irl2);
                            sb.append(" 20");
//                            s = sb.toString();
                        }
//                        if(!s.contains("40") && s.contains("20") && list2.size() == 2) {
                        if(sb.indexOf("40") == -1 && sb.indexOf("20") != -1 && list2.size() == 2) {
                            list2.add(irl3);
                            sb.append(" 40");
                        }
                        if(sb.indexOf("60") == -1 && sb.indexOf("40") != -1 && list2.size() == 3) { //订单评
                            list2.add(irl4);
                            sb.append(" 60");
                        }
                        if(sb.indexOf("50") == -1 && sb.indexOf("60") != -1 && list2.size() == 4) {
                            list2.add(irl5);
                        }
                        if(i < list.size()) {
                            list2.add(forHelper(sb, list.get(i)));
                        }
                        i++;

                    }
//                }

//            }
            /*for(IntegralRuleEntity ire : list) {
                IntegralRuleList irl = new IntegralRuleList();
                irl.setName(ire.getName());
                StringBuffer sb = new StringBuffer(ire.getDesc());
                if(sb.length() > 20) {
                    sb.replace(20,sb.length(),"......");
                }
                irl.setDesc(sb.toString());
                String rule = ire.getRule();
                Integer type = ire.getType();
                if(type != null && type == 10) {    //注册送
                    IntegralRule.RegisterRule registerRule1 = JSON.parseObject(rule, IntegralRule.RegisterRule.class);
                    int firstRegister = registerRule1.getFirstRegister();
                    irl.setIntegral(String.valueOf(firstRegister));
                }else if(type != null && type == 20) {
                    //
                    irl.setIntegral("按规则赠送");
                }else if(type != null && type == 40) {
                    //
                    irl.setIntegral("按规则赠送");
                } else {
                    irl.setIntegral("按规则赠送");
                }
                String limit = ire.getLimit() == 0 ? "/" : String.valueOf(ire.getLimit());
                irl.setLimit(limit);
                String status = ire.getStatus() == 0 ? "关闭" : "开启";
                irl.setStatus(status);
                System.out.print(ire.getUpdateTime());
                irl.setUpdateTime(ire.getUpdateTime() == null ? "未更新" : new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(ire.getUpdateTime()));
                list2.add(irl);
            }*/
            resultMap.put("items",list2);
            LOOGER.info("积分规则列表查询结果 : "+ list.toString());
        }
        return resultMap;
    }

    /**
     * 积分规则填充
     * @param stype
     * @param ire
     * @return
     */
    public IntegralRuleList forHelper(StringBuffer stype ,IntegralRuleEntity ire) {
        IntegralRuleList irl = new IntegralRuleList();
        irl.setName(ire.getName());
        /*if(!StringUtil.isEmpty(ire.getDesc())) {
            StringBuffer sb = new StringBuffer(ire.getDesc());
            if(sb.length() > 20) {
                sb.replace(20,sb.length(),"......");
                irl.setDesc(sb.toString());
            }
            irl.setDesc(sb.toString());
        }else  {
            irl.setDesc("");
        }*/
        String rule = ire.getRule();
        Integer type = ire.getType();
        if(type != null && type == 10) {    //注册送
            IntegralRule.RegisterRule registerRule1 = JSON.parseObject(rule, IntegralRule.RegisterRule.class);
            int firstRegister = registerRule1.getFirstRegister();
//            irl.setIntegral(String.valueOf(firstRegister));
            irl.setIntegral("游客完成首次注册成为会员后赠送"+String.valueOf(firstRegister)+"积分");
            irl.setDesc("完成注册任务获得积分");
        }else if(type != null && type == 20) {
            irl.setIntegral("按规则赠送");
            //获取规则说明的json
            String rule1 = ire.getRule();
            if(!StringUtil.isEmpty(rule1)) {
                StringBuffer sb = new StringBuffer();
                sb.append("单次签到赠送");
                //单次送分数
                Map map = JSON.parseObject(rule1, Map.class);
                sb.append(String.valueOf(map.get("value")==null? "" : map.get("value"))+"积分,");
                if(!StringUtil.isEmpty(map.get("max_num"))) {
                    sb.append("累计连续签到");
                    sb.append(String.valueOf(map.get("max_num"))+"天数，");
                    sb.append("额外赠送");
                    sb.append(String.valueOf(map.get("add_value")==null? "" : map.get("add_value"))+"积分");
                }
                if(sb.length() > 20) {
                    LOOGER.info("签到送积分规则说明: "+ sb.toString());
                    sb.replace(20,sb.length(),"......");
                    irl.setIntegral(sb.toString());
                } else {
                    irl.setIntegral(sb.toString());
                    LOOGER.info("签到送积分规则说明: "+ sb.toString());
                }
            }
            irl.setDesc("完成每日签到和连续签到任务获得积分");
        }else if(type != null && type == 40) {
            irl.setIntegral("按规则赠送");
//            {"type":"1","payLevel":[{"payMoney":100,"integral":"1"},{"payMoney":200,"integral":"2"},{"payMoney":300,"integral":"3"}]}
            String rule40 = ire.getRule();
            if(StringUtil.isNotEmpty(rule40)) {
                StringBuffer sb = new StringBuffer();
                //先转map
                Map map = JSON.parseObject(rule40, Map.class);
                //判断是1:满额送固定积分   2:送累计
//                sb.append(String.valueOf(map.get("type")).equals("1")? "满额送固定积分 :" : "满额送累计积分 :");

                if(String.valueOf(map.get("type")).equals("1")) {
                    String payLevel = String.valueOf(map.get("payLevel"));
                    if(StringUtil.isNotEmpty(payLevel)) {   //是满额且不空
                        sb.append("满额送固定积分: ");
                        List list = JSON.parseObject(payLevel, List.class);
                        for(int i =0; i < list.size(); i++) {
                            Map map1 = JSON.parseObject(String.valueOf(list.get(i)), Map.class);
                            if(StringUtil.isNotEmpty(String.valueOf(map1.get("payMoney")))) {
                                sb.append("订单现金支付金额大于等于");
                                sb.append(String.valueOf(map1.get("payMoney"))+"元 ,");
                                sb.append("送"+ String.valueOf(map1.get("integral")==null? "" : map1.get("integral"))+"积分");
                            }
                        }
                    } else {
                        sb.append("按规则赠送");
                    }
                } else if(String.valueOf(map.get("type")).equals("3")){ //等额送
                    //{"type":3,"equalAmount":{"consumeMoney":100}}
                    String equalAmount = String.valueOf(map.get("equalAmount"));
                    if(StringUtil.isNotEmpty(equalAmount)) {
                        sb.append("等额送积分: ");
                        Map map1 = JSON.parseObject(equalAmount, Map.class);
                        String consumeMoney = String.valueOf(map1.get("consumeMoney"));
                        if(StringUtil.isNotEmpty(consumeMoney)) {
                            DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
                            String filesize = df.format(Integer.valueOf(consumeMoney)/100);//返回的是String类型的
                            sb.append("会员每消费"+filesize+"元");
                            sb.append(",送 1 积分");
                        }
                    }
                }else {
                    sb.append("满额送累计积分 ");
                }
                if(sb.length() > 20) {
                    LOOGER.info("购物送积分规则说明: "+ sb.toString());
                    sb.replace(20,sb.length(),"......");
                    irl.setIntegral(sb.toString());
                } else {
                    irl.setIntegral(sb.toString());
                    LOOGER.info("购物送积分规则说明: "+ sb.toString());
                }
            }
            irl.setDesc("订单金额满足条件交易成功后赠送积分");
        }else if(type != null && type == 60){   //订单评价
            irl.setIntegral("按规则赠送");
            String orderRule = ire.getRule();
            if(StringUtil.isNotEmpty(orderRule)) {
                StringBuffer sb = new StringBuffer();
                Map map = JSON.parseObject(orderRule, Map.class);
                //{"orderEvaluate":"10"}
                String orderEvaluate = String.valueOf(map.get("orderEvaluate"));
                if(StringUtil.isNotEmpty(orderEvaluate)) {
                    sb.append("每评价1笔成功交易订单送"+orderEvaluate+"积分");
                }
                if(sb.length() > 20) {
                    LOOGER.info("订单评价送积分规则说明: "+ sb.toString());
                    sb.replace(20,sb.length(),"......");
                    irl.setIntegral(sb.toString());
                } else {
                    irl.setIntegral(sb.toString());
                    LOOGER.info("订单评价送积分规则说明: "+ sb.toString());
                }
            }
            irl.setDesc("成功交易后完成订单评价即赠送积分");
        }else if(type != null && type == 50){   //咨询评价
            irl.setIntegral("按规则赠送");
            String rule1 = ire.getRule();
            if(StringUtil.isNotEmpty(rule1)) {
                StringBuffer sb = new StringBuffer();
                Map map = JSON.parseObject(rule1, Map.class);
                //{"evaluate":"10"}
                String evaluate = String.valueOf(map.get("evaluate"));
                if(StringUtil.isNotEmpty(evaluate)) {
                    sb.append("咨询后评价医师送"+evaluate+"分");
                }
                if(sb.length() > 20) {
                    LOOGER.info("咨询评价送积分规则说明: "+ sb.toString());
                    sb.replace(20,sb.length(),"......");
                    irl.setIntegral(sb.toString());
                } else {
                    irl.setIntegral(sb.toString());
                    LOOGER.info("咨询评价送积分规则说明: "+ sb.toString());
                }
            }
            irl.setDesc("在微信端完成咨询后评价即赠送积分");
        } else {
            irl.setIntegral("按规则赠送");
        }
        String limit = ire.getLimit() == 0 ? "/" : String.valueOf(ire.getLimit());
        irl.setLimit(limit);
        String status = ire.getStatus() == 0 ? "关闭" : "开启";
        irl.setStatus(status);
        System.out.print(ire.getUpdateTime());
        irl.setUpdateTime(ire.getUpdateTime() == null ? "未更新" : new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(ire.getUpdateTime()));
        return irl;
    }

    /**
     * 送积分规则设置
     * @param request
     * @return
     */
    @RequestMapping("insertRule")
    @ResponseBody
    public Map<String, Object> insertRegisterRule(HttpServletRequest request) {
        Map<String,Object> resultMap=new HashMap<String,Object>();
        Map<String,Object> param=ParameterUtil.getParameterMap(request);
        //获取类型
        String type = (String) param.get("type");
        //获取rule
//        Map<String,Object> rule = (Map<String, Object>) param.get("rule");
        Map rule = JSON.parseObject((String) param.get("rule"), Map.class);
        if(StringUtil.isEmpty(param.get("id"))){    //新增
            //类型判断
            if(StringUtil.isNotEmpty(type) && type.equals("10")) {  //注册
                param.put("name", CommonConstant.LOG_DESC_REGIST);
                param.put("type",CommonConstant.TYPE_REGIST);
                //创建注册的规则实体
//                IntegralRule.RegisterRule registerRule = new IntegralRule().new RegisterRule();
                IntegralRule.RegisterRule registerRule = new IntegralRule.RegisterRule();
                // 获取注册送的积分
                String s = String.valueOf(rule.get("firstRegister"));
                registerRule.setFirstRegister(Integer.valueOf(s));
                //转换成json
                String registerRuleStr = JSON.toJSONString(registerRule);
                param.put("rule", registerRuleStr);
            }else if(StringUtil.isNotEmpty(type) && type.equals("20")) {    //签到
                param.put("name", CommonConstant.LOG_DESC_CHECKIN);
                param.put("type",CommonConstant.TYPE_CHECKIN);
            }else if(StringUtil.isNotEmpty(type) && type.equals("40")) {    //购物
                param.put("name", CommonConstant.LOG_DESC_BUY);
                param.put("type",CommonConstant.TYPE_BUY);
            }else if(StringUtil.isNotEmpty(type) && type.equals("50")) {
                param.put("name", CommonConstant.LOG_DESC_CONSULT_ASSESS);
                param.put("type",CommonConstant.TYPE_CONSULT_ASSESS);
            }else if(StringUtil.isNotEmpty(type) && type.equals("60")) {
                param.put("name", CommonConstant.LOG_DESC_ORDER_ASSESS);
                param.put("type",CommonConstant.TYPE_ORDER_ASSESS);
            }
            int result=integerRuleService.insertRule(param);
            if(result >0){
                resultMap.put("msg","success");
            }else{
                resultMap.put("msg","error");
            }
        }else{  //更新
            if(StringUtil.isNotEmpty(type) && type.equals("10")) {
                IntegralRule.RegisterRule registerRule = new IntegralRule.RegisterRule();
                String s = String.valueOf(rule.get("firstRegister"));
                registerRule.setFirstRegister(Integer.valueOf(s));
                String registerRuleStr = JSON.toJSONString(registerRule);
                param.put("rule", registerRuleStr);
            }
                int result=integerRuleService.updateRule(param);
            if(result >0){
                resultMap.put("msg","success");
            }else{
                resultMap.put("msg","error");
            }
        }

        return  resultMap;




    }

    /**
     *  注册送积分规则修改
     * @param request
     * @return
     */
    public ReturnDto updateRegisterRule(HttpServletRequest request, @RequestBody Map<String,Object> map) {
        //获取商家ID
        String siteId = request.getParameter("siteId");
        //判断是否为空
        if(siteId == null || "".equals(siteId)) {
            LOOGER.warn("修改注册送积分的siteId为空");
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }
        // 获取注册送的积分
//        int integral = (int) map.get("integral");
        String integral = (String) map.get("integral");
        //获取规则说明
        String desc = (String)map.get("desc");
        //获取状态
        Integer status = (Integer)map.get("status");
//        IntegralRule.RegisterRule registerRule = new IntegralRule().new RegisterRule();
        IntegralRule.RegisterRule registerRule = new IntegralRule.RegisterRule();
//        registerRule.setFirstRegister(integral);
        //转换成json串
        String registerRuleStr = JSON.toJSONString(registerRule);
        //根据siteId和type 10 去查询数据
        IntegralRuleEntity ruleEntity = integerRuleService.queryRegisterRule(new Integer(siteId));
        ruleEntity.setDesc(desc);
        ruleEntity.setRule(registerRuleStr);
        //设置规则开启
        ruleEntity.setStatus(status);//1 开启 0 关闭
        //更新修改时间
        ruleEntity.setUpdateTime(new Date());
        //更新数据
        Integer result = integerRuleService.updateRegisterRule(ruleEntity);

        // 判断是否更新成功
        if(result == 1) {
            return ReturnDto.buildSuccessReturnDto("注册规则更新成功!");
        }
        return ReturnDto.buildFailedReturnDto("注册规则更新失败!");
    }


    /**
     * 注册送积分
     * @return
     */
    @RequestMapping(value="/registerIntegral")
    @ResponseBody
    public ReturnDto queryRegisterIntegralRule(HttpServletRequest request) {
        //获取siteId
        String siteId = request.getParameter("siteId");
        //判断是否为空
        if(siteId == null || "".equals(siteId)) {
            LOOGER.warn("注册送积分的siteId为空");
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }
        //根据siteId查询规则列表
        List<IntegralRuleEntity> list = integerRuleService.queryIntegralRules(siteId);
        IntegralRuleEntity entity = null;
        //获取类型为注册的规则
        for (IntegralRuleEntity ie : list) {
            if(IntegralType.USER_REGISTER.equals(ie.getType().toString())) {
                entity = ie;
            }
        }
        //获取该商家注册送积分的对应积分
        String rule = entity.getRule();
        //将json串转换成对应的实体
//        IntegralRule.RegisterRule registerRule = JSON.parseObject(rule, new IntegralRule().new RegisterRule().getClass());
        IntegralRule.RegisterRule registerRule = JSON.parseObject(rule, IntegralRule.RegisterRule.class);
        //积分
//        int integral = registerRule.getFirstRegister();
        int integral = registerRule.getFirstRegister();
        //获取会员信息
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        int buyerId = (int) parameterMap.get("buyerId");
        //查询当前会员对应积分
        Member member = memberMapper.getMember(new Integer(siteId), buyerId);
        BigInteger integrate = member.getIntegrate();
        integrate.add(BigInteger.valueOf(integral));
//        integrate.add(BigInteger.valueOf(Integer.valueOf(integral)));
        member.setIntegrate(integrate);
        //保存到数据库
        memberMapper.updateVipMember(member);

        return ReturnDto.buildSuccessReturnDto("用户修改成功");
    }


    /*@RequestMapping("insertRule")
    @ResponseBody
    public Map<String,Object> insertRule(HttpServletRequest request){
        Map<String,Object> resultMap=new HashMap<String,Object>();
        Map<String,Object> param=ParameterUtil.getParameterMap(request);
        if(StringUtil.isEmpty(param.get("id"))){
            int result=integerRuleService.insertRule(param);
            if(result >0){
                resultMap.put("msg","success");
            }else{
                resultMap.put("msg","error");
            }
        }else{
            int result=integerRuleService.updateRule(param);
            if(result >0){
                resultMap.put("msg","success");
            }else{
                resultMap.put("msg","error");
            }
        }

        return  resultMap;

    }*/

    @RequestMapping("queryIntegral")
    @ResponseBody
    public Map<String,Object> queryIntegral(HttpServletRequest request){
        Map<String,Object> resultMap=new HashMap<String,Object>();
        Map<String,Object> param=ParameterUtil.getParameterMap(request);
        Map<String,Object> result = integerRuleService.queryIntegral(param);
        if(null != result){
            resultMap.put("result",result);
            resultMap.put("msg","success");
//        }else if(null == result && String.valueOf(param.get("type")).equals("40")) {    //等额送积分
//            result = new HashMap<>();
//            result.put("rule","{\"type\":3,\"equalAmount\":{\"consumeMoney\":100}}");
//            resultMap.put("result",result);
//            resultMap.put("msg","success");
        } else{
            resultMap.put("msg","error");
        }
        return resultMap;
    }
    @GetMapping("/ruleLog")
    @ResponseBody
    public ReturnDto getRuleLog(HttpServletRequest request){
        Map<String,Object> resultMap=new HashMap<String,Object>();
        Map<String,Object> param=ParameterUtil.getParameterMap(request);
        Object siteId = param.get("siteId");
        if("".equals(siteId)){
            return ReturnDto.buildFailedReturnDto("siteId为null");
        }

        int page = (param.get("page") == null || "".equals(param.get("page")))?1:Integer.valueOf(param.get("page").toString());
        int pageSize = (param.get("pageSize") == null || "".equals(param.get("pageSize")))?15:Integer.valueOf(param.get("pageSize").toString());
        PageHelper.startPage(page,pageSize);
        List<Map<String,Object>> integralRuleLogList = integerRuleService.getIntegralRuleLog(param);
        PageInfo<?> pageInfo = new PageInfo<>(integralRuleLogList);

        resultMap.put("page",pageInfo.getPageNum());
        resultMap.put("pageSize",pageInfo.getPageSize());
        resultMap.put("totalPages",pageInfo.getPages());
        resultMap.put("total",pageInfo.getTotal());
        resultMap.put("items",integralRuleLogList);

        System.out.println(integralRuleLogList);

        return ReturnDto.buildSuccessReturnDto(resultMap);
    }

    /**
     * 送积分
     * @param request
     * @return
     */
//    @PostMapping("/getIntegral")
//    @ResponseBody
//    public ReturnDto getIntegral(HttpServletRequest request){
//        Map<String,Object> param = ParameterUtil.getParameterMap(request);
//        Object siteId = param.get("siteId");
//        Object buyerId = param.get("buyerId");
//        Object type = param.get("type");
//
//        if("".equals(type)){
//            return ReturnDto.buildFailedReturnDto("type为null");
//        }
//        if("".equals(siteId)){
//            return ReturnDto.buildFailedReturnDto("siteId为null");
//        }
//
//        if("".equals(buyerId)){
//            return ReturnDto.buildFailedReturnDto("buyerId为null");
//        }
//
//        if(Integer.valueOf(type.toString()) == CommonConstant.TYPE_BUY && (!param.containsKey("tradesId") || StringUtil.isEmpty(param.get("tradesId")))){
//            return ReturnDto.buildFailedReturnDto("tradesId为null");
//        }
//
//        String resultString = "error";
//
//        if(Integer.valueOf(type.toString()) == CommonConstant.TYPE_BUY){
//
//            resultString = integerRuleService.getIntegralByShopping(param);
//
//        }else if(Integer.valueOf(type.toString()) == CommonConstant.TYPE_REGIST){
//
//            resultString = integerRuleService.getIntegralByRegister(param);
//        }
//
//
//        return ReturnDto.buildSuccessReturnDto(resultString);
//    }

    //商户是否支持签到校验
    @RequestMapping("/checkinCheck")
    @ResponseBody
    public ReturnDto checkinCheck(HttpServletRequest request){

        Map param = ParameterUtil.getParameterMap(request);

        String checkParam = mapKeyHelper(param, "siteId", "buyerId");
        if (StringUtil.isNotEmpty(checkParam)) {
            return resultHelper(false, "参数 " + checkParam + " 不能为空！");
        }

        //签到并增加积分
        return integerRuleService.checkinCheck(param);

    }

    /**
     * 签到及送积分
     *
     * @param request
     * @return
     */
    @RequestMapping("/checkinAddIntegeral")
    @ResponseBody
    public ReturnDto checkinAddIntegeral(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);

        String checkParam = mapKeyHelper(param, "siteId", "buyerId");
        if (StringUtil.isNotEmpty(checkParam)) {
            return resultHelper(false, "参数 " + checkParam + " 不能为空！");
        }

        //签到并增加积分
        return integerRuleService.integralAddForChicken(param);
    }

    @RequestMapping("convertIntegral")
    @ResponseBody
    public OrderResponse convertIntegral(@RequestBody HomeDeliveryAndStoresInvite homeDeliveryAndStoresInvite){
    /*    HomeDeliveryAndStoresInvite homeDeliveryAndStoresInvite = new HomeDeliveryAndStoresInvite();
        List<OrderGoods> orderGoodss = new ArrayList<OrderGoods>();
        OrderGoods orderGoods = new OrderGoods();
        orderGoods.setGoodsId(45747);
        orderGoods.setGoodsNum(1);
        orderGoodss.add(orderGoods);
        homeDeliveryAndStoresInvite.setSiteId(100073);
        homeDeliveryAndStoresInvite.setOrderType("2");//订单类型,1:送货上门订单,2：门店自提订单
        homeDeliveryAndStoresInvite.setOrderGoods(orderGoodss);
        homeDeliveryAndStoresInvite.setReceiverMobile("15921856482");
        homeDeliveryAndStoresInvite.setReceiverPhone("02188888888");
        homeDeliveryAndStoresInvite.setReceiverName("会淹死的鱼");
        homeDeliveryAndStoresInvite.setBuyerMessage("跟我快点发货哦，亲");
        homeDeliveryAndStoresInvite.setClerkInvitationCode("2_00001");
        homeDeliveryAndStoresInvite.setIntegralUse(0);
        homeDeliveryAndStoresInvite.setInvoiceTitle("上海伍壹健康科技有限公司");
        homeDeliveryAndStoresInvite.setLat("125.21");
        homeDeliveryAndStoresInvite.setLng("30.25");
        homeDeliveryAndStoresInvite.setMobile("15921856482");
        homeDeliveryAndStoresInvite.setFlag(1);
        //homeDeliveryAndStoresInvite.setPlatformType(110);
        homeDeliveryAndStoresInvite.setReceiverAddress("虹口区虹关路368号建邦大厦15楼");
        homeDeliveryAndStoresInvite.setReceiverCityCode("31100");
        homeDeliveryAndStoresInvite.setReceiverProvinceCode("43110");
        //homeDeliveryAndStoresInvite.setPostStyle("150");//配送方式：110(卖家包邮),120(平邮),130(快递),140(EMS),150(送货上门),160(门店自提)，170(门店直销)，180(货到付款),9999(其它)
        //homeDeliveryAndStoresInvite.setSelfTakenStore();//自提门店ID，当订单类型为2：门店自提订单时，改值必传
        homeDeliveryAndStoresInvite.setStoreUserId(5423);//门店促销员ID
        homeDeliveryAndStoresInvite.setTradesInvoice(1);//是否需要开发票
        homeDeliveryAndStoresInvite.setTradesSource(120);//订单来源: 110 (网站)，120（微信），130（app）, 140（店员帮用户下单），9999（其它）
        homeDeliveryAndStoresInvite.setTradesStore("323143");//订单来源门店
        homeDeliveryAndStoresInvite.setAccountSource(5);
        homeDeliveryAndStoresInvite.setUseCount(2);
        //homeDeliveryAndStoresInvite.setUserCouponId(232);//使用的优惠券ID*/
        OrderResponse response=orderService.convertIntegral(homeDeliveryAndStoresInvite);
        return  response;

    }



    public String mapKeyHelper(Map map, String... args) {
        for (String param : args) {
            if (!map.containsKey(param)) return param;
        }
        return "";
    }

    public ReturnDto resultHelper(boolean flag, String str) {
        if(flag){
            return ReturnDto.buildSuccessReturnDto(str);
        }else {
            return ReturnDto.buildFailedReturnDto(str);
        }
    }

    @RequestMapping(value="/queryIntegralDetailList", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queryIntegralDetailList(HttpServletRequest request, @RequestParam(name="pageNum",defaultValue = "1") Integer pageNum, @RequestParam(name="pageSize", defaultValue = "15") Integer pageSize) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        if(StringUtil.isEmpty(parameterMap.get("siteId"))) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }
        //开启分页
        PageHelper.startPage(pageNum,pageSize);
        List<Map> result = integralLogService.queryIntegralList(parameterMap);
        PageInfo pageInfo = new PageInfo(result);
        Map<String, Object> map = new HashedMap();
        map.put("pageNum",pageInfo.getPageNum());
        map.put("pageSize",pageInfo.getTotal());
        map.put("result",result);
        return ReturnDto.buildSuccessReturnDto(map);
    }

    @GetMapping("rules")
    @ResponseBody
    public ReturnDto rules(HttpServletRequest request){
        Map param = ParameterUtil.getParameterMap(request);
        if(StringUtil.isEmpty(param.get("siteId"))) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }

        List<com.jk51.model.integral.IntegralRule> ruleList = integralService.rules(param);


        return ReturnDto.buildSuccessReturnDto(ruleList);
    }
    @GetMapping("rulesSize")
    @ResponseBody
    public ReturnDto rulesSize(HttpServletRequest request){
        Map param = ParameterUtil.getParameterMap(request);
        if(StringUtil.isEmpty(param.get("siteId"))) {
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        }

        List<com.jk51.model.integral.IntegralRule> ruleList = integralService.rules(param);


        return ReturnDto.buildSuccessReturnDto(ruleList.size());
    }

    @RequestMapping("/getIntegralByGame")
    @ResponseBody
    public ReturnDto getIntegralByGame(HttpServletRequest request){
        Map<String,Object> param=ParameterUtil.getParameterMap(request);
        String result= integerRuleService.getIntegralByGame(param);
        if("送积分成功".equals(result)){
            return ReturnDto.buildSuccessReturnDto(result);
        }else{
            return ReturnDto.buildFailedReturnDto(result);
        }

    }

}
