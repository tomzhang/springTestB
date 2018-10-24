package com.jk51.modules.integral.mapper;

import com.jk51.model.integral.IntegralLog;
import com.jk51.model.integral.IntegralRule;
import com.jk51.model.order.OrderGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Mapper
public interface IntegralMapper {

    /**
     * 根据siteId和useCase查询
     *
     */
    Map query(Map m);

    int init(Map m);

    int logAdd(Map m);

    List<Map> logQuery(Map m);

    Map getOverPlus(Map m);

    int updateMemberData(Map m);

    int updateMemberInfoData(Map m);

    int updateChickenData(Map m);

    Map checkinYesterday(Map m);

    Map checkinToday(Map m);

    BigInteger queryDiffSum(Map m);

    BigInteger queryAddSum(Map m);

    BigDecimal queryIntegralProportion(Map m);

    List<Map> queryMemberi(Map item);

    List<Map> queryRules(Map item);

    int setProportion(Map map);

    int updateRule(Map map);

    Map selectMemberData(Map param);

    int insertList(List<IntegralLog> integralLogs);

    List<Map> queryIntegralList(Map<String, Object> parameterMap);

    List<IntegralRule> rules(Map<String, Object> param);

    int calcIntegral(@Param("siteId") int siteId, @Param("goodsList") List<OrderGoods> goodsList);
}
