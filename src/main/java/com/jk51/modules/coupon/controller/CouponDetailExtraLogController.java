package com.jk51.modules.coupon.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.coupon.service.CouponDetailExtraLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by javen73 on 2018/4/13.
 */
@RestController
@RequestMapping("/detailLog")
public class CouponDetailExtraLogController {
    @Autowired
    private CouponDetailExtraLogService couponDetailExtraLogService;

    private Logger logger = LoggerFactory.getLogger(CouponDetailExtraLogController.class);
    @RequestMapping("/findDetailExtraLog")
    public ReturnDto findDetailExtraLog(HttpServletRequest request){
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        ReturnDto dto = null;
        try {
            dto = couponDetailExtraLogService.findDetailExtraLogList(param);
        } catch (Exception e) {
            logger.error("查询核销记录表失败:{}",e);
            dto = ReturnDto.buildFailedReturnDto("查询核销记录表失败");
        }
        return dto;
    }
}
