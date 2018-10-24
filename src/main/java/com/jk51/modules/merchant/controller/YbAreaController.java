package com.jk51.modules.merchant.controller;

import com.jk51.modules.es.entity.ReturnDto;
import com.jk51.modules.merchant.service.YbAreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-05-27
 * 修改记录:
 */
@RequestMapping("/ybArea")
@Controller
public class YbAreaController {
    @Autowired
    private YbAreaService ybAreaService;

    private static final Logger log = LoggerFactory.getLogger(YbAreaController.class);

    @ResponseBody
    @PostMapping("queryAreaByAreaId")
    public Map queryAreaByAreaId(Integer cityId) {
        return ybAreaService.queryAreaByAreaId(cityId);
    }

    @ResponseBody
    @RequestMapping("queryAreaByParentId")
    public Map<String, Object> queryAreaByParentId(Integer parentId, Integer type) {
        Map<String, Object> result = new HashMap<>();
        result.put("areaList", ybAreaService.queryAreaByParentId(parentId, type));
        return result;
    }

    @ResponseBody
    @PostMapping("queryAreaIdByName")
    public Map<String, Object> queryAreaIdByName(Integer type, String name, Integer parentId) {
        Map<String, Object> result = new HashMap<>();
        result.put("areaList", ybAreaService.queryAreaIdByName(type, name, parentId));
        return result;
    }

    @ResponseBody
    @PostMapping("getcitys")
    public Map<String, Object> getcitys() {
        Map<String, Object> result = new HashMap<>();
        result.put("areaList", ybAreaService.getcitys());
        return result;
    }

    @ResponseBody
    @PostMapping("getParentId")
    public Integer getParentId(Integer area_id, Integer type) {
        return ybAreaService.getParentId(area_id, type);
    }

    @RequestMapping("queryAreaByTree")
    @ResponseBody
    public Object queryAreaByTree(String areaIds){
        Map<String,Object> rs=new HashMap<String,Object>();
        try {
            List<Map<String, Object>> tree = ybAreaService.queryAreaByTree(areaIds);
            rs.put("data",tree);
            rs.put("code","000");
        }catch (Exception e){
            log.info("获取省市失败{}",e);
            rs.put("data",null);
            rs.put("code","-1");
        }
        return rs;
    }



    /**
     * 通过市id判断，如果一个省的所有市id都存在，则返回该省id
     *
     * @param cityIds
     * @return
     */
    @ResponseBody
    @GetMapping("getProvinceIdByCityIds")
    public ReturnDto getProvinceIdByCityIds(String cityIds) {
        return ybAreaService.getProvinceIdByCityIds(cityIds);
    }
}
