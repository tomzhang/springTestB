package com.jk51.modules.im.mapper;

import com.jk51.model.BIMService;
import com.jk51.modules.im.controller.request.ClerkAnalyzeParam;
import com.jk51.modules.im.service.clerkAnalyze.response.ClerkAnalyze;
import com.jk51.modules.im.service.clerkAnalyze.response.Satisfactions;
import com.jk51.modules.im.util.CountResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-04-13
 * 修改记录:
 */
@Mapper
public interface BIMServiceMapper {

    Integer updateEvaluate(@Param("imServiceId") Integer imServiceId, @Param("evaluateParam") Integer evaluateParam);

    Integer insertSelective(BIMService bimService);

    Integer updateRaceTimeAndReceiver(@Param("imServiceId") Integer imServiceId, @Param("date") Date date, @Param("receiver")String receiver);

    Integer updateImEndType(@Param("imServiceId") Integer imServiceId,@Param("im_end_type")Integer im_end_type);

    Integer updateFirstReplyTimeIFFirstReplyTimeISNULL(Integer imServiceId);

    List<CountResult> findBIMServiceCountBySiteIdAndQueryTime(@Param("site_id") int site_id, @Param("startDay") String startDay, @Param("endDay")String endDay);

    List<CountResult> findEvaluateList(@Param("site_id")int site_id, @Param("startDay") String startDay, @Param("endDay")String endDay);

    List<CountResult> countLostNum(@Param("site_id")int site_id, @Param("startDay") String startDay, @Param("endDay")String endDay);

    List<CountResult> countClerkNum(@Param("site_id")int site_id,@Param("startDay") String startDay, @Param("endDay")String endDay);

    List<CountResult> findBySiteIdAndCreateTiemAndHasRace(@Param("site_id")int site_id, @Param("startDay") String startDay, @Param("endDay")String endDay);

    List<CountResult> findBySiteIdAndCreateTiemAndHasReply(@Param("site_id")int site_id, @Param("startDay") String startDay, @Param("endDay")String endDay);

    List<CountResult> findBySiteIdAndCreateTiemAndHasClosed(@Param("site_id")int site_id, @Param("startDay") String startDay, @Param("endDay")String endDay);

    List<CountResult> countClerkTimeOutTime(@Param("site_id")int site_id,@Param("startDay") String startDay, @Param("endDay")String endDay);

    List<ClerkAnalyze> finClerkAnalyzeBySiteIdAndStartTime(ClerkAnalyzeParam param);

    List<Satisfactions> findEvaluateAVGByMonth(@Param("site_id")int site_id, @Param("month")String month);

    List<Satisfactions> findEvaluateAVGByMonthAfter10(@Param("site_id")int site_id, @Param("month")String month);

    BIMService selectByPrimaryKey(Integer imServiceId);

    Integer selectEndTypeByPrimaryKey(Integer imServiceId);

    Integer checkIMEndAndIMReced(Integer imServiceId);

    Integer findJoinIMClerkNum(@Param("site_id")Integer site_id,@Param("end_day") String end_day);

    List<Map<String,Object>> getAdvisoryByDays(@Param("site_id") Integer site_id, @Param("dates") Map<String, Date> dates);
}
