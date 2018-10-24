package com.jk51.modules.erpprice.mapper;

import com.jk51.model.erpprice.ErpPriceSetting;
import com.jk51.model.erpprice.ErpPriceSettingExample;

import java.util.List;

import com.jk51.modules.erpprice.domain.pojo.ErpSettingPO;
import org.apache.ibatis.annotations.Param;

public interface ErpPriceSettingMapper {
    long countByExample(ErpPriceSettingExample example);

    int deleteByExample(ErpPriceSettingExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ErpPriceSetting record);

    int insertSelective(ErpPriceSetting record);

    List<ErpPriceSetting> selectByExample(ErpPriceSettingExample example);

    ErpPriceSetting selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ErpPriceSetting record, @Param("example") ErpPriceSettingExample example);

    int updateByExample(@Param("record") ErpPriceSetting record, @Param("example") ErpPriceSettingExample example);

    int updateByPrimaryKeySelective(ErpPriceSetting record);

    int updateByPrimaryKey(ErpPriceSetting record);

    int insertBatch(@Param("records") List<ErpPriceSetting> records, @Param("siteId") Integer siteId);

    List<ErpSettingPO> selectStoreIdsByStoreIdAndAreaCode(@Param("siteId") Integer siteId,
                                                          @Param("areaCode") Integer areaCode);

    int updateSettingStatus(@Param("status") Integer status, @Param("type") Integer type, @Param("siteId") Integer siteId);
}
