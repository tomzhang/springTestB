package com.jk51.modules.appInterface.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.coupon.tags.TagsGoodsPromotions;
import com.jk51.model.coupon.tags.TagsParam;
import com.jk51.modules.appInterface.service.AppClerkVisitService;
import com.jk51.modules.appInterface.service.AppQueryCustomerService;
import com.jk51.modules.coupon.tags.PromotionsTimeTagsFilter;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.toList;

/**
 * 回访
 *
 * @auhter zy
 * @create 2017-12-05 15:44
 */
@Controller
@ResponseBody
@RequestMapping("/visit")
public class AppClerkVisitController {

    private Logger logger = LoggerFactory.getLogger(AppClerkVisitController.class);

    @Autowired
    private AppClerkVisitService appClerkVisitService;

    @Autowired
    private AppQueryCustomerService appQueryCustomer;

    @Autowired
    private ServletContext servletContext;



    /**
     * 回访主页 页面详情
     * @param request
     * @return
     */
    @RequestMapping(value = "/getLogDetail",method = RequestMethod.POST)
    public ReturnDto getVisitDetail(HttpServletRequest request,@RequestParam(required = true, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "15") int pageSize) throws ExecutionException, InterruptedException {
        Map<String, Object> map = ParameterUtil.getParameterMap(request);
        //获取用户唯一id
        if(Objects.isNull(map.get("buyerId")) || Objects.isNull(map.get("userId"))){
            return ReturnDto.buildFailedReturnDto("信息错误，无法获取详情!");
        }
        Object id = map.get("id");
        String goods_id=appClerkVisitService.taskGoodsIdsList(Integer.valueOf(id.toString()));
        Integer buyerId = Integer.valueOf(map.get("buyerId").toString());
        Integer userId=Integer.valueOf(map.get("userId").toString());
        Integer storeId=Integer.valueOf(map.get("storeId").toString());
        Integer siteId=Integer.valueOf(map.get("siteId").toString());
        String [] goodsIds=goods_id.toString().split(",");

        //String authToken = String.valueOf(map.get("AuthToken"));
        //解析AuthToken(店员信息)
        //AuthToken auth = appClerkVisitService.parseAuthToken(authToken);
        //Integer userId =100923;// auth.getUserId();
        //Integer id = Integer.valueOf((map.get("id").toString()));
        //Integer siteId =100190; //auth.getSiteId();
        PageHelper.startPage(page,pageSize);
        Map<String, Object> resultMap = new HashedMap();
        List<Map<String,Object>> goodsList=appClerkVisitService.queryGoodsInfo(siteId, goodsIds,buyerId,userId);
        //是否购买,现价排序
        PageInfo pageInfo=new PageInfo<>(goodsList);
        goodsList=goodsList.stream().sorted(Comparator.comparing(AppClerkVisitController::comparingByLog).thenComparing(Comparator.comparing(AppClerkVisitController::comparingByShopPrice).reversed())).collect(toList());
        pageInfo.setList(goodsList);
        resultMap.put("goodsInfoMap",pageInfo);
        CompletableFuture<Map<String, Object>> future = CompletableFuture.supplyAsync(() -> {
            resultMap.put("customerInfoMap", appClerkVisitService.queryCustomerInfo(buyerId, siteId, storeId));//用户信息
            resultMap.put("dealAnalyzeMap", appClerkVisitService.queryDealAnalyze(siteId, buyerId));//交易分析
//            resultMap.put("shoppingNumMap", appClerkVisitService.queryShoppingNum(siteId, buyerId));
            resultMap.put("returnVisitLogList", appClerkVisitService.queryReturnVisitLog(siteId, buyerId));//回访记录
            return resultMap;
        });
//        String goodsIds = goodsList.stream().map(gd -> map.get("goodsId").toString()).collect(joining(","));
        TagsParam param = new TagsParam(siteId,userId,goods_id,3);
        PromotionsTimeTagsFilter tagsFilter = new PromotionsTimeTagsFilter(servletContext,param);
        try {
            tagsFilter.collection();
            tagsFilter.sorted().resolve();
            List<TagsGoodsPromotions> tags = tagsFilter.getTags();
            goodsList.forEach(gd->{
                String goodsId = gd.get("goodsId").toString();
                for (TagsGoodsPromotions tg : tags) {
                    String tgGoodsId = tg.getGoodsId();
                    if(Objects.equals(goodsId,tgGoodsId)){
                        gd.put("tags",tg.getTags());
                        break;
                    }
                }
            });
        } catch (Exception e) {
            logger.error("标签解析失败");
        }
        Map<String, Object> map1 = future.get();
        if(Objects.nonNull(map1)) {
            return ReturnDto.buildSuccessReturnDto(map1);
        }else {
            return ReturnDto.buildFailedReturnDto("暂无数据!");
        }

    }

   /* @RequestMapping("/queryGoodsActivity")
    public List<PromotionsActivity> queryGoodsActivity(){
        return appClerkVisitService.queryGoodsActivity(100190,259486,1780);
    }*/

   private static String comparingByLog(Map<String,Object> map){
       String key="buyThisLogMap";
       if(Objects.nonNull(map.get(key))){
           return "a";
       }
       return "z";
   }

   private static Integer comparingByShopPrice(Map<String,Object> map){
       String key="shopPrice";
       return Integer.valueOf(map.get(key).toString());

   }

    /**
     * 根据基本信息查询顾客列表
     * @param request
     * @return
     */
   @PostMapping("/queryCustomerByInfo")
   @ResponseBody
   public ReturnDto queryCustomerByInfo(HttpServletRequest request) {
       Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
       ReturnDto returnDto = appQueryCustomer.queryCustomerByInfo(parameterMap);
        return returnDto;
   }

    /**
     * 根据药品查询顾客列表
     * @param request
     * @return
     */
   @PostMapping("/queryCustomerByDrug")
   @ResponseBody
   public ReturnDto queryCustomerByDrug(HttpServletRequest request) {
       Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
       ReturnDto returnDto = appQueryCustomer.queryCustomerByDrug(parameterMap);
       return returnDto;
   }


    /**
     * 根据标签查询顾客列表
     * @param request
     * @return
     */
   @PostMapping("/getMembersByLabel")
   @ResponseBody
   public  ReturnDto queryCustomerByLabel(HttpServletRequest request) {
       Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
       ReturnDto returnDto = appQueryCustomer.getCustomerByLabel(parameterMap);
       return  returnDto;
   }

    /**
     * 查询顾客主页信息
     * @param request
     * @return
     */
   @PostMapping("/queryCustomerHomePage")
   @ResponseBody
   public ReturnDto getCustomerInfo(HttpServletRequest request) {
       Map<String, Object> map = ParameterUtil.getParameterMap(request);
       if (StringUtil.isEmpty(map.get("buyerId")) || StringUtil.isEmpty(map.get("siteId")) || StringUtil.isEmpty(map.get("userId")) || StringUtil.isEmpty(map.get("storeId"))) {
           return ReturnDto.buildFailedReturnDto("缺少必要参数,无法查询!");
       }
       Integer buyerId = Integer.valueOf(map.get("buyerId").toString());
       Integer siteId = Integer.valueOf(map.get("siteId").toString());
       Integer userId = Integer.valueOf(map.get("userId").toString());
       Integer storeId = Integer.valueOf(map.get("storeId").toString());
       CompletableFuture<Map<String, Object>> future = CompletableFuture.supplyAsync(() -> {
           Map<String, Object> resultMap = new HashedMap();
           resultMap.put("customerInfoMap", appClerkVisitService.getCustomerInfo(siteId,buyerId));//用户信息

//           resultMap.put("customerInfoMap", appClerkVisitService.getCustomerLabels(siteId,buyerId));//标签
            //查询用户标签
            resultMap.put("customerTag",appClerkVisitService.getCustomerTags(siteId,buyerId));
           resultMap.put("dealAnalyzeMap", appClerkVisitService.queryDealAnalyze(siteId, buyerId));//交易分析
           resultMap.put("returnVisitLogList", appClerkVisitService.queryReturnVisitLog(siteId, buyerId));//回访记录
           return resultMap;
       });
       Map<String, Object> result = null;
       try {
           result = future.get();
       } catch (Exception e) {
           e.printStackTrace();
       }
       if(Objects.nonNull(result)) {
           return ReturnDto.buildSuccessReturnDto(result);
       }else {
           return ReturnDto.buildFailedReturnDto("暂无数据!");
       }

   }

    @PostMapping("/getCustomerLabels2")
    @ResponseBody
    public Map<String, Object> getCustomerLabels2(HttpServletRequest request) {
       Integer siteId = 100190;
       Integer buyerId = 769745;
        return appClerkVisitService.getCustomerLabels(siteId,buyerId);
    }

    /**
     * 根据我的会员ID排序
     * @param map
     * @return
     */
    @PostMapping("/sortSvipMember")
    @ResponseBody
    public ReturnDto sortSvipMember(@RequestBody Map<String,Object> map) {
        ReturnDto returnDto = appClerkVisitService.sortVip(map);
        return returnDto;
    }


}
