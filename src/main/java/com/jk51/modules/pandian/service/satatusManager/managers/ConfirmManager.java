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
public class ConfirmManager {


    @Autowired
    private BPandianOrderStatusMapper bPandianOrderStatusMapper;


    public void updateStatus2Confirm(List<StatusParam> statusParams, int toStatus, Integer operateType){

        switch (operateType){
            case OperateType.ADMIN:

                updateStatus2ConfirmByAdmin(statusParams,toStatus);
                break;
            case OperateType.MANAGER:

                updateStatus2ConfirmByManager(statusParams,toStatus);
                break;
            case OperateType.STOREADMIN:

                updateStatus2ConfirmByStoreAdmin(statusParams,toStatus);
                break;
            default:
                throw new IllegalArgumentException("updateStatus2Audit operateType: "+operateType+" 未定义");
        }

    }


    private void updateStatus2ConfirmByAdmin(List<StatusParam> statusParams,int toStatus){

        for(StatusParam s:statusParams){
            bPandianOrderStatusMapper.updateStatus2ConfirmByAdmin(s.getStoreId(),s.getPandian_num(),toStatus);
        }
    }


    private void updateStatus2ConfirmByManager(List<StatusParam> statusParams,int toStatus){

        for(StatusParam s:statusParams){
            bPandianOrderStatusMapper.updateStatus2ConfirmByManager(s.getStoreId(),s.getStoreAdminId(),s.getPandian_num(),toStatus);
        }
    }

    private void updateStatus2ConfirmByStoreAdmin(List<StatusParam> statusParams,int toStatus){

        for(StatusParam s:statusParams){
            bPandianOrderStatusMapper.updateStatus2ConfirmByStoreAdmin(s.getStoreId(),s.getStoreAdminId(),s.getPandian_num(),toStatus);
        }
    }
}
