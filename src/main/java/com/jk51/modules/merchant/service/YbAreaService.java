package com.jk51.modules.merchant.service;

import com.jk51.modules.appInterface.mapper.YbAreaMapper;
import com.jk51.modules.es.entity.ReturnDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-05-27
 * 修改记录:
 */
@Service
public class YbAreaService {
    @Autowired
    private YbAreaMapper ybAreaMapper;

    private static final Logger log = LoggerFactory.getLogger(YbAreaService.class);

    public Map queryAreaByAreaId(Integer cityId) {
        return ybAreaMapper.queryAreaByAreaId(cityId);
    }


    public List<Map<String, Object>> queryAreaByParentId(Integer parentId, Integer type) {
        return ybAreaMapper.queryAreaByParentId(parentId, type);
    }


    public List<Map<String, Object>> queryAreaIdByName(Integer type, String name, Integer parentId) {
        return ybAreaMapper.queryAreaIdByName(type, name, parentId);
    }


    public List<Map<String, Object>> getcitys() {
        return ybAreaMapper.getcitys();
    }


    public Integer getParentId(Integer area_id, Integer type) {
        return ybAreaMapper.getParentId(area_id, type);
    }

    /**
     * 通过市id判断，如果一个省的所有市id都存在，则返回该省id
     *
     * @param cityIds
     * @return
     */
    public ReturnDto getProvinceIdByCityIds(String cityIds) {
        List<Integer> idsList = Stream.of(cityIds.split(","))
            .map(Integer::parseInt)
            .collect(toList());
        Map<String, List<Integer>> result = new HashMap<String, List<Integer>>() {{
            put("check", new ArrayList<>());
            put("halfcheck", new ArrayList<>());
        }};

        do {
            Integer cityId = idsList.get(0);
            int currentSize = idsList.size();
            Integer provinceId = ybAreaMapper.getParentId(cityId, 3);
            List<Map<String, Object>> list = ybAreaMapper.queryAreaByParentId(provinceId, 3);
            List<Integer> areaIdList = list.stream()
                .map(map -> map.get("areaid"))
                .map(o -> Integer.parseInt(o.toString()))
                .collect(toList());

            idsList.removeIf(id -> areaIdList.indexOf(id) != -1);
            if (currentSize - idsList.size() == areaIdList.size()) {
                result.get("check").add(provinceId);
            } else {
                result.get("halfcheck").add(provinceId);
            }
            if (currentSize - idsList.size() == 0) {
                return ReturnDto.buildFailedReturnDto("数据有误，请检查");
            }
        } while (idsList.size() > 0);

        return ReturnDto.buildSuccessReturnDto(result);
    }

    public List<Map<String, Object>> queryAreaByTree(String areaIds) {
        String[] ids_arr = areaIds.split(",");
        List<String> ids = Arrays.asList(ids_arr);
        List<Map<String, String>> list = ybAreaMapper.queryAreaByTree(ids);
        Map<String, List<Map<String, String>>> collect = list.stream().filter(map -> map.get("ptext") != null).collect(Collectors.groupingBy(map -> map.get("ptext")));
        List<Map<String, Object>> tree = new ArrayList<Map<String, Object>>();
        Set<String> keys = collect.keySet();
        for (String key : keys) {
            List<Map<String, String>> temp = collect.get(key);
            Map<String, Object> node = new HashMap<String, Object>();
//            temp.stream().forEach(map->map.remove("ptext"));
            node.put("text", key);
            node.put("nodes", temp);
            tree.add(node);
        }
        return tree;

    }

    public Map queryCommonArea() {
        Map map = new HashMap();
        List<Map> provinceList = ybAreaMapper.queryCommonArea("1");
        provinceList.stream().forEach(province -> {
            List<Map> cityList = ybAreaMapper.queryCommonArea(province.get("value") + "");
            province.put("children", cityList);
            cityList.stream().forEach(city -> {
                city.put("children", ybAreaMapper.queryCommonArea(city.get("value") + ""));
            });
        });
        map.put("status", "success");
        map.put("result", provinceList);
        return map;
    }
}
