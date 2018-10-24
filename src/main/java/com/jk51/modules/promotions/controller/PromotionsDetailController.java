package com.jk51.modules.promotions.controller;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.promotions.service.PromotionsDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/8                                <br/>
 * 修改记录:                                         <br/>
 */
@RestController
@RequestMapping("promotions/detail")
public class PromotionsDetailController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PromotionsDetailService service;

    /* -- 创建活动详情 开始 -- */

    /* -- 创建活动详情 结束 -- */


    /* -- 更新活动详情 开始 -- */

    /* -- 更新活动详情 结束 -- */


    /* -- 查询活动详情 开始 -- */

    /* -- 查询活动详情 结束 -- */

    @RequestMapping("getPromotionsTableList")
    @ResponseBody
    public Object getCouponTableList(HttpServletRequest request){
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        Map map = new HashMap();
        try {
            map = (Map<String,Object>) service.findCouponListTable(params);
        } catch (Exception e) {
            logger.error("查询优惠券使用情况列表失败:{}",e);
            map.put("code",-1);
            map.put("total",0);
        }
        return map;
    }

    @RequestMapping("getPromotionsCount")
    @ResponseBody
    public Object getPromotionsCount(HttpServletRequest request){
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        Map map = new HashMap();
        try {
            Integer count =  service.getPromotionsCount(params);
            map.put("value",count);
            map.put("code","000");
        } catch (Exception e) {
            logger.error("查询优惠券使用情况数量失败:{}",e);
            map.put("code",-1);
            map.put("value",0);
        }
        return map;
    }
    @RequestMapping("promotionsStatus")
    @ResponseBody
    public Object getPromotionsStatus(HttpServletRequest request){
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        Map map = null;
        Map result = new HashMap();
        try {
            map = service.getPromotionsStatus(params);
            result.put("code","000");
            result.put("value",map);
        }catch (Exception e){
            result.put("code","-1");
            result.put("msg",e.getMessage());
            logger.error("查询优惠券使用状态失败:{}",e);
        }
        return result;
    }

}
