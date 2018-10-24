package com.jk51.modules.im.netease.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.model.netease.NeteaseAccid;
import com.jk51.modules.distribution.result.Result;
import com.jk51.modules.im.controller.request.CreateAccidParam;
import com.jk51.modules.im.netease.NeteaseIm;
import com.jk51.modules.im.netease.Util;
import com.jk51.modules.im.netease.dao.NetaeaseAccidDao;
import com.jk51.modules.im.netease.mapper.SendMsgResMapper;
import com.jk51.modules.im.netease.response.BatchSendMsgRes;
import com.jk51.modules.im.netease.response.SendMsgRes;
import com.jk51.modules.im.netease.response.WechatCreateAccidRes;
import com.jk51.modules.wechat.service.ChAnswerRelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static com.jk51.modules.im.netease.Constant.SUCCESS;


/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/9/1
 * 修改记录:
 */
@Service
public class NeteaseService {

    private Logger logger = LoggerFactory.getLogger(NeteaseService.class);

    @Autowired
    private NetaeaseAccidDao netaeaseAccidDao;
    @Autowired
    private NeteaseIm neteaseIm;
    @Autowired
    private Util util;
    @Autowired
    private SendMsgResMapperService sendMsgResMapperService;
    @Autowired
    private BatchSendMsgResMapperService batchSendMsgResService;
    @Autowired
    private ChAnswerRelationService chAnswerRelationService;
    public NeteaseAccid getNeteaseAccid(String accid){

        NeteaseAccid neteaseAccid = netaeaseAccidDao.getNeteaseAccid(accid);

        if(neteaseAccid == null){
            neteaseAccid =  neteaseIm.creatAccid(accid);

            if(neteaseAccid.isActice()){
                netaeaseAccidDao.insertNeteaseAccid(neteaseAccid);
            }

        }

        return neteaseAccid;
    }


    public Result createAccid(CreateAccidParam param) {

        WechatCreateAccidRes res = new WechatCreateAccidRes();
        res.setNeteaseActive(util.neteaseActive());
        if(util.neteaseActive()){
            res.setNeteaseAccid(getNeteaseAccid(param.getAccid()));
        }
        res.setReceiver(chAnswerRelationService.getReceiverBySender(param.getAccid()));

        return Result.success(res);

    }


    public Map<String,Object> sendMsg(String from,String to,String msgContent) {
        Map<String,Object> result = new HashMap();

        if(isBatchMsg(to)){
            String[] toes = to.split(",");
            try {
                batchSend(from,toes,msgContent);
                result.put("status","OK");
            } catch (Exception e) {
                logger.error("batchSend 报错，报错信息：{}",ExceptionUtil.exceptionDetail(e));
                result.put("status","ERROR");
                result.put("message",ExceptionUtil.exceptionDetail(e));
            }

            return result;
        }


        SendMsgRes res = singleSend(from,to,msgContent);
        if(res.getCode() == SUCCESS){
            result.put("status","OK");
        }else {
            result.put("status","ERROR");
        }

        return result;
    }

    private boolean isBatchMsg(String to){

        if(StringUtil.isEmpty(to)){
            return false;
        }
        return to.contains(",");

    }

    private SendMsgRes singleSend(String from,String to,String msgContent){

        SendMsgRes res =  neteaseIm.sendMsg(util.getSendMsgParam(from,to,msgContent));
        logger.info("singleSend:res,{}",res);
        sendMsgResMapperService.asyncInsert(res);
        return res;
    }

    private void batchSend(String from,String[] toes,String msgContent) throws Exception{

        BatchSendMsgRes result = new BatchSendMsgRes();

        List<Future<SendMsgRes>> futures = new ArrayList();

        for(String to:toes){
            Future<SendMsgRes> future = neteaseIm.asyncSendMsg(util.getSendMsgParam(from,to,msgContent));
            futures.add(future);
        }

        for(Future<SendMsgRes> f:futures){
            SendMsgRes res = f.get();
            if(SUCCESS == res.getCode()){
                result.addSuccess(res.getFrom(),res.getTo());
            }else {
                result.addFail(res.getFrom(),res.getTo(),res.getDesc());
            }
        }

        batchSendMsgResService.asyncInSert(JacksonUtils.obj2json(result.getSuccess()),JacksonUtils.obj2json(result.getFail()));
        logger.info("batchSend result： {}",result);

    }


}
