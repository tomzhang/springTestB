package com.jk51.modules.appInterface.mapper;

import com.jk51.model.order.OrderPreQueryReq;
import com.jk51.model.treat.BGoodsPrebook;
import com.jk51.model.treat.BPrebookNew;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BGoodsPrebookMapper {

    BGoodsPrebook selectByPrimaryKey(@Param("siteId") String siteId, @Param("prebookId") String prebookId);

    Map<String,Object> selectByPrimaryKeyMap(@Param("siteId") String siteId, @Param("prebookId") String prebookId);

    void updataPrebook(@Param("siteId") String siteId, @Param("prebookId") String prebookId, @Param("prebookClerkId") String prebookClerkId, @Param("prebookState") int prebookState);

    List<BGoodsPrebook> getPrebookList(@Param("siteId") String siteId, @Param("prebookClerkId") String prebookClerkId);

    void acceptPrebook(@Param("siteId") String siteId, @Param("prebookId") String prebookId, @Param("prebookClerkId") String prebookClerkId, @Param("name") String name);

    Integer getPreOrderCount(@Param("storeAdminId")int storeAdminId, @Param("siteId")int siteId);

    Integer insertSelective(BGoodsPrebook bGoodsPrebook);

    List<Map<String,Object>> getPrebookListMap(@Param("siteId") String siteId, @Param("prebookClerkId") String prebookClerkId);

    List<BPrebookNew> getGoodsPrebookList(OrderPreQueryReq orderPreQueryReq);

    int savePreInfo(OrderPreQueryReq opq);

    int updatePreStatus(Map<String, Object> parameterMap);

    Integer getClosedNum();

    //查询预约单状态
    Map<String,Object> queryAllKindsNums(String siteId);
}
