package com.jk51.modules.goods.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.message.OldStyle;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.goods.PageData;
import com.jk51.model.goods.YbCategory;
import com.jk51.model.goods.YbGoodsGrid;
import com.jk51.model.goods.YbImagesAttr;
import com.jk51.modules.goods.dto.JoinCateDto;
import com.jk51.modules.goods.library.ResultMap;
import com.jk51.modules.goods.service.YbGoodsService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yeah
 * 创建日期: 2017-02-17
 * 修改记录:
 */
@Controller
@RequestMapping("/goods")
public class YbGoodsController {
    @Autowired
    private YbGoodsService ybGoodsService;

    private ObjectMapper MAPPER = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(YbGoodsController.class);

    /**
     * 后台管理系统 商品列表 分页、多条件查询商品列表
     * http://localhost:8765/goods/queryList
     *
     * @param ybGoodsResult
     * @param bindingResult
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/queryList")
    @ResponseBody
    public Map<String, Object> queryGoodsList(YbGoodsGrid ybGoodsResult, BindingResult bindingResult,
                                              @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize,
                                              String sDate, String eDate) {
        Map<String, Object> map = new HashedMap();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            if (StringUtil.isNotEmpty(sDate)) ybGoodsResult.setStart_date(format.parse(sDate + " 00:00:00"));
            if (StringUtil.isNotEmpty(eDate)) ybGoodsResult.setEnd_date(format.parse(eDate + " 23:59:59") );

            PageInfo<?> pageInfo = this.ybGoodsService.queryGoodsList(page, pageSize, ybGoodsResult);
            map.put("status", true);
            Map<String, Object> result = new HashedMap();
            map.put("result", result);
            result.put("current", pageInfo.getPageNum());
            result.put("before", pageInfo.getPrePage());
            result.put("next", pageInfo.getNextPage());
            result.put("total_pages", pageInfo.getPages());
            result.put("total_items", pageInfo.getTotal());
            result.put("items", pageInfo.getList());
        } catch (Exception e) {
            map.put("status", false);
            logger.error("查询商品列表错误,错误是" + e);
        }
        return map;
    }

    @RequestMapping(value = "/queryList2")
    @ResponseBody
    public Map<String, Object> queryGoodsList2(YbGoodsGrid ybGoodsResult, BindingResult bindingResult,
                                              @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize,
                                              String sDate, String eDate) {
        Map<String, Object> map = new HashedMap();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            if (StringUtil.isNotEmpty(sDate)) ybGoodsResult.setStart_date(format.parse(sDate + " 00:00:00"));
            if (StringUtil.isNotEmpty(eDate)) ybGoodsResult.setEnd_date(format.parse(eDate + " 23:59:59") );

            PageInfo<?> pageInfo = this.ybGoodsService.queryGoodsList2(page, pageSize, ybGoodsResult);
            map.put("status", true);
            Map<String, Object> result = new HashedMap();
            map.put("result", result);
            result.put("current", pageInfo.getPageNum());
            result.put("before", pageInfo.getPrePage());
            result.put("next", pageInfo.getNextPage());
            result.put("total_pages", pageInfo.getPages());
            result.put("total_items", pageInfo.getTotal());
            result.put("items", pageInfo.getList());
        } catch (Exception e) {
            map.put("status", false);
            logger.error("查询商品列表错误,错误是" + e);
        }
        return map;
    }

    /**
     * 后台管理系统 商品列表 分页、多条件查询商品列表
     * http://localhost:8765/goods/queryList
     *
     * @param ybGoodsResult
     * @param bindingResult
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/queryMerchantList")
    @ResponseBody
    public Map<String, Object> queryMerchantList(YbGoodsGrid ybGoodsResult, BindingResult bindingResult,
                                              @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize,
                                              String sDate, String eDate) {
        Map<String, Object> map = new HashedMap();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            if (StringUtil.isNotEmpty(sDate)) ybGoodsResult.setStart_date(format.parse(sDate + " 00:00:00"));
            if (StringUtil.isNotEmpty(eDate)) ybGoodsResult.setEnd_date(format.parse(eDate + " 23:59:59") );

            PageInfo<?> pageInfo = this.ybGoodsService.queryMerchantLis(page, pageSize, ybGoodsResult);
            map.put("status", true);
            Map<String, Object> result = new HashedMap();
            map.put("result", result);
            result.put("current", pageInfo.getPageNum());
            result.put("before", pageInfo.getPrePage());
            result.put("next", pageInfo.getNextPage());
            result.put("total_pages", pageInfo.getPages());
            result.put("total_items", pageInfo.getTotal());
            result.put("items", pageInfo.getList());
        } catch (Exception e) {
            map.put("status", false);
            logger.error("查询商品列表错误,错误是" + e);
        }
        return map;
    }



    /**
     * 批量删除商品
     * http://localhost:8765/goods/batchDel
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/batchDel")
    @ResponseBody
    @CacheEvict(value = "ybGoods", allEntries = true)
    public Map<String, Object> batchDelGoods(String ids) {
        String[] gids = StringUtils.split(ids, ",");
        Map<String, Object> map = new HashedMap();
        Map<String, Object> result = new HashedMap();
        try {
            this.ybGoodsService.batchDel(gids);
            map.put("status", true);
            map.put("result", result);
            result.put("msg", "成功删除" + gids.length + "条信息");
            return map;
        } catch (Exception e) {
            logger.error("批量删除商品错误,错误是" + e);
            map.put("status", false);
            map.put("result", result);
            result.put("msg", "删除失败");
            return map;
        }
    }

    /**
     * 跳转到商品编辑页面（商品数据回显，包含商品详情和商品扩展表数据）
     * http://localhost:8765/goods/edit?itemid=69487
     *
     * @param goodId
     * @return
     */
    @RequestMapping(value = "edit")
    @ResponseBody
    public Map<String, Object> toEditPage(@RequestParam(required = true, value = "goodId") Integer goodId) {
        Map<String, Object> data = this.ybGoodsService.querySingleGoodDetail(goodId);
        return data;
    }

    /**
     * 更新或者新增商品数据（包含商品详情和商品扩展表数据）
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "editGood")
    @ResponseBody
    @CacheEvict(value = "ybGoods", allEntries = true)
    public Map<String, Object> updateItem(HttpServletRequest request) {
        PageData pageData = new PageData();
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        pageData.putAll(parameterMap);
        if (StringUtils.isNotEmpty(pageData.getString("goods_id"))) {
            //商品更新
            try {
                this.ybGoodsService.updateGood(pageData);
                return new ResultMap("ok", new HashedMap());//商品更新返回值
            } catch (Exception e) {
                logger.error("更新商品失败,错误是" + e);
                return new ResultMap("error", "更新商品失败");
            }

        } else {
            //商品新增返回值
            this.ybGoodsService.saveGood(pageData);
            Map result = new HashMap();
            result.put("goods_id", pageData.get("goods_id"));
            return new ResultMap("ok", result);
        }

    }

    /**
     * 批量更新图片处理状态为已处理
     * http://localhost:8765/goods/batchUpdateImg
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/batchUpdateImg")
    @ResponseBody
    @CacheEvict(value = "ybGoods", allEntries = true)
    public Map<String, Object> batchYbzfUpdateImg(String ids) {
        Map<String, Object> map = new HashedMap();
        Map<String, Object> result = new HashedMap();
        map.put("result", result);
        try {
            String[] gids = StringUtils.split(ids, ",");
            this.ybGoodsService.batchUpdateImg(gids);
            map.put("status", true);
            result.put("msg", "成功处理" + gids.length + "条图片信息");
            return map;
        } catch (Exception e) {
            logger.error("批量处理图片状态错误,错误是" + e);
            map.put("status", true);
            result.put("msg", "批量处理失败");
            return map;
        }
    }

    /**
     * 跳转到图片更新页面（图片回显）
     * joinimg?itemid=69487
     *
     * @param goodId
     * @return
     */
    @RequestMapping(value = "editImg")
    @ResponseBody
    public Map<String, Object> toEditPicPage(@RequestParam(required = true, value = "goodId") Integer goodId) {
        try {
            List<YbImagesAttr> imgs = this.ybGoodsService.queryImgsByGoodId(goodId);
            Map<String, Object> result = new HashMap<>();
            result.put("imgs", imgs);
            return new ResultMap("ok", result);
        } catch (Exception e) {
            logger.error("获取图片信息失败,错误是" + e);
            return new ResultMap("error", "获取图片错误");
        }

    }

    /**
     * 删除单张图片
     *
     * @param goodId
     * @param hashId
     * @return
     */
    @RequestMapping(value = "delImg")
    @ResponseBody
    @CacheEvict(value = "ybGoods", allEntries = true)
    public Map<String, Object> delSinglePic(@RequestParam(required = true, value = "goodId") Integer goodId,
                                            @RequestParam(required = true, value = "hashId") String hashId) {
        Map<String, Object> map = new HashedMap();
        Map<String, Object> result = new HashedMap();
        map.put("result", result);
        try {
            this.ybGoodsService.delSinglePic(goodId, hashId);
            map.put("status", true);
            result.put("msg", "成功删除图片");
            return map;
        } catch (Exception e) {
            logger.error("删除单张商品失败,错误是" + e);
            map.put("status", false);
            result.put("msg", "删除图片失败");
            return map;
        }
    }

    /**
     * 设置商品主图
     *
     * @param
     * @return
     */
    @RequestMapping(value = "setDefaultImg")
    @ResponseBody
    @CacheEvict(value = "ybGoods", allEntries = true)
    public Map<String, Object> setDefaultImg(@RequestParam(required = true, value = "goodId") Integer goodId,
                                             @RequestParam(required = true, value = "hashId") String hashId) {
        logger.info("开始设置主图");
        try {
            this.ybGoodsService.setDefaultImg(goodId, hashId);
            return new ResultMap("ok", new HashedMap());
        } catch (Exception e) {
            logger.error("设置商品主图失败,错误是" + e);
            return new ResultMap("error", "设置商品主图失败");
        }

    }

    /**
     * 获得焦点查看该商品所有图片
     *
     * @param goodId
     * @return
     */
    @RequestMapping(value = "catImgs")
    @ResponseBody
    public Map<String, Object> queryImgs(@RequestParam("good_id") Integer goodId) {

        try {
            List<YbImagesAttr> imgs = this.ybGoodsService.queryImgsByGoodId(goodId);
            Map<String, Object> result = new HashMap<>();
            result.put("imgs", imgs);
            return ResultMap.successResult(result);
        } catch (Exception e) {
            logger.error("获取商品所有图片错误,错误是" + e);
            return new ResultMap("error", "查看所有商品失败");
        }
    }

    /**
     * 保存商品图片
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/saveImg", method = RequestMethod.POST)
    @ResponseBody
    @CacheEvict(value = "ybGoods", allEntries = true)
    public Map<String, Object> saveImg(HttpServletRequest request) {
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        YbImagesAttr ybImagesAttr = new YbImagesAttr();
        try {
            BeanUtils.populate(ybImagesAttr, parameterMap);
            this.ybGoodsService.saveImg(ybImagesAttr);
            return ResultMap.successResult(new HashMap());
        } catch (Exception e) {
            logger.error("保存图片失败", e);
            return ResultMap.errorResult("保存图片失败");
        }
    }

    @PostMapping("/restore")
    @ResponseBody
    public String restore(@RequestParam("goods_id") int goodsId) {
        boolean isRestore = ybGoodsService.restore(goodsId);
        Map result = new HashMap();

        if (isRestore) {
            result.put("msg", "商品还原成功");
            return OldStyle.render(result);
        }

        return OldStyle.render(1001, "操作失败");
    }

    /**
     * 修改商品分类
     *
     * */
    @RequestMapping("/updateGoodsCate")
    @ResponseBody
    public Map<String,Object> updateGoodsCate(HttpServletRequest request) {

        String gls = request.getParameter("gls");
        String cateId = request.getParameter("cateId");

        return ybGoodsService.updateGoodsCate(gls,cateId);
    }

    @RequestMapping("/category/{cateCode:\\d{4,12}}")
    @ResponseBody
    public Object queryCateByCateId(@PathVariable long cateCode) {
        try {
            YbCategory ybCategory = ybGoodsService.queryCateByCateCode(cateCode);
            if (ybCategory != null) {
                return ReturnDto.buildSuccessReturnDto(ybCategory);
            }
            return ReturnDto.buildFailedReturnDto("没有对应的记录");
        } catch (Exception e) {
            logger.debug("查询分类异常{}", e.getMessage());
            return ReturnDto.buildFailedReturnDto("查询异常");
        }
    }

    /**
     * 根据cateCode获取siteId对应记录
     * @param joinCateDto
     * @return
     */
    @RequestMapping("/category/join51jkByCode")
    @ResponseBody
    public Object join51jkByCode(@Valid @RequestBody JoinCateDto joinCateDto) {
        try {
            Map category = ybGoodsService.join51jkByCode(joinCateDto);
            if (category != null) {
                return ReturnDto.buildSuccessReturnDto(category);
            }
            return ReturnDto.buildFailedReturnDto("没有对应的记录");
        } catch (Exception e) {
            logger.debug("查询分类异常{}", e.getMessage());
            return ReturnDto.buildFailedReturnDto("查询异常");
        }
    }

    /**
     * 查询商品条形码是否存在
     *
     */
    @RequestMapping("barCode")
    @ResponseBody
    public Map join51jkByCode(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        return ybGoodsService.barCodeOne(param.get("barCode")+"");
    }
}
