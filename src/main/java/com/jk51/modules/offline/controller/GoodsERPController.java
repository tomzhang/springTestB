package com.jk51.modules.offline.controller;

import com.alibaba.fastjson.JSONArray;
import com.jk51.commons.message.OldStyle;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.modules.goods.library.BatchResult;
import com.jk51.modules.goods.request.BatchImportDto;
import com.jk51.modules.offline.service.GoodsERPServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-09-27
 * 修改记录:
 */
@Controller
@RequestMapping("erp")
public class GoodsERPController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsERPController.class);

    @Autowired
    private GoodsERPServices goodsERPServices;

    @ResponseBody
    @RequestMapping("goods/import")
    public Object importStorage(@Valid @RequestBody BatchImportDto batchImportDto) {
        BatchResult result = null;
        try {
            if (batchImportDto.getDetailTpl() == 110) {//erp库存导入
                result = goodsERPServices.importStorageFromEX(batchImportDto);
            } else if (batchImportDto.getDetailTpl() == 120) {//erp价格导入
                result = goodsERPServices.importPriceFromEX(batchImportDto);
            }
            return OldStyle.render(result);
        } catch (RuntimeException re) {
            LOGGER.info("导入出错:" + re);
            return OldStyle.render(31000, re.getMessage());
        } catch (Exception e) {
            LOGGER.info("导入出错:" + e);
            return OldStyle.render(31001, e.getMessage());
        }
    }

    @RequestMapping("goods/loadstorage")
    @ResponseBody
    public Map<String, Object> loadStorage(@RequestParam("siteId") Integer siteId,
                                           @RequestParam(value = "goods_name", required = false, defaultValue = "") String good_name,
                                           @RequestParam(value = "goods_code", required = false, defaultValue = "") String good_code,
                                           @RequestParam(value = "stores_name", required = false, defaultValue = "") String stores_name,
                                           @RequestParam(value = "stores_number", required = false, defaultValue = "") String stores_number,
                                           @RequestParam(value = "pageNum", required = false, defaultValue = "0") Integer pageNum,
                                           @RequestParam(value = "pageSize", required = false, defaultValue = "15") Integer pageSize) {
        Map result = new HashMap();
        result.put("result", goodsERPServices.selectStorageInfo(siteId, good_code, good_name, stores_name, stores_number, pageNum, pageSize));
        return result;
    }

    @RequestMapping("goods/loadstorageStores")
    @ResponseBody
    public Map<String, Object> loadstorageStores(@RequestParam("siteId") Integer siteId) {
        Map result = new HashMap();
        result.put("result", goodsERPServices.loadstorageStores(siteId));
        return result;
    }

    //通过接口调用保存库存数据
    @RequestMapping("/goods/insertStorage")
    @ResponseBody
    public Map<String, Object> insertStorage(HttpServletRequest request) {
        Map<String, Object> params = ParameterUtil.getParameterMap(request);
        Integer siteId = Integer.valueOf(params.get("siteId").toString());
        List<Map> storageList = JSONArray.parseArray(params.get("info") + "", Map.class);
        LOGGER.info("method:[/erp/goods/insertStorage],站点:{},传递参数:{}", siteId, storageList.toString());
        Map<String, Object> result = new HashMap<>();
        try {
            return goodsERPServices.application_insertStorage(siteId, storageList);
        } catch (Exception e) {
            LOGGER.info("insertStorage库存更新失败" + e);
            result.put("code", "400");
            result.put("msg", "库存更新失败");
            return result;
        }
    }
}
