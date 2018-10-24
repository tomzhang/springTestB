package com.jk51.modules.pandian.controller;


import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.modules.distribution.result.Result;
import com.jk51.modules.pandian.Response.ClerkCount;
import com.jk51.modules.pandian.elasticsearch.repositories.InventoryRepository;
import com.jk51.modules.pandian.param.*;
import com.jk51.modules.pandian.service.InventoryService;
import com.jk51.modules.pandian.service.PandianOrderRedisManager;
import com.jk51.modules.pandian.service.PandianTimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.jk51.modules.pandian.util.Constant.GETALL;


/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-10-27
 * 修改记录:
 */
@RestController
@RequestMapping("/inventory")
@ResponseBody
public class InventoryController {

    private Logger logger = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private PandianTimeService pandianTimeService;

    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private PandianOrderRedisManager pandianOrderRedisManager;

    @RequestMapping("asyncRedisSort")
    public Result asyncRedisSort(){

        pandianOrderRedisManager.mergeZsetForAsync();

        return Result.success();
    }

    /**
     * 盘点查询（排除商编重复，参数多个属性赋值）-
     */
    @RequestMapping("/inventories")
    public Result inventories(@RequestBody InventoryParam param) {

        param.setGet_request_time(new Date());
        Result result = inventoryService.getInventories(param);

        param.setResponse_time(new Date());

        //发送盘点时间消息队列
        try {
            pandianTimeService.sendMessageToMQ(param);
        } catch (IllegalArgumentException e){
            logger.info("盘点消息参数异常：param：{}，报错信息：{}",param,e.getMessage());
        } catch (Exception e) {
            logger.error("发送盘点时间消息队列异常：{}",e);
        }

        return result;
    }


    /**
     * 盘点查询（排除商编重复项）
     */
    @RequestMapping("/getInventoriesDeleteRepetition")
    public Result getInventoriesDeleteRepetition(@RequestBody InventoryParam param) {
        return inventoryService.getInventoriesDeleteRepetition(param);
    }

    /**
     * 盘点查询-
     */
    @RequestMapping("/getInventories")
    public Result getInventories(@RequestBody InventoryParam param) {
        return inventoryService.getInventories2(param);
    }

    /***
     *  盘点确认，成功后根据盘点单和商品编码查询库存集合-
     */
    @RequestMapping("/confirmInventories")
    public Result confirmInventories(@RequestBody InventoryConfirmParam param) {

        try{
            return inventoryService.confirmInventories(param);
        }catch (Exception e){

            logger.error("盘点确认异常,message:{},stackTrace:{}",e.getMessage(),e.getStackTrace());
            return Result.fail(StringUtil.isEmpty(e.getMessage())?"盘点确认异常":e.getMessage());
        }
    }

    /**
     * 手动修改数据接口
     * */
    @RequestMapping("manualOperationIntenvories")
    public Result manualOperationIntenvories(@RequestBody SyncInventoryDataParam param){

        String paramStr = "";

        try {
            paramStr = JacksonUtils.obj2json(param);
        } catch (Exception e) {
            return Result.fail(ExceptionUtil.exceptionDetail(e));
        }

        if(StringUtil.isEmpty(param.getPandianNum())||StringUtil.isEmpty(param.getStoreId())||StringUtil.isEmpty(param.getStoreAdminId())){

            return Result.fail("参数不对：param："+paramStr);
        }

        logger.info("使用手动修改盘点库存数据接口: param: {}",paramStr);
        inventoryRepository.manualOperationIntenvories(param);
        return Result.success();
    }


    /**
     * 盘点单中未盘点集合(复盘时查询需要复盘的明细)
     */
    @RequestMapping("/hasNotCheckInventories")
    public Result hasNotCheckInventories(@RequestBody HasNotCheckInventories param) {
        Result result = null;
        try{
            result = inventoryService.hasNotCheckInventories(param);
        }catch (Exception e){
            logger.error("待盘点查询报错：{}",ExceptionUtil.exceptionDetail(e));
            result = Result.fail("待盘点查询异常");
        }

        return result;
    }

    /**
     * 新增商品
     */
    @RequestMapping("/addInventories")
    public Result addInventories(@RequestBody InventoryConfirmParam param) {

        return inventoryService.addInventories(param);
    }


    /**
     * 复盘查询（查询该店员所有库存与实际存储不相符的数据）
     */
    @RequestMapping("/repeatInventory")
    public Result repeatInventoryCheck(@RequestBody RepeatInventoryParam param) {

        return inventoryService.repeatInventoryCheck(param.getPandian_num(), param.getStoreAdminId());
    }

    /**
     * 复盘条件查询接口(扫描和条件搜索)
     * */
    @RequestMapping("/repeatInventoryForCondition")
    public Result repeatInventoryForCondition(@RequestBody RepeatInventoryForConditionParam param){

        return inventoryService.repeatInventoryForCondition(param);
    }

    /**
     * 店员查询盘点计划
     */
    @RequestMapping("/pandianPlans")
    public Result getPandianPlan(@RequestBody PandianPlansParam param) {

        try {
            return inventoryService.getPandianPlan(param.getAuthToken());
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }

    }


    /**
     * 盘点状况
     */
    @RequestMapping("/getPandianOrderStatus")
    public Result getPandianOrderStatus(@RequestBody PandianOrderStatusParam param) {

        return inventoryService.getPandianOrderExtList(param);
    }

    /**
     * 查询参与盘点的门店
     */
    @RequestMapping("/getStores")
    public Result getStores(@RequestBody StoresParam param) {
        return inventoryService.getStores(param);
    }

    /**
     * 更新盘点状态
     * (下发，复盘，审核)
     * 返回更新失败的信息
     */
    @RequestMapping("/updateStatus")
    public Result updateStatus(@RequestBody PandianStatusUpdateParam param) {
        return inventoryService.updateStatus(param);
    }

    /**
     * 盘点明细查询
     */
    @RequestMapping("/details")
    public Result getPandianOrderDetail(@RequestBody PandianOrderDetailParam param) {

        return inventoryService.getPandianOrderDetail(param);
    }

    /**
     * 盘点人确认-
     */
    @RequestMapping("/storeAdminConfirm")
    public Result storeAdminConfirm(@RequestBody StoreAdminConfirmParam param) {
        return inventoryService.storeAdminConfirm(param);
    }

    /**
     * 查询盘点单是否允许门店上传
     * */
    @RequestMapping("/isallowSiteUpload")
    public Result isallowSiteUpload(@RequestBody PandianNum pandianNum){
        return inventoryService.isallowSiteUpload(pandianNum);
    }

    @RequestMapping("/waitInventoryNum")
    public Result waitInventoryNum(@RequestBody WaitInventoryNumParam param){
        return inventoryService.waitInventoryNum(param);
    }

    /**
     * 店员盘点情况统计
     * */
    @RequestMapping("/clerkCount")
    public Result clerkCount(@RequestBody WaitInventoryNumParam param){

        try{
            ClerkCount clerkCount = inventoryService.clerkCount(param);

            return Result.success(clerkCount);
        }catch (Exception e){
            return Result.fail(e.getMessage());
        }

    }

    /**
     *
     * 盘点单中各店员盘点情况统计
     * */
    @RequestMapping("/orderClerkCount")
    public Result orderClerkCount(@RequestBody OrderClerkCountParam param){
        try{

            if(param.getIsConfirm() == null){
                param.setIsConfirm(GETALL);
            }

            List<OrderClerkCount> orderClerkCount = inventoryService.orderClerkCount(param);
            return Result.success(orderClerkCount);
        }catch (Exception e){
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 实际参加盘点的店员
     * */
    @RequestMapping("/joinInventoryUsers")
    public Result joinInventoryUsers(@RequestBody JoinInvenrotyParam param , BindingResult result){

        if(result.hasErrors()){
            return Result.fail(result.getAllErrors().get(0).getDefaultMessage());
        }
        return inventoryService.joinInventoryUsers(param);

    }

    /**
     * 盘点计划中设置参加盘点的店员
     * */
    @RequestMapping("/planJoinInventoryUsers")
    public Result plandJoinInventoryUsers(@RequestBody PlandJoinUsersParam param , BindingResult result){

        if(result.hasErrors()){
            return Result.fail(result.getAllErrors().get(0).getDefaultMessage());
        }

        try{
            return Result.success(inventoryService.plandJoinInventoryUsers(param.getSiteId(),param.getStoreId(),param.getPlanId()));
        }catch (Exception e){
            return Result.fail(ExceptionUtil.exceptionDetail(e));
        }

    }
}
