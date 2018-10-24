package com.jk51.modules.coupon.controller;

import com.jk51.model.coupon.ErpParams;
import com.jk51.modules.coupon.service.CouponInformErpService;
import com.jk51.modules.coupon.service.CouponNoEncodingService;
import com.jk51.modules.es.entity.ReturnDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2017/9/25.
 */
@Controller
@RequestMapping("/Erp")
public class CouponErpController {

    @Autowired
    private CouponInformErpService couponInformErpService;

    @Autowired
    private CouponNoEncodingService couponNoEncodingService;

    @RequestMapping("/ErpUpdateByCouponNo")
    @ResponseBody
    public ReturnDto ErpUpdateByCouponNo(ErpParams erpParams){
        try {
            Integer count = couponInformErpService.ErpUpdateByCouponNo(erpParams.getSiteId(),erpParams.getCouponNo()
               , erpParams.getStatus());
            if(count>0){
                return ReturnDto.buildSuccessReturnDto();
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("更改时出错");
        }
        return  ReturnDto.buildFailedReturnDto("编码不存在");
        }
}
