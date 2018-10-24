package com.jk51.modules.distribution.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.distribution.service.RewardService;
import com.mysql.fabric.xmlrpc.base.Param;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/2/9.
 */
@Controller
@ResponseBody
@RequestMapping("/reward")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    /**
     * 列表展示，查询
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/queryRewardList")
    public Map<String,Object> getAwardSum(HttpServletRequest request){
        Map<String, Object> map = new HashMap();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);

        Integer page=1;
        Integer pageSize=15;
        if (param.get("page") != null && param.get("page")!=""){
            page = Integer.parseInt(param.get("page").toString()) ;
        }
        if (param.get("pageSize") != null && param.get("pageSize")!="") {
            pageSize = Integer.parseInt(param.get("pageSize").toString());
        }

        if (param.get("end_time") != null && param.get("end_time")!=""){
            param.put("end_time",param.get("end_time")+" 23:59:59");
        }

        PageHelper.startPage(page, pageSize);//开启分页
        PageInfo<Map<String,Object>> pageInfo = rewardService.queryRewardList(page, pageSize, param);
        List<Map<String,Object>> items = pageInfo.getList();
        map.put("pageSize",pageSize);
        map.put("current", page);
        map.put("before", pageInfo.getPrePage());
        map.put("next", pageInfo.getNextPage());
        map.put("total_pages", pageInfo.getPages());
        map.put("total_items", pageInfo.getTotal());
        map.put("items", items);
        return map;
    }

    /**
     * 根据商家ID,获取门店下所有已确认和未确认的奖励总金额
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/totalReward")
    public Map<String,Object> totalReward (HttpServletRequest request){
        //Integer siteId = Integer.parseInt(request.getSession().getAttribute("siteId").toString());
        Map<String,Object> map = ParameterUtil.getParameterMap(request);
        Integer siteId;

//        if(map.get("siteId") == null || map.get("siteId").toString() == "" || Integer.parseInt(map.get("siteId").toString()) == 0){
//            siteId = 100063;
//        }else {
            siteId = Integer.parseInt(map.get("siteId").toString());
   //     }
        return rewardService.totalReward(siteId);
    }

//    /**
//     * 根据siteId查询最小提现金额
//     * @param request
//     * @return
//     */
//    @RequestMapping("/queryMinMoney")
//    public Integer queryMinMoney (HttpServletRequest request){
//        Map<String,Object> map = ParameterUtil.getParameterMap(request);
//        Integer siteId;
//
////        if(map.get("siteId") == null || map.get("siteId").toString() == ""){
////            siteId = 100063;
////        }else {
//            siteId = Integer.parseInt(map.get("siteId").toString());
//    //    }
//        return rewardService.queryMinMoney(siteId);
//    }
//
//    /**
//     * 修改最小金额
//     * @param request
//     * @return
//     */
//    @RequestMapping("/updateMinMoney")
//    public Integer updateMinMoney (HttpServletRequest request){
//        Map<String,Object> map = ParameterUtil.getParameterMap(request);
//        Integer siteId;
//        Integer minMoney = Integer.parseInt(map.get("minMoney").toString());
//
////        if(map.get("siteId") == null || map.get("siteId").toString() == ""){
////            siteId = 100063;
////        }else {
//            siteId = Integer.parseInt(map.get("siteId").toString());
//      //  }
//        return rewardService.updateMinMoney(siteId,minMoney);
//    }


}
