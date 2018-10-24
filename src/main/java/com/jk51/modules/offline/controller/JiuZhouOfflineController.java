package com.jk51.modules.offline.controller;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.offline.service.JiuZOfflineService;
import com.jk51.modules.trades.service.TradesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen
 * 创建日期: 2017-04-06
 * 修改记录:
 */
@RestController
@ResponseBody
@RequestMapping("/offline/jz")
public class JiuZhouOfflineController {

    private static final Logger logger = LoggerFactory.getLogger(JiuZhouOfflineController.class);

    @Autowired
    private JiuZOfflineService jzService;
    @Autowired
    private TradesService tradesService;

  /*  @RequestMapping("/getuser")
    public Map<String, Object> getUser(Integer siteId, String mobile, String invite_code) {

        return jzService.getUser(siteId, mobile, invite_code);
    }

    @RequestMapping("/updateuser")
    public Map<String, Object> updateUser(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        return jzService.updateUser(param);
    }*/

    @RequestMapping("/queryscore")
    public Map<String, Object> queryScore(Integer siteId, String mobile) {
        return jzService.queryScore(siteId, mobile);
    }

    @RequestMapping("/givenlist")
    public Map<String, Object> getGivenList(String mobile) {
        return jzService.getGivenList(mobile);
    }

    @RequestMapping("/receive")
    public Map<String, Object> receive(Integer siteId, String mobileNo, String orderNo) {
        logger.info("siteId:[{}],mobile:[{}],orderNo:[{}]", siteId, mobileNo, orderNo);
        return jzService.receive(siteId, mobileNo, orderNo);
    }

    @RequestMapping("/scorelist")
    public Map<String, Object> getScoreList(String mobileNo) {
        return jzService.getScoreList(mobileNo);
    }

}
