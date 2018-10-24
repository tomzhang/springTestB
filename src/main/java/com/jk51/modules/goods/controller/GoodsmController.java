package com.jk51.modules.goods.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.distribute.OperationRecond;
import com.jk51.modules.goods.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 51健康商家 商品搜索 商品图片
 */
@RestController
@RequestMapping("goodsm")
public class GoodsmController {

    @Autowired
    private CategoryService service;

    @PostMapping("search")
    public Map<String, Object> queryGoods(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize, @RequestParam() Map map) {
        Map<String, Object> resultMap = new HashMap<>();
        Page<OperationRecond> page = PageHelper.startPage(pageNum, pageSize);

        //有无条形码处理

        List<Map<String, Object>> goodsList = service.goodsSearch(map);
        resultMap.put("recodeList", goodsList);
        resultMap.put("page", page.toPageInfo());
        return resultMap;
    }

    @PostMapping("queryByCate")
    public Map<String, Object> queryByCate(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize, String cateId, Integer siteId) {
        Map<String, Object> resultMap = new HashMap<>();
        Page<OperationRecond> page = PageHelper.startPage(pageNum, pageSize);
        Map param = new HashMap();
        param.put("siteId", siteId);
        param.put("cateId", cateId);

        List<Map<String, Object>> goodsList = service.goodsSearch(param);
        resultMap.put("recodeList", goodsList);
        resultMap.put("page", page.toPageInfo());
        return resultMap;
    }

    @PostMapping("queryImg")
    public List<Map> queryImg(HttpServletRequest request) {
        Map paramMap = ParameterUtil.getParameterMap(request);
        List<Map> resultMap = service.getByGoodsId(paramMap);
        return resultMap;
    }

    /**
     * 添加商品图片
     * 默认情况下，是否是默认图为0（不是）
     *
     * @param request
     * @return
     */
    @PostMapping("addImg")
    public JSONObject addImg(HttpServletRequest request) {
        Map paramMap = ParameterUtil.getParameterMap(request);
        if (!paramMap.containsKey("isDefault") || paramMap.get("isDefault") == null) {
            paramMap.put("isDefault", 0);
        }
        int hashNum = service.getByGoodsIdAndHash(paramMap);
        if(hashNum>0){
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("msg","图片已被设置，请上传其他图片！");
            return resultHelper(false,new JSONObject(map));
        }
        Map<String, Object> goodsList = service.getGoodsById(paramMap);
        if(goodsList.get("userCateid") != null && goodsList.get("userCateid").toString().length()==12){
            paramMap.put("cateCode",goodsList.get("userCateid"));
            paramMap.put("imgHash",paramMap.get("hash"));
            service.updateCategoryImg(paramMap);
        }
       int num=service.getByGoodsIdAndHashflag(paramMap);//是否存在已被删除的图片
        if(num>0){
            service.updateByGoodsIdAndHashFlag(paramMap);
        }else{
            service.insert(paramMap);
        }
        return resultHelper(true);
    }

    @PostMapping("setDefaultImg")
    public JSONObject setDefaultImg(HttpServletRequest request) {
        Map paramMap = ParameterUtil.getParameterMap(request);
        String str = mapKeyHelper(paramMap, "siteId", "goodsId", "imgHash");
        if (StringUtil.isNotEmpty(str)) {
            return resultHelper(false);
        }
        service.setDefaultImg(paramMap);
        return resultHelper(true);
    }

    @PostMapping("/delImg")
    public JSONObject delImg(HttpServletRequest request) {
        Map paramMap = ParameterUtil.getParameterMap(request);
        String str = mapKeyHelper(paramMap, "siteId", "goodsId", "imgHash");
        if (StringUtil.isNotEmpty(str)) {
            return resultHelper(false);
        }
        try {
            service.del(paramMap);
        } catch (BusinessLogicException e) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", e.getMessage());
            return resultHelper(false, jsonObject);
        }
        return resultHelper(true);
    }

    public String mapKeyHelper(Map map, String... args) {
        for (String param : args) {
            if (!map.containsKey(param)) return param;
        }
        return "";
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
    
    @PostMapping(value = "saveImgMulti")
    @ResponseBody
    public boolean saveImgMulti(@RequestBody Map param){
        boolean result;
//        Map param = ParameterUtil.getParameterMap(request);
//        System.out.println(param);
//        System.out.println(param.get("goods_id_old"));
        int goods_id_old = Integer.parseInt(String.valueOf(param.get("goods_id_old")));
        int goods_id = Integer.parseInt(String.valueOf(param.get("goods_id")));
        int site_id = Integer.parseInt(String.valueOf(param.get("site_id")));
        result = service.saveImgMulti(goods_id_old, goods_id, site_id);
        return result;
    }

}
