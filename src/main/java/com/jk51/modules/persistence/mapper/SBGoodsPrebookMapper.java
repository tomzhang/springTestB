package com.jk51.modules.persistence.mapper;

import com.jk51.model.order.SBGoodsPrebook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SBGoodsPrebookMapper {

    SBGoodsPrebook selectByPrimaryKey(@Param("siteId") String siteId, @Param("prebookId") String prebookId);

    Map<String,Object> selectByPrimaryKeyMap(@Param("siteId") String siteId, @Param("prebookId") String prebookId);

    void updataPrebook(@Param("siteId") String siteId, @Param("prebookId") String prebookId, @Param("prebookClerkId") String prebookClerkId, @Param("prebookState") int prebookState);

    List<SBGoodsPrebook> getPrebookList(@Param("siteId") String siteId, @Param("prebookClerkId") String prebookClerkId);

    void acceptPrebook(@Param("siteId") String siteId, @Param("prebookId") String prebookId, @Param("prebookClerkId") String prebookClerkId, @Param("name") String name);

    Integer getPreOrderCount(int storeUserId);

    Integer insertSelective(SBGoodsPrebook bGoodsPrebook);

    List<Map<String,Object>> getPrebookListMap(@Param("siteId") String siteId, @Param("prebookClerkId") String prebookClerkId);
}
