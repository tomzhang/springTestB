package com.jk51.modules.abutmentInterface.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.http.HttpClient;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.Member;
import com.jk51.model.order.SBMember;
import com.jk51.modules.abutmentInterface.service.HeartServeService;
import com.jk51.modules.concession.ConcessionCalculateBaseImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.http.HTTPException;
import java.rmi.MarshalledObject;
import java.util.Map;
import java.util.Objects;

/**
 * @author zhaoyang
 * @version 1.00
 * @ClassName HeartServiceController
 * @Description 心服务
 * @Date 2018-08-16 17:20
 */
@Controller
@RequestMapping("/heartService")
@ResponseBody
public class HeartServiceController {

    private final static Logger LOGGER = LoggerFactory.getLogger(HeartServiceController.class);

    @Autowired
    HeartServeService heartServeService;


    /**
     * 根据openId查询会员信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/getMemberInfo",method = RequestMethod.POST)
    public ReturnDto getMemberInfoByOpenId(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Map<String,Object> member = heartServeService.queryMemInfo(parameterMap);
        if (Objects.isNull(member)) {
            return ReturnDto.buildFailedReturnDto("没有查询到会员信息!");
        }
        return ReturnDto.buildSuccessReturnDto(member);
    }


    /**
     * 根据手机号查询是否已经有会员
     * 将会员信息存储到新的表中
     * 调用跃的注册接口
     * 接口返回页面是否成功
     * @param request
     * @return
     */
    @RequestMapping(value = "/sendUserInfo",method = RequestMethod.POST)
    public ReturnDto sendUserInfo(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        ReturnDto returnDto = heartServeService.sendUserInfos(parameterMap);
        return returnDto;
    }

    /**
     * 保存用户检查数据
     * @param request
     * @return
     */
    @RequestMapping(value = "/saveLogAndPush",method = RequestMethod.POST)
    @ResponseBody
    public String saveLogAndCallTemplate(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        String result = heartServeService.saveAndCall(parameterMap);
        LOGGER.debug("saveLogAndCallTemplate 接收检测结果返回 {}",result);
        return result;
    }


    /**
     * 查询设备信息
     * @return
     */
    @RequestMapping(value = "/queryEquipmentInfo",method = RequestMethod.POST)
    @ResponseBody
    public Map queryEquipmentInfo(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Map<String, Object> map = heartServeService.queryEquipmentInfos(parameterMap);
        return map;
    }


    @RequestMapping(value = "/queryUserRegistrionInfo",method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queryUserRegistion(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        return heartServeService.queryUserRegistionInfo(parameterMap);
    }

}
