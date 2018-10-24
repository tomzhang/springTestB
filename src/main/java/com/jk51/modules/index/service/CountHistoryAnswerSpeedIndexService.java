package com.jk51.modules.index.service;

import com.jk51.commons.date.DateUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.*;
import com.jk51.model.packageEntity.StoreAdminIndex;
import com.jk51.model.packageEntity.TargetIndexValue;
import com.jk51.model.packageEntity.TargetName;
import com.jk51.modules.im.mapper.RaceAnswerRecodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-17
 * 修改记录:
 *
 * 历史抢答速度指标
 */
@Service
public class CountHistoryAnswerSpeedIndexService {

    @Autowired
    private RaceAnswerRecodeMapper raceAnswerRecodeMapper;

    @Autowired
    private CountIndexService countIndexService;

    public void countHistoryAnswerSpeedIndexServiceOne(StoreAdminIndex index, Map<String, List<RaceAnswerRecode>> raceAnswerRecodeMap) {

        //初始化指标值
        //initValue(index);

        //获取1天的抢答记录
        List<RaceAnswerRecode> raceAnswerRecodeList = raceAnswerRecodeMap.get(index.getUser_name());

        //获取指标参数
        List<Target> TargetList = index.getTargetList();
        Target target = countIndexService.getTarget(TargetList, TargetName.HISTORY_ANSWER_SPEED_INDEX);
        double reference_value =  target.getReference_value();

        //参数区间值（指标分数的最大最小参）
        String score_parameter_section = target.getScore_parameter_section();
        Map<String,Double> scoreParameterSectionMaxAndMin = countIndexService.scoreParameterSectionMaxAndMin(score_parameter_section);


        //遍历抢答记录，计算分数
        if(!StringUtil.isEmpty(raceAnswerRecodeList)){
            for(RaceAnswerRecode rar:raceAnswerRecodeList){
                Timestamp sendTime = rar.getSend_race_answer_recode_time();
                Timestamp getTime = rar.getCreate_time();
                long num = DateUtils.getTimeDifference(getTime,sendTime);
                if(num<reference_value){
                    addIndex(index,scoreParameterSectionMaxAndMin.get("max"));
                }else if(num==reference_value){

                }else{
                    subtractIndex(index,scoreParameterSectionMaxAndMin.get("min"));
                }
            }
        }


    }

    //减1分
    private void subtractIndex(StoreAdminIndex index,double minIndex){
        //如果指标值于或等于maxIndex，不在加分
        if(index.getHistoryAnswerSpeedIndex()<=minIndex){
            return;
        }
        index.setHistoryAnswerSpeedIndex(index.getHistoryAnswerSpeedIndex()-1);
    }

    //加1分
    private void addIndex(StoreAdminIndex index,double maxIndex){

        //如果指标值大于或等于maxIndex，不在加分
        if(index.getHistoryAnswerSpeedIndex()>=maxIndex){
            return;
        }
        index.setHistoryAnswerSpeedIndex(index.getHistoryAnswerSpeedIndex()+1);

    }

}
