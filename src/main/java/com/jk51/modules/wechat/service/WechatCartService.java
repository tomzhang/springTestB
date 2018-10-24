package com.jk51.modules.wechat.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.WxCart;
import com.jk51.modules.persistence.mapper.SBMemberMapper;
import com.jk51.modules.persistence.mapper.WxCartMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-05-31
 * 修改记录:
 */
@Service
public class WechatCartService {
    private Logger log = LoggerFactory.getLogger(WechatCartService.class);
    @Autowired
    protected WxCartMapper wxCartMapper;


    public Map<String,Object> updateWxCart(Map<String, Object> param) {
        Integer siteId=Integer.parseInt(param.get("siteId").toString());
        Integer buyerId=Integer.parseInt(param.get("userId").toString());
        Integer goodsId=Integer.parseInt(param.get("goodsId").toString());
        Integer quantity=Integer.parseInt(param.get("quantity").toString());
        Map<String,Object> result = new HashMap<String,Object>();
        WxCart wxCart=wxCartMapper.get(goodsId,siteId,buyerId);
        if(StringUtil.isEmpty(wxCart)){
            wxCartMapper.insert(param);
        }else {
            if(quantity==1){
                param.put("quantity",wxCart.getQuantity()+quantity);
            }else {
                param.put("quantity",quantity);
            }
            wxCartMapper.update(param);
        }
        result.put("status", true);
        return result;
    }
    public Map<String,Object> delWxCart(Map<String, Object> param) {
        Integer siteId=Integer.parseInt(param.get("siteId").toString());
        Integer buyerId=Integer.parseInt(param.get("userId").toString());
        String goodsidArr=param.get("goodsId").toString();
        Map<String,Object> result = new HashMap<String,Object>();
        wxCartMapper.delete(goodsidArr.split(","),siteId,buyerId);
        result.put("status", true);
        return result;
    }
    public Map<String,Object> getWxCart(Map<String, Object> param) {
        Integer siteId=Integer.parseInt(param.get("siteId").toString());
        Integer buyerId=Integer.parseInt(param.get("userId").toString());
        Integer goodId=Integer.parseInt(param.get("goodsId").toString());
        Map<String,Object> result = new HashMap<String,Object>();
        WxCart wxCart=wxCartMapper.get(goodId,siteId,buyerId);
        result.put("wxCart", wxCart);
        return result;
    }
}
