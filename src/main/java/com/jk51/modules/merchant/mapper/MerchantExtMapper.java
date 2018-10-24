package com.jk51.modules.merchant.mapper;

import com.jk51.model.merchant.MerchantExt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Mapper
public interface MerchantExtMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MerchantExt record);

    int insertSelective(MerchantExt record);

    MerchantExt selectByPrimaryKey(Integer id);

    MerchantExt selectByMerchantId(@Param("merchant_id") Integer merchant_id);

    Integer selectErpGoodsPriceQu(@Param("merchant_id") Integer merchant_id);

    int updateByPrimaryKeySelective(MerchantExt record);

    int updateByPrimaryKey(MerchantExt record);

    int updateByMerchantId(MerchantExt record);

    int updateInviteCode(@Param("wei_show_invite_code") Integer wei_show_invite_code,
                         @Param("compu_show_invite_code") Integer compu_show_invite_code,
                         @Param("merchant_id") Integer merchant_id);

    int setOrderAssignType(Integer orderAssignType, Integer merchantId);

    int integralShopManger(Map<String, Object> map);

    Map<String, Object> getIntegralShopStatus(@Param("siteId") int siteId);
}
