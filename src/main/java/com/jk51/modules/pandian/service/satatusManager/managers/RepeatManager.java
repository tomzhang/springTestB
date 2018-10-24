package com.jk51.modules.pandian.service.satatusManager.managers;



/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-11-14
 * 修改记录:
 */
public class RepeatManager {

    //ps:由于不需要显示复盘操作的人员，也就不许要去记录复盘操作人员的信息，后续如果需要的时候在加
    /*@Autowired
    private BPandianOrderStatusMapper bPandianOrderStatusMapper;


    public void updateStatus2Repeat(List<StatusParam> statusParams, int toStatus, Integer operateType){

        switch (operateType){
            case OperateType.ADMIN:

                updateStatus2RepeatByAdmin(statusParams,toStatus);
                break;
            case OperateType.MANAGER:

                updateStatus2RepeatByManager(statusParams,toStatus);
                break;
            case OperateType.STOREADMIN:

                updateStatus2RepeatByStoreAdmin(statusParams,toStatus);
                break;
            default:
                throw new IllegalArgumentException("updateStatus2Audit operateType: "+operateType+" 未定义");
        }

    }


    private void updateStatus2RepeatByAdmin(List<StatusParam> statusParams,int toStatus){

        for(StatusParam s:statusParams){
            bPandianOrderStatusMapper.updateStatus2RepeatByAdmin(s.getStoreId(),s.getPandian_num(),toStatus);
        }
    }


    private void updateStatus2RepeatByManager(List<StatusParam> statusParams,int toStatus){

        for(StatusParam s:statusParams){
            bPandianOrderStatusMapper.updateStatus2RepeatByManager(s.getStoreId(),s.getStoreAdminId(),s.getPandian_num(),toStatus);
        }
    }

    private void updateStatus2RepeatByStoreAdmin(List<StatusParam> statusParams,int toStatus){

        for(StatusParam s:statusParams){
            bPandianOrderStatusMapper.updateStatus2RepeatByStoreAdmin(s.getStoreId(),s.getStoreAdminId(),s.getPandian_num(),toStatus);
        }
    }*/
}
