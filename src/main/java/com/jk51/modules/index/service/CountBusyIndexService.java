package com.jk51.modules.index.service;

import com.jk51.commons.date.DateUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.IMRecode;
import com.jk51.model.RaceAnswerRecode;
import com.jk51.model.Target;
import com.jk51.model.TargetRecode;
import com.jk51.model.packageEntity.StoreAdminIndex;
import com.jk51.model.packageEntity.TargetIndexValue;
import com.jk51.model.packageEntity.TargetName;
import com.jk51.modules.im.mapper.ImRecodeMapper;
import com.jk51.modules.im.mapper.RaceAnswerRecodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-20
 * 修改记录:
 * 抢答繁忙度
 */
@Service
public class CountBusyIndexService {

    @Autowired
    private RaceAnswerRecodeMapper raceAnswerRecodeMapper;
    @Autowired
    private ImRecodeMapper iMRecodeMapper;
    @Autowired
    private CountIndexService countIndexService;


    public void CountBusyIndexOnde(StoreAdminIndex index, Map<String, List<RaceAnswerRecode>> raceAnswerRecodeMap){

        //初始化指標分
        //initValue(index);

        //查询1天中抢答到的记录
        List<RaceAnswerRecode> raceAnswerRecodeList = raceAnswerRecodeMap.get(index.getUser_name());

        //根据抢答记录查询时间最小的一条回复记录,并计分
        List<IMRecode> iMRecodeList = getImRecodeByReceiverAndSender(raceAnswerRecodeList,index);
    }

    //根据抢答记录查询时间最小的一条回复记录
    private List<IMRecode> getImRecodeByReceiverAndSender( List<RaceAnswerRecode> raceAnswerRecodeList,StoreAdminIndex index){
        List<IMRecode> result = new ArrayList<IMRecode>();

        if(StringUtil.isEmpty(raceAnswerRecodeList)){
            return result;
        }

        //遍历抢答记录，查询相应的时间最小的一条聊天记录，比较抢答时间与回复时间的为回复速度，回复速度大于参考速度则加分，否则减分
        for(RaceAnswerRecode rar:raceAnswerRecodeList){
            IMRecode imRecode =  iMRecodeMapper.findImRecodeByReceiverAndSender(rar.getReceiver(),rar.getSender());
            if(imRecode!=null){
                compareDate(rar,imRecode,index);
            }
        }

        return result;
    }


    private void compareDate(RaceAnswerRecode rar,IMRecode imRecode,StoreAdminIndex index){

        long num = DateUtils.getTimeDifference(new Timestamp(imRecode.getCreate_time().getTime()),rar.getCreate_time());

        //获取指标参考速度
        List<Target> TargetList = index.getTargetList();
        Target target = countIndexService.getTarget(TargetList, TargetName.BUSY_INDEX);
        double reference_value =  target.getReference_value() * 60;

        //参数区间值（指标分数的最大最小参）
        String score_parameter_section = target.getScore_parameter_section();
        Map<String,Double> scoreParameterSectionMaxAndMin = countIndexService.scoreParameterSectionMaxAndMin(score_parameter_section);

        if(num <reference_value){
            addIndex(index,scoreParameterSectionMaxAndMin.get("max"));
        }else if(num ==reference_value){

        }else{
            subtractIndex(index,scoreParameterSectionMaxAndMin.get("min"));
        }

    }

    //加1分
    private void addIndex(StoreAdminIndex index,double maxIndex){
        //如果指标值大于或等于maxIndex，不在加分
        if(index.getBusyIndex()>=maxIndex){
            return;
        }
        index.setBusyIndex(index.getBusyIndex()+1);
    }

    //减1分
    private void subtractIndex(StoreAdminIndex index,double minIndex){
        //如果指标值于或等于maxIndex，不在加分
        if(index.getBusyIndex()<=minIndex){
            return;
        }
        index.setBusyIndex(index.getBusyIndex()-1);
    }

    /**
     *初始化指标值（查询最新指标记录，如果有设为初始值，如果没有，去指标参数中的初始化值）
     * *//*
    private void initValue(StoreAdminIndex index){
        TargetRecode targetRecode =  countIndexService.getTargetRecode(index);
        if(targetRecode != null){
            String target_record = targetRecode.getTarget_record();
            try {
                TargetIndexValue value = JacksonUtils.json2pojo(target_record, TargetIndexValue.class);
                index.setBusyIndex(value.getBusyIndex());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{

            List<Target> TargetList = index.getTargetList();
            Target target = countIndexService.getTarget(TargetList, TargetName.BUSY_INDEX);
            index.setHistoryAnswerSpeedIndex(target.getInitial_value());
        }
    }*/
}
