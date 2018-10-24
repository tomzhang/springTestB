package com.jk51.modules.registration.controller;

import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.goods.PageData;
import com.jk51.model.registration.models.MyMingYiYuYueDetail;
import com.jk51.model.registration.models.ServceOrder;
import com.jk51.model.registration.requestParams.ServerOrderParams;
import com.jk51.modules.registration.request.ServceOrderRequestParam;
import com.jk51.modules.registration.request.SubscribeDetailRequestParam;
import com.jk51.modules.registration.service.ServceOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * filename :com.jk51.modules.registration.controller.
 * author   :mqq
 * date     :2017/4/7
 * Update   :
 */
@RestController
@RequestMapping("serviceorder")
public class ServiceOrderController {
    private static  final Logger logger= LoggerFactory.getLogger(ServiceOrderController.class);

    @Autowired
    ServceOrderService servceOrderService;

    @RequestMapping("addServiceOrder")
    @ResponseBody
    public ReturnDto addServiceOrder(ServceOrder order){
        if(order.getSiteId() == null){
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }
        if(order.getGoodsId() == null){
            return ReturnDto.buildFailedReturnDto("Goodsid不能为空");
        }

        if(order.getStoreId() == null){
            return ReturnDto.buildFailedReturnDto("门店id不能为空");
        }

        if(order.getDiagnoseStatus() == null){
            return ReturnDto.buildFailedReturnDto("初，复诊 状态不能空");
        }

        if(order.getServeStatus() == null){
            return ReturnDto.buildFailedReturnDto("服务 状态不能空");
        }

        if(order.getUseDetailId() == null){
            return ReturnDto.buildFailedReturnDto("UseDetailId不能空");
        }

        if(order.getSchedulePersonId() == null){
            return ReturnDto.buildFailedReturnDto("SchedulePersonId不能空");
        }
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        boolean result=this.servceOrderService.insert(order);
        return ReturnDto.buildSuccessReturnDto(result);
    }

    @RequestMapping("queryAllServiceOrder")
    @ResponseBody
    public ReturnDto queryAllServiceOrder(ServceOrder order){
        if(order.getSiteId() == null){
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }
        if(order.getGoodsId() == null){
            return ReturnDto.buildFailedReturnDto("Goodsid不能为空");
        }

        if(order.getStoreId() == null){
            return ReturnDto.buildFailedReturnDto("门店id不能为空");
        }

        List<ServceOrder> list=this.servceOrderService.queryAllServceOrder(order);
        return ReturnDto.buildSuccessReturnDto(list);
    }

    @RequestMapping("getMyMingyiYuYueCount")
    @ResponseBody
    public ReturnDto queryAllServiceOrder(@RequestBody ServerOrderParams orderParams){
        if(null==orderParams.getMemberId()){
            return ReturnDto.buildFailedReturnDto("memberId为空");
        }
        int result =this.servceOrderService.queryMyMingYiYuYueCount(orderParams.getMemberId());
        return ReturnDto.buildSuccessReturnDto(result);
    }

    /**
     *
     * @param orderParams
     * @return
     */

    @RequestMapping(value = "getMyMingyiYuYueList")
    @ResponseBody
    public ReturnDto getMyMingyiYuYueList(@RequestBody ServerOrderParams orderParams){
        if(null==orderParams.getMemberId()){
            return ReturnDto.buildFailedReturnDto("memberId为空");
        }
        if(null==orderParams.getSiteId()){
            return ReturnDto.buildFailedReturnDto("siteId");
        }
        Map<String,Object> paramMap=new HashMap<String,Object>();
        paramMap.put("memberId",orderParams.getMemberId());
        paramMap.put("siteId",orderParams.getSiteId());
        List<MyMingYiYuYueDetail> result =this.servceOrderService.queryMyMingYiYuYueDetailList(paramMap);
        return ReturnDto.buildSuccessReturnDto(result);
    }

    /**
     *
     * @param orderParams
     * @return
     */

    @RequestMapping("selectAndUpdateServiceOrder")
    @ResponseBody
    public ReturnDto selectAndUpdateServiceOrder(@RequestBody ServerOrderParams orderParams){
        if(orderParams.getSiteId() == null){
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }
        if(orderParams.getGoodsId() == null){
            return ReturnDto.buildFailedReturnDto("商品id不能为空");
        }


        if(orderParams.getType()==null){
            return ReturnDto.buildFailedReturnDto("操作类型不能为空");
        }

       return this.servceOrderService.queryAndUpdateServerOrderForGoodStatus(orderParams);
    }

    @RequestMapping("deleteServiceOrder")
    @ResponseBody
    public ReturnDto deleteServiceOrder(@RequestBody ServceOrder order){
        if(order.getSiteId()== null){
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }

        if(order.getId() == null){
            return ReturnDto.buildFailedReturnDto("id不能为空");
        }

        if(order.getServeStatus()==null||order.getServeStatus() != 3){
            return ReturnDto.buildFailedReturnDto("取消预约状态错误");
        }

        int  result=this.servceOrderService.deleteServderOrderServerStatus(order);

        return ReturnDto.buildSuccessReturnDto(result);
    }

    @RequestMapping(value = "updateServiceOrder")
    @ResponseBody
    public ReturnDto updateServiceOrder(@RequestBody ServceOrder order){
        if(order.getSiteId()== null){
            return ReturnDto.buildFailedReturnDto("siteId为空");
        }

        if(order.getId() == null){
            return ReturnDto.buildFailedReturnDto("id不能为空");
        }
        int  result=this.servceOrderService.updateservceorder(order);

        return ReturnDto.buildSuccessReturnDto(result);
    }

    /**
     *
     * @param subscribeDetailRequestParam
     * @return
     */
    @RequestMapping(value = "queryAllServerOrderDetailPage")
    @ResponseBody
    public ReturnDto queryAllServerOrderDetailPage(SubscribeDetailRequestParam subscribeDetailRequestParam) {
        PageInfo<?> pageInfo=null;
        try {
            pageInfo = this.servceOrderService.queryAllDoctorDetailByStore(subscribeDetailRequestParam);
        } catch (Exception e) {
            logger.error("获取列表失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("查询对账单出错");
        }
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }

    /**
     *
     * @param queryServceOrder
     * @return
     */
    @RequestMapping(value = "queryServerOrderPage")
    @ResponseBody
    public ReturnDto queryServerOrderPage(ServceOrderRequestParam queryServceOrder) {
        PageInfo<?> pageInfo=null;
        try {
            pageInfo = this.servceOrderService.queryServceOrderDetail(queryServceOrder);
        } catch (Exception e) {
            logger.error("获取列表失败,错误是" + e);
            return ReturnDto.buildFailedReturnDto("查询对账单出错");
        }
        return ReturnDto.buildSuccessReturnDto(pageInfo);
    }


    @RequestMapping(value = "queryServerOrderDailInfo")
    @ResponseBody
    public ReturnDto queryServerOrderDailInfo(ServceOrderRequestParam queryServceOrder) {
        if(null==queryServceOrder.getServceOrdId()){
            return ReturnDto.buildFailedReturnDto("ServceOrdId为空");
        }

        if(null==queryServceOrder.getSiteId()){
            return ReturnDto.buildFailedReturnDto("SiteId为空");
        }

        List<PageData> list = servceOrderService.queryServceOrderDetailInfo(queryServceOrder);

        return ReturnDto.buildSuccessReturnDto(list);
    }
}
