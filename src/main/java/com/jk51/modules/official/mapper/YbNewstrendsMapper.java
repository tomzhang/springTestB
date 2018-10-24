package com.jk51.modules.official.mapper;


import com.jk51.model.official.YbNewstrends;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface YbNewstrendsMapper {


    List<YbNewstrends> selectNewsList(Map<String,Object> map);

    Integer deleteNewsById(@Param("newsId") Integer newsId);

    Integer updateNews(YbNewstrends ybNewstrends);

    Integer insertNews(YbNewstrends ybNewstrends);

}
