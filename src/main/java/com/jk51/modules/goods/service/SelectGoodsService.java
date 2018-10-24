package com.jk51.modules.goods.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.string.StringUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.erpprice.BGoodsErp;
import com.jk51.model.order.Store;
import com.jk51.model.treat.MerchantExtTreat;
import com.jk51.modules.erpprice.service.ErpPriceService;
import com.jk51.modules.es.entity.ReturnDto;
import com.jk51.modules.esn.service.GoodsEsService;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.persistence.mapper.SGoodsMapper;
import com.jk51.modules.treat.mapper.MerchantExtTreatMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SelectGoodsService {


    private static final Logger logger = LoggerFactory.getLogger(SelectGoodsService.class);

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private SGoodsMapper sGoodsMapper;
    @Autowired
    private MerchantExtTreatMapper merchantExtTreatMapper;
    @Autowired
    private StoresMapper storesMapper;
    @Autowired
    private ErpPriceService erpPriceService;
    @Autowired
    private GoodsEsService goodsEsService;
    public Map<String,Object>  getGoodsD(Map<String, Object> param) {
        Map<String,Object> result = new HashMap<String,Object>();
        if (StringUtil.isEmpty(param.get("goodsId"))||StringUtil.isEmpty(param.get("siteId"))){
            result.put("msg","商品ID不能为空！");

        }else{
            //把NULL字符转换成""
            Map map = goodsMapper.getGoodsD(param);
            if(!StringUtil.isEmpty(map)){
                for(Object entry:map.keySet()){
                    if(map.get(entry)!=null && ("NULL").equalsIgnoreCase(map.get(entry).toString())){
                        map.put(entry,"");
                    }
                }
                //清空b_goods中erp价格
                map.put("erpPrice", null);
                //有门店位置
                if (param.containsKey("erpStoreId")){
                    Integer siteId = Integer.parseInt(param.get("siteId").toString());
                    Integer erpStoreId = Integer.parseInt(param.get("erpStoreId").toString());
                    Integer goodsId = Integer.parseInt(param.get("goodsId").toString());
                    Integer erpAreaCode = Integer.parseInt(param.get("erpAreaCode").toString());
                    Map<Integer, BGoodsErp> bGoodsErpMap = erpPriceService.selectERPPrice(siteId, Arrays.asList(goodsId), erpStoreId, erpAreaCode);
                    if (Objects.nonNull(bGoodsErpMap.get(goodsId))) {
                        map.put("erpPrice", bGoodsErpMap.get(goodsId).getPrice());
                    }
                }
                if (Objects.isNull(map.get("erpPrice"))){
                    map.remove("erpPrice");
                }

                try {
                    if(map.get("integralGoodsId") != null && map.get("integralGoodsIsDel") != null && 1 == Integer.parseInt(map.get("integralGoodsIsDel").toString())){
                        //                map.put("integralGoodsIsDel",1);
                    }else {
                        map.put("integralGoodsIsDel",0);
                    }
                } catch (Exception e) {
                    map=new HashMap();
                    map.put("integralGoodsIsDel",0);
                }

                if(!Optional.ofNullable(map.get("integralGoodsLimitEach")).isPresent() || map.get("integralGoodsLimitEach").toString().equals("0")){
                    map.put("integralGoodsLimitEach",null);
                }

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                Optional integralGoodsStartTime = Optional.ofNullable(map.get("integralGoodsStartTime"));
                Optional integralGoodsEndTime = Optional.ofNullable(map.get("integralGoodsEndTime"));

                map.put("integralGoodsStartTime" , integralGoodsStartTime.isPresent() ? dateFormat.format((Timestamp)integralGoodsStartTime.get()): null) ;
                map.put("integralGoodsEndTime" , integralGoodsEndTime.isPresent() ? dateFormat.format((Timestamp)integralGoodsEndTime.get()): null) ;

                Object integralGoodsStoreIds = map.get("integralGoodsStoreIds");

                try {
                    List<Store> storeList = storesMapper.selectAllStore(Integer.parseInt(param.get("siteId").toString()),null,null,null);
                    Long storeIdCount = storeList.stream().filter(store -> store.getServiceSupport().contains("160")).count();
                    map.put("storeIdCount",storeIdCount);

                    if(Optional.ofNullable(integralGoodsStoreIds).isPresent() && StringUtil.isNotBlank(integralGoodsStoreIds.toString())){
                        String[] storeIds = integralGoodsStoreIds.toString().split(",");

                        //                List<Integer> storeIdsList = Arrays.asList(storeIds).parallelStream().mapToInt(storeId -> {
                        //                    try {
                        //                        return Integer.parseInt(storeId);
                        //                    } catch (NumberFormatException e) {
                        //                        return -1;
                        //                    }
                        //                }).collect(() -> new ArrayList<Integer>(),ArrayList :: add,ArrayList :: addAll);
                        //
                        //                List<String> storeNames = storesMapper.getStoreNamesByStoreIds(Integer.parseInt(param.get("siteId").toString()),storeIdsList);
                        //
                        //                map.put("storeNames",storeNames);
                        Integer storeIdNum = storeIds.length;
                        map.put("storeIdNum",storeIdNum);
                        map.put("allStore",false);
                    }else{
                        map.put("allStore",true);
                    }

                } catch (Exception e) {
                    logger.error("积分商品自提门店数据数据处理错误",e);
                }
            }



            result.put("goods",map);
            List<Map<String, String>> imgList=goodsMapper.getGoodsImg(param);
            if(imgList.size()>1){
                ckGoodsDImg(param);
            }
            result.put("goodsImg",imgList);
        }

        return result;
    }
    @Async
    public void  ckGoodsDImg(Map<String, Object> param) {
        List<Map<String, String>> imgList=goodsMapper.ckByGoodsId(param);
        if(imgList.size()>1){
            goodsMapper.ckUpdateByGoodsId(imgList.get(0));

        }
    }
    public Map<String,Object>  getGoodsDStatus(Map<String, Object> param) {
        Map<String,Object> result = new HashMap<String,Object>();
        if (StringUtil.isEmpty(param.get("goodsId"))||StringUtil.isEmpty(param.get("siteId"))){
            result.put("msg","商品ID不能为空！");
        }else{
            //把NULL字符转换成""
            Map map = goodsMapper.getGoodsDStatus(param);
            //有门店位置
            if (param.containsKey("erpStoreId")){
                Integer siteId = Integer.parseInt(param.get("siteId").toString());
                Integer erpStoreId = Integer.parseInt(param.get("erpStoreId").toString());
                Integer goodsId = Integer.parseInt(param.get("goodsId").toString());
                Integer erpAreaCode = Integer.parseInt(param.get("erpAreaCode").toString());
                Map<Integer, BGoodsErp> bGoodsErpMap = erpPriceService.selectERPPrice(siteId, Arrays.asList(goodsId), erpStoreId, erpAreaCode);
                if (Objects.nonNull(bGoodsErpMap.get(goodsId))) {
                    map.put("shopPrice", bGoodsErpMap.get(goodsId).getPrice());
                }
            }
            result.put("goods",map);
            if (!StringUtil.isEmpty(param.get("queryType"))&&"queryCart".equals(param.get("queryType"))){
                result.put("goodsImg",goodsMapper.getGoodsImg(param));
            }

        }

        return result;
    }
    public Map<String,Object>  getGoodsList(Map<String, Object> param) {
        Map<String,Object> result = new HashMap<String,Object>();
        if (param.containsKey("goodsCode")){
            param.put("goodsCode", param.get("goodsCode").toString().trim());
        }
        if (StringUtil.isEmpty(param.get("siteId"))){
            result.put("msg","商户ID不能为空！");
        }else{

            logger.info(param.toString());
            Object goodsIdsObject = param.get("goodsIds");
            if(null != goodsIdsObject){
                String goodsIds = (String)goodsIdsObject;
                String[] goodIdsArr = org.apache.commons.lang.StringUtils.split(goodsIds, ",");
                param.put("goodsIds", goodIdsArr);
            }
            int startRow=Integer.parseInt(StringUtils.isEmpty(param.get("startRow"))?"0":param.get("startRow").toString()) ;
            int pageSize=Integer.parseInt(StringUtils.isEmpty(param.get("pageSize"))?"10":param.get("pageSize").toString()) ;
            PageHelper.startPage(startRow, pageSize);//开启分页
            List<Map<String, String>> list = goodsMapper.getGoodsList(param);
            MerchantExtTreat merchantExt = merchantExtTreatMapper.selectByMerchantId(Integer.parseInt(param.get("siteId").toString()));
            result.put("has_erp_price",merchantExt.getHas_erp_price());
            list.stream().parallel().forEach(map -> {
                    Map<String,Object> params = new HashMap<>();
                    if (!map.containsKey("imgHash") || "".equals(map.get("imgHash"))){
                        params.put("goodsId",map.get("goodsId"));
                        params.put("siteId",param.get("siteId"));
                        String goodsImg = goodsMapper.getDefaultImg(params);

                        if(goodsImg != "" && goodsImg != null){
                            System.out.println(goodsImg);
                            map.put("imgHash",goodsImg);
                        }else{
                            logger.info("siteId:{},goodsId:{}：没有图片",param.get("siteId"),map.get("goodsId"));
                        }

                    }
                }
            );

            result.put("goodsPage", new PageInfo<>(list));
        }

        return result;
    }


    public ReturnDto getCorrelationGoodsList(Map<String, Object> param) {
        Map<String,Object> result = new HashMap<String,Object>();
        MerchantExtTreat merchantExt = merchantExtTreatMapper.selectByMerchantId(Integer.parseInt(param.get("siteId").toString()));
        result.put("has_erp_price",merchantExt.getHas_erp_price());
        List<Map<String, String>> list = goodsMapper.getCorrelationGoodsList(param);
        list.stream().forEach(map -> {
                Map<String,Object> params = new HashMap<>();
                if (!map.containsKey("hash") || "".equals(map.get("hash"))){
                    params.put("goodsId",map.get("goodsId"));
                    params.put("siteId",param.get("siteId"));
                    String goodsImg = goodsMapper.getDefaultImg(params);
                    if(goodsImg != "" && goodsImg != null){
                        map.put("imgHash",goodsImg);
                    }else{
                        logger.info("siteId:{},goodsId:{}：没有图片",param.get("siteId"),map.get("goodsId"));
                    }
                }
                //清空b_goods中erp价格
                map.put("erp_price", null);

                //有门店位置
                if (param.containsKey("erpStoreId")){
                    Integer siteId = Integer.parseInt(param.get("siteId").toString());
                    Integer erpStoreId = Integer.parseInt(param.get("erpStoreId").toString());
                    Integer goodsId = Integer.parseInt(String.valueOf(map.get("goods_id")));
                    Integer erpAreaCode = Integer.parseInt(param.get("erpAreaCode").toString());
                    Map<Integer, BGoodsErp> bGoodsErpMap = erpPriceService.selectERPPrice(siteId, Arrays.asList(goodsId), erpStoreId, erpAreaCode);
                    if (Objects.nonNull(bGoodsErpMap.get(goodsId))) {
                        map.put("shop_price", bGoodsErpMap.get(goodsId).getPrice().toString());
                    }
                }

                if (Objects.isNull(map.get("erp_price"))){
                    map.remove("erp_price");
                }
            }
        );

        if ((list.size() >0)) {
            result.put("goodsInfo",list);
            return ReturnDto.buildSuccessReturnDto(result);
        }else {
            return ReturnDto.buildFailedReturnDto("未查询到相关商品");
        }
    }


    @Transactional
//    @Async
    public Map<String,Object>  updateBuyWay(Map<String, Object> param) {
        Map<String,Object> result = new HashMap<String,Object>();
        if (StringUtil.isEmpty(param.get("site_id"))){
            result.put("result","商户ID不能为空！");
        }else{
            logger.info(param.toString());
            Object goodsIdsObject = param.get("changeBuyWay");
            String[] goodIdsArr = null;
            if(null != goodsIdsObject){
                String goodsIds = (String)goodsIdsObject;
                goodIdsArr = org.apache.commons.lang.StringUtils.split(goodsIds, ",");
                param.put("changeBuyWay", goodIdsArr);
            }
            Integer num= goodsMapper.updateBuyWay(param);
//            list.stream().forEach(map -> {
//                String goods_id = map.get("goods_id");
//                Map<String,Object> m2 = new HashedMap();
//                m2.put("goods_id",goods_id);
//                List<Map<String, String>> goodsImg = goodsMapper.getGoodsImg(m2);
//            });
            if(num == null || num == 0 || "".equals(String.valueOf(num))) {
                result.put("result","fail");
            }else if (num == goodIdsArr.length) {
                /*//更新分销的商品信息
                Integer num2 = goodsMapper.updateByWayDis(param);
                if(num == goodIdsArr.length) {
                    result.put("result", "success");
                }*/
                result.put("result", "success");
                //同步更新ES
                for (int i = 0; i < goodIdsArr.length; i++) {
                    String gId = goodIdsArr[i];
                    try {
//                        logger.info("update brandid:{} ,goodsid:{}",param.get("site_id"),gId);
                        goodsEsService.updateGoodsInxALlIndices("b_shop_"+param.get("site_id"), gId, "");
                    }catch (RuntimeException e){
                        logger.error("HomeController.updateGoods error , error Message:",e);
                    }catch (Exception e){
                        logger.error("HomeController.updateGoods error , error Message:",e);
                    }
                }

            }

        }

        return result;
    }

    public List<Map<String,String>> getGoodsListNoPage(Map<String, Object> param){
        return goodsMapper.getGoodsList(param);
    }

    public List<Map<String,Object>> getGoodsInfo(String siteId,List<Integer> goodsIds)throws BusinessLogicException {
        try {
            //商品数据,包括商品图片信息
            return sGoodsMapper.getGoodsInfoByGoodIds(Integer.parseInt(siteId),goodsIds);
        } catch (Exception e) {
            logger.error("查询商品信息出错",e);
            throw new BusinessLogicException("查询商品信息出错",e);
        }
    }

    public Map<String,Object> getGoodsInfoByPage(String siteId, List<Integer> goodsIds, Integer page, Integer pageSize) {
        //对商品id进行排序
        PageHelper.startPage(page,pageSize);
        List<Map<String, Object>> goodsList = sGoodsMapper.getGoodsInfoByGoodIds(Integer.parseInt(siteId), goodsIds);
        PageInfo<Map<String,Object>> pageInfo = new PageInfo<>(goodsList);
        List<Map<String, Object>> goodsListResult = pageInfo.getList();
        //总记录数
        long total = pageInfo.getTotal();
        int pages = pageInfo.getPages();
        Map<String,Object> result  = new HashMap<String,Object>(){{
           put("data",goodsListResult);
           put("total",total);
           put("pages",pages);
        }};
        return result;
    }
}
