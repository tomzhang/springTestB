package com.jk51.modules.pandian.service;

import com.github.pagehelper.PageInfo;
import com.jk51.commons.des.EncryptUtils;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.model.*;
import com.jk51.modules.appInterface.util.AuthToken;
import com.jk51.modules.distribution.result.Result;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.offline.service.OfflineCheckService;
import com.jk51.modules.pandian.Response.*;
import com.jk51.modules.pandian.dto.PandianLinkedDto;
import com.jk51.modules.pandian.dto.PandianSortRedisDto;
import com.jk51.modules.pandian.elasticsearch.modle.StoreCount;
import com.jk51.modules.pandian.elasticsearch.repositories.InventoryRepository;
import com.jk51.modules.pandian.elasticsearch.service.InventoriesESMenager;
import com.jk51.modules.pandian.error.ParseAccessTokenException;

import com.jk51.modules.pandian.mapper.BInventoryLogMapper;
import com.jk51.modules.pandian.mapper.BPandianOrderStatusMapper;
import com.jk51.modules.pandian.mapper.BPandianPlanExecutorMapper;
import com.jk51.modules.pandian.mapper.BPandianPlanMapper;
import com.jk51.modules.pandian.param.*;
import com.jk51.modules.pandian.service.satatusManager.InventoriesStatusManager;
import com.jk51.modules.pandian.util.Constant;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.jk51.modules.pandian.util.Constant.*;
import static com.jk51.modules.pandian.util.StatusConstant.REPEATION;
import static com.jk51.modules.pandian.util.StatusConstant.WAITCONFIRM;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-10-27
 * 修改记录:
 */

@Service
public class InventoryService {

    private Logger logger = LoggerFactory.getLogger(InventoryService.class);

    private static final int ENNABLE_PANDIAN_ORDER = 1;
    //全盘
    public static final int ALL_DO = 0;

    @Autowired
    private InventoriesManager inventoriesManager;
    @Autowired
    private InventoryRedisManager inventoryRedisManager;
    @Autowired
    private InventoriesStatusManager inventoriesStatusManager;
    @Autowired
    private BPandianPlanMapper pandianPlanMapper;
    @Autowired
    private PandianOrderRedisManager pandianOrderRedisManager;
    @Autowired
    private OfflineCheckService offlineCheckService;
    @Autowired
    private StoresMapper storesMapper;
    @Autowired
    private BPandianOrderStatusMapper bPandianOrderStatusMapper;
    @Autowired
    private InventoriesESMenager inventoriesESMenager;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private PandianAsyncService pandianAsyncService;
    @Autowired
    private StoreAdminMapper storeAdminMapper;
    @Autowired
    private BPandianPlanExecutorMapper bPandianPlanExecutorMapper;
    @Autowired
    private StoreAdminExtMapper storeAdminExtMapper;

    /**
     * 根据盘点号和(商品名称或者商品编码或商品二维码)查询商库存集合
     */
    public Result getInventories(InventoryParam param) {
        List<Inventories> list = inventoriesESMenager.getInventoriesOr(param);
        return getResult(new HashSet(list));

    }

    public Result getInventories2(InventoryParam param) {
        return getResult(inventoriesESMenager.getInventories(param));
    }

    public Result getInventoriesDeleteRepetition(InventoryParam param) {
        return getResult(new HashSet(inventoriesESMenager.getInventories(param)));
    }


    /**
     * 盘点库存确认
     */
    public Result confirmInventories(InventoryConfirmParam param) {


        //设置storeAdminId
        AuthToken authToken = parseAccessToken(param.getAuthToken());
        param.setStoreAdminId(authToken.getStoreAdminId());
        param.setStoreId(authToken.getStoreId());
        param.setSiteId(authToken.getSiteId());

        String checkResult = checkParam(param);
        if(!StringUtil.isEmpty(checkResult)){
            return Result.fail(checkResult);
        }

        //该商品编码的商品是否有其他店员盘点过
        String storeAdminName = inventoriesManager.haveInventory(param);
        if (!StringUtil.isEmpty(storeAdminName)) {
            return Result.fail("该商品[" + storeAdminName + "]已盘点!");
        }

        //该商品编码的商品是否已经有店员盘点确认过(店员盘点确认后不能在盘)
        String storeAdminName2 = inventoriesManager.haveStoreAdminConfirm(param);
        if (!StringUtil.isEmpty(storeAdminName2)) {
            return Result.fail("店员[" + storeAdminName2 + "]已经盘点确认,不能再修改!");
        }

        boolean statusValid = checkPandianNumStatus(param.getPandian_num(),param.getStoreId(),param.getSiteId());

        if(!statusValid){
            return Result.fail("盘点单[" + param.getPandian_num() + "]状态无效,不能再修改!");
        }

        if(batchNumHasSame(param.getBatchNums())){
            return Result.fail("商品的批号不能相同,请重新修改。");
        }

        //该盘点单是否为连续盘点
        if (inventoriesManager.checkIsdoubleCheck(param.getPandian_num(),param.getSiteId())) {

            return doubleCheckConfirm(param);

        } else {

            return notdoubleCheckConfirm(param);

        }


    }



    private String checkParam(InventoryConfirmParam param){

        String result = "";

        BPandianPlan pandianPlan = pandianPlanMapper.findByPandianNum(param.getPandian_num(),param.getSiteId());

        if(pandianPlan.getPlanCheckType().equals(ALL)){

            List<InventoryBatchNum> batchNums = param.getBatchNums();
            for(InventoryBatchNum b:batchNums){

                if(StringUtil.isEmpty(b.getBatchNum())){

                    result = "批号不能为空";
                }
            }
        }


        return result;

    }

    /**
     * app盘点确认时,盘点单状态确认,只有状态在待确认的状态才是正确的
     * */
    private boolean checkPandianNumStatus(String pandianNum,Integer storeId,Integer siteId){

        StatusParam param = new StatusParam();
        param.setPandian_num(pandianNum);
        param.setStoreId(storeId);
        param.setSiteId(siteId);
        Integer status = bPandianOrderStatusMapper.getStatus(param);

        if(status.equals(WAITCONFIRM)||status.equals(REPEATION)){

            return true;
        }

        return false;
    }
    /**
     * 盘点单中未盘点集合(复盘时查询需要复盘的明细)
     */
    public Result hasNotCheckInventories(HasNotCheckInventories param) throws Exception {

        param = setUserInfo(param);

        HasNotCheckInventoriesRes res = null;

        StatusParam statusParam = getStatusParam(param.getPandian_num(),param.getSiteId(),param.getStoreId());

        if(isRepratInventory(statusParam)){
            res = inventoriesManager.getHasNotCheckInventoriesForRepeat(param);
        }else {
            res = inventoriesManager.getHasNotCheckInventories(param);
        }
        return Result.success(res);
    }

    private HasNotCheckInventories setUserInfo(HasNotCheckInventories param){

        AuthToken authToken = parseAccessToken(param.getAuthToken());
        param.setStoreId(authToken.getStoreId());
        param.setSiteId(authToken.getSiteId());
        param.setStoreAdminId(authToken.getStoreAdminId());

        return param;
    }

    private boolean isRepratInventory(StatusParam param){

        boolean result = false;
        Integer status = bPandianOrderStatusMapper.getStatus(param);

        if(status.equals(REPEATION)){
            result = true;
        }

        return result;
    }

    private StatusParam getStatusParam(String pandianNum,Integer siteId,Integer storeId){
        StatusParam result = new StatusParam();
        result.setPandian_num(pandianNum);
        result.setSiteId(siteId);
        result.setStoreId(storeId);

        return result;
    }
    /**
     * 新增商品
     */
    public Result addInventories(InventoryConfirmParam param) {

        try {

            AuthToken authToken = parseAccessToken(param.getAuthToken());
            param.setStoreAdminId(authToken.getStoreAdminId());
            param.setStoreId(authToken.getStoreId());
            param.setSiteId(authToken.getSiteId());

            if(batchNumHasSame(param.getBatchNums())){
                return Result.fail("商品的批号不能相同,请重新修改。");
            }

            //判断盘点单中，是否有这个指定商品编码的商品
            if (hasInventoryForGoodsCode(param.getPandian_num(), param.getGoods_code(), param.getStoreId(),param.getSiteId())) {
                return Result.fail("该商品编码已存在,无法重复添加!");
            }

            inventoriesManager.addNewGoods(param);
        } catch (Exception e) {

            logger.error("新增商品失败,失败原因:{}", e.getMessage());
            return Result.fail(e.getMessage());
        }

        return Result.success();
    }

    //批号是否一致
    private boolean batchNumHasSame( List<InventoryBatchNum> batchNums){
        boolean result = true;
        Set<InventoryBatchNum> set = new HashSet<>();
        set.addAll(batchNums);

        if(batchNums.size() == set.size()){
            result = false;
        }
        return result;
    }

    //判断盘点单中，是否有这个指定商品编码的商品
    private boolean hasInventoryForGoodsCode(String pandianNum, String goods_code, Integer storeId, Integer siteId) {

        if (StringUtil.isEmpty(inventoriesManager.getInventoryByGoodsCode(goods_code, pandianNum, storeId,siteId))) {
            return false;
        }

        return true;
    }


    /**
     * 复盘模式下店员下的有差异的库存集合(根据盘点单号和storeAdminId查询该店员下的有差异的库存集合)
     */
    public Result repeatInventoryCheck(String pandian_num, int storeAdminId) {

        return Result.success(new HashSet(inventoriesManager.getHasDifferenceInventories(pandian_num, storeAdminId)));
    }


    /**
     * 复盘条件查询接口
     */
    public Result repeatInventoryForCondition(RepeatInventoryForConditionParam param) {

        return Result.success(new HashSet(inventoriesManager.repeatInventoryForCondition(param)));
    }


    /**
     * 店员查询盘点计划
     */
    public Result getPandianPlan(String authToken) {


        AuthToken authToken1 = parseAccessToken(authToken);
        ClerkParam param = new ClerkParam();
        param.setStoreId(authToken1.getStoreId());
        param.setSiteId(authToken1.getSiteId());
        param.setStoreAdminId(authToken1.getStoreAdminId());

        List<PandainPlanMap> pandainPlanMaps = inventoriesManager.getPandianPlan(param);

        return Result.success(getPandianPlans(pandainPlanMaps,param));
    }

    /**
     * 盘点状况
     */
    public Result getPandianOrderExtList(PandianOrderStatusParam param) {

        if (!StringUtil.isEmpty(param.getCreateTime())) {
            param.setCreateTime(param.getCreateTime() + " 00:00:00");
        }

        if (!StringUtil.isEmpty(param.getEndTime())) {
            param.setEndTime(param.getEndTime() + " 23:59:59");
        }

        PageInfo<BPandianOrderList> info = inventoriesManager.getPandianOrderExtList(param);

        List<PandianOrderResponse> responses = getPandianOrderResponse(info.getList());

        PageInfo<?> pageInfo = new PageInfo(responses);
        pageInfo.setPageNum(info.getPageNum());
        pageInfo.setPageSize(info.getPageSize());
        pageInfo.setTotal(info.getTotal());
        pageInfo.setPages(info.getPages());
        return Result.success(pageInfo);
    }


    /**
     * 查询参与盘点的门店
     */
    public Result getStores(StoresParam param) {

        List<StoresResponse> storesResponses = inventoriesManager.getStores(param);
        if (StringUtil.isEmpty(storesResponses)) {
            Result.success(new ArrayList<>());
        }

        return Result.success(storesResponses);
    }

    private List<PandianOrderResponse> getPandianOrderResponse(List<BPandianOrderList> list) {

        List<PandianOrderResponse> responses = new ArrayList<>();
        if (StringUtil.isEmpty(list)) {
            return responses;
        }


        for (BPandianOrderList p : list) {

            StoreCount storeCount = getStoreCount(p.getPandianNum(),p.getStoreId(),p.getSiteId(),p.getPlanId());

            PandianOrderResponse response = new PandianOrderResponse();
            response.setPandian_num(p.getPandianNum());
            response.setStoreName(p.getStoreName());
            response.setCreateTime(p.getCreateTime());
            response.setPandianType(getPandainType(p));
            response.setActualStoreTotal(StringUtil.isEmpty(storeCount.getSumActual())?0:storeCount.getSumActual());
            response.setInventoryTotal(StringUtil.isEmpty(storeCount.getSumInventory())?0:storeCount.getSumInventory());
            response.setProfitAndLossNum(storeCount.getProfitAndLossNum());
            response.setStatu(getPandianStatus(p.getStatus()));
            response.setStatusNum(p.getStatus());
            response.setProfitAndLossStatus(storeCount.getProfitAndLossStatus());
            response.setCheckerNum(storeCount.getCheckerNum());
            response.setCountInventoryed(storeCount.getCountInventoryed());
            response.setCountNotInventoryed(storeCount.getNotInventoryed());
            response.setPlanCheckerNum(storeCount.getPlanCheckerNum());
            response.setPlan_id(p.getPlanId());
            response.setStoreId(p.getStoreId());
            response.setPandianOrderId(p.getId());
            response.setIsUpSite(p.getIsUpSite());
            response.setStoresNumber(p.getStoresNumber());
            response.setBillid(p.getBillid());
            responses.add(response);
        }

        return responses;
    }



    private StoreCount getStoreCount(String pandianNum,Integer storeId,Integer siteId,Integer planId){

        StoreCount result =  inventoryRepository.countInventoryByPandianNumAndStoreId(siteId,storeId,pandianNum);
        result.setPlanCheckerNum(bPandianPlanExecutorMapper.getPlanCheckNum(siteId,storeId,planId));
        result.setProfitAndLossNum(inventoryRepository.getProfitAndLossNum(siteId,storeId,pandianNum));
        return result;
    }


    private BigDecimal getProfitAndLossNum(BPandianOrderList p){

        if( p.getCount()==null  ){
            return null;
        }

        return p.getCount().getProfitAndLossNum();
    }
    private BigDecimal getInventoryTotal(BPandianOrderList p){

        if( p.getCount()==null  ){
            return null;
        }

        return p.getCount().getInventoryTotal();
    }
    private BigDecimal getActualStoreTotal(BPandianOrderList p){
        if( p.getCount()==null  ){
            return null;
        }

        return p.getCount().getActualStoreTotal();
    }

    private String getProfitAndLossStatus(BPandianOrderList p){

        if( p.getCount()==null ||  p.getCount().getProfitAndLossStatus()==null ){
            return "未开始盘点";
        }

        return p.getCount().getProfitAndLossStatus();
    }


    private String getPandianStatus(Integer status) {

        String result = "";
        switch (status) {
            case 0:
                result = "待上传";
                break;
            case 100:
                result = "待下发";
                break;
            case 200:
                result = "待确认";
                break;
            case 300:
                result = "待审核";
                break;
            case 400:
                result = "已审核";
                break;
            case 500:
                result = "复盘";
                break;
            case 600:
                result = "关闭";
                break;
            default:
                result = "未定义状态";
                break;

        }

        return result;
    }


    //解析Token
    public AuthToken parseAccessToken(String accessToken) {


        String token = EncryptUtils.base64DecodeToString(accessToken.getBytes());
        AuthToken authToken = null;
        try {
            authToken = JacksonUtils.json2pojo(token, AuthToken.class);

        } catch (Exception e) {
            logger.error("解析AccessToken失败,报错信息{},token{}", e, token);
            throw new ParseAccessTokenException("解析AccessToken失败");
        }

        return authToken;
    }

    //获取盘点计划
    private List<PandianPlanResponse> getPandianPlans(List<PandainPlanMap> pandainPlanMaps, ClerkParam param) {

        List<PandianPlanResponse> list = new ArrayList<>();

        if (StringUtil.isEmpty(pandainPlanMaps)) {
            return list;
        }

        for (PandainPlanMap m : pandainPlanMaps) {

            PandianPlanResponse r = new PandianPlanResponse();
            r.setPandian_num(m.getPandian_num());
            r.setType(getPlanType(m));
            r.setDescription(getPlanDescription(m));
            r.setPlanType(m.getPlan_check_type());

            WaitInventoryNumParam wparam = getWaitInventoryNumParam(m.getPandian_num(),param);

            if(m.getStatus().equals(REPETITION)){
                r.setRepeatNum(inventoryRepository.repeatInventoryNum(wparam));
            }else {
                r.setWaitNum(inventoryRepository.waitInventoryNum(wparam));
            }
            r.setClerckNum(clerksNum(m.getClerks(),param));
            r.setCreateTime(m.getCreateTime());
            list.add(r);
        }

        return list;
    }

    private int clerksNum(String clerks,ClerkParam param){

        int result = 0;
        try{

            if(clerks.equals(CLERKS_EMPTY)){

                result = storeAdminMapper.getStoreAdminIdsByStore(param.getSiteId(),param.getStoreId()).size();
            }else {

                String[] clerkArray = clerks.split(",");
                result = clerkArray.length;
            }
        }catch (Exception e){
            logger.error("盘点获取参与人数失败，clerks:{},param:{},报错信息：{}",clerks,param,ExceptionUtil.exceptionDetail(e));
        }

        return result;
    }

    private WaitInventoryNumParam getWaitInventoryNumParam(String pandianNum,ClerkParam param){

        WaitInventoryNumParam result = new WaitInventoryNumParam();
        result.setStoreId(param.getStoreId());
        result.setSiteId(param.getSiteId());
        result.setPandian_num(pandianNum);
        result.setStoreAdminId(param.getStoreAdminId());

        return result;
    }


    private String getPlanDescription(PandainPlanMap m) {

        StringBuilder builder = new StringBuilder();
        builder.append(m.getType() == 0 ? "总部盘点计划" : "门店盘点计划");
        builder.append("-");
        builder.append(getPlanType(m));
        builder.append("-计划时间(");
        builder.append(m.getPlan_type() == 1 ? "立即" : m.getPlan_hour() + "点");
        builder.append(")");

        return builder.toString();

    }


    //获取盘点计划类型
    private String getPlanType(PandainPlanMap m) {

        String result = "";
        if (m.getStatus().equals(REPETITION)) {
            result = Constant.REPETITIONSTR;
        } else {
            result = switchType(m.getPlan_check_type());
        }

        return result;
    }


    private String getPandainType(BPandianOrderList p) {

        return switchType(p.getPlanCheckType());
    }


    private String switchType(int type) {

        String result = "";
        switch (type) {
            case 0:
                result = Constant.ALLINVENTTORY;
                break;
            case 1:
                result = Constant.DYNAMICINVENTTORY;
                break;
            case 2:
                result = Constant.NOTBATCHINVENTTORY;
                break;
            case 3:
                result = Constant.RANDOMINVENTTORY;
                break;
            default:
                result = "未定义盘点类型";
                break;
        }

        return result;
    }


    //连续盘点方式的盘点确认
    private Result doubleCheckConfirm(InventoryConfirmParam param) {

        boolean hasDifferent = checkInventoryHasDifferent(param);
        boolean notConfirmInfo = inventoryRedisManager.notConfirmInfo(param);

        if (hasDifferent && notConfirmInfo) {

            return Result.fail("盘点数据和账面不符，请重新盘点!");
        }


        return inventoryConfirm(param);
    }


    //判断实际库存与盘点库存是否一致
    private boolean checkInventoryHasDifferent(InventoryConfirmParam param) {


        boolean result = false;

        //是否有新增批号
        if (param.hasNewBatch()) {
            result = true;
        }

        //判断有InventoryId的批号，判断实际库存与导入数据库存是否一致
        for (InventoryBatchNum i : param.getBatchNums()) {


            if (StringUtil.isEmpty(i.getInventoryId())) {
                continue;
            }


            if (!inventoriesManager.checkNum(i.getInventoryId(), i.getNum())) {
                result = true;
            }
        }

        return result;
    }

    //非连续盘点方式的盘点确认
    private Result notdoubleCheckConfirm(InventoryConfirmParam param) {

        return inventoryConfirm(param);
    }





    /**
     * 1.盘点确认更新数据
     * 2.盘点序列功能
     */
    private Result inventoryConfirm(InventoryConfirmParam param) {
        try {

            //1.盘点确认更新数据
            inventoriesManager.confirmInventories(param);

            pandianAsyncService.asyncInsertInventoryLog(getBInventoryLog(param));

            //2.盘点序列功能
            List<Inventories> list = new ArrayList<>();
            //app是否开启了盘点排序
            //禁用排序功能
            if (param.getEnableOrder() == ENNABLE_PANDIAN_ORDER) {


                BPandianPlan plan = pandianPlanMapper.findByPandianNum(param.getPandian_num(), param.getSiteId());
                boolean isAlldo = plan.getPlanCheckType().equals(ALL_DO);

                //盘点计划是否为全盘类型
                if (isAlldo) {
                    PandianSortRedisDto dto = getPandianSortRedisDto(param);
                    list = pandianOrderRedisManager.pandianSort(dto);
                }

            }

            return Result.success(list);

        } catch (Exception e) {
            logger.error("盘点确认失败,错误信息：{}", ExceptionUtil.exceptionDetail(e));
            return Result.fail(e.getClass().getSimpleName() + ":" + e.getMessage());
        }
    }



    private BInventoryLog getBInventoryLog(InventoryConfirmParam param){
        BInventoryLog result = new BInventoryLog();
        result.setSiteId(param.getSiteId());
        result.setStoreId(param.getStoreId());
        result.setStoreAdminId(param.getStoreAdminId());
        result.setPandianNum(param.getPandian_num());
        result.setGoodsCode(param.getGoods_code());
        result.setEnableOrder(param.getEnableOrder());
        try {
            result.setBatchNums(JacksonUtils.obj2json(param.getBatchNums()));
        } catch (Exception e) {
            logger.error("盘点确认BatchNums转换JSON报错：param:{},报错信息:{}",param,ExceptionUtil.exceptionDetail(e));
        }
        result.setCreateTime(new Date());
        result.setUpdateTime(new Date());

        return result;
    }

    private PandianSortRedisDto getPandianSortRedisDto(InventoryConfirmParam param) {
        PandianSortRedisDto result = new PandianSortRedisDto();
        result.setSiteId(param.getSiteId());
        result.setStoreId(param.getStoreId());
        result.setCurrentStoreAdminId(param.getStoreAdminId());
        result.setCurrentGoodsCode(param.getGoods_code());
        result.setPandianNum(param.getPandian_num());
        result.setBatchNums(param.getBatchNums());
        result.setEnableOrder(param.getEnableOrder());
        return result;
    }

    private List<Inventories> pandianSort(InventoryConfirmParam param) throws ExecutionException, InterruptedException {

        Future<List<Inventories>> future = new AsyncResult(new ArrayList<>());

        //app是否开启了盘点排序
        if (param.getEnableOrder() == ENNABLE_PANDIAN_ORDER) {

            BPandianPlan plan = pandianPlanMapper.findByPandianNum(param.getPandian_num(), param.getSiteId());
            boolean isAlldo = plan.getPlanCheckType().equals(ALL_DO);

            //盘点计划是否为全盘类型
            if (isAlldo) {

                PandianLinkedDto orderDto = getPandianOrderDto(param);
                future = inventoriesManager.pandianLinked(orderDto);
            }

        }

        return future.get();
    }

    private PandianLinkedDto getPandianOrderDto(InventoryConfirmParam param) {

        PandianLinkedDto result = new PandianLinkedDto();
        result.setSiteId(param.getSiteId());
        result.setStoreId(param.getStoreId());
        result.setGoodsCode(param.getGoods_code());
        result.setPandianNum(param.getPandian_num());
        result.setStoreAdminId(param.getStoreAdminId());
        return result;
    }


    private Result getResult(Collection collection) {

        if (StringUtil.isEmpty(collection)) {
            return Result.fail("查询结果为空");
        }

        return Result.success(collection);
    }

    private <T> Result getResultForPageHelp(PageInfo<List<T>> pageInfo) {

        if (pageInfo.getTotal() == 0) {
            return Result.fail("查询结果为空");
        }

        return Result.success(pageInfo);
    }

    /**
     * 更新盘点状态
     * (下发，复盘，审核)
     * 返回更新失败的信息
     */
    public Result updateStatus(PandianStatusUpdateParam param) {

        List<StatusResponse> statusResponses = inventoriesStatusManager.updatePandianStatus(param);

        if (StringUtil.isEmpty(statusResponses)) {
            if (param.getTo_status() == 400) {//审核成功
                for (StatusParam statusParam : param.getStatusParams()) {
                    offlineCheckService.sendMq(statusParam.getSiteId(), statusParam.getPandian_num(),
                        storesMapper.getStore(statusParam.getStoreId(), statusParam.getSiteId()).getStores_number());
                }
            }
            return Result.success();
        } else {
            return Result.fail(statusResponses);
        }

    }


    /**
     * 盘点明细查询
     */
    public Result getPandianOrderDetail(PandianOrderDetailParam param) {

        return Result.success(inventoriesManager.getPandianOrderDetail(param));
    }

    /**
     * 盘点人确认
     */
    public Result storeAdminConfirm(StoreAdminConfirmParam param) {

        try {
            inventoriesManager.storeAdminConfirm(param);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }

        return Result.success();
    }

    /**
     * 查询盘点单是否允许门店上传
     */
    public Result isallowSiteUpload(PandianNum pandianNum) {

        AuthToken authToken = parseAccessToken(pandianNum.getAuthToken());

        BPandianOrder order = inventoriesManager.getPandianOrde(pandianNum.getPandian_num(),authToken.getSiteId());

        if (StringUtil.isEmpty(order)) {
            return Result.fail("根据盘点单[" + pandianNum.getPandian_num() + "]查询数据为空");
        } else {
            return Result.success(order.getIsUpSite());
        }
    }


    public Result waitInventoryNum(WaitInventoryNumParam param) {

        StatusParam  statusParam = getStatusParam(param.getPandian_num(),param.getSiteId(),param.getStoreId());
        if(isRepratInventory(statusParam)){

            return Result.success(countInventoryNumForRepeat(param));
        }else {

            try {
                return  Result.success(countInventoryNum(param));
            } catch (Exception e) {
                return  Result.fail(e.getMessage());
            }

        }

    }

    private WaitInventoryNum countInventoryNumForRepeat(WaitInventoryNumParam param){
        WaitInventoryNum result = new WaitInventoryNum();
        Long done =  inventoryRepository.doneInventoryNumforRepeat(param);
        Long wait =  inventoryRepository.waitInventoryNumforRepeat(param);

        result.setWait(wait);
        result.setDone(done);

        return result;
    }

    private WaitInventoryNum countInventoryNum(WaitInventoryNumParam param) throws Exception {

        WaitInventoryNum result = new WaitInventoryNum();
        Future<Long> done =  inventoryRepository.doneInventoryNum(param);
        Long wait =  inventoryRepository.waitInventoryNum(param);

        result.setWait(wait);
        result.setDone(done.get());

        return result;
    }

    private HasNotCheckInventories getHasNotCheckInventories(String pandianNum,Integer siteId,Integer storeId){
        HasNotCheckInventories result = new HasNotCheckInventories();
        result.setPandian_num(pandianNum);
        result.setSiteId(siteId);
        result.setStoreId(storeId);
        return result;
    }


    public ClerkCount clerkCount(WaitInventoryNumParam param) throws Exception {

        Future<Long> done = inventoryRepository.doneInventoryNum(param);
        Future<Long> more = inventoryRepository.moreInventoryNum(param);
        Future<Long> less = inventoryRepository.lessInventoryNum(param);
        Future<Long> same = inventoryRepository.sameInventoryNum(param);
        Future<Long> notConfirmNum = inventoryRepository.notClerkConfirmNum(param);

        ClerkCount clerkCount = new ClerkCount();
        clerkCount.setPandianNum(param.getPandian_num());
        clerkCount.setMore(more.get());
        clerkCount.setTotal(done.get());
        clerkCount.setSame(same.get());
        clerkCount.setLess(less.get());

        if(notConfirmNum.get() == 0 && done.get()!=0){
            clerkCount.setStatus(DONE);
        }else {
            clerkCount.setStatus(NOT_DONE);
        }

        return clerkCount;

    }

    public List<OrderClerkCount> orderClerkCount(OrderClerkCountParam param) {
        List<OrderClerkCount> result = new ArrayList<>();

        List<OrderClerkCount> list =  inventoryRepository.orderClerkCount(param);
        List<JoinInventoryUserResp> joinList = plandJoinInventoryUsers(param.getSiteId(),param.getStoreId(),param.getPlanId());

        for(JoinInventoryUserResp user:joinList){

            OrderClerkCount count = getOrderClerkCount(list,user);
            if(count == null){
                count = getEmptyCount(user);
            }
            result.add(count);
        }

        if(param.getIsConfirm().equals(IS_CONFIRMED)){
            result = filterIsConfire(result,true);
        }

        if(param.getIsConfirm().equals(NOT_CONFIRMED)){
            result = filterIsConfire(result,false);
        }

        if(!StringUtil.isEmpty(param.getStoreAdminId())){
            result = filterStoreaAdmin(result,param.getStoreAdminId());
        }

        return result;
    }

    private List<OrderClerkCount> filterIsConfire(List<OrderClerkCount> list,boolean confirm){

        List<OrderClerkCount> result = new ArrayList<>();

        for(OrderClerkCount count :list){
            if(count.getIsConfirm() == confirm){
                result.add(count);
            }
        }

        return result;
    }

    private List<OrderClerkCount> filterStoreaAdmin( List<OrderClerkCount> list,Integer storeAdminId){
        List<OrderClerkCount> result = new ArrayList<>();
        for(OrderClerkCount count :list){
            if(count.getStoreAdminId().equals(storeAdminId)){
                result.add(count);
            }
        }

        return result;
    }

    private OrderClerkCount getEmptyCount(JoinInventoryUserResp user){
        OrderClerkCount result = new OrderClerkCount();
        result.setName(user.getUserName());
        result.setStoreAdminId(user.getStoreAdminId());
        result.setLess(0L);
        result.setMore(0L);
        result.setSame(0L);
        result.setSumActual(0d);
        result.setCountInventoryed(0L);
        return result;
    }

    private OrderClerkCount getOrderClerkCount(List<OrderClerkCount> list,JoinInventoryUserResp user){
        OrderClerkCount result = null;
        for(OrderClerkCount count:list){
            if(user.getStoreAdminId().equals(count.getStoreAdminId())){
                count.setName(user.getUserName());
                return count;
            }
        }

        return result;
    }


    private List<JoinInventoryUserResp> getJoinInvenrotyParam(Integer siteId,Integer storeId, String pandianNum){

        List<JoinInventoryUserResp> result = new ArrayList<>();
        Set<Integer> storeAdminIds = inventoryRepository.findInventoryCheckers(siteId,storeId,pandianNum);
        if(StringUtil.isEmpty(storeAdminIds)){
            return result;
        }
        return storeAdminExtMapper.findUserNameByStoreAdminIds(siteId,storeId,storeAdminIds);
    }

    //查询参与盘点的店员
    public Result joinInventoryUsers(JoinInvenrotyParam param) {

        try{
            return Result.success(getJoinInvenrotyParam(param.getSiteId(),param.getStoreId(),param.getPandianNum())) ;
        }catch (Exception e){
            return Result.fail(ExceptionUtil.exceptionDetail(e));
        }

    }

    public List<JoinInventoryUserResp> plandJoinInventoryUsers(Integer siteId,Integer storeId,Integer planId){

        List<JoinInventoryUserResp> result = new ArrayList<>();
        BPandianPlanExecutor executor = bPandianPlanExecutorMapper.findBPandianPlanExecutor(siteId,storeId,planId);

        if(executor.getClerks().equals(ALL_CLERK)){
            result = findAll(siteId,storeId);

        }else{
            Set<Integer> storeAdminIds = getStoreAdminIds(executor.getClerks());
            result = storeAdminExtMapper.findUserNameByStoreAdminIds(siteId,storeId,storeAdminIds);
        }

        return result;
    }

    private List<JoinInventoryUserResp> findAll(Integer siteId,Integer storeId){
        List<JoinInventoryUserResp> result = new ArrayList<>();

        List<StoreAdminExt> list = storeAdminExtMapper.selectAllByUsed(siteId,storeId);

        for(StoreAdminExt s :list){

            JoinInventoryUserResp j = new JoinInventoryUserResp();
            j.setUserName(s.getName());
            j.setStoreAdminId(s.getStoreadmin_id());
            result.add(j);
        }
        return result;
    }

    private Set<Integer> getStoreAdminIds(String storeAdminStr){
        Set<Integer> result = new HashSet();

        try{

            storeAdminStr = storeAdminStr.substring(1,storeAdminStr.length()-1);
            String[] array = storeAdminStr.split(",");
            for(String str:array){
                result.add(Integer.parseInt(str));
            }

        }catch (Exception e){
            logger.error("解析clerks报错,storeAdminStr:{},报错信息：{}",storeAdminStr,ExceptionUtil.exceptionDetail(e));
        }

        return result;
    }
}
