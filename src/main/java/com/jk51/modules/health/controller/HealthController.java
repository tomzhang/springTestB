package com.jk51.modules.health.controller;

import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.health.service.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: ChengShanyunduo
 * 创建日期: 2017-11-20
 * 修改记录:
 */
@RequestMapping("/health")
@Controller
public class HealthController {
    @Autowired
    HealthService healthService;

    @PostMapping("/list")
    @ResponseBody
    public ReturnDto healthList(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        List<Map<String, Object>> result = healthService.healthList(param);
        PageInfo pageInfo = new PageInfo<>(result);
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    @RequestMapping("/editHealthFile")
    @ResponseBody
    public ReturnDto editHealthFile(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        return healthService.editHealthFile(param);
    }
}
