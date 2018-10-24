package com.jk51.modules.merchant.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.message.OldStyle;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.CityHasStores;
import com.jk51.model.order.GoodsInfo;
import com.jk51.model.order.Store;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.grouppurchase.service.GroupPurChaseService;
import com.jk51.modules.im.service.wechatUtil.WechatUtil;
import com.jk51.modules.index.service.StoresService;
import com.jk51.modules.merchant.request.GoodsIconStatus;
import com.jk51.modules.merchant.request.QRcodeTips;
import com.jk51.modules.merchant.request.WechatConfigDTO;
import com.jk51.modules.merchant.service.MerchantService;
import com.jk51.modules.merchant.service.SiteSettingService;
import com.jk51.modules.merchant.service.YbMetaService;
import com.jk51.modules.order.service.DistributeOrderService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商户设置
 */
@Controller
@RequestMapping("/site")
public class SiteController {
    @Autowired
    YbMetaService ybMetaService;

    @Autowired
    SiteSettingService siteSettingService;
    @Autowired
    private DistributeOrderService distributeOrderService;
    @Autowired
    private StoresService storesService;

    @Autowired
    MerchantService merchantService;

    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    GroupPurChaseService groupPurChaseService;
    @Autowired
    WechatUtil wechatUtil;


    @PostMapping("/setGoodsIconStatus")
    @ResponseBody
    public String setGoodsIconStatus(@RequestBody @Valid GoodsIconStatus goodsIconStatus, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return render(1000, bindingResult);
        }

        boolean hasSuccess = ybMetaService.setGoodsIconStatus(goodsIconStatus);
        if (!hasSuccess) {
            return render(10001, "设置失败");
        }

        Map result = new HashMap();
        result.put("msg", "设置成功");
        return render(result);
    }

    @PostMapping("/getGoodsIconStatus")
    @ResponseBody
    public String getGoodsIconStatus(@RequestBody GoodsIconStatus goodsIconStatus) {
        List<Map> records = ybMetaService.getGoodsIconStatus(goodsIconStatus);
        if (records != null && records.size() > 0) {
            Map result = new HashMap();
            result.put("items", records);
            return render(result);
        }

        return render(1002, "没有记录");
    }

    @PostMapping("/setQRcodeTips")
    @ResponseBody
    public String setQRcodeTips(@RequestBody @Valid QRcodeTips qRcodeTips, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return render(1000, bindingResult);
        }

        boolean hasSuccess = siteSettingService.setQRcodeTips(qRcodeTips);

        if (!hasSuccess) {
            return render(10001, "设置失败");
        }

        Map result = new HashMap();
        result.put("msg", "设置成功");
        return render(result);
    }

    @PostMapping("/getQRcodeTips")
    @ResponseBody
    public String getQRcodeTips(@RequestBody QRcodeTips qRcodeTips) {
        Map record = siteSettingService.getQRcodeTips(qRcodeTips);

        if (record != null) {
            return render(record);
        }

        return render(1002, "没有记录");
    }

    private String render(int code, BindingResult bindingResult) {
        FieldError fe = bindingResult.getFieldError();
        return render(code, fe.getField() + fe.getDefaultMessage());
    }

    private String render(Object obj) {
        return OldStyle.render(obj);
    }

    private String render(int code, String msg) {

        return OldStyle.render(code, msg);
    }

    /**
     * 将门店按照城市和库存进行分组
     *
     * @return
     */
    @PostMapping("/getStoreByCityId")
    @ResponseBody
    public List<CityHasStores> getStoreByCityId(@RequestBody Map<String, Object> params) {
        Integer siteId = Integer.parseInt(params.get("siteId").toString());
        String serviceSupport = params.get("serviceSupport").toString();
        List<CityHasStores> cityHasStoresList = storesService.GroupStoresByCityAndServiceSupport(siteId, serviceSupport);
        Object goodIds = params.get("goodsId");
        List<Integer> goodsIds = new ArrayList<>();
        if (!StringUtil.isEmpty(goodIds)) {
                //如果没有给定商品列表，不走库存
                String[] s = goodIds.toString().split(",");
                List<GoodsInfo> goodsInfoInfos = new ArrayList<>();
                for (String goodsIdarr : s) {
                    String goodsId = goodsIdarr.split("@")[0];
                    String num = goodsIdarr.split("@")[1];
                    goodsIds.add(Integer.parseInt(goodsId));
                    GoodsInfo info = new GoodsInfo();
                    info.setGoodsId(Integer.parseInt(goodsId));
                    info.setControlNum(Integer.parseInt(num));
                    String goodsCode = goodsMapper.getGoodsCodeID(siteId, Integer.parseInt(goodsId));
                    info.setGoodsCode(goodsCode);
                    goodsInfoInfos.add(info);
                }
                List<CityHasStores> cityHasStorageStores = new ArrayList<>();
                for (CityHasStores cityHasStores : cityHasStoresList) {
                    List<Store> bestStorageStore = new ArrayList<>();
                    CityHasStores cityHasStore = new CityHasStores();
                    cityHasStore.setCityId(cityHasStores.getCityId());
                    cityHasStore.setCityName(cityHasStores.getCityName());
                    List<Store> storeList = cityHasStores.getbStores();
                    bestStorageStore = distributeOrderService.getBestStorageStore(siteId, storeList, goodsInfoInfos);
                    cityHasStore.setbStores(bestStorageStore);
                    cityHasStorageStores.add(cityHasStore);
                }
                return cityHasStorageStores;
        } else {
            return cityHasStoresList;
        }
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
        List<Store> storeList = storesService.selectStoresByCityAndServiceSupport(siteId, cityId, serviceSupport);

        //获取拼团id，根据活动删选门店
        if(!StringUtil.isEmpty(params.get("proActivityId"))){
            Integer proActivityId = Integer.parseInt(params.get("proActivityId").toString());
            List<String> storesList = groupPurChaseService.getStoresByProActivityId(siteId,proActivityId).get();
            if(!StringUtil.isEmpty(storesList)){
                String storesStr= org.apache.commons.lang.StringUtils.join(storesList.toArray(), ",");
                storeList.stream()
                        .filter(store -> ("," + storesStr + ",").indexOf("," + store.getId() + ",") > -1)
                        .collect(Collectors.toList());
            }
        }


        Object goodIds = params.get("goodsId");
        List<Integer> goodsIds = new ArrayList<>();
        if (!StringUtil.isEmpty(goodIds)) {
            //如果没有给定商品列表，不走库存
            String[] s = goodIds.toString().split(",");
            List<GoodsInfo> goodsInfoInfos = new ArrayList<>();
            for (String goodsIdarr : s) {
                String goodsId = goodsIdarr.split("@")[0];
                String num = goodsIdarr.split("@")[1];
                goodsIds.add(Integer.parseInt(goodsId));
                GoodsInfo info = new GoodsInfo();
                info.setGoodsId(Integer.parseInt(goodsId));
                info.setControlNum(Integer.parseInt(num));
                String goodsCode = goodsMapper.getGoodsCodeID(siteId, Integer.parseInt(goodsId));
                info.setGoodsCode(goodsCode);
                goodsInfoInfos.add(info);
            }
            return distributeOrderService.getBestStorageStore(siteId, storeList, goodsInfoInfos);
        } else {
            return storeList;
        }
    }

    @GetMapping("/wechat/config/{siteId:\\d{6}}")
    @ResponseBody
    public ReturnDto wechatConfig(@PathVariable Integer siteId) {
        Map<String, Object> merchantInfo = merchantService.getMerchantBySiteId(siteId);
        int hasErpPrice = MapUtils.getInteger(merchantInfo, "has_erp_price", 0);
        String title = MapUtils.getString(merchantInfo, "shop_weixin", "微信商城");
        String merchantName = MapUtils.getString(merchantInfo, "merchant_name", "");
        String integralName = MapUtils.getString(merchantInfo, "integral_name", "积分");
        String pcLogurl = MapUtils.getString(merchantInfo,"shop_logurl","店铺logo地址");
        String baiduScript = MapUtils.getString(merchantInfo,"baidu_script","");

        WechatConfigDTO wechatConfig = new WechatConfigDTO();
        wechatConfig.setHasErpPrice(hasErpPrice == 1);
        wechatConfig.setSiteId(siteId);
        wechatConfig.setTitle(title);
        wechatConfig.setIntegralName(integralName);
        wechatConfig.setPcLogurl(pcLogurl);
        wechatConfig.setBaiduScript(baiduScript);
        wechatConfig.setMerchantName(merchantName);

        return ReturnDto.buildSuccessReturnDto(wechatConfig);
    }
    @RequestMapping("/getTencentToken")
    @ResponseBody
    private String getTencentToken(Integer siteId)  {

        String fromRedisAccessToken = wechatUtil.getAccessToken(siteId);
        return fromRedisAccessToken;
    }
}
