package com.jk51.modules.index.controller;

import com.jk51.model.FirstWeight;
import com.jk51.modules.index.service.FirstWeightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/13.
 */
@Controller
@ResponseBody
@RequestMapping("/firstWeight")
public class FirstWeightController {

    @Autowired
    private FirstWeightService firstWeightService;

    /**
     *根据商家查询一层权重
     * @param owner
     * @result Map<String,Object>
     * */
    @RequestMapping("/some")
    public Map<String,Object> getFirstWeightByOwner(String owner){

        Map<String,Object> result = new HashMap<String,Object>();
        List<FirstWeight> firstWeightList = firstWeightService.getFirstWeightByOwner(owner);
        result.put("firstWeightList",firstWeightList);
        return result;
    }


    /**
     *根据商家id更新一层权重，如果该商家ID不存在一层权重则插入
     * @param owner
     * @param weight_name
     * @param weight_value
     * @result Map<String,Object>
     * */
    @RequestMapping("/one")
    public Map<String,Object> updateOrInsertFirstWeightByOwner(String first_weight_id,String owner,String weight_name,String weight_value){

        Map<String,Object> result = new HashMap<String,Object>();
        Map<String,Object> data = firstWeightService.updateOrInsertFirstWeightByOwner(first_weight_id,owner,weight_name,weight_value);
        result.put("data",data);
        return result;
    }
}
