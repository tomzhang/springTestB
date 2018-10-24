package com.jk51.modules.index.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.StoreAdminExt;
import com.jk51.model.packageEntity.StoreAdminIndex;
import com.jk51.model.Target;
import com.jk51.model.packageEntity.TargetName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-02-17
 * 修改记录:
 * 员职称指标
 */
@Service
public class CountTitleIndexService {

    //职称常量
    private static final String Professional_Pharmacists = "职业药师";
    private static final String Pharmacist_In_Charge = "主管药师";
    private static final String Small_Pharmacists = "小药师";
    private static final String From_The_Pharmacist = "从业药师";

    @Autowired
    private CountIndexService countIndexService;
    @Autowired
    private StoreAdminExtService storeAdminExtService;

    /**
     *计算店员职称指标分
     *
     * */
    public void countTitleIndexOne(List<StoreAdminIndex> indexList){

        int titleIndex = 0;

        if(StringUtil.isEmpty(indexList)){
            return;
        }

        for(StoreAdminIndex index :indexList){

            //店员未设置职称，职称指标设置为0
            if(StringUtil.isEmpty(index.getClerk_job())){
                index.setTitleIndex(titleIndex);
                return;
            }
            switch (index.getClerk_job()){
                case Professional_Pharmacists:
                    titleIndex = 4;
                    break;
                case Pharmacist_In_Charge:
                    titleIndex = 3;
                    break;
                case From_The_Pharmacist:
                    titleIndex = 2;
                    break;
                case Small_Pharmacists:
                    titleIndex = 1;
                    break;
                default:
                    titleIndex = 0;
            }

            index.setTitleIndex(titleIndex);
        }



    }


}
