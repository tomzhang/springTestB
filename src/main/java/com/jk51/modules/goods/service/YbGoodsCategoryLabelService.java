package com.jk51.modules.goods.service;

import com.jk51.modules.goods.mapper.YbGoodsCategoryLabelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Administrator on 2018/2/1.
 */
@Service
public class YbGoodsCategoryLabelService {
    @Autowired
    private YbGoodsCategoryLabelMapper ybGoodsCategoryLabelMapper;

    private static final Logger log = LoggerFactory.getLogger(YbGoodsCategoryLabelMapper.class);

    /**
     * 查询所有的疾病标签
     * @return
     */
    public Map<String, Object> getDiseaseAllLabel() {
        Map<String, Object> returnMap = new HashMap<>();
        try {
            List<Map<String,Object>> goodsLabelList = ybGoodsCategoryLabelMapper.getDiseaseAllLabel();
            Set<String> goodsLabelSet = new HashSet<>();
            for (Map<String,Object> goods : goodsLabelList) {//获取所有的分类
                String categoryName = String.valueOf(goods.get("category_name"));
                goodsLabelSet.add(categoryName);
            }
            //获取分类下所有的标签
            List<Map<String,Object>> diseaseList = new ArrayList<>();
            for (String label : goodsLabelSet){
                List<String> labelList = new ArrayList<>();
                Map<String, Object> map = new HashMap<>();
                for (Map<String,Object> goods : goodsLabelList){
                    String categoryName = String.valueOf(goods.get("category_name"));
                    if (label.equals(categoryName)){
                        String diseaseName = String.valueOf(goods.get("disease_name"));
                        labelList.add(diseaseName);
                    }
                }
                map.put("categoryName",label);
                map.put("labelList",labelList);
                diseaseList.add(map);
            }
            returnMap.put("diseaseList", diseaseList);
            returnMap.put("msg", "查询成功");
            returnMap.put("status", 0);
            return returnMap;
        } catch (Exception e) {
            log.info("查询所有的疾病标签异常:{}", e);
            returnMap.put("status", -1);
            return returnMap;
        }
    }

    /**
     * 查询所有的功效标签
     * @return
     */
    public Map<String, Object> getEffectAllLabel() {
        Map<String, Object> returnMap = new HashMap<>();
        try {
            List<String> goodsLabelList = ybGoodsCategoryLabelMapper.getEffectAllLabel();
            returnMap.put("effectList", goodsLabelList);
            returnMap.put("msg", "查询成功");
            returnMap.put("status", 0);
            return returnMap;
        } catch (Exception e) {
            log.info("查询所有的功效标签:{}", e);
            returnMap.put("status", -1);
            return returnMap;
        }
    }

    /**
     * 根据商品ID查询商品的疾病标签与功效标签
     * @return
     */
    public Map<String, Object> getEffectAndDiseaseLabelById(Map<String, Object> params) {
        Map<String, Object> returnMap = new HashMap<>();
        try {
            Map<String,Object> goodsLabelMap = ybGoodsCategoryLabelMapper.getEffectAndDiseaseLabelById(params);
            returnMap.put("goodsLabelMap", goodsLabelMap);
            returnMap.put("msg", "查询成功");
            returnMap.put("status", 0);
            return returnMap;
        } catch (Exception e) {
            log.info("根据商品ID查询商品的疾病标签与功效标签异常:{}", e);
            returnMap.put("status", -1);
            return returnMap;
        }
    }

    /**
     * 根据商品ID修改商品的疾病标签与功效标签
     * @return
     */
    public Map<String, Object> updateEffectAndDiseaseLabelById(Map<String, Object> params) {
        Map<String, Object> returnMap = new HashMap<>();
        try {
            Integer i = ybGoodsCategoryLabelMapper.updateEffectAndDiseaseLabelById(params);
            if (i == 1){
                returnMap.put("msg", "修改成功");
                returnMap.put("status", 0);
                return returnMap;
            }else {
                returnMap.put("msg", "修改失败");
                returnMap.put("status", -1);
                return returnMap;
            }
        } catch (Exception e) {
            log.info("根据商品ID修改商品的疾病标签与功效标签异常:{}", e);
            returnMap.put("status", -1);
            return returnMap;
        }
    }



}
