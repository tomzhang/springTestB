package com.jk51.modules.merchant.mapper;

import com.jk51.model.merchant.MerchantApply;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MerchantApplyMapper {
    void save(MerchantApply merchantApply);

    MerchantApply getById(Integer id);

    List<MerchantApply> getList();

    MerchantApply getByMerchantId(Integer merchant_id);

    MerchantApply getByApplicantId(Integer applicant_id);

    void update(MerchantApply merchantApply);

}
