package com.jk51.modules.erpprice.controller;

import com.jk51.commons.message.OldStyle;
import com.jk51.exception.BusinessLogicException;
import com.jk51.modules.goods.library.BatchResult;
import com.jk51.modules.goods.request.BatchImportDto;
import com.jk51.modules.erpprice.service.ErpPriceImportService;
import com.jk51.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Objects;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: aaron
 * 创建日期: 2017-10-27 16:19
 * 修改记录:
 */
@RequestMapping("/erp")
@Controller
public class ErpPriceImportController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ErpPriceImportController.class);

    @Autowired
    ErpPriceImportService erpPriceImportService;

    @PostMapping("/import/erpPriceForStore")
    @ResponseBody
    public Object importErpPriceForStore(@Valid @RequestBody BatchImportDto batchImportDto, BindingResult bindingResult){
        try {
            ValidationUtils.check(bindingResult);
            BatchResult batchResult = erpPriceImportService.updateErpPriceForStore(batchImportDto);
            Objects.requireNonNull(batchResult,"没有数据要处理");
            return new HashMap<String,Object>(){{
                put("status","true");
                put("code","");
                put("result",batchResult);
            }};
        } catch (BusinessLogicException e) {
            LOGGER.error("",e);
            return render(31000, e.getMessage());
        }catch (Exception e){
            LOGGER.error("",e);
            return render(31001, e.getMessage());
        }
    }

    protected String render(int code, String msg) {
        return OldStyle.render(code, msg);
    }

}
