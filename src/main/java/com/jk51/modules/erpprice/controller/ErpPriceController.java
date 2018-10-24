package com.jk51.modules.erpprice.controller;

import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.exception.BusinessLogicException;
import com.jk51.model.erpprice.BGoodsErp;
import com.jk51.modules.erpprice.domain.pojo.ErpSettingPO;
import com.jk51.modules.erpprice.domain.pojo.GoodsErpPriceDTO;
import com.jk51.modules.erpprice.service.ErpPriceService;
import com.jk51.util.ValidationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: ChengShanyunduo
 * 创建日期: 2017-10-27
 * 修改记录:
 */
@RestController
@RequestMapping("/erpPrice")
public class ErpPriceController {
    public static final Logger logger = LoggerFactory.getLogger(ErpPriceController.class);

    @Autowired
    ErpPriceService erpPriceService;

    /**
     * erp价格列表
     * @param request
     * @return
     */
    @PostMapping("/list")
    public ReturnDto taskList(HttpServletRequest request){
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        List<Map<String, Object>> result = erpPriceService.erpPriceList(param);
        PageInfo pageInfo = new PageInfo<>(result);
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    /**
     * 商户下门店所在地区   type=3市，type=4区
     * @param siteId
     * @param type
     * @return
     */
    @PostMapping("/storeArea")
    public ReturnDto storeArea(Integer siteId, Integer type){
        return ReturnDto.buildListOnEmptyFail(erpPriceService.storeArea(siteId, type));
    }

    /**
     * 删除Erp价格
     * @param request
     * @return
     */
    @PostMapping("/deletePrice")
    public ReturnDto delete(HttpServletRequest request){
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        String ids = param.get("id").toString();
        int[] id = Arrays.stream(ids.split(",")).mapToInt(taskId -> Integer.parseInt(taskId)).toArray();

        boolean result = erpPriceService.deletePrice(id);
        if (result){
            return ReturnDto.buildSuccessReturnDto();
        }else{
            return ReturnDto.buildFailedReturnDto("删除失败");
        }
    }

    /**
     * 批量修改Erp价格
     * @param
     * @return
     */
    @PostMapping("/batch/changePrice")
    public ReturnDto batchChangePrice(String[] ids,Integer price){
//        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        boolean result = erpPriceService.batchChangePrice(ids,price);
        if (result){
            return ReturnDto.buildSuccessReturnDto();
        }else{
            return ReturnDto.buildFailedReturnDto("批量修改Erp价格失败");
        }
    }

    /**
     * 单个商品修改Erp价格
     * @param
     * @return
     */
    @PostMapping("/singleErpPriceEdit")
    public ReturnDto singleErpPriceEdit(String id,Integer price){
//        Map<String, Object> map = new HashedMap();
        boolean result = erpPriceService.editPrice(id,price);
        if (result){
            return ReturnDto.buildSuccessReturnDto();
        }else{
            return ReturnDto.buildFailedReturnDto("修改Erp价格失败");
        }
    }

    /**
     * 获取ERP门店
     * @return
     */
    @GetMapping("/store/{siteId:\\d{6}}/{areaCode:\\d{6}}")
    public ReturnDto getErpStore(@PathVariable("siteId") Integer siteId,
                                 @PathVariable("areaCode") Integer areaCode,
                                 @RequestParam Float lat,
                                 @RequestParam Float lng) {
        ErpSettingPO erpStore = erpPriceService.getErpStore(siteId, areaCode, lat, lng);

        return ReturnDto.buildIfNull(erpStore, "没有符合条件的ERP门店");
    }


    /**
     * 获取商品ERP价格
     * <code>
     *     {
     *         "siteId": 100190,
     *         "goodsIds": [1,2,3,3,4,4,5,5,6]
     *         "erpStore": 123
     *     }
     * </code>
     * @return
     */
    @PostMapping("/getErpPrice")
    public ReturnDto getErpPrice(@Valid @RequestBody GoodsErpPriceDTO priceDTO, BindingResult bindingResult) throws BusinessLogicException {
        ValidationUtils.check(bindingResult);

        Map<Integer, BGoodsErp> priceMap = erpPriceService.selectERPPrice(priceDTO.getSiteId(), priceDTO.getGoodsIds(), priceDTO.getErpStoreId(), priceDTO.getErpAreaCode());

        return ReturnDto.buildIfEmpty(priceMap, "没有ERP价格数据");
    }
}
