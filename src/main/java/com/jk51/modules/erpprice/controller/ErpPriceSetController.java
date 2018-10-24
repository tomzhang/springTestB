package com.jk51.modules.erpprice.controller;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.exception.BusinessLogicException;
import com.jk51.modules.erpprice.domain.dto.BErpSettingDTO;
import com.jk51.modules.erpprice.service.ErpPriceImportService;
import com.jk51.modules.erpprice.service.ErpPriceSetService;
import com.jk51.modules.erpprice.service.SyncService;
import com.jk51.modules.index.service.StoresService;
import com.jk51.modules.offline.service.ErpStoresPriceService;
import com.jk51.modules.task.domain.AddGroup;
import com.jk51.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron
 * 创建日期: 2017-10-30 16:33
 * 修改记录:
 */
@RequestMapping("/erp")
@Controller
public class ErpPriceSetController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ErpPriceSetController.class);

    @Autowired
    ErpPriceSetService erpPriceSetService;
    @Autowired
    StoresService storesService;
    @Autowired
    SyncService syncService;
    @Autowired
    ErpStoresPriceService erpStoresPriceService;

    @RequestMapping("/store/{siteId}")
    @ResponseBody
    public ReturnDto getStoreBySiteId(@PathVariable("siteId") Integer siteId) {

        try {
            return ReturnDto.buildSuccessReturnDto(erpPriceSetService.getStoreBySiteId(siteId));
        } catch (Exception e) {
            LOGGER.error("查询商户下的门店失败门店 -> {}", e);
            return ReturnDto.buildFailedReturnDto("查询商户下的门店失败门店");
        }

    }

    /**
     * 插入商户区域指定价格的门店
     *
     * @param bErpSettingDTO
     * @param bindingResult
     * @return
     */
    @PostMapping("/setting/save")
    @ResponseBody
    public ReturnDto saveErpPriceSetting(@Validated(AddGroup.class) @RequestBody BErpSettingDTO bErpSettingDTO, BindingResult bindingResult) {
        try {
            ValidationUtils.check(bindingResult);
            if (!erpPriceSetService.saveErpPriceSetting(bErpSettingDTO)) {
                throw new BusinessLogicException("商户价格类型插入失败");
            }
            //获取多价格设置的门店数据并调用接口保存价格数据
            erpStoresPriceService.getStoresPriceFromErp(bErpSettingDTO.getSiteId(), bErpSettingDTO.getType());
            return ReturnDto.buildSuccessReturnDto("ERP对接设置成功");
        } catch (Exception e) {
            return ReturnDto.buildFailedReturnDto("ERP对接设置失败 -> " + e);
        }
    }

    @RequestMapping("/setting/detail/{siteId}")
    @ResponseBody
    public ReturnDto getSettingDetail(@PathVariable Integer siteId) {
        try {
            Objects.requireNonNull(siteId, "商户编号不存在");
            return ReturnDto.buildSuccessReturnDto(erpPriceSetService.getSettingDetailBySiteId(siteId));
        } catch (Exception e) {
            LOGGER.error("获取erp对接设置的价格失败->{}", e);
            return ReturnDto.buildFailedReturnDto("获取erp对接设置的价格失败-> " + e);
        }
    }

    @RequestMapping("/setting/cancelSetting/{siteId}")
    @ResponseBody
    public ReturnDto cancelSetting(@PathVariable Integer siteId) {
        try {
            Objects.requireNonNull(siteId, "商户编号不存在");
            return ReturnDto.buildSuccessReturnDto(erpPriceSetService.cancelSetting(siteId));
        } catch (Exception e) {
            LOGGER.error("取消多价格设置失败->{}", e);
            return ReturnDto.buildFailedReturnDto("取消多价格设置失败-> " + e);
        }
    }
}
