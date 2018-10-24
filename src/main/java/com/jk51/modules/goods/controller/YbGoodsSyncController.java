package com.jk51.modules.goods.controller;

import com.github.pagehelper.PageInfo;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.goods.PageData;
import com.jk51.model.goods.YbGoodsSyncGrid;
import com.jk51.modules.goods.library.ResultMap;
import com.jk51.modules.goods.service.YbGoodsService;
import com.jk51.modules.goods.service.YbGoodsSyncService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: yeah
 * 创建日期: 2017-02-24
 * 修改记录:
 */
@Controller
@RequestMapping("/goodsSync")
public class YbGoodsSyncController {

    @Autowired
    YbGoodsSyncService ybGoodsSyncService;
    @Autowired
    YbGoodsService ybGoodsService;

    private static final Logger logger = LoggerFactory.getLogger(YbGoodsSyncController.class);

    /**
     * 分页条件关联查询（商品更新页）
     *
     * @param ybGoodsSyncGrid
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> querySyncList(YbGoodsSyncGrid ybGoodsSyncGrid,
                                      @RequestParam(value = "page", defaultValue = "1", required = true) Integer page,
                                      @RequestParam(value = "pageSize", defaultValue = "15", required = true) Integer pageSize) {
        Map<String, Object> map = new HashedMap();
        try {
            logger.debug("开始查询第[{}]页数据", page);
            PageInfo<?> pageInfo = this.ybGoodsSyncService.querySyncList(ybGoodsSyncGrid, page, pageSize);
            logger.debug("查询第[{}]页，一共有[{}]条数据", pageInfo.getPageNum(), pageInfo.getSize());
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
            logger.error("获取商品列表失败,错误是" + e);
            map.put("status", false);
        }
        return map;
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "batchDel", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> batchDel(@RequestParam("ids") String ids) {
        String[] good_ids = StringUtils.split(ids, ",");
        try {
            this.ybGoodsSyncService.batchDelSyncGoods(good_ids);
            logger.debug("批量删除商品[{}]成功！", ids);
            return new ResultMap("ok", new HashedMap());
        } catch (Exception e) {
            logger.error("批量删除商品[{}]失败,错误是" + e, ids);
            return new ResultMap("error", "批量删除失败");
        }
    }

    /**
     * 获取yb_config_goods_sync单条或者全部数据
     *
     * @param detail_tpl
     * @return
     */
    @RequestMapping(value = "getTings", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> getTings(@RequestParam(value = "detail_tpl",required = false) Integer detail_tpl) {
        Map<String, Object> result = new HashedMap();
        try {
            List<PageData> items = this.ybGoodsSyncService.queryGoodsConfigList(detail_tpl);
            for (PageData item:items) {
                if (10==Integer.parseInt(item.get("detail_tpl").toString())){
                    item.put("detail_tpl","药品类模板");
                }else if(20==Integer.parseInt(item.get("detail_tpl").toString())){
                    item.put("detail_tpl","其他类模板");
                }else if(30==Integer.parseInt(item.get("detail_tpl").toString())){
                    item.put("detail_tpl","器械类模板");
                }else if(40==Integer.parseInt(item.get("detail_tpl").toString())){
                    item.put("detail_tpl","保健品模板");
                }else if(50==Integer.parseInt(item.get("detail_tpl").toString())){
                    item.put("detail_tpl","2.0版本废弃");
                }else if(60==Integer.parseInt(item.get("detail_tpl").toString())){
                    item.put("detail_tpl","化妆品模板");
                }else if(70==Integer.parseInt(item.get("detail_tpl").toString())){
                    item.put("detail_tpl","中药材模板");
                }else if(70==Integer.parseInt(item.get("detail_tpl").toString())){
                    item.put("detail_tpl","礼品类模板");
                }else{
                    item.put("detail_tpl","消毒类模板");
                }
            }
            result.put("items", items);
            return new ResultMap("ok", result);
        } catch (Exception e) {
            logger.error("获取商品同步表信息失败,错误是" + e);
        }
        return new ResultMap("error", "获取商品同步表信息失败");
    }

    /**
     * 设置各个模板是否允许更新、新增
     * 设置各个模板更新字段列表 设置可更新的字段主键
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "setTings", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> setTings(HttpServletRequest request) {
        PageData pageData = new PageData();
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        pageData.putAll(parameterMap);
        try {
            this.ybGoodsSyncService.setGoodConfig(pageData);
            return new ResultMap("ok", new HashedMap());
        } catch (Exception e) {
            logger.error("设置各个模板是否允许更新,各个模板更新字段列表失败,错误是" + e);
        }
        return new ResultMap("error", "设置各个模板是否允许更新,各个模板更新字段列表失败");
    }

    /**
     * 查询yb_goods yb_goodsextd与yb_goods_sync_draft yb_goods_sync_draft_ext两条数据（放到页面进行对比）
     *
     * @param
     * @return
     */
    @RequestMapping(value = "showDiff")
    @ResponseBody
    public  Map<String, Object> showDiff(HttpServletRequest request) {
        String id =request.getParameter("id");
        int sync_draft_id=Integer.parseInt(id);
        try {
            Map<String, Object> syncMap = this.ybGoodsSyncService.querySyncGoodById(sync_draft_id);
            return new ResultMap("ok", syncMap);
        } catch (Exception e) {
            logger.error("获取对比商品信息失败,错误是" + e);
        }
        return new ResultMap("error", "获取对比商品信息失败");
    }


    /**
     * 将商户商品数据更新到后台商品表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "updateDiff", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> updateDiff(HttpServletRequest request) {
        PageData pageData = new PageData();
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        pageData.putAll(parameterMap);
        Map<String,Object> reMap = new HashMap<String,Object>();
        try {
            String resault =this.ybGoodsService.updateGoodOfMerchant(pageData);
            if (resault=="notAllowUpdate"){

                reMap.put("status",resault);
                return  reMap;
            }
            return ResultMap.successResult(new HashedMap());
        } catch (Exception e) {
            logger.error("将商户商品数据更新到后台商品表失败,错误是" + e);
        }
        return ResultMap.errorResult("将商户商品数据更新到后台商品表失败");
    }

    /**
     * 将商户商品数据作为一条新数据存入后台商品表
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "insertDiff", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> insertDiff(HttpServletRequest request) {
        PageData pageData = new PageData();
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        pageData.putAll(parameterMap);
        Map<String,Object> reMap = new HashMap<String,Object>();
        try {
            String resault =this.ybGoodsService.saveGoodOfMerchant(pageData);
            if (resault=="notAllowAdd"){

                reMap.put("status",resault);
                return  reMap;
            }
            return ResultMap.successResult(new HashedMap());
        } catch (Exception e) {
            logger.error("将商户商品数据作为一条新数据存入后台商品表失败,错误是" + e);
        }
        return ResultMap.errorResult("将商户商品数据作为一条新数据存入后台商品表失败");
    }

    /**
     * 忽略商户商品的更新
     *
     * @param
     * @return
     */
    @RequestMapping(value = "ignoreUpdate", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> ignoreUpdate(HttpServletRequest request) {
        Map<String,Object> paramMap = ParameterUtil.getParameterMap(request);
        String syncId =paramMap.get("syncGood_id").toString();
        int good_sync_id=Integer.parseInt(syncId);
        try {
            this.ybGoodsSyncService.ignoreUpdate(good_sync_id);
            return ResultMap.successResult(new HashedMap());
        } catch (Exception e) {
            logger.error("忽略商户商品的更新失败,错误是" + e);
        }
        return ResultMap.errorResult("忽略商户商品的更新失败");
    }

    /**图片回显（商品更新）
     * @param
     * @return
     */
    @RequestMapping(value = "imgdiff", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getImgs(Integer id) {
        return this.ybGoodsSyncService.getSyncImgs(id);
    }

    /**单张图片更新
     * @param
     * @return
     */
    @RequestMapping(value = "updatePic")
    @ResponseBody
    public Map<String, Object> updatePic(HttpServletRequest request) {
        return this.ybGoodsSyncService.updatePic(ParameterUtil.getParameterMap(request));
    }

    /**标记商品图片为已处理
     * @param
     * @return
     */
    @RequestMapping(value = "handleImgStatus")
    @ResponseBody
    public Map<String, Object> handleUpdateImgStatus(HttpServletRequest request) {
        return this.ybGoodsSyncService.handleUpdateImgStatus(ParameterUtil.getParameterMap(request));
    }

    /**批量同步商品情况查询
     *
     * @return
     */
    @RequestMapping(value = "goodsSyncQuery", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> goodsSyncQuery(HttpServletRequest request,
                                              @RequestParam(value = "page", defaultValue = "1", required = true) Integer page,
                                              @RequestParam(value = "pageSize", defaultValue = "15", required = true) Integer pageSize) {
        Map<String, Object> map = new HashedMap();
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        try {

            PageInfo<?> pageInfo = this.ybGoodsSyncService.goodsSyncQueryList(parameterMap, page, pageSize);

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
            logger.error("获取批量商品同步列表失败,错误是" + e);
            map.put("status", false);
        }
        return map;
    }

    /**
     * 批量删除批量同步报表中商品
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/batchSyncDel")
    @ResponseBody
    public Map<String, Object> batchSyncDelGoods(String ids) {
        String[] gids = StringUtils.split(ids, ",");
        Map<String, Object> map = new HashedMap();
        Map<String, Object> result = new HashedMap();
        try {
            this.ybGoodsSyncService.batchSyncDel(gids);
            map.put("status", true);
            map.put("result", result);
            result.put("msg", "成功删除" + gids.length + "条信息");
            return map;
        } catch (Exception e) {
            logger.error("批量删除批量同步报表中商品,错误是" + e);
            map.put("status", false);
            map.put("result", result);
            result.put("msg", "删除失败");
            return map;
        }
    }
}
