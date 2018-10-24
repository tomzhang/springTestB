package com.jk51.modules.storedelivery.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.storedelivery.StoreDelivery;
import com.jk51.modules.storedelivery.service.StoreDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: linwang
 * 创建日期: 2017-03-29
 * 修改记录:
 */
@Controller
public class StoreDeliveryController {

    @Autowired
    StoreDeliveryService storeDeliveryService;

    @ResponseBody
    @RequestMapping("/storedelivery/getStoreDelivery")
    public ReturnDto getStoreDelivery(@RequestParam int siteId, @RequestParam int storeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("siteId", siteId);
        params.put("storeId", storeId);
        List<StoreDelivery> result = storeDeliveryService.getStoreDeliveryByParam(params);
        if (result == null || result.isEmpty() || result.size() == 0){
            return ReturnDto.buildSuccessReturnDto(null);
        }else {
            return ReturnDto.buildSuccessReturnDto(result.get(0));
        }
    }
}
