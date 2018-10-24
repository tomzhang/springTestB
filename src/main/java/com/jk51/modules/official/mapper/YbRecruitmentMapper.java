package com.jk51.modules.official.mapper;


import com.jk51.model.official.YbRecruitment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YbRecruitmentMapper {

    int add(YbRecruitment ybRecruitment);

    List<YbRecruitment> queryList(YbRecruitment ybRecruitment);

    YbRecruitment queryOne(@Param("id") int id);

}
