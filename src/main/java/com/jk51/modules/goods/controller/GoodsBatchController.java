package com.jk51.modules.goods.controller;

import com.jk51.commons.message.OldStyle;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.goods.library.BatchResult;
import com.jk51.modules.goods.request.BatchImportDto;
import com.jk51.modules.goods.request.BatchJoinIntegralGoods;
import com.jk51.modules.goods.request.GoodsDataList;
import com.jk51.modules.goods.request.GoodsIdList;
import com.jk51.modules.goods.service.GoodsImportService;
import com.jk51.modules.goods.service.GoodsService;
import com.jk51.modules.grouppurchase.mapper.GroupPurChaseMapper;
import com.jk51.modules.grouppurchase.service.GroupPurChaseService;
import com.jk51.modules.integral.service.IntegralGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ResponseBody
public class GoodsBatchController extends WebMvcConfigurerAdapter {

    @Autowired
    protected GoodsService goodsService;

    @Autowired
    protected GoodsImportService goodsImportService;

    @Autowired
    protected IntegralGoodsService integralGoodsService;

    @Autowired
    protected GroupPurChaseMapper groupPurChaseMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 新增商品 多个
     */
    @PostMapping(value = "batch/add"/*, consumes="application/json"*/)
    public Object batchAdd(@Valid @RequestBody GoodsDataList goodsDatas, BindingResult bindingResult) throws Exception {
        try {
            hasErrors(bindingResult);
            BatchResult batchResult = goodsService.create(goodsDatas.getItems(), goodsDatas.getSiteId());

            return render(batchResult);
        } catch (RuntimeException re) {
            return render(32000, re.getMessage());
        } catch (Exception e) {
            return render(33001, e.getMessage());
        }
    }

    /**
     * 更新商品 多个
     */
    @PostMapping(value = "batch/update"/*, consumes="application/json"*/)
    public Object batchUpdate(@Valid @RequestBody GoodsDataList goodsDatas, BindingResult bindingResult) {
        try {
            hasErrors(bindingResult);
            BatchResult batchResult = goodsService.updateGoods(goodsDatas.getItems(), goodsDatas.getSiteId());

            return render(batchResult);
        } catch (RuntimeException re) {
            return render(32000, re.getMessage());
        } catch (Exception e) {
            return render(33001, e.getMessage());
        }
    }


    @PostMapping(value = "batch/listing")
    public Object batchListing(@Valid @RequestBody GoodsIdList goodsIdList, BindingResult bindingResult, HttpServletRequest request) {
        try {
            hasErrors(bindingResult);
            int[] goodsIds = goodsIdList.getIdList();
            //------根据需求，添加相应字段------start
            Integer appChecked = goodsIdList.getAppChecked();
            Integer appCheckedValue = goodsIdList.getAppCheckedValue();
            Integer onLineShopChecked = goodsIdList.getOnLineShopChecked();
            Integer onLineShopCheckedValue = goodsIdList.getOnLineShopCheckedValue();
            Map<String,Object> map = new HashMap<>();
            Integer status = goodsIdList.getStatus();////0:批量还原、1:线上商城(上架/下架/还原)、2:app(上架/下架/还原)
            if (Objects.nonNull(status)){
                map.put("status",status);
            }
            if (Objects.nonNull(appChecked)){
                map.put("appChecked",appChecked);
            }
            if (Objects.nonNull(appCheckedValue)){
                map.put("appCheckedValue",appCheckedValue);
            }
            if (Objects.nonNull(onLineShopChecked)) {
                map.put("onLineShopChecked",onLineShopChecked);
            if (Objects.nonNull(onLineShopCheckedValue)){
                map.put("onLineShopCheckedValue",onLineShopCheckedValue);
            }
            }
            //------根据需求，添加相应字段------end
            BatchResult batchResult = goodsService.listing(goodsIds, goodsIdList.getSiteId(),map);

            return render(batchResult);
        } catch (RuntimeException re) {
            return render(32000, re.getMessage());
        } catch (Exception e) {
            return render(33001, e.getMessage());
        }
    }


    @PostMapping(value = "batch/delisting")
    public Object batchDeListing(@Valid @RequestBody GoodsIdList goodsIdList, BindingResult bindingResult, HttpServletRequest request) {
        try {
            hasErrors(bindingResult);
            int[] goodsIds = goodsIdList.getIdList();
            //------根据需求，添加相应字段------start
            Integer appChecked = goodsIdList.getAppChecked();
            Integer onLineShopChecked = goodsIdList.getOnLineShopChecked();
            Integer status = goodsIdList.getStatus();//0:批量还原或单个还原、1:线上商城(上架/下架)、2:app(上架/下架)
            Map<String,Object> map =new HashMap<>();
            if (Objects.nonNull(status)){
                map.put("status",status);
            }
            if (Objects.nonNull(appChecked)){
                map.put("appChecked",appChecked);
            }
            if (Objects.nonNull(onLineShopChecked)){
                map.put("onLineShopChecked",onLineShopChecked);
            }
            //------根据需求，添加相应字段------end
            BatchResult batchResult = goodsService.delisting(goodsIds, goodsIdList.getSiteId(),map);

            return render(batchResult);
        } catch (RuntimeException re) {
            return render(30000, re.getMessage());
        } catch (Exception e) {
            return render(30001, e.getMessage());
        }
    }

    @PostMapping(value = "batch/join")
    public Object batchJoin(@Valid @RequestBody BatchJoinIntegralGoods integralGoods, BindingResult bindingResult) {
        try {
            hasErrors(bindingResult);
            Map<String,Object> result=  goodsService.joinIntegralGoods(integralGoods);

            return render(result);
        } catch (RuntimeException re) {
            return render(32000, re.getMessage());
        } catch (Exception e) {
            return render(33001, e.getMessage());
        }
    }

    @PostMapping(value = "batch/delete")
    public Object batchDelete(@Valid @RequestBody GoodsIdList goodsIdList, BindingResult bindingResult) {
        try {
            hasErrors(bindingResult);
            int[] goodsIds = goodsIdList.getIdList();
            BatchResult batchResult = goodsService.delete(goodsIds, goodsIdList.getSiteId());

            return render(batchResult);
        } catch (RuntimeException re) {
            return render(31000, re.getMessage());
        } catch (Exception e) {
            return render(31001, e.getMessage());
        }
    }

    @RequestMapping(value = "/batch/deleteGoods")
    @ResponseBody
    public Map<String, Object> batchDeleteGoods(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> param = ParameterUtil.getParameterMap(request);
        Integer count = goodsService.batchDeleteGoods(param);
        Map<String, Object> result = new HashMap();
        result.put("count", count);
        return result;
    }

    /**
     * 批量导入任务
     *
     * @param batchImportDto
     * @param bindingResult
     * @return
     */
    @PostMapping(value = "batch/import")
    public Object batchImport(@Valid @RequestBody BatchImportDto batchImportDto, BindingResult bindingResult) {
        try {
            hasErrors(bindingResult);

            BatchResult result = null;

            if(StringUtil.equalsIgnoreCase("add", batchImportDto.getOption())){
                // 下载文件
                result = goodsImportService.batchImportTaskNew(batchImportDto);
            }else {
                result = goodsImportService.batchImportTask(batchImportDto);
            }


            return render(result);
        } catch (RuntimeException re) {
            return render(31000, re.getMessage());
        } catch (Exception e) {
            return render(31001, e.getMessage());
        }
    }

    protected void hasErrors(BindingResult bindingResult) throws RuntimeException {
        if (bindingResult.hasErrors()) {
            FieldError fe = bindingResult.getFieldError();
            String err = fe.getDefaultMessage();

            throw new RuntimeException(err);
        }
    }

    protected String render(int code, String msg) {
        return OldStyle.render(code, msg);
    }

    protected String render(Object obj) {
        return OldStyle.render(obj);
    }

    protected String render(String successTips) {
        Map result = new HashMap();
        result.put("code", 0);
        result.put("msg", successTips);
        return OldStyle.render(result);
    }
}
