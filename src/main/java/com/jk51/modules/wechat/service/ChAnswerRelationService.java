package com.jk51.modules.wechat.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.modules.im.mapper.BIMServiceMapper;
import com.jk51.modules.im.mapper.ChAnswerRelationMapper;
import com.jk51.modules.im.service.IMExpireService;
import com.jk51.modules.im.util.IMParameter;
import com.jk51.modules.im.util.IMRedisKey;
import com.jk51.modules.integral.service.IntegerRuleService;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-05-31
 * 修改记录:
 */
@Service
public class ChAnswerRelationService {
    @Autowired
    private ChAnswerRelationMapper chAnswerRelationMapper;
    private Logger log = LoggerFactory.getLogger(ChAnswerRelationService.class);

    @Autowired
    private BIMServiceMapper bimServiceMapper;
    @Autowired
    private IMExpireService imExpireService;
    @Autowired
    private IntegerRuleService integralService;

    public String  getReceiverBySender(String user_openid) {
        return chAnswerRelationMapper.getReceiverBySender(user_openid);
    }

    public Integer updateEvaluate(Integer imServiceId, Integer evaluateParam, String sender, String recervice, String appId, Integer siteId) {
        Integer x = 0;



        try {
            x = bimServiceMapper.updateEvaluate(imServiceId, evaluateParam);
            if(x==1){
                //当评价成功后，解除会员与顾客之间的聊天绑定关系
                chAnswerRelationMapper.delete(sender, imServiceId);

                integralService.getIntegralByConsult(siteId,imServiceId);

                //发送会员已评价提醒给店员
                if(StringUtil.isEmpty(recervice)){
                    return x;
                }
                IMRedisKey imRedisKey = new IMRedisKey();
                imRedisKey.setReceiver(recervice);
                imRedisKey.setSender(sender);
                imRedisKey.setSiteId(siteId);
                imRedisKey.setAppId(appId);
                imRedisKey.setiMServiceId(imServiceId);
                imExpireService.sendRemind2Clerk(imRedisKey, IMParameter.evaluated_REMIND);
            }
        }catch (Exception e){
            x = 0;
            log.error(e.getMessage());
        }

        return x;
    }
}
