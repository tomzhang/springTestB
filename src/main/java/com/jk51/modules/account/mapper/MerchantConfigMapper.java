package com.jk51.modules.account.mapper;

import com.jk51.model.account.models.MerchantConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MerchantConfigMapper {

    List<MerchantConfig> getAll();

    void update(MerchantConfig merchantConfig);

    MerchantConfig get(Integer id);

}
