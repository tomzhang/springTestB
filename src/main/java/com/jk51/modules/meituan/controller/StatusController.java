package com.jk51.modules.meituan.controller;


import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.meituan.service.MtService;
import com.jk51.modules.tpl.service.EleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RequestMapping("meituan")
@Controller
public class StatusController {

    private static final Logger logger = LoggerFactory.getLogger(StatusController.class);
    @Autowired
    private EleService eleService;
    @Autowired
    private MtService mtService;
    @RequestMapping("mt")
    @ResponseBody
    public void cb(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        logger.info("param{}", param);
        eleService.insertLog("美团配送回调参数：" + JacksonUtils.mapToJson(param));
        mtService.cb(param);
    }
}
