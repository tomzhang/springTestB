package com.jk51.modules.index.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.FirstWeight;
import com.jk51.modules.index.mapper.FirstWeightMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/13.
 */
@Service
public class FirstWeightService {

    @Autowired
    private FirstWeightMapper firstWeightMapper;

    /**
     *根据商家查询一层权重
     * @param owner
     * @result FirstWeight
     * */
    public List<FirstWeight> getFirstWeightByOwner(String owner){
        return firstWeightMapper.findFirstWeightByOwner(owner);
    }

    public FirstWeight getFirstWeightByPrimaryKey(String first_weight_id){
        return firstWeightMapper.getFirstWeightByPrimaryKey(first_weight_id);
    }
    /**
     *根据商家id更新一层权重，如果该商家ID不存在一层权重则插入
     * @param owner
     * @param weight_name
     * @param weight_value
     * @result Map<String,Object>
     * */
    public Map<String,Object> updateOrInsertFirstWeightByOwner(String first_weight_id, String owner,String weight_name,String weight_value){

        Map<String,Object> result = new HashMap<String,Object>();
        int num = 0;
        if(!StringUtil.isNumber(first_weight_id)){
            result.put("msg","first_weight_id  格式错误");
            return result;
        }
        if(!StringUtil.isNumber(owner)){
            result.put("msg","owner id 格式错误");
            return result;
        }

        if(!StringUtil.isDouble(weight_value)){
            result.put("msg","weight_value 格式错误");
            return result;
        }

        if(weight_name==null){
            result.put("msg","weight_name 为空");
            return result;
        }
        FirstWeight firstWeight = getFirstWeightByPrimaryKey(first_weight_id);
        if(firstWeight==null){
            firstWeight = new FirstWeight();
            firstWeight.setOwner(StringUtil.convertToInt(owner));
            firstWeight.setWeight_name(weight_name);
            firstWeight.setWeight_value(StringUtil.convertToDouble(weight_value));
            firstWeight.setCreate_time(new Timestamp(System.currentTimeMillis()));
            firstWeight.setUpdate_time(new Timestamp(System.currentTimeMillis()));
            num = firstWeightMapper.insertFirstWeight(firstWeight);
        }else{
            firstWeight.setWeight_name(weight_name);
            firstWeight.setOwner(StringUtil.convertToInt(owner));
            firstWeight.setWeight_value(StringUtil.convertToDouble(weight_value));
            firstWeight.setUpdate_time(new Timestamp(System.currentTimeMillis()));
            num = firstWeightMapper.updateFirstWeight(firstWeight);
        }

        if(num==1){
            result.put("msg","success");
        }else{
            result.put("msg","插入或更新数据失败");
        }

        return result;
    }


    //查询所有商家的一级权重，以商家为键的Map返回
    public Map<Integer,List<FirstWeight>> getAllFirstWeight() {

        Map<Integer,List<FirstWeight>> map = new HashMap<Integer,List<FirstWeight>>();
        List<FirstWeight> allFirstWeightList = firstWeightMapper.getAllFirstWeight();
        if(allFirstWeightList==null && allFirstWeightList.isEmpty()){
            return map;
        }

        for(FirstWeight fw:allFirstWeightList){

            List<FirstWeight> firstWeightList = new ArrayList<FirstWeight>();
            if(map.get(fw.getOwner())!=null){
                firstWeightList = map.get(fw.getOwner());
                firstWeightList.add(fw);
                map.put(fw.getOwner(),firstWeightList);

            }else{
                firstWeightList.add(fw);
                map.put(fw.getOwner(),firstWeightList);
            }
        }

        return map;
    }


    @Cacheable(value="firstWeight" ,keyGenerator="defaultKeyGenerator")
    public List<FirstWeight> getFirstWeightBySiteId(int site_id) {

        return firstWeightMapper.getFirstWeightBySiteId(site_id);
    }
}
