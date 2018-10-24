package com.jk51.map;

import com.jk51.Bootstrap;
import com.jk51.model.map.Coordinate;
import com.jk51.model.map.GeoJson;
import com.jk51.modules.map.service.MapService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by chen on 2017/2/16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
@ActiveProfiles("test")
public class MapServiceTest {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(MapServiceTest.class);

    @Autowired
    MapService mapService;

    /*
    测试高德地理编码
     */
    @Test
    public void testCoordinate() {
        //logger.debug(mapService.geoCoordinate("归德路与侯询路交叉口东200蓝色港湾东苑西门").toString());
        GeoJson result = mapService.geoSearch("归德路与侯询路交叉口东200蓝色港湾东苑西门");
//        System.out.println(mapService.geoCoordinate("北京市朝阳区阜通东大街6号"));
    }

    /**
     * 测试高德步行距离计算
     */
    @Test
    public void testDistance() {
        logger.debug(mapService.geoDistance(new Coordinate(116.481028, 39.989643), new Coordinate(116.434446, 39.90816)));
        System.out.println("高德距离：" + mapService.geoDistance(new Coordinate(116.481028, 39.989643), new Coordinate(116.434446, 39.90816)));
    }

    /**
     * 测试百度地理编码
     */
    @Test
    public void testBDCoo() {
        logger.debug(mapService.bdCoordinate("北京市朝阳区阜通东大街6号").toString());
//        System.out.println(mapService.bdCoordinate("北京市朝阳区阜通东大街6号"));
    }

    /**
     * 测试百度求直线距离
     */
    @Test
    public void testBDDistance() {
        logger.debug(mapService.bdDistance(new Coordinate(116.481028, 39.989643), new Coordinate(116.434446, 39.90816)));
//        System.out.println(mapService.bdDistance(new Coordinate(116.481028,39.989643),new Coordinate(116.434446,39.90816)));
    }

    /**
     * 测试高德 regeo
     */
    @Test
    public void testReGeo() {

        Coordinate co1 = new Coordinate(116.481028, 39.989643);
        Coordinate co2 = new Coordinate(116.434446, 39.90816);

        mapService.reGeo(co1, co2);

    }
    /**
     * 测试百度地理编码
     */
    @Test
    public void testGeoConvert() {
        logger.debug(mapService.geoConvert("121.491730,31.268522").toString());
//        System.out.println(mapService.bdCoordinate("北京市朝阳区阜通东大街6号"));
    }

}
