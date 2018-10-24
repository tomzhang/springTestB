package com.jk51.modules.goods.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.goods.Category;
import com.jk51.modules.goods.library.ResultMap;
import com.jk51.modules.goods.service.CategoryService;
import com.jk51.modules.goods.service.CategoryService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 描述: 51健康商家 商品类目
 */

@RequestMapping("category")
@Controller
@ResponseBody
public class CategoryController {

    private static final Log logger = LogFactory.getLog(CategoryController.class);
    @Autowired
    private CategoryService service;

    /**
     * 查询类目
     * 返回树状数据
     */
    @PostMapping("/query")
    public JSONObject queryCategoryTreeOld(String siteId, boolean fast) {
        Map param = new HashMap();
        param.put("siteId", siteId);
        List<Category> categories = service.queryCategory(param);
//        Map<Integer, List<Category>> map = handleCategories(categories);
        Map<Integer, List<Category>> map = handleCategory(categories, fast);
        JSONObject json = new JSONObject();
        json.put("siteId", siteId);
        json.put("cateId", 0);//一级类目的parentId为0
        addNode(map, json);
        return resultHelper(true, json);
    }

    private Map<Integer, List<Category>> handleCategory(List<Category> category, boolean fast) {
        if (!fast) {
            category.stream().parallel().filter(cate->StringUtil.isEmpty(cate.getImgHash())).forEach(cate->{
            //category.stream().parallel().forEach(cate->{
                Map<String,Object> param = new HashMap<>();
                param.put("siteId", cate.getSiteId());
                param.put("cateCode", cate.getCateCode());
                String img=service.querGoodsHashImg(param);
                if(!StringUtil.isEmpty(img)){
                    cate.setImgHash(img);
                }
            });
        }

        return category.parallelStream().collect(Collectors.groupingBy(Category::getParentId));
    }
    /**
     * 找出parentId相同的数据并存入map
     *
     * @param categories
     * @return
     */
    public Map<Integer, List<Category>> handleCategories(List<Category> categories) {
        Map<Integer, List<Category>> map = new HashMap<>();
        for (Category cate : categories) {
            List<Category> l = new ArrayList<>();
            for (Category c : categories) {

                if (cate.getParentId().intValue() == c.getParentId().intValue()) {
                    l.add(c);
                }
            }
            if (l.size() > 0) {
                map.put(cate.getParentId(), l);
            }
        }
        return map;
    }

    public void addNode(Map<Integer, List<Category>> map, JSONObject json) {
        List<Category> categories = map.get(json.getInteger("cateId"));
        if (categories != null && !categories.isEmpty()) {
            JSONArray array = new JSONArray();
            for (Category cate : categories) {
//                if (cate.getCateCode().length() == 12) {//商品类目第三级的cate_code长度为12
//                    cate.setImgHash(queryGoodsImgExtra(cate));
//                }
                JSONObject child = (JSONObject) JSONObject.toJSON(cate);
                array.add(child);
                addNode(map, child);
            }
            json.put("children", array);
        }
    }

    public String queryGoodsImgExtra(Category cate) {
        Map param = new HashMap();
        param.put("siteId", cate.getSiteId());
        param.put("cateCode", cate.getCateCode());
        return service.queryGoodsImgExtra(param);
    }

    @PostMapping("ins")
    public JSONObject insCategory(Category cate, String parentCode) {
        try {
            if (cate != null) {
                Map map = cateCodeHelper(cate.getSiteId(), parentCode);
                cate.setCateCode(map.get("code").toString());
                cate.setParentId(Integer.parseInt(map.get("pid").toString()));
                service.cateIns(cate);
                return resultHelper(true);
            }
        } catch (Exception e) {
            logger.error("异常", e);
        }
        return resultHelper(false);
    }

    /**
     * 根据parnat_id查询子类目
     *
     * @param parentId
     * @param siteId
     * @return
     */
    @PostMapping("queryByPid")
    public List<Category> queryCategoryByPid(@RequestParam(defaultValue = "0") Integer parentId, Integer siteId) {
        Map param = new HashMap();
        param.put("siteId", siteId);
        param.put("parentId", parentId);
        List<Category> categoryData = service.queryCategoryByPid(param);
        return categoryData;
    }

    /**
     * 生成商品分类code
     *
     * @param siteId
     * @param parentCode
     * @return
     */
    public Map cateCodeHelper(Integer siteId, String parentCode) {
        Map reParam = new HashMap();
        Integer pid = 0;
        if (!StringUtil.isEmpty(parentCode)) {
            Map parampent = new HashMap();
            parampent.put("siteId", siteId);
            parampent.put("cateName", parentCode);
            Category parentCategory = service.getByCateName(parampent);
            if (!StringUtil.isEmpty(parentCategory)) {
                pid = parentCategory.getCateId();
            }
        }
        Map param = new HashMap();
        param.put("siteId", siteId);
        param.put("pid", pid);
        String code = service.getCodeIncrease(param);

        if (StringUtil.isEmpty(code)) { //没有子类目
            param.put("cateId", pid);
            Category cate = service.queryCategoryById(param);
            String pcate = cate != null ? cate.getCateCode() : "";
            code = pcate + "1001";//父类目cateCode加上1001
        }
        reParam.put("pid", pid);
        reParam.put("code", code);
        return reParam;
    }

    @PostMapping("del")
    public JSONObject delCategory(Category cate) {
        if (cate != null) {
            Map map = new HashMap();
            map.put("siteId", cate.getSiteId());
            map.put("cateId", cate.getCateId());
            map.put("cateCode", cate.getCateCode());
            int cateNum = service.getGoodsCountByCateCode(map);
            if (cateNum > 0) {
                return resultHelper(false, "该类目下包含商品,不允许删除！");//如果该类目下包含商品则不允许删除
            } else {
                 service.cateDel(map);
            }
        }
        return resultHelper(true);
    }

    @PostMapping("update")
    public Map<String, Object> updateCategory(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Map<String, Object> result = null;
        result = service.cateUpdate(param);
        return ResultMap.successResult(result);
    }

    /**
     * 更新img_hash字段
     *必须字段siteId,imgHash,cateCode
     *
     * @param request
     * @return
     */
    @PostMapping("updateCategoryImg")
    public Map<String, Object> updateCategoryImg(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Map<String, Object> result = new HashedMap();
        int count = service.updateCategoryImg(param);
        result.put("msg", "更新 "+count + " 条记录");
        return ResultMap.successResult(result);
    }

    /**
     * 更新img_hash字段为空
     *必须字段siteId,cateCode
     *
     * @param request
     * @return
     */
    @PostMapping("delCategoryImg")
    public Map<String, Object> delCategoryImg(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Map<String, Object> result = new HashedMap();
        int count = service.delCategoryImg(param);
        result.put("msg", "更新 "+count + " 条记录");
        return ResultMap.successResult(result);
    }

    /**
     * 根据cateCode获取商品类目
     *
     * @param request
     * @return
     */
    @PostMapping("getParentsCate")
    public JSONObject getParentsCate(HttpServletRequest request) {
        Map map = ParameterUtil.getParameterMap(request);
        JSONObject result = new JSONObject();
        Category cate = service.getByCateCode(map);
        List<JSONObject> list = new ArrayList<>();
        getParentsCateHelper(list, cate);
        result.put("result", cateListSort(list));
        return result;
    }

    /**
     * 根据cateCode获取商品类别（不含父商品类别）
     *
     * @param request
     * @return
     */
    @PostMapping("getByCateCode")
    public Category getByCateCode(HttpServletRequest request) {
        Map map = ParameterUtil.getParameterMap(request);
        JSONObject result = new JSONObject();
        return service.getByCateCode(map);
    }

    public void getParentsCateHelper(List<JSONObject> list, Category cate) {
        if (cate == null) return;
        Map param = new HashMap();
        param.put("siteId", cate.getSiteId());
        param.put("cateId", cate.getParentId());//parentId
        Category cateParent = service.queryCategoryById(param);//查询父类目
        JSONObject catem = (JSONObject) JSON.toJSON(cate);
        list.add(catem);
        if (cateParent != null) {
            getParentsCateHelper(list, cateParent);
        }
    }

    public List<JSONObject> cateListSort(List<JSONObject> list) {
        List<JSONObject> r = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            r.add(list.get(i));
        }
        return r;
    }

    /**
     * 批量更新商品类目
     *
     * @param gls      逗号隔开的goods_id字符串
     * @param siteId
     * @param cateCode
     * @return
     */
    @PostMapping("updateGoodsCate")
    public JSONObject updateGoodsCate(String gls, String siteId, String cateCode) {
        List gl = new ArrayList();
        for (String str : gls.split(",")) {
            gl.add(str);
        }
        Map param = new HashMap();
        param.put("gIdList", gl);
        param.put("siteId", siteId);
        param.put("cateCode", cateCode);
        service.updateGoodsCate(param);

        return resultHelper(true);
    }

    @RequestMapping("getCategoryById")
    public Category getCategoryById(String cateId, String siteId) {
        Map param = new HashMap();
        param.put("siteId", siteId);
        param.put("cateId", cateId);
        return service.queryCategoryById(param);
    }

    @RequestMapping("getCategoryByName")
    public JSONObject getCategoryByName(String cateName, String siteId) throws Exception {
        Map param = new HashMap();
        param.put("siteId", siteId);
        param.put("cateName", cateName);
        Category category = service.getByCateName(param);
        if(category==null){
            return resultHelper(false);
        }
        return resultHelper(true,category.getCateCode());
    }


    public JSONObject resultHelper(boolean flag) {
        JSONObject result = new JSONObject();
        result.put("status", flag ? "success" : "error");
        return result;
    }

    public JSONObject resultHelper(boolean flag, JSONObject data) {
        JSONObject result = new JSONObject();
        result.put("status", flag ? "success" : "error");
        result.put("result", data);
        return result;
    }

    public JSONObject resultHelper(boolean flag, String str) {
        JSONObject result = new JSONObject();
        result.put("status", flag ? "success" : "error");
        result.put("result", str);
        return result;
    }
}
