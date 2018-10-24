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
public class AuditManager {

    @Autowired
    private BPandianOrderStatusMapper bPandianOrderStatusMapper;


    public void updateStatus2Audit(List<StatusParam> statusParams, int toStatus, Integer operateType){

        switch (operateType){
            case OperateType.ADMIN:

                updateStatus2AuditByAdmin(statusParams,toStatus);
                break;
            case OperateType.MANAGER:

                updateStatus2AuditByManager(statusParams,toStatus);
                break;
            case OperateType.STOREADMIN:

                updateStatus2AuditByStoreAdmin(statusParams,toStatus);
                break;
                default:
                    throw new IllegalArgumentException("updateStatus2Audit operateType: "+operateType+" 未定义");
        }

    }


    private void updateStatus2AuditByAdmin(List<StatusParam> statusParams,int toStatus){

        for(StatusParam s:statusParams){
            bPandianOrderStatusMapper.updateStatus2AuditByAdmin(s.getStoreId(),s.getPandian_num(),toStatus);
        }
    }


    private void updateStatus2AuditByManager(List<StatusParam> statusParams,int toStatus){

        for(StatusParam s:statusParams){
            bPandianOrderStatusMapper.updateStatus2AuditByManager(s.getStoreId(),s.getStoreAdminId(),s.getPandian_num(),toStatus);
        }
    }

    private void updateStatus2AuditByStoreAdmin(List<StatusParam> statusParams,int toStatus){

        for(StatusParam s:statusParams){
            bPandianOrderStatusMapper.updateStatus2AuditByStoreAdmin(s.getStoreId(),s.getStoreAdminId(),s.getPandian_num(),toStatus);
        }
    }
}
