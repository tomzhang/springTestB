package com.jk51.modules.goods.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.GoodsStoreRelation;
import com.jk51.modules.goods.mapper.GoodsStoreRelationMapper;
import com.jk51.modules.goods.service.GoodsStoreRelationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 商品门店关联
 * Created by Administrator on 2018/6/29.
 */

@Controller
@RestController
@RequestMapping(value = "/goodsStoreRelationinsert")
public class GoodsStoreRelationController {
    private static final Logger logger = LoggerFactory.getLogger(GoodsStoreRelationService.class);
    @Autowired
    private GoodsStoreRelationService goodsStoreRelationService;

    @ResponseBody
    @PostMapping(value = "/goodsStoreRelationAdd")
    public ReturnDto insertGoodsStoreRelation(HttpServletRequest request) throws Exception{
        logger.info("开始新增门店显示门店");
        Map<String, Object> parameterMap = ParameterUtil.getParameterMap(request);
        Object siteIdObj = parameterMap.get("siteId");
        Object goodsIdObj = parameterMap.get("goodsId");
        Object storeIdsObj = parameterMap.get("storeIds");
        if (Objects.isNull(siteIdObj)){
            return ReturnDto.buildFailedReturnDto("siteId不能为空！");
         }if(Objects.isNull(goodsIdObj)){
            return ReturnDto.buildFailedReturnDto("goodsId不能为空");
        }
         if(Objects.isNull(storeIdsObj) || org.apache.commons.lang3.StringUtils.isBlank(storeIdsObj.toString())){
            return ReturnDto.buildFailedReturnDto("storeIds不能为空");
         }
        GoodsStoreRelation goodsStoreRelation = new GoodsStoreRelation();
         goodsStoreRelation.setSiteId(Integer.valueOf(siteIdObj.toString()));
         goodsStoreRelation.setGoodsId(Integer.valueOf(goodsIdObj.toString()));
         goodsStoreRelation.setStoreIds(storeIdsObj.toString());
         goodsStoreRelationService.storeRelationAdd(goodsStoreRelation);
         return ReturnDto.buildSuccessReturnDto();
    }

    @ResponseBody
    @RequestMapping(value = "/goodsStoreRelationList",method =RequestMethod.POST )
    public ReturnDto goodsStoreRelationList(HttpServletRequest request,Integer siteId, Integer goodsId){
        logger.info("查询门店开始------");
        if (Objects.isNull(siteId))
            return ReturnDto.buildFailedReturnDto("siteId不能为空！");
        if(Objects.isNull(goodsId))
            return ReturnDto.buildFailedReturnDto("storeIds不能为空");

        String storeIds = goodsStoreRelationService.goodsStoreRelationStoreIds(siteId,goodsId);
        if (Objects.isNull(storeIds)){
            return ReturnDto.buildFailedReturnDto("暂无门店信息");
        }else if(Objects.equals(storeIds,"-1")){
            return ReturnDto.buildSuccessReturnDto("all");
        }else{
            return ReturnDto.buildSuccessReturnDto(storeIds);
        }

    }


}
