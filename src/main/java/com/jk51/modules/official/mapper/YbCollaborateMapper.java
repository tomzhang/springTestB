package com.jk51.modules.official.mapper;


import com.jk51.model.official.YbCollaborate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YbCollaborateMapper {

    int add(YbCollaborate ybCollaborate);

    int updateById(YbCollaborate ybCollaborate);

    List<YbCollaborate> queryList(YbCollaborate ybCollaborate);

    YbCollaborate queryById(@Param("id") int id);
}
