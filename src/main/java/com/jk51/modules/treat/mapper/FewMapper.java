package com.jk51.modules.treat.mapper;


import com.jk51.model.account.models.AccountCommissionRate;
import com.jk51.model.account.models.PayPlatform;
import com.jk51.model.treat.IconLib;
import com.jk51.model.treat.YbSettlementdayConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface FewMapper {

    List<YbSettlementdayConfig> findSettlementday(String siteId);

    List<Map<String,String>> findDatetimeSetting(String siteId);

    AccountCommissionRate getCommissionRatById(@Param("siteId") Integer siteId);

    List<PayPlatform> getPayPlatformById(@Param(value = "site_id") Integer site_id);

    Integer getWeChatTradesAll(@Param("siteId")Integer siteId, @Param("buyerId")String buyerId);

    int insertAndGet(IconLib record);

    int initCategory(@Param("siteId")Integer siteId);

    int insertQrLog(@Param("siteId")Integer siteId,@Param("openid")String openid,@Param("eventkey")String eventkey);

    List<Map> queryStore(@Param("siteId")String siteId);

    List<Map> queryGoods(@Param("siteId")String siteId);

    Map queryBDErpMember(@Param("siteId")Integer siteId, @Param("mobile")String mobile);

    Map queryBDErpMemberByMobile(@Param("siteId")Integer siteId, @Param("mobile")String mobile);

    int updateBDMember(@Param("site_id")Integer siteId,@Param("buyer_id")String buyer_id,
                       @Param("name")String name,@Param("idcard_number")String idcard_number,@Param("sex")Integer sex,@Param("offline_integral")Integer offline_integral);

    int updateBDMemberInfo(@Param("site_id")Integer siteId, @Param("member_id")String member_id,
                           @Param("address")String address , @Param("tag")String tag, @Param("birthday")Date birth, @Param("case_history")String case_history, @Param("allergies")String allergies);
}
