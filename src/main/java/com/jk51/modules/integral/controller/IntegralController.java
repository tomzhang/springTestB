package com.jk51.modules.integral.controller;

import com.jk51.commons.CommonConstant;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.SMeta;
import com.jk51.model.integral.IntegralRule;
import com.jk51.modules.integral.service.IntegralService;
import com.jk51.modules.merchant.service.MerchantExtService;
import com.jk51.modules.meta.service.MetaService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/27.
 */
@RestController
@RequestMapping("/integral")
public class IntegralController {

    @Autowired
    private MerchantExtService merchantExtService;
    @Autowired
    private IntegralService integralService;
    @Autowired
    private MetaService metaService;

    @PostMapping("/shopManage")
    public ReturnDto ShopManage(HttpServletRequest request){
        Map param = ParameterUtil.getParameterMap(request);
        //获取商家ID
        String siteId = param.get("siteId").toString();
        String isOpen = param.get("isOpen").toString();
        //判断是否为空
        if(siteId == null || "".equals(siteId)) {
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }

        if(isOpen == null || "".equals(isOpen)) {
            return ReturnDto.buildFailedReturnDto("isOpen为空");
        }
        Map map = new HashMap();
        map.put("siteId",param.get("siteId"));
        map.put("status",1);
        List<IntegralRule> integralRuleList = integralService.rules(map);
        if(integralRuleList.size() == 0){
            return ReturnDto.buildFailedReturnDto("请先设置积分任务");
        }
        if(merchantExtService.integralShopManger(param) > 0){
            return ReturnDto.buildSuccessReturnDto("修改成功");
        }else{
            return ReturnDto.buildFailedReturnDto("修改失败");
        }
    }

    @GetMapping("integralShopStatus")
    public ReturnDto getIntegralShopStatus(HttpServletRequest request){
        Map param = ParameterUtil.getParameterMap(request);
        //获取商家ID
        String siteId = param.get("siteId").toString();

        //判断是否为空
        if(siteId == null || "".equals(siteId)) {
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }

        Map<String,Object>map = merchantExtService.getIntegralShopStatus(Integer.valueOf(siteId));
        return ReturnDto.buildSuccessReturnDto(map);
    }

    @GetMapping("shipping")
    public ReturnDto queryShippingStyle(HttpServletRequest request){
        Map<String,Object> map = ParameterUtil.getParameterMap(request);
        //获取商家ID
        int siteId = NumberUtils.toInt(map.get("siteId").toString());
        String metaType = CommonConstant.META_KEY_INTEGRAL_SHIPPING;

        SMeta meta = metaService.queryMeta(siteId,metaType);

        if(!StringUtil.isEmpty(meta)){
            return ReturnDto.buildSuccessReturnDto(meta);
        }else{
            return ReturnDto.buildFailedReturnDto("error");
        }

    }

    @PostMapping("shipping/save")
    public ReturnDto shippingSave(@RequestBody Map<String,Object>map , HttpServletRequest request) {
//        Map<String, Object> map = ParameterUtil.getParameterMap(request);
        int result = 0;

        int siteId = Integer.valueOf(map.get("siteId").toString());

        try{
            String metaVal = JacksonUtils.obj2json(map.get("metaVal"));
            if(map.containsKey("id")){
                int metaId = Integer.valueOf(map.get("id").toString());
                SMeta meta = metaService.findBySiteIdAndMetaId(siteId, metaId);
                if(!StringUtil.isEmpty(meta)){
                    meta.setMetaVal(metaVal);
                }
                result = metaService.updateMeta(meta);
            }else{
                SMeta meta = new SMeta();
                meta.setSiteId(siteId);
                meta.setMetaType(CommonConstant.META_KEY_INTEGRAL_SHIPPING);
                meta.setMetaDesc("积分配送方式配置");
                meta.setMetaStatus(1);
                meta.setMetaKey(CommonConstant.META_KEY_INTEGRAL_SHIPPING+"_style");
                meta.setMetaVal(metaVal);
            result = metaService.addmeta(meta);
                System.out.println(meta);
            }

            if(result > 0){
                return ReturnDto.buildSuccessReturnDto("success");
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return ReturnDto.buildFailedReturnDto("error");
    }
}
