package com.jk51.modules.merchant.mapper;

import com.jk51.model.statistics.WebPage;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-06-20
 * 修改记录:
 */
public interface WebPageMapper {
    int insertSelect(Map<String, Object> webPage);

    List<WebPage> selectWebPage(@Param("siteId") Integer siteId, @Param("createTime") String createTime);

    List<Map<String, Object>> queryWebVisitors(@Param("siteId") Integer site_id, @Param("dates") Map<String, Date> dates);

    List<Map<String, Object>> queryWebVisitors2(@Param("siteId") Integer site_id, @Param("dates") Map<String, Date> dates);

    List<Map<String, Object>> queryGoodsWebVisitors(@Param("siteId") Integer site_id, @Param("dates") List<Map<String, Date>> dates);

    List<Map<String, Object>> queryGoodsWebVisitors2(@Param("siteId") Integer site_id, @Param("dates") Map<String, Date> dates);

    Integer queryWebYesterDayAndLastWeek(@Param("siteId") Integer site_id, @Param("dates") Map<String, Date> proportion);

    Integer queryGoodsWebYesterDayAndLastWeek(@Param("siteId") Integer site_id, @Param("dates") Map<String, Date> proportion);

    /**
     * 获取一定时间内浏览人次数
     * @param siteId
     * @param start
     * @param end
     * @return
     */

    List<Map<String, Object>> onlineVisitorsFunnel(@Param("siteId") Integer siteId, @Param("start") String start, @Param("end") String end);
    List<Map<String, Object>> selectMemberCount(@Param("siteId") Integer siteId, @Param("dates") Map<String, Date> dates);
    //查询新访客
    List<Map<String, Object>> queryNewVisitor(@Param("siteId") Integer siteId,@Param("dates")  List<Map<String, Date>> dates);
    //查询平均访客
    List<Map<String,Object>> queryAverageVisitor(@Param("siteId") Integer siteId,@Param("dates") Map<String, Date> datesMap);
    //按时段查询
    List<Map<String,Object>> selectWebMemberCountByHour(@Param("siteId") Integer siteId, @Param("dates") Map<String, Date> datesMap);
    //性别分布
    List<Map<String,Object>> sexDistribution(@Param("siteId") Integer siteId,@Param("dates") Map<String, Date> datesMap);
    //年龄分布
    List<Map<String,Object>> ageDistribution(@Param("siteId") Integer siteId,@Param("dates") Map<String, Date> datesMap);
    //跳失率
    List<Map<String,Object>> bounceRate(@Param("siteId") Integer siteId,@Param("dates") List<Map<String, Date>> dates);
    //区域分布
    List<Map<String,Object>> areaDistribution(@Param("siteId") Integer siteId,@Param("dates")  Map<String, Date> datesMap);
    //查询老访客
//    List<Map<String,Object>> queryOldVisitor(@Param("siteId")Integer siteId,@Param("dates") List<Map<String, Date>> dates);
    //查询商品停留时间
    String getAvgReadTime(@Param("siteId")Integer siteId,@Param("date") String date);
    //查询当前日期的停留时间
    Map<String,Object> getAvgReadTimeToday(@Param("siteId")Integer siteId,@Param("date") String date);

    //初始化数据使用
    Double getAvgReadTimeTodayInit(@Param("siteId")Integer siteId,@Param("date") String date);
    Map<String,Object> getAvgReadTime2Init(@Param("siteId")Integer siteId,@Param("date") String date);
    //查询新访客 (2)
    List<String> queryMemberIdsByOneDays(@Param("siteId") Integer siteId,@Param("dates") Map<String, Date> date);
    //查询新访客OpendId (2)
    List<String> queryOpenIdsByOneDays(@Param("siteId") Integer siteId,@Param("dates") Map<String, Date> date,@Param("ids") List<String> member_ids);
    //查询新访客 ip (2)
    List<String> queryIpsByOneDays(@Param("siteId") Integer siteId,@Param("dates") Map<String, Date> date);

    List<Map<String,Object>> getAvgReadTimeAll(@Param("siteId")Integer siteId, @Param("dates") List<String> dates);

    List<Map<String, Object>> selectMemeberIds(@Param("siteId") Integer siteId, @Param("start") String start, @Param("end") String end);

    Integer selectMemberIdCount(@Param("siteId") Integer siteId, @Param("start") String start, @Param("memberIds") List<Integer> memberIds);

    List<Map<String, Object>> selectOpenIds(@Param("siteId") Integer siteId, @Param("start") String start, @Param("end") String end);

    Integer selectopenIdCount(@Param("siteId") Integer siteId, @Param("start") String start, @Param("openIds") List<String> openIds);

    List<Map<String, Object>> selectIPs(@Param("siteId") Integer siteId, @Param("start") String start, @Param("end") String end);

    Integer selectIPCount(@Param("siteId") Integer siteId, @Param("start") String start, @Param("ips") List<String> IPs);


    String getFlowChartsMap(@Param("siteId") Integer siteId, @Param("date") String date);

    //交易分析优化
    String getTransChartsMap(@Param("siteId") Integer siteId, @Param("date") String date);

    String getTransMap(@Param("siteId") Integer siteId, @Param("date") String date);

    String getTransPicMap(@Param("siteId") Integer siteId, @Param("date") String date);

    String getTransGraphMap(@Param("siteId") Integer siteId, @Param("date") String date);

    Date findStayTime(@Param("memberId") String memberId,@Param("openId") String openId,@Param("ip") String ip,@Param("id") Integer id);

    //修改区域名称
    String selectIPForID(@Param("id") Integer id);

    Integer updateIPForID(@Param("id") Integer id, @Param("name") String name);

    //修改停留时间
    Map<String,Object> selectIdsForID(@Param("id") Integer id);

    Integer updateTime4DB(@Param("id") Integer id, @Param("time") Long time);

    String getFirstFunnelStatistics(@Param("siteId") Integer siteId, @Param("time") String time);

    String getYestodayData(@Param("siteId") Integer siteId, @Param("time") String time);

    Integer selectopenIdCountTwo(@Param("siteId") Integer siteId, @Param("start") String start);

    Integer updateStruts(@Param("start") String start,@Param("end") String end);

    Integer selectopenIdCountThree(@Param("siteId") Integer siteId, @Param("sTime") String sTime , @Param("eTime") String eTime);

    Integer queryPageNumByStoreadminId(@Param("siteId")String siteId,@Param("startTime")String startTime,@Param("endTime") String endTime);
}
