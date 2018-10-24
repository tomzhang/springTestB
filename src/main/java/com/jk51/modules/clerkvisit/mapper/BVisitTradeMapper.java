package com.jk51.modules.clerkvisit.mapper;

import com.jk51.model.clerkvisit.BVisitTrade;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BVisitTradeMapper {


    int deleteByPrimaryKey(Integer id);

    int insert(BVisitTrade record);

    int insertSelective(BVisitTrade record);

    BVisitTrade selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BVisitTrade record);

    int updateByPrimaryKey(BVisitTrade record);

    int queryCountForActivityTrades(Map<String,Object> param);

    List<Map<String, Object>> selectByVisitIdAndSiteId(@Param("id") String  id,@Param("siteId") String  siteId);

}
