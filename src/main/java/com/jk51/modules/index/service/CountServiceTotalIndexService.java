package com.jk51.modules.index.service;

import com.jk51.commons.date.DateUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.packageEntity.StoreAdminIndex;
import com.jk51.model.Target;
import com.jk51.model.packageEntity.TargetName;
import com.jk51.modules.im.service.IMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-17
 * 修改记录:
 * 历史服务人数指标
 */
@Service
public class CountServiceTotalIndexService {

    @Autowired
    private IMService iMService;
    @Autowired
    private CountIndexService countIndexService;
    @Autowired
    private TargetService targetService;

    /**
     *历史服务人数指标值计算
     *
     * */
    public void countServiceTotalIndex(List<StoreAdminIndex> storeAdminIndexList, Map<String, Integer> serviceTotal){


         //设置店员30天的服务人数、設置指標
        setIMRecodeNum(storeAdminIndexList,serviceTotal);

        Map<Integer,List<StoreAdminIndex>> siteMap = getSiteStoreAdminIndexMap(storeAdminIndexList);
        for (Map.Entry<Integer, List<StoreAdminIndex>> entry : siteMap.entrySet()) {
            ladderServiceTotalIndex( entry.getValue());
        }

    }

    /**
     *店员分阶梯
     *
     * */
    private void ladderServiceTotalIndex(List<StoreAdminIndex> sdiList){

        //按服务人数排序
        Collections.sort(sdiList, new CampareServiceTotalNum());


        //20%+3
        int firstSize = sdiList.size();
        int firstIndex = (int)Math.ceil(firstSize*0.2);
        List<StoreAdminIndex> first = sdiList.subList(0,firstIndex);
        setServiceTotalIndex(first,3);

        //40%+2
        int secondIndex = (int)Math.ceil(firstSize*0.4);
        List<StoreAdminIndex> second = sdiList.subList(firstIndex,secondIndex);
        setServiceTotalIndex(second,2);

        //60%+1
        int thirdIndex = (int)Math.ceil(firstSize*0.6);
        List<StoreAdminIndex> third = sdiList.subList(secondIndex,thirdIndex);
        setServiceTotalIndex(third,1);

        //80%+0.5
        int fourthIndex = (int)Math.ceil(firstSize*0.8);
        List<StoreAdminIndex> fourth = sdiList.subList(thirdIndex,fourthIndex);
        setServiceTotalIndex(fourth,0.5);
    }

    /**
     *设置指标分
     *
     */
    private void setServiceTotalIndex(List<StoreAdminIndex> list,double num){
        if(!list.isEmpty()){
            for(StoreAdminIndex sai:list){
               // initIndexValue(sai);
                sai.setServiceTotalIndex(sai.getServiceTotalIndex()+num);
            }
        }
    }

   /* *//**
     *初始化分数(以指标初始化值参数初始化分数)
     * *//*
    private void initIndexValue(StoreAdminIndex index){
        List<Target> targetList = index.getTargetList();
        Target target = countIndexService.getTarget(targetList,TargetName.SERVICE_TOTAL_INDEX);
        index.setServiceTotalIndex(target.getInitial_value());
    }*/

    /**
     * 店员按商家分类
     */
    private Map<Integer,List<StoreAdminIndex>> getSiteStoreAdminIndexMap(List<StoreAdminIndex> storeAdminIndexList){

        Map<Integer,List<StoreAdminIndex>> siteStoreAdminIndexMap = new HashMap<Integer,List<StoreAdminIndex>>();
        if(!storeAdminIndexList.isEmpty()){
            for(StoreAdminIndex sai:storeAdminIndexList){
                List<StoreAdminIndex> indexList = siteStoreAdminIndexMap.get(sai.getSite_id());
                if(indexList!=null && indexList.size()>0){
                    List<StoreAdminIndex> sdiList = siteStoreAdminIndexMap.get(sai.getSite_id());
                    sdiList.add(sai);
                    siteStoreAdminIndexMap.put(sai.getSite_id(),sdiList);
                }else{
                    List<StoreAdminIndex> sdiList = new ArrayList<StoreAdminIndex>();
                    sdiList.add(sai);
                    siteStoreAdminIndexMap.put(sai.getSite_id(),sdiList);
                }
            }
        }

        return siteStoreAdminIndexMap;
    }

    /**
     *  设置店员30天的服务人数、設置指標
     * */
    private void setIMRecodeNum(List<StoreAdminIndex> storeAdminIndexList, Map<String, Integer> serviceTotal){

        if(!storeAdminIndexList.isEmpty()){
            for(StoreAdminIndex index:storeAdminIndexList){
                index.setServiceTotalNum(StringUtil.isEmpty(serviceTotal.get(index.getUser_name()))?0:serviceTotal.get(index.getUser_name()));

                //获取指标参数
               // index.setTargetList(targetService.getTargetBySiteId(100002));
            }
        }
    }

    /**
     *查询店员30天的服务人数
     * */
    private int findIMRecodeNum(String user_name){

        Date now = new Date();
        Date beforeDate = DateUtils.getBeforeOrAfterDate(now,-30);
        return iMService.findIMRecodeNumByTiemScope(now,beforeDate,user_name);

    }

    private final class CampareServiceTotalNum implements Comparator<StoreAdminIndex>{

        @Override
        public int compare(StoreAdminIndex o1, StoreAdminIndex o2) {
            int numa = o1.getServiceTotalNum();
            int numb = o2.getServiceTotalNum();
            if (numa < numb)
                return 1;
            else if (numb == numb)
                return 0;
            else
                return -1;
        }
    }
}
