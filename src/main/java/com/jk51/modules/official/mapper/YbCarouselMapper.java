package com.jk51.modules.official.mapper;


import com.jk51.model.official.YbCarousel;
import com.jk51.model.official.YbNewstrends;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface YbCarouselMapper {

    YbCarousel selectCarlouselRecord(Map<String,Object> map);

    int updateCarouselRecord(YbCarousel ybCarousel);

    int insertCarouselRecord(YbCarousel ybCarousel);

    int deleteCarouselRecord(@Param("id") Integer integer);

    List<YbCarousel> selectCarlousels(@Param("project") Integer project);

}
