package com.jk51.modules.notice.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.Upgrade;
import com.jk51.modules.notice.service.NoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-08-09 16:18
 * 修改记录:
 */
@RequestMapping("/notice")
@Controller
public class NoticeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoticeController.class);

    @Autowired
    NoticeService noticeService;

    @RequestMapping(value = "/save")
    @ResponseBody
    public ReturnDto saveNotice(HttpServletRequest request, HttpServletResponse response, @Valid Upgrade upgrade , BindingResult bindingResult){

        try {
            hasErrors(bindingResult);
            return noticeService.saveNotice(upgrade);
        } catch (RuntimeException e) {
            LOGGER.error("保存51jk公告参数绑定失败",e);
            return ReturnDto.buildFailedReturnDto("保存51jk公告参数绑定失败 -> " + e.getMessage() ) ;
        } catch (Exception e) {
            LOGGER.error("保存51jk公告失败",e);
            return ReturnDto.buildFailedReturnDto("保存51jk公告失败 -> " + e.getMessage());
        }
    }

    @RequestMapping(value = "/getNoticeGroup")
    @ResponseBody
    public ReturnDto getNoticeGroup(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String,Object> params){

        try {
            return ReturnDto.buildSuccessReturnDto(noticeService.getNoticeGroup(params));
        } catch (Exception e) {
            LOGGER.error("获取51jk公告列表失败",e);
            return ReturnDto.buildFailedReturnDto("获取51jk公告列表失败 -> " + e.getMessage());
        }
    }

    @RequestMapping(value = "/update")
    @ResponseBody
    public ReturnDto updateNotice(HttpServletRequest request, HttpServletResponse response, @Valid Upgrade upgrade , BindingResult bindingResult){

        try {
            hasErrors(bindingResult);
            return noticeService.updateNotice(upgrade);
        } catch (RuntimeException e) {
            LOGGER.error("更新51jk公告参数绑定失败，id: " + upgrade.getId(),e);
            return ReturnDto.buildFailedReturnDto("更新51jk公告参数绑定失败，id: " + upgrade.getId() + " -> " + e.getMessage() ) ;
        } catch (Exception e) {
            LOGGER.error("更新51jk公告失败，id: " + upgrade.getId(),e);
            return ReturnDto.buildFailedReturnDto("更新51jk公告失败，id: " + upgrade.getId() + " -> " + e.getMessage() ) ;
        }
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public ReturnDto deleteNotice(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String,Object> params){

        try {
            return noticeService.deleteNotice(params);
        } catch (Exception e) {
            LOGGER.error("删除51jk公告失败，id: " + params.get("id"),e);
            return ReturnDto.buildFailedReturnDto("删除51jk公告失败，id: " + params.get("id")+ " -> " + e.getMessage() ) ;
        }
    }

    @RequestMapping(value = "/selectByCreateTime")
    @ResponseBody
    public Upgrade selectByCreateTime(HttpServletRequest request){
        return noticeService.selectByCreateTime(Integer.parseInt(request.getParameter("type")));
    }


    private void hasErrors(BindingResult bindingResult) throws RuntimeException {
        if (bindingResult.hasErrors()) {
            FieldError fe = bindingResult.getFieldError();
            String err = fe.getDefaultMessage();

            throw new RuntimeException(err);
        }
    }

}
