package com.jk51.modules.task.controller;

import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.exception.BusinessLogicException;
import com.jk51.modules.task.service.ExchangeManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guosheng on 2017/9/29.
 */
@RestController
@RequestMapping("/exchangeManage")
public class ExchangeManageController {
    public static final Logger logger = LoggerFactory.getLogger(ExchangeManageController.class);

    @Autowired
    ExchangeManageService exchangeManageService;
    /**
     * 提兑管理数据获取
     * @return
     */
    @PostMapping("/getList")
    public ReturnDto getList(@RequestParam HashMap queryMap, int pageNum, int pageSize){
        ReturnDto response;
        try {
            List<Map<String,Object>> exchangeManageList =exchangeManageService.getexchangeManageList(queryMap,pageNum,pageSize);
            response = ReturnDto.buildSuccessReturnDto(new PageInfo<>(exchangeManageList));
        } catch (Exception e) {
            logger.error("获取提兑管理数据失败!", e);
            response = ReturnDto.buildFailedReturnDto("获取提兑管理数据失败:" + e);
        }
        return response;
    }

    /**
     * 修改提兑状态
     * @return
     */
    @PostMapping("/change")
    public ReturnDto getList(@RequestParam  Integer  id)throws BusinessLogicException{
        try {

            boolean flag = exchangeManageService.changeStatus(id);
            if (flag) {
                return ReturnDto.buildSuccessReturnDto();
            }
            return ReturnDto.buildFailedReturnDto("修改失败");

        } catch (Exception e) {
            logger.error("修改提兑状态失败!", e);
            throw new BusinessLogicException(e.getMessage());

        }

    }

    /**
     * 软删除提兑记录
     * @return
     */
    @PostMapping("/updateStatus")
    public ReturnDto updateStatus(@RequestParam  Integer  id)throws BusinessLogicException{
        try {

            boolean flag = exchangeManageService.updateStatus(id);
            if (flag) {
                return ReturnDto.buildSuccessReturnDto();
            }
            return ReturnDto.buildFailedReturnDto("软删除失败");

        } catch (Exception e) {
            logger.error("软删除失败!", e);
            throw new BusinessLogicException(e.getMessage());

        }

    }

}
