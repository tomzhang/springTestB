package com.jk51.modules.im.mapper;

import com.jk51.model.SendRaceAnswerRecode;
import com.jk51.modules.im.util.RLMessageParameter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-16
 * 修改记录:
 */
@Mapper
public interface SendRaceAnswerRecodeMapper {
    int insertSelective(SendRaceAnswerRecode sendRaceAnswerRecode);

    List<SendRaceAnswerRecode> getSendRaceAnswerRecode(RLMessageParameter param);


}
