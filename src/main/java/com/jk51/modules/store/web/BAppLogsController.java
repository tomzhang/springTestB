package com.jk51.modules.store.web;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.order.SBAppLogs;
import com.jk51.modules.store.service.BAppLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: zhangkuncheng
 * 创建日期: 2017-05-23
 * 修改记录:
 */
@Controller
@RequestMapping("/common")
public class BAppLogsController {

    @Autowired
    private BAppLogsService bAppLogsService;

    /**
     * 查询门店后台日志
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/css/selective")
    @ResponseBody
    public Map<String, Object> selective(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.parseInt(String.valueOf(param.get("siteId")));
        Integer store_id = Integer.parseInt(String.valueOf(param.get("store_id")));
        Integer pageNum = Integer.parseInt(String.valueOf(param.get("pageNum")));
        Integer pageSize = Integer.parseInt(String.valueOf(param.get("pageSize")));
        String operatorName = String.valueOf(param.get("operatorName"));
        if ("null".equals(String.valueOf(param.get("operatorName")))) {
            operatorName = null;
        }
        return bAppLogsService.selective(siteId, store_id, operatorName, pageNum, pageSize,
            param.get("action").toString(),param.get("startTime").toString(),param.get("endTime").toString(),param.get("content").toString());
    }

    @RequestMapping(value = "/css/insertSelective")
    @ResponseBody
    public int insertSelective(HttpServletRequest request) {
        Map result = new HashMap<String, Object>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        String str = JacksonUtils.mapToJson(param);
        SBAppLogs bAppLogs = null;
        try {
            bAppLogs = JacksonUtils.json2pojo(str, SBAppLogs.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int i = bAppLogsService.insertSelective(bAppLogs);
        return i;
    }


}
