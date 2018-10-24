package com.jk51.modules.erpprice.mapper;

import com.jk51.model.erpprice.BErpSetting;
import com.jk51.model.erpprice.BErpSettingExample;

import java.util.List;

import com.jk51.modules.erpprice.domain.pojo.ErpStorePO;
import org.apache.ibatis.annotations.Param;

public interface BErpSettingMapper {
    long countByExample(BErpSettingExample example);

    int deleteByExample(BErpSettingExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BErpSetting record);

    int insertSelective(BErpSetting record);

    List<BErpSetting> selectByExample(BErpSettingExample example);

    BErpSetting selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BErpSetting record, @Param("example") BErpSettingExample example);

    int updateByExample(@Param("record") BErpSetting record, @Param("example") BErpSettingExample example);

    int updateByPrimaryKeySelective(BErpSetting record);

    int updateByPrimaryKey(BErpSetting record);

    ErpStorePO selectErpStore(@Param("siteId") Integer siteId);

    int insertOrUpdate(BErpSetting record);

    BErpSetting selectBySiteId(@Param("siteId") Integer siteId);

    int updateType(@Param("type") Integer type, @Param("siteId") Integer siteId);

    String getErpStoresNumbers(@Param("siteId") Integer siteId);

}
