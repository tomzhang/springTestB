package com.jk51.modules.distribution.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.model.distribute.QueryTemplate;
import com.jk51.model.distribute.RewardTemplate;
import com.jk51.model.goods.PageData;
import com.jk51.modules.distribution.mapper.GoodsDistributeMapper;
import com.jk51.modules.distribution.mapper.RewardTemplateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by guosheng on 2017/4/17.
 */
@Service
public class RewardTemplateService {
    private static final Logger logger = LoggerFactory.getLogger(RewardTemplate.class);

    @Autowired
    private RewardTemplateMapper rewardTemplateMapper;

    @Autowired
    private GoodsDistributeMapper goodsDistributeMapper;

    public PageInfo<?> queryTemplete(QueryTemplate queryTemplate) {

        PageHelper.startPage(queryTemplate.getPageNum(), queryTemplate.getPageSize());
        List<PageData> list = rewardTemplateMapper.queryTempleteList(queryTemplate);
        return new PageInfo<>(list);

    }

    public String addRewardTemplate(RewardTemplate rewardTemplate) {
        if(rewardTemplate==null)
            return "500";
        //rewardTemplate.setIsUsed(0);
        int i = rewardTemplateMapper.insertTemplate(rewardTemplate);
        if(i!=0)
            return "200";
        return "500";
    }

    public String updateRewardTemplate(RewardTemplate rewardTemplate) {
        if (rewardTemplate==null || rewardTemplate.getId()==null)
            return "500";
        int i = rewardTemplateMapper.updateByPrimaryKey(rewardTemplate);
        if(i!=0)
            return "200";
        return "500";
    }

    public String editRewardTemplate(RewardTemplate rewardTemplate) {
        if (rewardTemplate==null || rewardTemplate.getId()==null)
            return "500";
        int i = rewardTemplateMapper.editByPrimaryKeySelective(rewardTemplate);
        if(i!=0)
            return "200";
        return "500";

    }

    public PageInfo<?> queryTempleteUser(QueryTemplate queryTemplate) {
        PageHelper.startPage(queryTemplate.getPageNum(), queryTemplate.getPageSize());
        List<PageData> list = rewardTemplateMapper.queryTempleteUser(queryTemplate);
        return new PageInfo<>(list);
    }

    public String changeById(String id) {
        if (id==null)
            return "500";
        int i = goodsDistributeMapper.changeById(id);
        if(i!=0)
            return "200";
        return "500";
    }

    public List<Map<String,Object>>  queryDiscountMax(QueryTemplate queryTemplate) throws Exception {
        PageHelper.startPage(1,0);
        List<Map<String, Object>> list=rewardTemplateMapper.queryDiscountMax(queryTemplate);
        Map<String, Object>  discount = new HashMap(){
            {
                put("level1", 100);
                put("level2", 100);
                put("level3", 100);
                put("level4", 100);
                put("level5", 100);
            }
        };
        Map<String, Object>  rewardL1 = new HashMap(){
            {
                put("level1", 0);
            }
        };
        Map<String, Object>  rewardL2 = new HashMap(){
            {
                put("level2", 0);
            }
        };
        Map<String, Object>  rewardL3 = new HashMap(){
            {
                put("level3", 0);
            }
        };
//        Map MAP=new HashMap<String,Object>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = JacksonUtils.json2map(list.get(i).get("discount").toString());
            discount.entrySet().forEach((entry) ->{
                String key = entry.getKey();
                Double value = Double.parseDouble((String) Optional.ofNullable(map.get(key)).orElse("0"));
                if (value < Double.parseDouble(entry.getValue().toString()) && value >0) {
                    entry.setValue(value);
                }
            });

            Map<String, Object> map2 = JacksonUtils.json2map(list.get(i).get("reward").toString());

            int key =Integer.parseInt(rewardL1.get("level1").toString());
            int value = Double.valueOf((String) Optional.ofNullable(map2.get("level1")).orElse("0")).intValue();
            if (value > key && value >0) {
                rewardL1.put("level1",value);
                rewardL1.put("levelType",Integer.valueOf(list.get(i).get("type").toString()));
            }

            int key2 =Integer.parseInt(rewardL2.get("level2").toString());
            int value2 = Double.valueOf((String) Optional.ofNullable(map2.get("level2")).orElse("0")).intValue();
            if (value2 > key2 && value >0) {
                rewardL2.put("level2",value2);
                rewardL2.put("levelType",Integer.valueOf(list.get(i).get("type").toString()));
            }

            int key3 =Integer.parseInt(rewardL3.get("level3").toString());
            int value3 = Double.valueOf((String) Optional.ofNullable(map2.get("level3")).orElse("0")).intValue();
            if (value3 > key3 && value >0) {
                rewardL3.put("level3",value3);
                rewardL3.put("levelType",Integer.valueOf(list.get(i).get("type").toString()));
            }

//            for (Map.Entry<String, Object> entrySet  :rewardL2.entrySet()
//                    ) {
//                String key =entrySet.getKey();
//                int value = Integer.parseInt((String) Optional.ofNullable(map2.get(key)).orElse("0"));
//                if (value > Integer.parseInt(entrySet.getValue().toString())) {
//                    entrySet.setValue(value);
//                    rewardL2.put("levelType",Integer.valueOf(list.get(i).get("type").toString()));
//                }
//            }
//
//
//            for (Map.Entry<String, Object> entrySet  :rewardL3.entrySet()
//                    ) {
//                String key =entrySet.getKey();
//                int value = Integer.parseInt((String) Optional.ofNullable(map2.get(key)).orElse("0"));
//                if (value > Integer.parseInt(entrySet.getValue().toString())) {
//                    entrySet.setValue(value);
//                    rewardL3.put("levelType",Integer.valueOf(list.get(i).get("type").toString()));
//                }
//            }
        }

        List<Map<String,Object>> listresult = new ArrayList<>();
        listresult.add(discount);
        listresult.add(rewardL1);
        listresult.add(rewardL2);
        listresult.add(rewardL3);
        return listresult;
    }
}