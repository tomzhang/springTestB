package com.jk51.modules.goods.controller;

import com.alibaba.rocketmq.shade.io.netty.handler.codec.http.HttpRequest;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.Goods;
import com.jk51.model.coupon.requestParams.StockUpParams;
import com.jk51.model.grouppurchase.GroupPurchase;
import com.jk51.modules.distribution.result.Result;
import com.jk51.modules.goods.mapper.GoodsMapper;
import com.jk51.modules.goods.request.GoodsData;
import com.jk51.modules.goods.request.GoodsIdRequireData;
import com.jk51.modules.grouppurchase.request.GroupPurchaseParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

@Controller
@RequestMapping("/goods")
public class GoodsController extends GoodsBatchController {

    /**
     * 获取商品列表,获取列表中的商品信息
     */
    @PostMapping(value = "query")
    public List<Goods> query(HttpServletRequest request) {
        // 商品id
        String goodsId = request.getParameter("goods_id");

        Map<String, Object> param = new HashMap<>();

        param.put("goods_id", goodsId);
        //param.put("goods_title", "123456");

        List<Goods> goods = goodsService.find(param);

        return goods;
    }


    /**
     * 根据批准文号查询商品
     * @param request
     * @return
     */
    @PostMapping(value = "queryByApprovalNo")
    public Map<String,Object> queryByApprovalNo(HttpServletRequest request) {
        // 商品批准文号
        String approval_number = request.getParameter("approval_number");
        String site_id = request.getParameter("site_id");

        Map<String, Object> param = new HashMap<>();

        param.put("approval_number", approval_number);
        param.put("site_id", site_id);

        List<Map<String,Object>> goods = goodsService.getByApprovalNumber(param);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("goodsList",goods);
        return result;
    }
    @RequestMapping("/queryByBarCodeSYS")
    public Map<String,Object> queryByBarCodeSYS(HttpServletRequest request){

        String bar_code = request.getParameter("bar_code");
        String site_id = request.getParameter("siteId");
        return goodsService.queryByBarCodeSYS(bar_code,site_id);
    }

    @RequestMapping("/queryByBarCode")
    public Map<String,Object> queryByBarCode(HttpServletRequest request){

        String bar_code = request.getParameter("bar_code");
        String site_id = request.getParameter("siteId");
        String goods_id = request.getParameter("goods_id");
        return goodsService.queryByBarCode(bar_code,site_id,goods_id);
    }

    @RequestMapping("/queryByGoodsCode")
    public Map<String,Object> queryByGoodsCode(HttpServletRequest request){

        String goods_code = request.getParameter("goods_code");
        String site_id = request.getParameter("siteId");
        return goodsService.queryByGoodsCode(goods_code,site_id);
    }

    /**
     * 新增商品 单个
     * @api {post} /goods/create 新增商品
     * @apiName 新增商品
     * @apiGroup goods
     * @apiSchema (请求参数) {jsonschema=../schema/goods.create.req.json} apiParam
     * @apiSchema (响应内容) {jsonschema=../schema/goods.create.res.json} apiSuccess
     *
     * @apiSuccessExample {json} 请求成功示例:
     * {
     *     "status": true,
     *     "result": {
     *         "goods_id": 123456
     *     }
     * }
     *
     * @apiErrorExample {json} 请求失败示例:
     * {
     *     "status": false,
     *     "result": {
     *         "msg": "detail_tpl不能为空",
     *         "code": 1001
     *     }
     * }
     */
    @PostMapping(value = "/create"/*, consumes="application/json"*/)
    @ResponseBody
    public Object create(@Valid @RequestBody GoodsData goodsData, BindingResult bindingResult) throws Exception {
        try {

            hasErrors(bindingResult);
            int goodsId = goodsService.create(goodsData);
            if (StringUtil.isEmpty(goodsId)) {
                throw new Exception("商品创建失败");
            }

            // success
            Map result = new HashMap();
            result.put("goods_id", goodsId);

            return render(result);
        } catch (RuntimeException re) {
            return render(22003, re.getMessage());
        } catch (Exception e) {
            return render(22004, e.getMessage());
        }
    }

    /**
     * 更新商品 单个
     */
    @PostMapping(value = "/update")
    @ResponseBody
    public Object update(@Valid @RequestBody GoodsData goodsData, BindingResult bindingResult) throws Exception {
        try {
            hasErrors(bindingResult);
            goodsService.updateGoodsOnFail(goodsData);
            Map result = new HashMap();
            result.put("goods_id", goodsData.getGoodsId());
            return render(result);
        } catch (RuntimeException re) {
            return render(21003, re.getMessage());
        } catch (Exception e) {
            return render(21004, e.getMessage());
        }
    }
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 删除商品(软删除)  单个
     */
    @PostMapping(value = "delete",produces = "application/json; charset=utf-8")
    @ResponseBody
    public Object delete(@Valid @RequestBody GoodsIdRequireData goodsIdRequireData, BindingResult bindingResult) {
        try {
            hasErrors(bindingResult);
            Integer status = goodsIdRequireData.getStatus();
            Map<String, Object> param = new HashMap<>();
            param.put("status", status);
            goodsService.deleteOnFail(goodsIdRequireData.getGoodsId(), goodsIdRequireData.getSiteId(), param);
            return render("删除成功");
        } catch (RuntimeException re) {
            return render(20003, re.getMessage());
        } catch (Exception e) {
            return render(20004, e.getMessage());
        }
    }

    /**
     * 商品上架 单个
     */
    @PostMapping(value = "listing")
    @ResponseBody
    public Object listing(@Valid @RequestBody GoodsIdRequireData goodsIdRequireData, BindingResult bindingResult) {
        try {
            hasErrors(bindingResult);
            boolean isChange = goodsService.listingOnFail(goodsIdRequireData.getGoodsId(), goodsIdRequireData.getSiteId());
            if (! isChange) {
                throw new Exception("上架失败");
            }

            return render("上架成功");
        } catch (RuntimeException re) {
            return render(23003, re.getMessage());
        } catch (Exception e) {
            return render(23004, e.getMessage());
        }
    }

    /**
     * 商品下架 单个
     */
    @PostMapping(value = "delisting")
    @ResponseBody
    public Object delisting(@Valid @RequestBody GoodsIdRequireData goodsIdRequireData, BindingResult bindingResult) throws Exception {
        try {
            hasErrors(bindingResult);
            boolean isChange = goodsService.delistingOnFail(goodsIdRequireData.getGoodsId(), goodsIdRequireData.getSiteId());
            if (! isChange) {
                throw new Exception("下架失败");
            }

            return render("下架成功");
        } catch (RuntimeException re) {
            return render(24003, re.getMessage());
        } catch (Exception e) {
            return render(24004, e.getMessage());
        }
    }


    @RequestMapping("findBarndId")
    public Map<String, Object> findBarndId(HttpServletRequest request){
        Map param =  ParameterUtil.getParameterMap(request);
        Map<String,Object> map = new HashMap<String,Object>();
        int barndId = goodsService.getBarndId(param.get("barndName").toString(),Integer.valueOf(param.get("siteId").toString()));
        map.put("barndId",barndId);
        return map;
    }

    @ResponseBody
    @RequestMapping("queryGoodsInfoByIds")
    public ReturnDto queryGoodsInfoByIds(Integer siteId, String goodsIds) {
        if (siteId == null)
            return ReturnDto.buildFailedReturnDto("siteId不能为null");
        if (goodsIds == null)
            return ReturnDto.buildFailedReturnDto("goodsIds不能为null");

        return goodsService.queryGoodsInfoByIds(siteId, goodsIds);
    }

    @ResponseBody
    @RequestMapping("queryGoodsInfoByIds2")
    public ReturnDto queryGoodsInfoByIds2(@RequestParam(value = "page",defaultValue = "1") Integer page,
                                          @RequestParam(value = "pageSize",defaultValue = "15") Integer pageSize,
                                          Integer siteId, String goodsIds) {
        if (siteId == null)
            return ReturnDto.buildFailedReturnDto("siteId不能为null");
        if (goodsIds == null)
            return ReturnDto.buildFailedReturnDto("goodsIds不能为null");

        return goodsService.queryGoodsInfoByIds2(page,pageSize,siteId, goodsIds);
    }


    /**
     * 根据价格修改商品
     *
     */
    @RequestMapping("/goodPrice/edit")
    @ResponseBody
    public String updateGoodByPrice(HttpServletRequest request) {
        Map param = ParameterUtil.getParameterMap(request);
        return goodsService.updateGoodByPrice(param);
    }


    @RequestMapping("searchStatus")
    @ResponseBody
    public ReturnDto searchStatus(@RequestBody StockUpParams stockUpParams) {
        GroupPurchase goruoLeaderStatus1 = groupPurChaseMapper.getGoruoLeaderStatus1(stockUpParams.getSiteId(), stockUpParams.getId());
        if(goruoLeaderStatus1.getParentId()!=null){
        GroupPurchase goruoLeaderStatus2 = groupPurChaseMapper.getGoruoLeaderStatus2(stockUpParams.getSiteId(), goruoLeaderStatus1.getParentId());
            return ReturnDto.buildSuccessReturnDto(goruoLeaderStatus2);
        }
        return ReturnDto.buildSuccessReturnDto(goruoLeaderStatus1);
    }

    @RequestMapping("queryById")
    @ResponseBody
    public ReturnDto queryById(Integer siteId, Integer goodsId) {
        Optional<Goods> optional = goodsService.queryById(siteId, goodsId);

        return optional.map(ReturnDto::buildSuccessReturnDto).orElseGet(() -> ReturnDto.buildFailedReturnDto("无此商品信息"));
    }

    @RequestMapping("addDefaultImage")
    @ResponseBody
    public Result addDefaultImage(Integer siteId, String goodsCode) {
        try {
            return goodsService.addDefaultImage(siteId, goodsCode);
        } catch (Exception e) {
            return Result.fail("插入图片异常：" + e);
        }
    }
}
