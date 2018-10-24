package com.jk51.modules.goods.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.Barnd;
import com.jk51.modules.es.entity.ReturnDto;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.goods.service.BarndService;
import com.jk51.modules.goods.service.SelectGoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Controller
@RequestMapping("/goods")
public class SelectGoodsController extends WebMvcConfigurerAdapter {
    private static final Logger log = LoggerFactory.getLogger(SelectGoodsController.class);
    @Autowired
    private SelectGoodsService service;

    @Autowired
    private BarndService barndService;

    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 获取商品列表,获取列表中的商品信息
     */
    @RequestMapping(value = "/bgoodsList")
    @ResponseBody
    public Map<String, Object> queryList(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        result = service.getGoodsList(param);
        return result;
    }


    @RequestMapping(value = "/getCorrelationGoodsList")
    @ResponseBody
    public ReturnDto getCorrelationGoodsList(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        return service.getCorrelationGoodsList(param);
    }

    /**
     * 批量修改商品购买方式
     */
    @RequestMapping(value = "buyWay")
    @ResponseBody
    public Map<String, Object> buyWay(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        result = service.updateBuyWay(param);
        return result;
    }

    /**
     * 根据商品id查询商品信息及属性
     */
    @RequestMapping(value = "bgoodsOne")
    @ResponseBody
    public Map<String, Object> queryOne(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);

        result = service.getGoodsD(param);
        if (result.get("goods") != null) {
            Map goods = (Map) result.get("goods");
            try {
                int barndId = StringUtil.convertToInt((String) goods.get("barndId"));
                int siteId = StringUtil.convertToInt((String) param.get("siteId"));
                /*String userCateid = String.valueOf(param.get("userCateid"));
                //根据user_cateid查询关联分类
                if (userCateid.length() > 8) {
                    Map<String, Object> map = goodsMapper.queryRelevanceClassify(siteId, userCateid);
                    if (Objects.nonNull(map)) {
                        goods.put("relevanceClassify", String.valueOf(map.get("relevanceClassify")));
                        goods.put("relevanceReson", String.valueOf(map.get("relevanceReson")));
                    }
                }*/
                if (barndId > 0) {
                    Barnd barnd = barndService.findById(barndId, siteId);
                    if (barnd != null) {
                        goods.put("barndName", barnd.getBarndName());
                        result.put("goods", goods);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
//            Goods goods = JacksonUtils.map2pojo((Map)result.get("goods"), Goods.class);
        }
//        if (result.get(""))
        return result;
    }

    @RequestMapping(value = "bgoodsOneStatus")
    @ResponseBody
    public Map<String, Object> queryOneGoodsStatus(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> param = ParameterUtil.getParameterMap(request);

        result = service.getGoodsDStatus(param);
        return result;
    }
    /**
     * 获取商品列表,获取列表中的商品信息（不分页，用于数据导出）
     */
    @RequestMapping(value = "bgoodsListNoPage")
    @ResponseBody
    public Map getGoodsListNoPage(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Map result = new HashMap();
        result.put("goodsList", service.getGoodsListNoPage(param));
        return result;
    }

    /**
     * 根据商家ID和商品ID查询商品详细信息(数据库)，
     */
    @RequestMapping(value = "getGoodsInfo")
    @ResponseBody
    public Map<String, Object> getGoodsInfo(HttpServletRequest request) {
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        String siteId = String.valueOf(param.get("siteId"));
        JSONArray arr1 = JSON.parseArray(param.get("goodsIds") + "");
        List<Integer> goodsIds = new ArrayList<>();
        for (int i = 0; i < arr1.size(); i++) {
            goodsIds.add((Integer) arr1.get(i));
        }
        List<Map<String, Object>> mList = null;
        try {
            mList = service.getGoodsInfo(siteId, goodsIds);
        } catch (Exception e) {
            log.error("查询商品详细信息失败:{}", e);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("mList", mList);
        return map;
    }

    @RequestMapping(value = "getGoodsInfoByPage")
    @ResponseBody
    public com.jk51.commons.dto.ReturnDto getGoodsInfoByPage(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize) {
        try {
            long start = System.currentTimeMillis();
            System.out.println(start);
            Map<String, Object> param = ParameterUtil.getParameterMap(request);
            String siteId = String.valueOf(param.get("siteId"));
            JSONArray arr1 = JSON.parseArray(param.get("goodsIds") + "");
            List<Integer> goodsIds = new ArrayList<Integer>() {{
                for (int i = 0; i < arr1.size(); i++) {
                    add((Integer) arr1.get(i));
                }
            }};

            Map<String, Object> goodsInfoByPage = service.getGoodsInfoByPage(siteId, goodsIds, page, pageSize);
            long end = System.currentTimeMillis();
            System.out.println(end);
            System.out.println(end - start);
            return com.jk51.commons.dto.ReturnDto.buildSuccessReturnDto(goodsInfoByPage);
        } catch (Exception e) {
            log.error("查询分页商品详细信息失败:{}", e);
        }
        return com.jk51.commons.dto.ReturnDto.buildFailedReturnDto("查询失败");
    }


    @RequestMapping("getGoodInfoByIdsAndFields")
    @ResponseBody
    public ReturnDto getGoodInfoByIdsAndFields(int[] ids, Integer siteId, String[] fields) {
        List<Map> result = goodsMapper.findByIds(ids, siteId, fields);
        return ReturnDto.buildSuccessReturnDto(result);
    }

    @PostMapping("getGIdsByGcodes")
    @ResponseBody
    public List<String> getGIdsByGcodes(@RequestBody Map<String, Object> requestParams) {
        try {
            Integer siteId = Integer.parseInt(requestParams.get("siteId").toString());
            List<String> gCodes = JSONArray.parseArray(JacksonUtils.obj2json(requestParams.get("gCodes")), String.class);
            return goodsMapper.getGidsByGcodes(siteId, gCodes);
        } catch (Exception e) {
            log.info("根据商品获取所有商品id失败" + e.getMessage());
            return null;
        }
    }

}
