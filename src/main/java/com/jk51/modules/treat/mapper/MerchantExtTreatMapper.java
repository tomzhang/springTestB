package com.jk51.modules.treat.mapper;

import com.jk51.model.treat.MerchantExtTreat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface MerchantExtTreatMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MerchantExtTreat record);

    int insertSelective(MerchantExtTreat record);

    MerchantExtTreat selectByPrimaryKey(Integer id);

    MerchantExtTreat selectByMerchantId(@Param("merchant_id") Integer merchant_id);

    int updateByPrimaryKeySelective(MerchantExtTreat record);

    int updateByPrimaryKey(MerchantExtTreat record);

    int updateInviteCode(@Param("wei_show_invite_code") Integer wei_show_invite_code,
                         @Param("compu_show_invite_code") Integer compu_show_invite_code,
                         @Param("merchant_id") Integer merchant_id);

    int setOrderAssignType(Integer orderAssignType, Integer merchantId);

    MerchantExtTreat getByWxOriginalId(@Param("wx_original_id") String wxOriginalId);

    /**
     * 商户是否拥有对接库存管理
     *
     * @param merchant_id
     * @return
     */
    int selectStorage(@Param("merchant_id") Integer merchant_id);

    /**
     * 商户是否拥有对接erp价格管理
     *
     * @param merchant_id
     * @return
     */
    int selectERPPrice(@Param("merchant_id") Integer merchant_id);

    Map queryLogisticsFlag(@Param("siteId")String siteId);

    int updateLogisticsFlag(@Param("siteId") String siteId, @Param("logistics_flag_mode") Integer logistics_flag_mode);


    Integer selectCheck(@Param("merchant_id") Integer merchant_id);
    Integer selectCheckStore(@Param("siteId") Integer siteId, @Param("storeId") Integer storeId);

    int selectHealth(@Param("merchant_id") Integer merchant_id);
    int selectHealthStore(@Param("merchant_id") Integer merchant_id);

    String querySettingDis(@Param("siteId")String site_id);
    int updateSettingDis(@Param("siteId")String site_id, @Param("str") String str,@Param("metaVal") String metaVal);

    Integer getIsService(@Param("siteId") String siteId);
}
