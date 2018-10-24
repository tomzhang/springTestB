package com.jk51.modules.integral.service;

import com.jk51.commons.CommonConstant;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.integral.IntegralLog;
import com.jk51.model.order.Member;
import com.jk51.modules.integral.mapper.IntegralMapper;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: hulan
 * 创建日期: 2017-03-08
 * 修改记录:
 */
@Service
public class IntegralLogService {
    private static final Logger logger = LoggerFactory.getLogger(IntegralLogService.class);

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private IntegralMapper integralMapper;

    @Transactional
    public void storeUpdateIntegral(int siteId, int memberId, int integral) {
        Member member = memberMapper.getMember(siteId, memberId);
        int scope = member.getIntegrate().intValue() + integral;
        member.setIntegrate(BigInteger.valueOf(scope));

        memberMapper.updateIntegral(member);
    }

//    public String selectIntegralLog(int memberId) {
//        String json = "";
//        try {
//            List<IntegralLog> integralLogList = integralMapper.selectIntegralLog(memberId);
//            json = JacksonUtils.getInstance().writeValueAsString(integralLogList);
//        } catch (Exception e) {
//            logger.error("JackJson error", e);
//        }
//        return json;
//    }

    @Transactional
    public String integraUpdate(Map<String,Object> param){

        Map map = new HashMap();
        map.put("siteId",param.get("siteId"));
        map.put("buyerId",param.get("memberId"));
        map = integralMapper.selectMemberData(map);

        int isBack = 0;
        if(!StringUtil.isEmpty(param.get("isBack"))){
            isBack = (int) param.get("isBack");
        }
        int money=0;
        if(!StringUtil.isEmpty(param.get("money"))){
            money=Integer.parseInt(param.get("money").toString());
        }
        int realPay=0;
        if(!StringUtil.isEmpty(param.get("realPay"))){
            realPay=Integer.parseInt(param.get("realPay").toString());
        }
        BigInteger diff = new BigInteger((param.get("integralDiff")==null?"0":(Integer)param.get("integralDiff")).toString());
        BigInteger add = new BigInteger((param.get("integralAdd")==null?"0":(Integer)param.get("integralAdd")).toString());
        BigInteger integrate = new BigInteger((map.get("integrate") == null?"0":map.get("integrate").toString()));
        BigInteger totalGetIntegrate = new BigInteger(map.get("totalGetIntegrate")==null?"0":map.get("totalGetIntegrate").toString());
        BigInteger totalConsumeIntegrate = new BigInteger(map.get("totalConsumeIntegrate")==null?"0":map.get("totalConsumeIntegrate").toString());

        //int comTO = integrate.compareTo(diff);

        //比较当前积分与赠送积分，当前积分比赠送积分少就不退，-1不可以退
        if(diff.intValue()!=0 && money == realPay){
            integrate = integrate.subtract(diff);
            totalConsumeIntegrate = totalConsumeIntegrate.add(diff);
        }
        if(isBack!=0&&add.intValue()!=0){//是否退还消费的积分
            integrate = integrate.add(add);
            totalGetIntegrate = totalGetIntegrate.add(add);
        }

        //定义更新参数
        Map map1= new HashMap();
        map1.put("siteId",param.get("siteId"));
        map1.put("buyerId",param.get("memberId"));
        map1.put("integrate",integrate);
        map1.put("totalGetIntegrate",totalGetIntegrate);
        map1.put("totalConsumeIntegrate",totalConsumeIntegrate);
        int i = integralMapper.updateMemberData(map1);
        int j=integralMapper.updateMemberInfoData(map1);

        if(i==0 || j==0){//加日志
            return "faild";
        }
        List<IntegralLog> integralLogs = new ArrayList<>();
        IntegralLog integralLog ;

        if(isBack!=0&&add.intValue()!=0){//记录不是0的消费积分
            integralLog = new IntegralLog();
            integralLog.setSiteId((Integer) param.get("siteId"));
            integralLog.setBuyerNick((String) map.get("buyerNick"));
            integralLog.setMemberId((Integer) param.get("memberId"));
            integralLog.setIntegralDesc("退积分抵现金:"+param.get("tradesId"));
            integralLog.setIntegralAdd(add);
            integralLog.setIntegralDiff(BigInteger.valueOf(0));
            integralLog.setIntegralOverplus(integrate);
            integralLog.setMark("退积分抵现金:"+param.get("tradesId"));
            integralLog.setType(CommonConstant.TYPE_ORDER_DIFF);

            //添加到list
            integralLogs.add(integralLog);
        }


        if(diff.intValue()!=0 && money == realPay){//记录不是0的赠送积分
            integralLog = new IntegralLog();
            integralLog.setSiteId((Integer) param.get("siteId"));
            integralLog.setBuyerNick((String) map.get("buyerNick"));
            integralLog.setMemberId((Integer) param.get("memberId"));
            integralLog.setIntegralDesc("退赠送积分:"+param.get("tradesId"));
            integralLog.setIntegralAdd(BigInteger.valueOf(0));
            integralLog.setIntegralDiff(diff.intValue() >0 ?diff:BigInteger.valueOf(0));
            integralLog.setIntegralOverplus(integrate);
            integralLog.setMark("退赠送积分:"+param.get("tradesId"));
            integralLog.setType(CommonConstant.TYPE_ORDER_BACK);
            integralLogs.add(integralLog);
        }

        if(integralLogs.size()!=0){
            int result = integralMapper.insertList(integralLogs);
            if(result==integralLogs.size()) return "ok";
            return "faild";
        }else{
            return "faild";//没有插入退款记录
        }


    }

    public List<Map> queryIntegralList(Map<String, Object> parameterMap) {
        return integralMapper.queryIntegralList(parameterMap);
    }
}
