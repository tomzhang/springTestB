package com.jk51.modules.integral.mapper;

import com.jk51.model.integral.IntegralRule;
import com.jk51.modules.integral.domain.IntegralRuleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/6/1.
 */
@Mapper
public interface IntegralRuleMapper {

    //查询积分规则记录
    List<IntegralRuleEntity> queryIntegralRules(@Param(value = "siteId") String siteId);


    //设置注册积分规则
    Integer insertRegisterRule(IntegralRuleEntity ruleEntity);

    //查询单个注册积分规则对象
    IntegralRuleEntity queryRegisterRule(Integer integer);

    //更新注册积分规则对象
    Integer updateRegisterRule(IntegralRuleEntity ruleEntity);

    //新增积分规则
    int insertRule(Map<String, Object> param);

    //修改积分规则
    int updateRule(Map<String, Object> param);

    //查询积分规则
    Map<String, Object> queryIntegral(Map<String, Object> param);

    //查询积分日志
    List<Map<String, Object>> getIntegralRuleLog(Map<String, Object> param);

    //购物送积分
//    Map<String,Object> integralByShopping(Map<String ,Object> param);

    //查询连续签到次数与上次签到时间
    Map<String, Object> checkinNumber(Map<String, Object> map);

    //查询今天是否已赠送积分
    Map<String, Object> isGiveIntegral(Map<String, Object> map);

    //查询赠送规则
    Map<String, Object> queryIntegralRule(Map<String, Object> map);

    IntegralRule getIntegralRule(Map<String, Object> map);

    //查询会员积分,mobile
    Map<String, Object> memberIntegral(Map<String, Object> map);

    Map<String, Object> memberInfo(Map<String, Object> map);

    //修改b_member中积分
    Integer updateMemberIntegral(Map<String, Object> map);

    //修改b_member_info中积分、签到次数
    Integer updateMemberInfo(Map<String, Object> map);

    //插入b_integrallog表记录
    Integer insertIntegralLog(Map<String, Object> map);

    //新增规则变更记录

    int insertRuleLog(Map<String, Object> param);

    int insertlogList(Map<String, Object> insertLogs);

    //线下使用线上积分时更改线上积分使用情况
    Integer updateMemberIntegralbyOffChange(Map<String, Object> param);

}
