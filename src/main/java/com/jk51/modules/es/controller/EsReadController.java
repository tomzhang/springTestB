package com.jk51.modules.es.controller;

import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.BSearchLog;
import com.jk51.modules.es.entity.*;
import com.jk51.modules.es.service.EsService;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.Map;
import java.util.Objects;

@RequestMapping("es_interface")
@RestController
public class EsReadController {

    @Autowired
    private EsService gService;

    private static final Logger logger = LoggerFactory.getLogger(EsReadController.class);
    @RequestMapping("getGoodsListByAdmin")
    @SuppressWarnings("all")
    public GoodsInfosResp getGoodsListByAdmin(@RequestBody GoodsInfosAdminReq gInfosReq) {
        logger.info("****getGoodsListByAdmin request:{}", gInfosReq.toString());
        try {
            ObjectMapper om = new ObjectMapper();
            //logger.info("getGoodsListByAdmin request json:{}",om.writeValueAsString(gInfosReq));
            //新增搜索记录
            if(StringUtil.isNotBlank(gInfosReq.getPhone_num())){
                String phone_num = gInfosReq.getPhone_num();
                int i = Integer.parseInt(gInfosReq.getDbname().substring(7));
                String goods_name = gInfosReq.getGoods_name();
                //插入之前判断关键词是不是和上一条相同
                Map<String,Object> record = gService.queryLastRecord(i,phone_num);
                Object key_word = "";
                Object is_clean = "";
                if(record != null) {
                    key_word = record.get("key_word") == null? "" : record.get("key_word");
                    is_clean = record.get("is_clean") == null? "" : record.get("is_clean");
                }
                if(StringUtil.isNotEmpty(goods_name)) {
                    if(!goods_name.equals(key_word) || (goods_name.equals(key_word) && "1".equals(is_clean))) {
                        BSearchLog bSearchLog=new BSearchLog();
                        bSearchLog.setMobile(phone_num);
                        bSearchLog.setSiteId(i);
                        bSearchLog.setKeyWord(goods_name);
                        gService.insert(bSearchLog);
                    }

                }

            }
            if (StringUtils.isBlank(gInfosReq.getKeyword())) {
                return gService.getGoodsListByAdmin(gInfosReq);
            } else {
                return gService.getGoodsListByUser(gInfosReq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return GoodsInfosResp.buildSystemErrorResp();
    }

    @RequestMapping("/getGoodsListByGrugName")
    @SuppressWarnings("all")
    @ResponseBody
    public GoodsInfosResp getGoodsListByDrugName(@RequestBody GoodsInfosAdminReq gInfosReq) {
        logger.info("****getGoodsListByAdmin request:{}", gInfosReq.toString());
        try {
            ObjectMapper om = new ObjectMapper();
            //logger.info("getGoodsListByAdmin request json:{}",om.writeValueAsString(gInfosReq));
                return gService.getGoodsListByGrugName(gInfosReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return GoodsInfosResp.buildSystemErrorResp();
    }

    /**
     * 查询推荐商品
     * @param gInfosReq
     * @return
     */
    @RequestMapping("/getRecommendGoodsList")
    @ResponseBody
    public GoodsInfosResp getRecommendGoodsList(@RequestBody GoodsInfosAdminReq gInfosReq) {
        logger.info("****getGoodsListByAdmin request:{}", gInfosReq.toString());
        try {
            return gService.getRecommendGoodsList(gInfosReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return GoodsInfosResp.buildSystemErrorResp();
    }

    /**
     * 查询出销量前四的
     * @param gInfosReq
     * @return
     */
    @RequestMapping(value = "/ecBgoodsListForSales",method = RequestMethod.POST)
    @SuppressWarnings("all")
    public GoodsInfosResp getGoodsListForSales(@RequestBody GoodsInfosAdminReq gInfosReq) {
        logger.info("****getGoodsListByAdmin request:{}", gInfosReq.toString());
        try {
            return gService.getGoodsListForSales(gInfosReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return GoodsInfosResp.buildSystemErrorResp();
    }


    @RequestMapping("searchGoodsExtra")
    public GoodsInfosResp searchAppointGoods(@RequestBody GoodsInfosAdminReq gInfosReq) {
        try {
            if (StringUtils.isBlank(gInfosReq.getKeyword())) {
                return gService.searchAppointGoods(gInfosReq);
            } else {
                return gService.getGoodsListByUser(gInfosReq);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return GoodsInfosResp.buildSystemErrorResp();
    }

    @RequestMapping("getGoodsById")
    public GoodsInfoResp getGoodsById(@RequestBody()GoodsInfoReq gInfoReq) {
        logger.info("****getGoodsById method request:{}", gInfoReq.toString());
        try {
            return gService.getGoodsById(gInfoReq);
        } catch (Exception e) {
            logger.error("****getGoodsById method error:{}",e.getMessage());
            e.printStackTrace();
        }
        return GoodsInfoResp.buildSystemErrorResp();
    }


    @RequestMapping("suggest")
    public ReturnDto suggest(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        try {
            return gService.querySuggest(param.get("key") + "", param.get("siteId") + "");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("****suggest method error:{}", e.getMessage());
        }
        return ReturnDto.buildSystemErrorReturnDto();
    }

    @RequestMapping("delGoodsById/{index}/{type}/{id}")
    public ReturnDto delGoodsById(@PathParam("index")String index, @PathParam("type")String type, @PathParam("id")String id){
        return gService.delGoodsById(index, type, id);
    }

    @RequestMapping("delGoodsByType/{index}/{type}")
    public ReturnDto delGoodsByType(@PathParam("index")String index,@PathParam("type")String type){
        return gService.delGoodsByType(index, type);
    }

    @RequestMapping("insert")
    public ReturnDto insert (BSearchLog bSearchLog){
        return  gService.insert(bSearchLog);
    }

    @RequestMapping(value = "querySearchLog" ,method = RequestMethod.POST)
    public ReturnDto querySearchLog(HttpServletRequest request){
        Map param = ParameterUtil.getParameterMap(request);
        if(Objects.nonNull(param.get("siteId")) && Objects.nonNull(param.get("phone_num"))){
            return gService.searchLogList(param.get("siteId").toString(),param.get("phone_num").toString());
        }else{
            return ReturnDto.buildSystemErrorReturnDto();
        }

    }

    @RequestMapping(value = "batchUpdate" ,method = RequestMethod.POST)
    public ReturnDto batchUpdate(HttpServletRequest request){
        Map param = ParameterUtil.getParameterMap(request);
        if(Objects.nonNull(param.get("siteId")) && Objects.nonNull(param.get("phone_num"))){
           return gService.batchUpdate(param.get("siteId").toString(),param.get("phone_num").toString());
        }else{
            return ReturnDto.buildSystemErrorReturnDto();
        }
    }

    @RequestMapping(value = "queryBtopSearch",method = RequestMethod.POST)
    public ReturnDto queryBtopSearch(String siteId){
        if(null != siteId && !"".equals(siteId)){
            return gService.queryBtopSearch(siteId);
        }else{
            return  ReturnDto.buildSystemErrorReturnDto();
        }
    }
    
}
