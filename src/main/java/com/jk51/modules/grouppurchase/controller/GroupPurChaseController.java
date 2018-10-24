package com.jk51.modules.grouppurchase.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.grouppurchase.request.GroupPurchaseForGoods;
import com.jk51.modules.grouppurchase.service.GroupPurChaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Created by mqq on 2017/11/20.
 */
@Controller
@RequestMapping(name = "团购接口", value = "/groupPurchase")
public class GroupPurChaseController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GroupPurChaseService groupPurChaseService;

    @RequestMapping(name = "商品获取拼团信息接口", value = "/getGroupPurchaseDataForGoods")
    @ResponseBody
    public ReturnDto getGroupPurchaseDataForGoods(@RequestBody GroupPurchaseForGoods groupPurchaseForGoods) {
        logger.info("商品获取拼团信息接口:groupPurchaseForGoods:" + ParameterUtil.ObjectConvertJson(groupPurchaseForGoods));
        if (null == groupPurchaseForGoods.getSiteId())
            return ReturnDto.buildFailedReturnDto("siteId不能为空");
        if (null == groupPurchaseForGoods.getGoodsId())
            return ReturnDto.buildFailedReturnDto("goodsId不能为空");
        /*if (null == groupPurchaseForGoods.getMemberId())
            return ReturnDto.buildFailedReturnDto("memberId不能为空");*/

        Map<String, Object> resultMap = groupPurChaseService.getGroupPurchaseDataForGoods(groupPurchaseForGoods);

        if (resultMap == null)
            return ReturnDto.buildFailedReturnDto("没查到相应信息");
        else
            return ReturnDto.buildSuccessReturnDto(resultMap);

    }
}
