package com.jk51.modules.official.mapper;


import com.jk51.model.official.YbStation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface YbStationMapper {

    int add(YbStation ybStation);

    int update(YbStation ybStation);

    YbStation queryStationById(@Param("id") int id);

    List<YbStation> queryStationList(YbStation ybStation);

}
