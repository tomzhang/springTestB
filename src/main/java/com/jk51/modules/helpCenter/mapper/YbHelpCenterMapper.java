package com.jk51.modules.helpCenter.mapper;


import com.jk51.model.YbHelpCenter;

import java.util.List;
import java.util.Map;

public interface YbHelpCenterMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(YbHelpCenter record);

    int insertSelective(YbHelpCenter record);

    YbHelpCenter selectByPrimaryKey(Integer id);

    List<YbHelpCenter> getHelpList(Map<String,Object> param);

    int updateByPrimaryKeySelective(YbHelpCenter record);

    int updateByPrimaryKeyWithBLOBs(YbHelpCenter record);

    int updateByPrimaryKey(YbHelpCenter record);
}