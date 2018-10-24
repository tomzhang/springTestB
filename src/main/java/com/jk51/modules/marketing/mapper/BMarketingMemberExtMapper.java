package com.jk51.modules.marketing.mapper;

import com.jk51.model.BMarketingMemberExt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BMarketingMemberExtMapper {
    int insertSelective(BMarketingMemberExt record);

    Integer changeStatus(@Param("siteId") Integer siteId,@Param("id") Integer id,@Param("status") Integer status,@Param("remark") String remark);
}
