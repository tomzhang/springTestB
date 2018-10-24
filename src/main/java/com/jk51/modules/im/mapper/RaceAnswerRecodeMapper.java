package com.jk51.modules.im.mapper;

import com.jk51.model.RaceAnswerRecode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-16
 * 修改记录:
 */
@Mapper
public interface RaceAnswerRecodeMapper {

    List<RaceAnswerRecode> getRaceAnswerRecode(@Param("sender")String sender, @Param("msg")String msg);

    int insertSelective(RaceAnswerRecode raceAnswerRecode);

    /**
     *查询抢答记录，不管是否成功
     *
     * */
    List<RaceAnswerRecode> getRaceAnswerRecodeByReceiver(@Param("user_name")String user_name,@Param("now") Date now,@Param("beforeTime") Date beforeTime);

    /**
     *查询抢答成功的记录
     * */
    List<RaceAnswerRecode> getRaceAnswerRecodeByOneDay(@Param("user_name")String user_name, @Param("now")Date now, @Param("beforeTime")Date before);

    RaceAnswerRecode getByPrimarykey(String msg_id);

    //根据时间间隔查询抢答记录
    List<RaceAnswerRecode> getRaceAnswerRecodeBytime(@Param("now")Date now, @Param("beforeTime")Date beforeTime);
}
