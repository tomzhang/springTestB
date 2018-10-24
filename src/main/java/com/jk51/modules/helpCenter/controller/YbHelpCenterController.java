package com.jk51.modules.helpCenter.controller;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.YbHelpCenter;
import com.jk51.modules.helpCenter.service.YbHelpCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/4.
 */
@Controller
@RequestMapping("/help")
public class YbHelpCenterController {
    @Autowired
    private YbHelpCenterService helpCenterService;


    @RequestMapping("query")
    @ResponseBody
    public Map<String,Object> query(HttpServletRequest request){
        Map<String,Object> resultMap=new HashMap<String,Object>();
        Map<String,Object> param=ParameterUtil.getParameterMap(request);
        List<YbHelpCenter> list=helpCenterService.getHelpList(param);
        if(list.size() >0){
            resultMap.put("list",list);
            resultMap.put("msg","success");
        }else{
            resultMap.put("msg","error");
        }

        return resultMap;
    }

    @RequestMapping("queryById")
    @ResponseBody
    public Map<String,Object> queryById(HttpServletRequest request){
        Map<String,Object> resultMap=new HashMap<String,Object>();
        Map<String,Object> param=ParameterUtil.getParameterMap(request);
       YbHelpCenter ybHelpCenter=helpCenterService.selectByPrimaryKey(Integer.valueOf(param.get("id").toString()));
        if(null != ybHelpCenter){
            resultMap.put("value",ybHelpCenter);
            resultMap.put("msg","success");
        }else{
            resultMap.put("msg","error");
        }
        return resultMap;
    }

    @RequestMapping("insert")
    @ResponseBody
    public Map<String,Object> insert(HttpServletRequest request,YbHelpCenter helpCenter){
        Map<String,Object> param=ParameterUtil.getParameterMap(request);
        Map<String,Object> resultMap=new HashMap<>();
        int result=0;
        if( null != helpCenter.getId()){
            result=  helpCenterService.update(helpCenter);
        }else{
             result=helpCenterService.insert(helpCenter);
        }
        if(result >0){
            resultMap.put("msg","success");
        }else{
            resultMap.put("msg","error");
        }
        return resultMap;
    }
}
