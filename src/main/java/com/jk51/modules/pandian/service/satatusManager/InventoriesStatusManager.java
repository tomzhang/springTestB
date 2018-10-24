package com.jk51.modules.pandian.service.satatusManager;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.model.BStores;
import com.jk51.modules.appInterface.mapper.BStoresMapper;
import com.jk51.modules.pandian.Response.StatusResponse;
import com.jk51.modules.pandian.elasticsearch.repositories.InventoryRepository;
import com.jk51.modules.pandian.job.PandianSortJob;
import com.jk51.modules.pandian.mapper.BPandianOrderStatusMapper;
import com.jk51.modules.pandian.mapper.InventoriesMapper;
import com.jk51.modules.pandian.param.PandianStatusUpdateParam;
import com.jk51.modules.pandian.param.StatusParam;
import com.jk51.modules.pandian.service.PandianAsyncService;
import com.jk51.modules.pandian.service.satatusManager.managers.AuditManager;
import com.jk51.modules.pandian.service.satatusManager.managers.ConfirmManager;
import com.jk51.modules.pandian.service.satatusManager.managers.StartManager;
import com.jk51.mq.mns.CloudQueueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.jk51.modules.pandian.util.StatusConstant.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-11-03
 * 修改记录:
 */

@Component
public class InventoriesStatusManager {

    @Autowired
    private BPandianOrderStatusMapper bPandianOrderStatusMapper;
    @Autowired
    private BStoresMapper bStoresMapper;
    @Autowired
    private AuditManager auditManager;
    @Autowired
    private StartManager startManager;
    @Autowired
    private ConfirmManager confirmManager;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private PandianAsyncService pandianAsyncService;
    @Autowired
    private InventoriesMapper inventoriesMapper;

    private Logger logger = LoggerFactory.getLogger(InventoriesStatusManager.class);

    /**
     * 更新盘点状态
     * (下发，复盘，审核)
     * 返回更新失败的信息
     * */
    public List<StatusResponse> updatePandianStatus(PandianStatusUpdateParam param){


        switch (param.getTo_status()){
            case WAITCONFIRM:
                return status2Start(param);
            case WAITAUDIT:
                return status2Confirm(param);
            case AUDITED:
                return status2Audit(param);
            case REPEATION:
                return status2Repeat(param);
            case CLOSE:
                return status2Close(param);
            default:
                throw new IllegalArgumentException("更新状态未定义");

        }
    }

    //盘点关闭
    private List<StatusResponse> status2Close(PandianStatusUpdateParam param) {

        List<StatusParam> fialStatusParam = getStatusFailParamForClocse(param.getStatusParams());

        updateStatus2Close(filterFailStatusParam(param.getStatusParams(),fialStatusParam),param.getTo_status());

        return getfailStatusResponseForClose(fialStatusParam);
    }


    //更新盘点状态
    private void updateStatus2Close(List<StatusParam> statusParams, Integer to_status) {

        for(StatusParam s :statusParams){
            bPandianOrderStatusMapper.updateStatus2Repeat(s.getStoreId(),s.getPandian_num(),to_status);
        }
    }

    //监盘确认
    private List<StatusResponse> status2Confirm(PandianStatusUpdateParam param) {


        //获取监盘确认状态错误的StatusParam
        List<StatusParam> fialStatusParam = getStatusFailParamForConfirm(param.getStatusParams());

        //过滤掉盘点单状态错误的的StatusParam
        List<StatusParam> correctStatusParam =  filterFailStatusParam(param.getStatusParams(),fialStatusParam);

        //获取盘点单中还存在没有盘点确认的盘点单
        List<StatusParam> hasNotStoreAdminConfirmStatusParam = getHasNotStoreAdminConfirmStatusParam(correctStatusParam);

        //过滤掉盘点单还存在没有盘点确认的盘点单
        List<StatusParam> hasStoreAdminConfirmStatusParam = filterFailStatusParam(correctStatusParam,hasNotStoreAdminConfirmStatusParam);

        //更新满足条件的盘点单状态
        confirmManager.updateStatus2Confirm(hasStoreAdminConfirmStatusParam,param.getTo_status(),param.getOperateType());


        //获取监盘确认状态错误的StatusResponse
        List<StatusResponse> fialStatusResponse =   getfailStatusResponseForConfirm(fialStatusParam);

        //获取盘点单中还存在没有盘点确认的StatusResponse
        List<StatusResponse> hasNotStoreAdminConfirmStatusResponse = getHasNotStoreAdminConfirmStatusResponse(hasNotStoreAdminConfirmStatusParam);

        fialStatusResponse.addAll(hasNotStoreAdminConfirmStatusResponse);
        return fialStatusResponse;
    }



    //下发盘点
    private List<StatusResponse> status2Start(PandianStatusUpdateParam param){

        List<StatusParam> fialStatus = getStatusFailParam(param.getStatusParams(),WAITSTART);
        List<StatusParam> pandianDetailEmprtyParam = getPandianDetailEmprtyParam(param.getStatusParams());

        List<StatusParam> filterFailStatusParam = filterFailStatusParam(param.getStatusParams(),fialStatus);
        List<StatusParam> filterPandianDetailEmptyParam = filterFailStatusParam(filterFailStatusParam,pandianDetailEmprtyParam);

        startManager.updateStatus2Start(filterPandianDetailEmptyParam,param.getTo_status(),param.getOperateType());

        List<StatusResponse> result =  getfailStatusResponse(fialStatus,WAITSTART);

        result.addAll(getPandianDetailEmprtyResponse(pandianDetailEmprtyParam));

        return result;
    }

    //获取盘点明细为空的盘点单的放回信息集合
    private List<StatusResponse> getPandianDetailEmprtyResponse(List<StatusParam> pandianDetailEmprtyParam) {

        List<StatusResponse> result = new ArrayList<>();
        for(StatusParam s:pandianDetailEmprtyParam){

            StatusResponse response = new StatusResponse();
            response.setSiteId(s.getSiteId());
            response.setStoreId(s.getStoreId());
            response.setPandian_num(s.getPandian_num());
            response.setErrorMessage("盘点单["+s.getPandian_num()+"]中没有盘点明细，不能下发");
            BStores bStores = bStoresMapper.selectByPrimaryKey(s.getSiteId(),s.getStoreId());

            if(!StringUtil.isEmpty(bStores)){
                response.setStoreName(bStores.getName());
            }

            result.add(response);
        }

        return result;
    }

    //获取盘点单中无数据的StatusParam
    private List<StatusParam> getPandianDetailEmprtyParam(List<StatusParam> statusParams) {

        List<StatusParam> result = new ArrayList<>();

        for(StatusParam s:statusParams){
            Integer pandianDetailCount = inventoriesMapper.getPandianDetailCount(s.getPandian_num(),s.getStoreId());

            if(pandianDetailCount==0){
                result.add(s);
            }
        }

        return result;
    }

    //复盘
    private List<StatusResponse> status2Repeat(PandianStatusUpdateParam param){

        List<StatusParam> fialStatus = getStatusFailParam(param.getStatusParams(),WAITAUDIT);

        updateStatus2RepeatAndRestStoreAdminConfim(filterFailStatusParam(param.getStatusParams(),fialStatus),param.getTo_status());

        return getfailStatusResponse(fialStatus,WAITAUDIT);
    }

    //审核
    private List<StatusResponse> status2Audit(PandianStatusUpdateParam param){

        List<StatusParam> fialStatus = getStatusFailParam(param.getStatusParams(),WAITAUDIT);

        auditManager.updateStatus2Audit(filterFailStatusParam(param.getStatusParams(),fialStatus),param.getTo_status(),param.getOperateType());

        List<StatusResponse> statusResponses =  getfailStatusResponse(fialStatus,WAITAUDIT);

        //审核成功时发送盘点排序消息
        if(StringUtil.isEmpty(statusResponses)){
            sendPandianSortMessage2MQ(param.getStatusParams().get(0));
        }

        return statusResponses;
    }


    private void sendPandianSortMessage2MQ(StatusParam statusParams)  {

        try {

            CloudQueue queue =  CloudQueueFactory.create(PandianSortJob.MQ_TOPIC_PANDIAN_SORT);
            Message message = new Message(JacksonUtils.obj2json(statusParams).getBytes());
            queue.putMessage(message);
        } catch (Exception e) {
            logger.error("发送盘点排序消息失败：statusParams：{},报错信息：{}",statusParams,ExceptionUtil.exceptionDetail(e));
        }
    }

    //1.更新状态
    //2.重置盘点单中盘点有差异的盘点数据，店员盘点确认为未盘点确认
    private void updateStatus2RepeatAndRestStoreAdminConfim(List<StatusParam> statusParams,int toStatus){

        for(StatusParam s:statusParams){
            bPandianOrderStatusMapper.updateStatus2Repeat(s.getStoreId(),s.getPandian_num(),toStatus);
            inventoryRepository.restInventoryonfirm(s.getStoreId(),s.getPandian_num());
            pandianAsyncService.asyncRestInventoryonfirm(s.getStoreId(),s.getPandian_num());
        }


    }




    //过滤掉不满足更新状态的参数
    private List<StatusParam> filterFailStatusParam(List<StatusParam> statusParams,List<StatusParam> fialStatus){

        List<StatusParam> statusParamsCopy = new ArrayList<>(statusParams);
        statusParamsCopy.removeAll(fialStatus);
        return statusParamsCopy;
    }

    //判断状态是否一致,返回不一致的信息
    private List<StatusParam> getStatusFailParam(List<StatusParam> statusParams, int status){

        List<StatusParam> fialStatus = new ArrayList<>();
        for(StatusParam s:statusParams){
           Integer oldStatus =  bPandianOrderStatusMapper.getStatus(s);
           if(StringUtil.isEmpty(oldStatus)||oldStatus!=status){
               fialStatus.add(s);
           }
        }

        return fialStatus;
    }


    private List<StatusParam> getStatusFailParamForClocse(List<StatusParam> statusParams) {

        List<StatusParam> fialStatus = new ArrayList<>();
        for(StatusParam s:statusParams){
            Integer oldStatus =  bPandianOrderStatusMapper.getStatus(s);
            if(StringUtil.isEmpty(oldStatus)||oldStatus==AUDITED||oldStatus==CLOSE){
                fialStatus.add(s);
            }
        }

        return fialStatus;
    }


    private List<StatusParam> getStatusFailParamForConfirm(List<StatusParam> statusParams) {

        List<StatusParam> fialStatus = new ArrayList<>();
        for(StatusParam s:statusParams){
            Integer oldStatus =  bPandianOrderStatusMapper.getStatus(s);
            if(StringUtil.isEmpty(oldStatus)||(oldStatus!=WAITCONFIRM&&oldStatus!=REPEATION)){
                fialStatus.add(s);
            }
        }

        return fialStatus;
    }


    //获取状态更新失败的信息
    private List<StatusResponse> getfailStatusResponse(List<StatusParam> fialStatus,int status){

        List<StatusResponse> statusResponse = new ArrayList<>();
        for(StatusParam s:fialStatus){

            StatusResponse response = new StatusResponse();
            response.setSiteId(s.getSiteId());
            response.setStoreId(s.getStoreId());
            response.setPandian_num(s.getPandian_num());
            response.setErrorMessage("状态需要是("+getStatusDes(status)+")");
            BStores bStores = bStoresMapper.selectByPrimaryKey(s.getSiteId(),s.getStoreId());

            if(!StringUtil.isEmpty(bStores)){
                response.setStoreName(bStores.getName());
            }

            statusResponse.add(response);
        }



        return statusResponse;

    }

    //获取状态更新失败的信息
    private List<StatusResponse> getfailStatusResponseForClose(List<StatusParam> fialStatus){

        List<StatusResponse> statusResponse = new ArrayList<>();
        for(StatusParam s:fialStatus){

            StatusResponse response = new StatusResponse();
            response.setSiteId(s.getSiteId());
            response.setStoreId(s.getStoreId());
            response.setPandian_num(s.getPandian_num());
            response.setErrorMessage("状态为(已审核或关闭)不能关闭");
            BStores bStores = bStoresMapper.selectByPrimaryKey(s.getSiteId(),s.getStoreId());

            if(!StringUtil.isEmpty(bStores)){
                response.setStoreName(bStores.getName());
            }

            statusResponse.add(response);
        }



        return statusResponse;

    }



    //获取状态更新失败的信息
    private List<StatusResponse> getfailStatusResponseForConfirm(List<StatusParam> fialStatus){

        List<StatusResponse> statusResponse = new ArrayList<>();
        for(StatusParam s:fialStatus){

            StatusResponse response = new StatusResponse();
            response.setSiteId(s.getSiteId());
            response.setStoreId(s.getStoreId());
            response.setPandian_num(s.getPandian_num());
            response.setErrorMessage("状态需要是(待确认或者复盘)");
            BStores bStores = bStoresMapper.selectByPrimaryKey(s.getSiteId(),s.getStoreId());

            if(!StringUtil.isEmpty(bStores)){
                response.setStoreName(bStores.getName());
            }

            statusResponse.add(response);
        }



        return statusResponse;

    }

   //获取盘点状态的描述
    private String getStatusDes(int status){

        switch (status){
            case WAITUPLOAD:
                return "待上传";
            case WAITSTART:
                return "待下发";
            case WAITCONFIRM:
                return "待确认";
            case WAITAUDIT:
                return "待审核";
            case AUDITED:
                return "已审核";
            case REPEATION:
                return "复盘";
            default:
                throw new IllegalArgumentException("错误的状态："+status);
        }

    }

    //监盘人确认时，判断还有库存没有确认的盘点单
    private List<StatusParam> getHasNotStoreAdminConfirmStatusParam(List<StatusParam> statusParams){

        List<StatusParam> result = new ArrayList<>();
        for(StatusParam s:statusParams){

          Integer num = inventoryRepository.getHasNotStoreAdminConfirmNum(s.getPandian_num(),s.getStoreId());
          if((!StringUtil.isEmpty(num))&&num!=0){
              result.add(s);
          }
        }

        return result;
    }



    private List<StatusResponse> getHasNotStoreAdminConfirmStatusResponse(List<StatusParam> statusParams) {

        List<StatusResponse> result = new ArrayList<>();
        for(StatusParam s:statusParams){

            StatusResponse response = new StatusResponse();
            response.setSiteId(s.getSiteId());
            response.setStoreId(s.getStoreId());
            response.setPandian_num(s.getPandian_num());
            response.setErrorMessage("盘点单["+s.getPandian_num()+"]中还有未盘点确认的数据");
            BStores bStores = bStoresMapper.selectByPrimaryKey(s.getSiteId(),s.getStoreId());

            if(!StringUtil.isEmpty(bStores)){
                response.setStoreName(bStores.getName());
            }

            result.add(response);
        }

        return result;
    }

}
