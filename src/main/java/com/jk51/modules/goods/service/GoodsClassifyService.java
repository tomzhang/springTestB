package com.jk51.modules.goods.service;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.Goods;
import com.jk51.model.goods.YbCategory;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.goods.mapper.YbCategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:商品分类
 * 作者: gaojie
 * 创建日期: 2017-02-28
 * 修改记录:
 */
@Service
public class GoodsClassifyService {

    private Logger logger = LoggerFactory.getLogger(GoodsClassifyService.class);

    @Autowired
    private YbCategoryMapper ybCategoryMapper;
    @Autowired
    private YbCategoryService ybCategoryService;
    @Autowired
    private GoodsMapper goodsMapper;

    //添加商品分类
    @CacheEvict(value = "ybCategory",allEntries = true)
    public ReturnDto addGoodsClassify(String parent_id, String cate_name) {


        YbCategory category = new YbCategory();
        category.setParent_id(StringUtil.convertToInt(parent_id));
        category.setCate_name(cate_name);
        int num = ybCategoryService.addGoodsClassify(category);

        //cate_code需要唯一的字段,用cate_id存储
        category.setCate_code(category.getCate_id().toString());
        ybCategoryService.updateByprimaryKey(category);

        if (num == 1) {
            return ReturnDto.buildStatusOK();
        } else {
            return ReturnDto.buildStatusERRO("添加商品分类失败");
        }


    }

    //删除商品分类，有对应的商品时不能删除
    @CacheEvict(value = "ybCategory",allEntries = true)
    public Map<String, Object> category_delete(String cate_id) {

        Map<String, Object> result = new HashMap<String, Object>();

        List<Goods> goodsList = goodsMapper.getListByUserCateId(cate_id);
        if (!StringUtil.isEmpty(goodsList)) {
            result.put("status", "ERROR");
            result.put("errorMessage", "该商品分类有对应的商品，不能删除");
            return result;
        }

        int num = ybCategoryMapper.deleteByCate_id(cate_id);
        if (num == 1) {
            result.put("status", "OK");
        } else {
            result.put("status", "ERROR");
            result.put("errorMessage", "删除商品分类失败");
        }

        return result;
    }

    @Transactional
    @CacheEvict(value = "ybCategory",allEntries = true)
    public Map<String, Object> category_batchEdit(String category_list) {

        Map<String, Object> result = new HashMap<String, Object>();
        List<LinkedHashMap<String, Object>> list = JacksonUtils.json2listMap(category_list);
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            String cate_id = (String) map.get("cate_id");
            String cate_name = (String) map.get("cate_name");
            int num = ybCategoryMapper.updateCateName(cate_id, cate_name);

            if (num != 1) {
                String msg = "";
                if (result.get("msg") != null) {
                    msg = (String) result.get("msg");
                }
                result.put("msg", msg + "," + cate_name + ":修改失败");
                throw new RuntimeException("cate_name修改失败");
            }
        }

        if (result.get("msg") != null) {
            result.put("status", "ERROR");
        } else {
            result.put("status", "OK");
        }

        return result;
    }


    @Cacheable(value = "ybCategory",keyGenerator = "defaultKeyGenerator")
    public Map<String, Object> getAllGoodsClassify() {

        Map<String, Object> result = new HashMap<String, Object>();
        List<YbCategory> ybCategoryList = ybCategoryMapper.getAllGoodsClassify();
        result.put("ybCategoryList", ybCategoryList);
        return result;
    }

    @Cacheable(value = "ybCategory",keyGenerator = "defaultKeyGenerator")
    public Map<String,Object> getGoodsClassifyByParentId(String parent_id) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<YbCategory> ybCategoryList = ybCategoryMapper.getGoodsClassifyByParentId(parent_id);
        result.put("ybCategoryList", ybCategoryList);
        return result;
    }
}
