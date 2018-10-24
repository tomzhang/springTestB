package com.jk51.modules.official.service;

import com.jk51.model.official.YbCarousel;
import com.jk51.model.official.YbNewstrends;
import com.jk51.modules.integral.domain.IntegralRule;
import com.jk51.modules.official.mapper.YbCarouselMapper;
import com.jk51.modules.official.mapper.YbNewstrendsMapper;
import jdk.nashorn.internal.ir.ReturnNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 官网后台配置
 *
 * @auhter zy
 * @create 2017-11-07 14:17
 */
@Service
public class YbOffcialService {

    @Autowired
    YbCarouselMapper ybCarouselMapper;

    @Autowired
    YbNewstrendsMapper ybNewstrendsMapper;

    //查询单个记录
    public YbCarousel selectCarlouselRecord(Map<String,Object> map) {
        return ybCarouselMapper.selectCarlouselRecord(map);
    }

    //更新
    public int updateCarouselRecord(YbCarousel ybCarousel) {
        return ybCarouselMapper.updateCarouselRecord(ybCarousel);
    }

    //插入
    public int insertCarouselRecord(YbCarousel ybCarousel) {
        return ybCarouselMapper.insertCarouselRecord(ybCarousel);
    }

    //删除
    public int deleteCarouselRecord(Integer integer) {
        return ybCarouselMapper.deleteCarouselRecord(integer);
    }

    //查询轮播图列表
    public List<YbCarousel> selectCarlousels(Integer project) {
        return ybCarouselMapper.selectCarlousels(project);
    }

    //新闻列表
    public List<YbNewstrends> selectNewsList(Map<String,Object> map) {
        return ybNewstrendsMapper.selectNewsList(map);
    }

    //删除
    public Integer deleteNewsById(Integer newsId) {
        return ybNewstrendsMapper.deleteNewsById(newsId);
    }

    public Integer updateNews(YbNewstrends ybNewstrends) {
        return ybNewstrendsMapper .updateNews(ybNewstrends);
    }
    //新增修改
    public Integer insertNews(YbNewstrends ybNewstrends) {
        return ybNewstrendsMapper .insertNews(ybNewstrends);
    }
}
