package com.jk51.modules.index.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.Target;
import com.jk51.model.TargetRecode;
import com.jk51.model.packageEntity.StoreAdminIndex;
import com.jk51.model.packageEntity.TargetIndexValue;
import com.jk51.model.packageEntity.TargetName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-22
 * 修改记录:
 */
@Service
public class IndexInitService {

    @Autowired
    private CountIndexService countIndexService;

    private static final Logger logger = LoggerFactory.getLogger(IndexInitService.class);


    //各指标设置初始值
    public void initIndex(StoreAdminIndex index, Map<Integer, TargetRecode> targetRecodeMap){

        //指标记录
        TargetRecode recode = targetRecodeMap.get(index.getTarget_record_id());

        //targetRecode json 字符串转bean
        TargetIndexValue value = null;
        if(recode!=null && !StringUtil.isNumber(recode.getTarget_record())){
            value = targetRecodeJsonToBean(recode.getTarget_record());
        }


        initAllIndex(index,value);

    }


    //初始化所有的参数
    private void initAllIndex(StoreAdminIndex index,TargetIndexValue value){
        List<Target> targetList = index.getTargetList();

        //门店属性-店员数量
        Target storeAttributesClerkQuantityTarget = countIndexService.getTarget(targetList, TargetName.STORE_ATTRIBUTES_CLERK_QUANTITY_INDEX);
        index.setStoreAttributesClerkQuantityIndex(storeAttributesClerkQuantityTarget.getInitial_value());

        //门店属性-订单量
        Target storeAttributesOrderquantityTarget = countIndexService.getTarget(targetList, TargetName.STORE_ATTRIBUTES_ORDERQUANTITY_INDEX);
        index.setStoreAttributesOrderquantityIndex(storeAttributesOrderquantityTarget.getInitial_value());

        //门店属性-旗舰店指标
        Target storeAttributesFlagshipstoreTarget = countIndexService.getTarget(targetList, TargetName.STORE_ATTRIBUTES_FLAGSHIPSTORE_INDEX);
        index.setStoreAttributesFlagshipstoreIndex(storeAttributesFlagshipstoreTarget.getInitial_value());

        //初始店员下单量指标------没有使用StoreAdminIndex类与初始店员下单量指标对应，初始化在对应的service中做
        //Target orderNumTarget = countIndexService.getTarget(targetList, TargetName.ORDER_NUM_INDEX);
        //index.setOrderNumIndex(orderNumTarget.getInitial_value());

        //与用户的历史数据（交集指标）------没有使用StoreAdminIndex类与初始店员下单量指标对应，初始化在对应的service中做
       // Target intersectionTarget = countIndexService.getTarget(targetList, TargetName.INTERSECTION_INDEX);
        //index.setIntersectionIndex(intersectionTarget.getInitial_value());

        //初始历史服务人数指标
        Target serviceTotalTarget = countIndexService.getTarget(targetList, TargetName.SERVICE_TOTAL_INDEX);
        index.setServiceTotalIndex(serviceTotalTarget.getInitial_value());

        //初始满意度指标
        if(value==null){
            Target satisfactionTarget = countIndexService.getTarget(targetList, TargetName.SATISFACTION_INDEX);
            index.setSatisfactionIndex(satisfactionTarget.getInitial_value());
        }else{
            index.setSatisfactionIndex(value.getSatisfactionIndex());
        }

        //初始店员职称指标
        Target titleTarget = countIndexService.getTarget(targetList, TargetName.TITLE_INDEX);
        index.setTitleIndex(titleTarget.getInitial_value());

        //初始抢答后繁忙度
        if(value==null){
            Target busyTarget = countIndexService.getTarget(targetList, TargetName.BUSY_INDEX);
            index.setBusyIndex(busyTarget.getInitial_value());
        }else{
            index.setBusyIndex(value.getBusyIndex());
        }

        //初始历史抢答速度指标
        if(value==null){
            Target historyAnswerSpeedTarget = countIndexService.getTarget(targetList, TargetName.HISTORY_ANSWER_SPEED_INDEX);
            index.setHistoryAnswerSpeedIndex(historyAnswerSpeedTarget.getInitial_value());
        }else{
            index.setHistoryAnswerSpeedIndex(value.getHistoryAnswerSpeedIndex());
        }


    }


    //targetRecode json 字符串转bean
    public TargetIndexValue targetRecodeJsonToBean(String jsonStr){

        TargetIndexValue value = null;
        try {
            value = JacksonUtils.json2pojo(jsonStr,TargetIndexValue.class);
        } catch (Exception e) {
            logger.error("targetRecode json 字符串转bean异常",e);
        }
        return value;
    }
}
