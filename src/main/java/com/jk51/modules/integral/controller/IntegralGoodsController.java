package com.jk51.modules.integral.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.CityHasStores;
import com.jk51.model.order.BestStoreQueryParams;
import com.jk51.model.order.GoodsInfo;
import com.jk51.model.order.Store;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.index.service.StoresService;
import com.jk51.modules.integral.service.IntegralGoodsService;
import com.jk51.modules.order.service.DistributeOrderService;
import com.jk51.modules.order.service.OrderService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by guosheng on 2017/6/2.
 */
@Controller
@ResponseBody
@RequestMapping("/integralGoods")
public class IntegralGoodsController {

    private static final Logger logger = LoggerFactory.getLogger(IntegralGoodsController.class);

    @Autowired
    private IntegralGoodsService integralGoodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DistributeOrderService distributeOrderService;
    @Autowired
    private StoresService storesService;
    @Autowired
    GoodsMapper goodsMapper;

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public ReturnDto queryIntegralGoods( Integer page,
                                         Integer pageSize, HttpServletRequest request){
        List<Map<String,Object>> list;
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);

        try {
            if (page!=null && pageSize!=null){
                PageHelper.startPage(page, pageSize);
            }
            list= this.integralGoodsService.queryIntegralGoods(parameterMap);


        } catch (Exception e) {
            logger.error("获取积分商品失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("查询积分商品出错");
        }
        return ReturnDto.buildSuccessReturnDto(new PageInfo<>(list));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String  deleteIntegralGoods(Integer id){

        try {
            String code = integralGoodsService.deleteIntegralGoods(id);
            return  code;
        } catch (Exception e) {
            logger.error("删除积分商品失败,错误是" + e);

        }
        return "500";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public String  updateIntegralGoods(HttpServletRequest request){
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        try {
            String code = integralGoodsService.updateIntegralGoods(parameterMap);
            return  code;
        } catch (Exception e) {
            logger.error("更新积分商品失败,错误是" + e);

        }
        return "500";
    }

    @RequestMapping(value = "/queryOne")
    @ResponseBody
    public ReturnDto getOneIntegralGoods(Integer siteId,Integer goodsId){
        try {
            return ReturnDto.buildSuccessReturnDto(integralGoodsService.getIntegralGoods(siteId,goodsId));
        } catch (Exception e) {
            logger.error("查询积分商品失败，siteId:" + siteId + " goodsId" + goodsId , e);
            return ReturnDto.buildFailedReturnDto("查询积分商品失败:" + e);
        }
    }

    /**
     * 门店自提
     *
     * @param params
     * @return
     */
    @RequestMapping("/getIntegralGoodStore")
    @ResponseBody
    public ReturnDto getBestStore(@RequestBody BestStoreQueryParams params) {
        logger.info("获取最优门店请求参数:{}", params.toString());
        ReturnDto response;
        try {
            Map<String, Object> store =orderService.getBestStore(params);
            response = ReturnDto.buildSuccessReturnDto(store);
        } catch (Exception e) {
            logger.error("获取最优门店信息失败!", e);
            response = ReturnDto.buildStatusERRO(ExceptionUtils.getRootCauseMessage(e));
        }
        return response;
    }

    /**
     * 将门店按照城市和库存进行分组
     *
     * @return
     */
    @PostMapping("/getStoreWithCityId")
    @ResponseBody
    public List<CityHasStores> getStoreByCityId(@RequestBody Map<String, Object> params) {
        Integer siteId = Integer.parseInt(params.get("siteId").toString());
        String serviceSupport = params.get("serviceSupport").toString();
        List<CityHasStores> cityHasStoresList = integralGoodsService.GroupStoresByCityAndServiceSupport(siteId,serviceSupport,params.get("goodsId").toString());
//        Object goodIds = params.get("goodsId");
//        List<Integer> goodsIds = new ArrayList<>();
//        if (!StringUtil.isEmpty(goodIds)) {
//            //如果没有给定商品列表，不走库存
//            String[] s = goodIds.toString().split(",");
//            List<GoodsInfo> goodsInfoInfos=new ArrayList<>();
//            for (String goodsIdarr : s) {
//                String goodsId=goodsIdarr.split("@")[0];
//                String num=goodsIdarr.split("@")[1];
//                goodsIds.add(Integer.parseInt(goodsId));
//                GoodsInfo info =new GoodsInfo();
//                info.setGoodsId(Integer.parseInt(goodsId));
//                info.setControlNum(Integer.parseInt(num));
//                String goodsCode = goodsMapper.getGoodsCodeID(siteId, Integer.parseInt(goodsId));
//                info.setGoodsCode(goodsCode);
//                goodsInfoInfos.add(info);
//            }
//
//            List<CityHasStores> cityHasStorageStores = new ArrayList<>();
//            for (CityHasStores cityHasStores : cityHasStoresList) {
//                List<Store> bestStorageStore = new ArrayList<>();
//                CityHasStores cityHasStore = new CityHasStores();
//                cityHasStore.setCityId(cityHasStores.getCityId());
//                cityHasStore.setCityName(cityHasStores.getCityName());
//                List<Store> storeList = cityHasStores.getbStores();
//
//                bestStorageStore = distributeOrderService.getBestStorageStore(siteId, storeList,goodsInfoInfos);
//                cityHasStore.setbStores(bestStorageStore);
//                cityHasStorageStores.add(cityHasStore);
//            }
//            if (siteId == 100166) {
//                return cityHasStorageStores;
//            } else {
//                return cityHasStoresList;
//            }
//        } else {
//            return cityHasStoresList;
//        }
        return cityHasStoresList;
    }

    /**
     * 将门店按照城市和库存进行分组
     *
     * @return
     */
    @PostMapping("/selectByCityIdAndSiteIdAndGroup")
    @ResponseBody
    public List<Store> selectByCityIdAndSiteIdAndGroup(@RequestBody Map<String, Object> params) {
        Integer siteId = Integer.parseInt(params.get("siteId").toString());
        Integer cityId = Integer.parseInt(params.get("cityId").toString());
        String serviceSupport = params.get("serviceSupport").toString();


        List<Store> storeList = integralGoodsService.selectStoresByCityAndServiceSupport(siteId, cityId, serviceSupport,params.get("goodsId").toString());
        Object goodIds = params.get("goodsId");
        List<Integer> goodsIds = new ArrayList<>();
        if (!StringUtil.isEmpty(goodIds)) {
            //如果没有给定商品列表，不走库存
            String[] s = goodIds.toString().split(",");
            List<GoodsInfo> goodsInfoInfos=new ArrayList<>();
            for (String goodsIdarr : s) {
                String goodsId=goodsIdarr.split("@")[0];
                String num=goodsIdarr.split("@")[1];
                goodsIds.add(Integer.parseInt(goodsId));
                GoodsInfo info =new GoodsInfo();
                info.setGoodsId(Integer.parseInt(goodsId));
                info.setControlNum(Integer.parseInt(num));
                String goodsCode = goodsMapper.getGoodsCodeID(siteId, Integer.parseInt(goodsId));
                info.setGoodsCode(goodsCode);
                goodsInfoInfos.add(info);
            }
            List<Store> cityHasStorageStores = distributeOrderService.getBestStorageStore(siteId, storeList, goodsInfoInfos);
            return cityHasStorageStores;
        } else {
            return storeList;
        }
    }


}
