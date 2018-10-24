package com.jk51.modules.wechat.controller;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.Goods;
import com.jk51.modules.goods.service.GoodsService;
import com.jk51.modules.im.service.IMExpireRedisKeyService;
import com.jk51.modules.im.util.RLMessageParameter;
import com.jk51.modules.wechat.service.ChAnswerRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang1
 * 创建日期: 2017-05-31
 * 修改记录:
 */
@Controller
@RequestMapping("/wechat/im")
public class ChAnswerController {
    @Autowired
    private ChAnswerRelationService chAnswerRelationService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private IMExpireRedisKeyService iMExpireRedisKeyService;


    @ResponseBody
    @PostMapping("/getReceiverBySender")
    public String getReceiverBySender(String rltoken) {
        return  chAnswerRelationService.getReceiverBySender(rltoken);
       // return StringUtil.isEmpty(receiver)?"":receiver;
    }
    @ResponseBody
    @PostMapping("/delete")
    public String delete(String sender) {
        return chAnswerRelationService.getReceiverBySender(sender);
    }

    /**
     * 保存评价 并 解除绑定关系
     */
    @PostMapping("/updateEvalute")
    public @ResponseBody Integer updateEvaluate(Integer imServiceId,Integer evaluteParam, String sender, String recervice,Integer siteId,String appId) {

        int x = chAnswerRelationService.updateEvaluate(imServiceId,evaluteParam,sender,recervice,appId,siteId);
        if(x==1){

            //删除会员过期评价键、删除会员过期断开关系键
            RLMessageParameter param = new RLMessageParameter();
            param.setAppId(appId);
            param.setReceiver(recervice);
            param.setSite_id(siteId);
            param.setSender(sender);
            iMExpireRedisKeyService.delExpireForMember(param);

        }

        return x;
    }


    /**
     * 获取推荐商品  根据商品Id数组，商铺Id
     * @return
     */
    @RequestMapping("getRecommendGoodsByIds")
    public @ResponseBody
    List<Map> getRecommendGoodsByIds(String ids, Integer siteId){

        String[] arrId = ids.split(",");
        Integer[] idLst = new Integer[arrId.length];
        for(int i=0;i<arrId.length;i++){
            idLst[i] = Integer.parseInt(arrId[i]);
        }
        List<Map> goodsLst = goodsService.getRecommendGoodsByIds(siteId, Arrays.asList(idLst));

        return goodsLst;
    }
}
