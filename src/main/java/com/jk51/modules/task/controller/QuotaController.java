package com.jk51.modules.task.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.task.TQuota;
import com.jk51.modules.task.service.QuotaService;
import com.jk51.modules.userScenarios.service.QrcodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.Quota;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: ChengShanyunduo
 * 创建日期: 2017-08-17
 * 修改记录:
 */
@RestController
@RequestMapping("/quota")
public class QuotaController {
    @Autowired
    QuotaService quotaService;

    @RequestMapping("/list")
    public ReturnDto quotaList(HttpServletRequest request, HttpServletResponse response){
        List<TQuota> list = quotaService.quotaList();
        return ReturnDto.buildListOnEmptyFail(list);
    }
}
