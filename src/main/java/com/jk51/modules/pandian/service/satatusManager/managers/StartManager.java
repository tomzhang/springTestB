package com.jk51.modules.pandian.service.satatusManager.managers;

import com.jk51.modules.pandian.mapper.BPandianOrderStatusMapper;
import com.jk51.modules.pandian.param.StatusParam;
import com.jk51.modules.pandian.util.OperateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-11-07
 * 修改记录:
 */
@Component
public class StartManager {

    @Autowired
    private BPandianOrderStatusMapper bPandianOrderStatusMapper;


    public void updateStatus2Start(List<StatusParam> statusParams, int toStatus,int operateType){


        switch (operateType){
            case OperateType.ADMIN:

                updateStatus2StartByAdmin(statusParams,toStatus);
                break;
            case OperateType.MANAGER:

                updateStatus2StartByManager(statusParams,toStatus);
                break;
            case OperateType.STOREADMIN:

                updateStatus2StartByStoreAdmin(statusParams,toStatus);
                break;
            default:
                throw new IllegalArgumentException("updateStatus2Audit operateType: "+operateType+" 未定义");
        }


    }


    private void updateStatus2StartByAdmin(List<StatusParam> statusParams, int toStatus){

        for(StatusParam s:statusParams){
            bPandianOrderStatusMapper.updateStatus2StartByAdmin(s.getStoreId(),s.getPandian_num(),toStatus);
        }
    }

    private void updateStatus2StartByManager(List<StatusParam> statusParams, int toStatus){

        for(StatusParam s:statusParams){
            bPandianOrderStatusMapper.updateStatus2StartByManager(s.getStoreId(),s.getStoreAdminId(),s.getPandian_num(),toStatus);
        }
    }

    private void updateStatus2StartByStoreAdmin(List<StatusParam> statusParams, int toStatus){

        for(StatusParam s:statusParams){
            bPandianOrderStatusMapper.updateStatus2StartByStoreAdmin(s.getStoreId(),s.getStoreAdminId(),s.getPandian_num(),toStatus);
        }
    }
}
