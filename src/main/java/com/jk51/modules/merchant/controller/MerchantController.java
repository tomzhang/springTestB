package com.jk51.modules.merchant.controller;

import com.github.pagehelper.Page;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.order.SManager;
import com.jk51.model.order.SMerchant;
import com.jk51.modules.es.entity.ReturnDto;
import com.jk51.modules.merchant.service.MerchantService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-06-02
 * 修改记录:
 */
@Controller
@RequestMapping("merchant")
public class MerchantController {
    @Autowired
    private MerchantService merchantService;

    @PostMapping("getMerchants")
    @ResponseBody
    public List<SMerchant> getMerchants(Integer siteId, String username, String password) {
        return merchantService.getMerchants(siteId, username, password);
    }

    @PostMapping("getUserName")
    @ResponseBody
    public List<SManager> getUserName(Integer siteId, String username, String password) {
        return merchantService.getUsername(siteId, username, password);
    }

    @PostMapping("/manager/updatePassword")
    @ResponseBody
    public Integer managerUpdatePassword(Integer siteId, Integer userId, String password) {
        return merchantService.managerUpdatePassword(siteId, userId, password);
    }

    @PostMapping("/merchant/updatePassword")
    @ResponseBody
    public Integer merchantUpdatePassword(Integer siteId, Integer userId, String password) {
        return merchantService.merchantUpdatePassword(siteId, userId, password);
    }

    @GetMapping("/getMerchantBySiteId")
    @ResponseBody
    public ReturnDto getMerchantBySiteId(HttpServletRequest request){
        Map param = ParameterUtil.getParameterMap(request);

        String siteId = param.get("siteId").toString();

        if("".equals(siteId)||"null".equals(siteId)){
            return ReturnDto.buildFailedReturnDto("siteId null");
        }

        Map result = merchantService.getMerchantBySiteId(Integer.valueOf(siteId));

        return ReturnDto.buildSuccessReturnDto(result);
    }

    @RequestMapping("getWXStoreQRUrl")
    @ResponseBody
    public ReturnDto getWXStoreQRUrl(Integer siteId, String storeName,
                                         @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                         @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        if (StringUtils.isBlank(storeName)) storeName = null;
        try {
            List<Map<String, Object>> result = merchantService.getWXStoreQRUrl(siteId, storeName, pageNum, pageSize);
            if (result instanceof Page) return ReturnDto.buildSuccessReturnDto(((Page<Map<String,Object>>) result).toPageInfo());
            return ReturnDto.buildSuccessReturnDto(result);
        } catch (Exception e) {
            return ReturnDto.buildFailedReturnDto("查询列表异常");
        }
    }
}
