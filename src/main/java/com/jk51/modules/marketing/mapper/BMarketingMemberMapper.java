package com.jk51.modules.marketing.mapper;

import com.jk51.model.BMarketingMember;
import com.jk51.modules.marketing.request.MarketingMemberParm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BMarketingMemberMapper {
    int insertSelective(BMarketingMember record);

    List<Map> getLst(MarketingMemberParm parms);

    int updNum(@Param("id") Integer id,@Param("siteId") Integer siteId,@Param("ceiling") Integer ceiling,@Param("drawNum") Integer drawNum);//更新抽取次数

    BMarketingMember selectByBuyerId(@Param("siteId") Integer siteId, @Param("planId") Integer planId, @Param("buyerId") Integer buyerId);

    int updateDrawNum(@Param("siteId") Integer siteId, @Param("id") Integer id, @Param("memberDrawNum") Integer memberDrawNum);
}
