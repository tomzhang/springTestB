package com.jk51.modules.pay.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.PayLogs;
import com.jk51.modules.pay.service.PayLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
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
 * 创建日期: 2017-03-27
 * 修改记录:
 */
@Controller
public class PayLogsController {
    @Autowired
    PayLogsService payLogsService;

    @RequestMapping("/pay/findPayLogs")
    @ResponseBody
    public ReturnDto findPayLogs(@RequestParam Map map, @RequestParam( defaultValue = "1") int pageNo,
                                 @RequestParam( defaultValue = "20") int pageSize,
                                 @RequestParam( defaultValue = "true") boolean isCount, @RequestParam( defaultValue = "-1" ) long total) {
        Page<PayLogs> page = PageHelper.startPage(pageNo, pageSize, isCount);
        Map<String, Object> data = new HashMap<>();
        List<PayLogs> payLogss = payLogsService.findPayLogs(map);
        data.put("data", payLogss);
        if(!isCount && total>0) {
            page.setTotal(total);
        }
        data.put("page", page.toPageInfo());
        return ReturnDto.buildSuccessReturnDto(data);
    }
}
