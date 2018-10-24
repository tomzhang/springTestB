package com.jk51.modules.index.service;

import com.jk51.model.Target;
import com.jk51.modules.index.mapper.TargetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/2/13.
 */
@Service
public class TargetService {

    @Autowired
    private TargetMapper targetMapper;

    /**
     *根据主键指标ID，获取当前指标
     * @param targetId
     * @return Target
     */
    public Target getTargetById(String targetId) {
        return targetMapper.getTargetById(targetId);
    }

    /**
     * 根据商家ID和一层权重ID，获取当前一层权重下指标列表
     * @param firstWeigthId
     * @param owner
     * @return
     */
    public List<Target> getTargetByOwnerAndFirstWeigthId(String firstWeigthId, String owner) {
        return targetMapper.getTargetByOwnerAndFirstWeigthId(firstWeigthId, owner);
    }

    /**
     * 根据指标ID，更新指标表
     * @param paramsMap
     * @return
     */
    public Map<String,Object> updateTarget(Map<String, Object> paramsMap) {
        Map<String,Object> resultMap = new HashMap<String,Object>();
        Target target = null;
        if(paramsMap.get("target_id") != null){
            target = targetMapper.getTargetById((String) paramsMap.get("target_id"));
        }
        if(target == null){
            resultMap.put("msg", "NotFound");
            return resultMap;
        }

        if(paramsMap.get("owner") != null){
            target.setOwner(Integer.parseInt((String) paramsMap.get("owner")));
        }
        if(paramsMap.get("target_name") != null){
            target.setTarget_name((String) paramsMap.get("target_name"));
        }
        if(paramsMap.get("second_weigth_value") != null){
            target.setSecond_weigth_value(Double.parseDouble((String) paramsMap.get("second_weigth_value")));
        }
        if(paramsMap.get("use_status") != null){
            target.setUse_status((String) paramsMap.get("use_status"));
        }
        if(paramsMap.get("first_weigth_id") != null){
            target.setFirst_weigth_id(Integer.parseInt((String) paramsMap.get("first_weigth_id")));
        }
        if(paramsMap.get("initial_value") != null){
            target.setInitial_value(Integer.parseInt((String) paramsMap.get("initial_value")));
        }
        if(paramsMap.get("score_parameter_section") != null){
            target.setScore_parameter_section((String) paramsMap.get("score_parameter_section"));
        }
        if(paramsMap.get("reference_value") != null){
            target.setReference_value(Double.parseDouble((String) paramsMap.get("second_weigth_value")));
        }

        try {
            int i = targetMapper.updateTarget(target);
            resultMap.put("msg", "success");
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("msg", "error");
        }
        return resultMap;
    }

    /**
     * 添加指标
     * @param paramsMap
     * @return
     */
    public Map<String,Object> insertTarget(Map<String, Object> paramsMap) {
        Map<String,Object> resultMap = new HashMap<String,Object>();

        Target target = new Target();
        if(paramsMap.get("owner") != null){
            target.setOwner(Integer.parseInt((String) paramsMap.get("owner")));
        }
        if(paramsMap.get("target_name") != null){
            target.setTarget_name((String) paramsMap.get("target_name"));
        }
        if(paramsMap.get("second_weigth_value") != null){
            target.setSecond_weigth_value(Double.parseDouble((String) paramsMap.get("second_weigth_value")));
        }
        if(paramsMap.get("use_status") != null){
            target.setUse_status((String) paramsMap.get("use_status"));
        }
        if(paramsMap.get("first_weigth_id") != null){
            target.setFirst_weigth_id(Integer.parseInt((String) paramsMap.get("first_weigth_id")));
        }
        if(paramsMap.get("initial_value") != null){
            target.setInitial_value(Integer.parseInt((String) paramsMap.get("initial_value")));
        }
        if(paramsMap.get("score_parameter_section") != null){
            target.setScore_parameter_section((String) paramsMap.get("score_parameter_section"));
        }
        if(paramsMap.get("reference_value") != null){
            target.setReference_value(Double.parseDouble((String) paramsMap.get("second_weigth_value")));
        }

        try {
            int i = targetMapper.insertTarget(target);
            resultMap.put("msg", "success");
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("msg", "error");
        }
        return resultMap;
    }

    /**
     * 根据商家ID，指标名称查询指标
     * */
    public Target getTargetBySiteIdtargetName(String site_id,String targetName){
       List<Target> targetList =  targetMapper.getTargetBySiteIdAndTargetName(site_id,targetName);
        Target target = null;
       if(targetList!=null && targetList.size()>0){
           target = targetList.get(0);
       }
       return target;
    }


    @Cacheable(value="target",keyGenerator = "defaultKeyGenerator")
    public List<Target> getTargetBySiteId(int site_id) {
        return targetMapper.getTargetBySiteId(site_id);
    }

    //查询所有商家的指标参数，以商家为键的Map返回
    public Map<Integer,List<Target>> getAllTarget() {

        Map<Integer,List<Target>> map = new HashMap<Integer,List<Target>>();
        List<Target> allTarget = targetMapper.getAllTarget();

        if(allTarget==null && allTarget.isEmpty()){
            return map;
        }

        for(Target target:allTarget){

            List<Target> list = new ArrayList<Target>();
            if(map.get((int)target.getOwner())!=null){

                list = map.get((int)target.getOwner());
                list.add(target);
                map.put((int)target.getOwner(),list);
            }else{
                list.add(target);
                map.put((int)target.getOwner(),list);
            }
        }

        return map;
    }
}
