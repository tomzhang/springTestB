package com.jk51.modules.offline.controller;

import com.jk51.modules.offline.service.QianjinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 千金
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-04-07 11:54
 * 修改记录:
 */
@Controller
@ResponseBody
@RequestMapping("/offline/qj")
public class QianjinOfflineController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QianjinOfflineController.class);

    @Autowired
    private QianjinService qianjinService;

    /**
     * 库存接口
     * @param GOODSNO
     * @param UID
     * @return
     */
   /* @RequestMapping("/getStock")
    public Map<String, Object> getStock(String GOODSNO, String UID) {
        return qianjinService.getStock(GOODSNO, UID);
    }*/

  /*  *//**
     * 获取会员信息接口
     *
     * @param siteId
     * @param mobile
     * @return
     *//*
    @RequestMapping("/getuser")
    public Map<String, Object> getUser(Integer siteId, String mobile) {
        return qianjinService.getUser(siteId, mobile);
    }*/

 /*   *//**
     * 添加会员接口
     *
     * @return
     *//*
    @RequestMapping("/adduser")
    public Map<String, Object> addUser(@RequestBody Map<String, Object> param) {
        //Map<String, Object> param = ParameterUtil.getParameterMap(request);
        return qianjinService.addUser(param);
    }*/

  /*  *//**
     * 更新会员接口
     *
     * @return
     *//*
    @RequestMapping("/updateuser")
    public Map<String, Object> updateUser(@RequestBody Map<String, Object> param) {
        //Map<String, Object> param = ParameterUtil.getParameterMap(request);
        return qianjinService.updateUser(param);
    }*/

}
