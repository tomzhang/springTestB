package com.jk51.modules.pandian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.*;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.index.mapper.StoresMapper;
import com.jk51.modules.pandian.Response.*;
import com.jk51.modules.pandian.dto.PandianLinkedDto;
import com.jk51.modules.pandian.elasticsearch.mapper.InventoriesExtMapper;
import com.jk51.modules.pandian.elasticsearch.service.InventoriesESMenager;
import com.jk51.modules.pandian.error.*;
import com.jk51.modules.pandian.mapper.*;
import com.jk51.modules.pandian.param.*;
import org.apache.tiles.servlet.context.ServletTilesRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Future;

import static com.jk51.modules.pandian.elasticsearch.util.DoubleUtil.round;
import static com.jk51.modules.pandian.util.Constant.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-10-18
 * 修改记录:
 */
@Component
public class InventoriesManager {

    @Autowired
    private InventoriesMapper inventoriesMapper;
    @Autowired
    private BPandianOrderMapper bPandianOrderMapper;
    @Autowired
    private BPandianPlanExecutorMapper bPandianPlanExecutorMapper;
    @Autowired
    private StoreAdminExtMapper storeAdminExtMapper;
    @Autowired
    private StoresMapper storesMapper;
    @Autowired
    private BPandianLinkedMapper bPandianLinkedMapper;
    @Autowired
    private InventoriesESMenager inventoriesESMenager;
    @Autowired
    private InventoriesExtMapper inventoriesExtMapper;
    @Autowired
    private BPandianPlanMapper bPandianPlanMapper;

    /**
     *盘点状况
     * */
    public PageInfo<BPandianOrderList> getPandianOrderExtList(PandianOrderStatusParam param){

        PageHelper.startPage(param.getPageNum(),param.getPageSize());
        List<BPandianOrderList> bPandianOrderExts =  bPandianOrderMapper.getBPandianOrderExtList(param);
        PageInfo<BPandianOrderList> pageInfo =  new PageInfo(bPandianOrderExts);
        return pageInfo;

    }



    /**
     * 盘点明细状态
     * */
    public PageInfo getPandianOrderDetail(PandianOrderDetailParam param){

        List<PandianOrderDetailResponse> result = new ArrayList();

        if(!StringUtil.isEmpty(param.getCheckerName())){
            Set<Integer> storeAdminIds = storeAdminExtMapper.findStoreAdminIdsByUserName(param.getSiteId(),param.getStoreId(),param.getCheckerName());

            if(StringUtil.isEmpty(storeAdminIds)){
                return new PageInfo();
            }
            param.setCheckerStoreAdminId(storeAdminIds);
        }


        Page<Inventories> page = inventoriesESMenager.getPandianDetail(param);

        Map<String,String> orderStatusMap = inventoriesExtMapper.findPandianDetailExt(param.getStoreId(),param.getPandian_num());

        Set<Integer> storeAdminIds = new HashSet();
        for(Inventories i:page.getContent()){
            if(StringUtil.isEmpty(i.getInventory_checker())){
                continue;
            }
            storeAdminIds.add(i.getInventory_checker());
        }

        List<Map<String,Object>> checkerNameMap = new ArrayList<>();
        if(!StringUtil.isEmpty(storeAdminIds)){
            checkerNameMap = inventoriesExtMapper.findCheckerName(param.getSiteId(),storeAdminIds);
        }

        for(Inventories i:page.getContent()){

            PandianOrderDetailResponse response = new PandianOrderDetailResponse();
            response.setPandian_num(i.getPandian_num());
            response.setPlan_id(i.getPlan_id());
            response.setActual_store(StringUtil.isEmpty(i.getActual_store())?null:round(i.getActual_store(),4));
            response.setBatch_number(i.getBatch_number());
            response.setCreate_time(i.getCreate_time());
            response.setDrug_name(i.getDrug_name());
            response.setGoods_code(i.getGoods_code());
            response.setGoods_company(i.getGoods_company());
            response.setInventory_accounting(round(i.getInventory_accounting(),4));
            response.setUpdate_time(i.getUpdate_time());
            response.setProfitAndLossNum(getProfitAndLossNum(i));
            response.setProfitAndLossStatus(getProfitAndLossStatus(i));
            response.setInventory_confirm(i.getInventory_confirm());
            response.setSpecif_cation(i.getSpecif_cation());
            response.setQuality(i.getQuality());
            response.setConfirm_checker_name(orderStatusMap.get("confirm_checker_name"));
            response.setAudit_checker_name(orderStatusMap.get("audit_checker_name"));
            response.setStoreName(orderStatusMap.get("storeName"));
            response.setStores_number(orderStatusMap.get("stores_number"));
            response.setCheckerName(getChecherName(checkerNameMap,i.getInventory_checker()));

            result.add(response);
        }

        return getPandianDetailPagaInfo(result,page);

    }

    private PageInfo<PandianOrderDetailResponse> getPandianDetailPagaInfo(List<PandianOrderDetailResponse> list,Page<Inventories> page){

        PageInfo<PandianOrderDetailResponse> result = new PageInfo(list);

        BigDecimal total = new BigDecimal(page.getTotalElements());
        BigDecimal size = new BigDecimal(page.getSize());
        result.setPages((int) Math.ceil(total.divide(size,2, BigDecimal.ROUND_HALF_DOWN).doubleValue()));
        result.setTotal(page.getTotalElements());
        result.setPageNum(page.getNumber()+1);
        return result;
    }

    private String getChecherName(List<Map<String,Object>> checkerNameMap,Integer storeAdminId){

        String result = "";

        if(StringUtil.isEmpty(storeAdminId)){
            return result;
        }

        for(Map<String,Object> m:checkerNameMap){

            if(m.get("storeadmin_id").equals(storeAdminId)){
                result = (String) m.get("name");
                break;
            }
        }

        return result;
    }

    private String getProfitAndLossStatus(Inventories i){
        Float num =  getProfitAndLossNum(i)==null?null:getProfitAndLossNum(i).floatValue();

        if(StringUtil.isEmpty(num)){
            return "未开始盘点";
        }

        if(num>0){
            return "盘盈";
        }

        if(num<0){
            return "盘亏";
        }


        return "盘平";

    }

    private Double getProfitAndLossNum(Inventories i){

        if(StringUtil.isEmpty(i)){
            return null;
        }

        if(StringUtil.isEmpty(i.getInventory_accounting())&&StringUtil.isEmpty(i.getActual_store())){
            return null;
        }

        if(StringUtil.isEmpty(i.getInventory_accounting())){

            return i.getActual_store()-0;
        }

        if(StringUtil.isEmpty(i.getActual_store())){
            return  null;
        }


        return round(i.getActual_store() - i.getInventory_accounting(),4) ;
    }


    /**
     * 参与盘点的门店查询
     *
     * */
    public List<StoresResponse> getStores(StoresParam param){
       return bPandianPlanExecutorMapper.getStores(param);
    }

    /**
     * 合并查询
     *  全盘、无批次、随机、动态
     *
     * 根据盘点单号和(商品名称或商品编号或商品二维码或商品批号)查询库存
     * */
    public List<Inventories> getInventories(InventoryParam param){

        return inventoriesESMenager.getInventories(param);
    }

    public List<Inventories> getNextNotCheckerInventories(NextNotCheckerParam param){

        return inventoriesMapper.getNextNotCheckerInventories(param);
    }

    /**
     * 普通查询
     * */
    public List<Inventories> getInventories2(InventoryParam param) {

        return inventoriesMapper.getInventories2(param);
    }


    /**
     *复盘
     * 根据复盘的盘点号，查询所有账目库存和实际库存又差异的数据
     *
     * */
    public List<Inventories> getHasDifferenceInventories(String pandian_num,int storeAdminId){

        return inventoriesESMenager.getHasDifferenceInventories(pandian_num,storeAdminId);
    }

    /**
     * 复盘条件查询接口
     * */
    public List<Inventories> repeatInventoryForCondition(RepeatInventoryForConditionParam param) {

        return inventoriesESMenager.repeatInventoryForCondition(param);
    }



    /**
     * 待盘点
     * 根据盘点单，查询未盘点的明细
     * */
    public HasNotCheckInventoriesRes getHasNotCheckInventories(HasNotCheckInventories param) throws Exception {

        Future<Page<Inventories>> pageInfo =  inventoriesESMenager.hasNotCheckPageInfoforAsync(param);
        Future<Long> total = inventoriesESMenager.countHasNotCheckAsync(param);

        HasNotCheckInventoriesRes result = new HasNotCheckInventoriesRes();
        result.setPageInfo(getPageInfo(pageInfo.get(),param.getPandian_num(),param.getSiteId()));
        result.setWaitInventoryNum(pageInfo.get().getTotalElements());
        result.setTotal(total.get());

        return result;
    }


    public HasNotCheckInventoriesRes getHasNotCheckInventoriesForRepeat(HasNotCheckInventories param)throws Exception {

        Future<Page<Inventories>> pageInfo =  inventoriesESMenager.hasNotCheckPageInfoforRepeat(param);
        Future<Long> total = inventoriesESMenager.countHasNotCheckforRepeat(param);

        HasNotCheckInventoriesRes result = new HasNotCheckInventoriesRes();
        result.setPageInfo(getPageInfo(pageInfo.get(),param.getPandian_num(),param.getSiteId()));
        result.setWaitInventoryNum(pageInfo.get().getTotalElements());
        result.setTotal(total.get());

        return result;
    }


    private PageInfo<Inventories> getPageInfo(Page<Inventories> page,String pandianNum,Integer siteId){
        PageInfo<Inventories> result = new PageInfo();
        result.setList(setPlanStockShow(pandianNum,siteId,page.getContent()));
        result.setPages(page.getNumber());
        result.setTotal(page.getTotalElements());
      /*  result.setPageSize(page.getSize());
        result.setPageNum(page.getNumber()+1);
        result.setSize(page.getContent().size());*/
        result.setHasNextPage(hashNextPage(page));
        return result;
    }

    private List<Inventories> setPlanStockShow(String pandianNum,Integer siteId,List<Inventories> list){

        BPandianPlan bPandianPlan =  bPandianPlanMapper.findByPandianNum(pandianNum,siteId);

        for(Inventories i:list){
            i.setPlan_stock_show(bPandianPlan.getPlanStockShow());
        }

        return list;
    }

    private boolean hashNextPage(Page<Inventories> page){

        boolean result = false;
        int totalPage = page.getTotalPages();
        if(totalPage>page.getNumber()){
            result = true;
        }

        return result;
    }




    /**
     * 盘点确定
     * （全盘、无批次、随机、动态）
     *  inventoriesId、
     * */
    @Async
    public void confirmInventories(InventoryConfirmParam param) throws Exception{


        for(InventoryBatchNum batchNum:param.getBatchNums()){

            if(StringUtil.isEmpty(batchNum.getInventoryId())){

                addNotBatchNumInventory(param.getStoreAdminId(),param.getStoreId(),param.getGoods_code(),batchNum,param.getPandian_num(),param.getSiteId());
            }else{

                updateInventories(batchNum,param.getStoreAdminId());

            }
        }

    }



    public void updateInventories(InventoryBatchNum batchNum,Integer storeAdminId){

        Inventories inventories = getInventoryByPrimaryKey(batchNum.getInventoryId());
        if(inventories==null){
            throw new NotFoundInventoryException("根据盘点库存ID查询商品失败");
        }

        inventories.setActual_store(round(batchNum.getNum(),4));
        //inventories.setQuality(batchNum.getQuality());
        inventories.setInventory_checker(storeAdminId);
        if(!StringUtil.isEmpty(batchNum.getBatchNum())){
            inventories.setBatch_number(batchNum.getBatchNum());
        }
        inventories.setSpecif_cation(batchNum.getSpecif_cation());
        inventories.setModify(1);
        inventories.setUpdate_time(new Date());

        boolean result = updateInventory(inventories);

        if(!result){
            throw new InventoryUpdateException("更新库存盘点数据失败");
        }
    }


    /**
     * 添加没有批号的商品
     * */
    public void addNotBatchNumInventory(Integer storeAdminId, int storeId, String goodsCode, InventoryBatchNum batchNum, String pandian_num, Integer siteId) {

        Inventories inventories =  getInventoryByGoodsCode(goodsCode,pandian_num,storeId,siteId);

        if(inventories==null){
            throw new NotFoundInventoryException("根据商品编码查询商品失败");
        }

        inventories.setPandian_num(pandian_num);
        inventories.setBatch_number(batchNum.getBatchNum());
        inventories.setSpecif_cation(batchNum.getSpecif_cation());
        inventories.setActual_store(round(batchNum.getNum(),4));
        inventories.setInventory_accounting(NOT_INVENTORY_ACCOUNTING);
        //inventories.setQuality(batchNum.getQuality());
        inventories.setInventory_checker(storeAdminId);
        boolean result = insertInventory(inventories);

        if(!result){
            throw new InventoryInsertException("添加没有批号的商品失败");
        }
    }


    /**
     *根据商品编码查询商品
     *
     * */
    public Inventories getInventoryByGoodsCode(String goodsCode, String pandian_num,Integer storeId,Integer siteId){

        InventoryParam param = new InventoryParam();
        param.setPandian_num(pandian_num);
        param.setGoods_code(goodsCode);
        param.setStoreId(storeId);
        param.setSiteId(siteId);

        List<Inventories> list  =  inventoriesESMenager.getInventories(param);

        if(StringUtil.isEmpty(list)){
            return null;
        }else {
            return list.get(0);
        }
    }

    /**
     * 新增库存
     *
     * */
    public boolean insertInventory(Inventories inventories){

       return inventoriesESMenager.insert(inventories);
    }

    /**
     * 新增商品
     * */
    @Transactional
    public void addNewGoods(InventoryConfirmParam param) throws Exception{

        OrderInfo order =  bPandianOrderMapper.getBPandianOrderByPandianNumAndStoreId(param.getPandian_num(),param.getStoreId());

        Stores stores = storesMapper.getStore(param.getStoreId(),param.getSiteId());

        if(StringUtil.isEmpty(order)){
            throw new NotFoundPandianOrderException("没有查询到对应的盘点计划");
        }

        if(StringUtil.isEmpty(stores)||StringUtil.isEmpty(stores.getStores_number())){
            throw new NotFoundStoresException("店员信息为空");
        }

        if(StringUtil.isEmpty(param.getBatchNums())){
            throw new InventoryBatchNumisEmptyException("BatchNums不能为空");
        }

        for(InventoryBatchNum batchNum :param.getBatchNums()){

            Inventories inventories = new Inventories();
            inventories.setPlan_id(order.getPlanId());
            inventories.setPandian_num(param.getPandian_num());
            inventories.setStore_id(order.getStoreId());
            inventories.setGoods_code(param.getGoods_code());
            inventories.setDrug_name(param.getDrug_name());
            inventories.setSpecif_cation(batchNum.getSpecif_cation());
            inventories.setGoods_company(param.getGoods_company());
            inventories.setInventory_accounting(NOT_INVENTORY_ACCOUNTING);
            inventories.setActual_store(batchNum.getNum());
            inventories.setInventory_checker(param.getStoreAdminId());
            inventories.setBatch_number(batchNum.getBatchNum());
            inventories.setQuality(batchNum.getQuality());
            inventories.setSite_id(order.getSiteId());
            inventories.setOrder_id(order.getOrderId());
            inventories.setStore_num(stores.getStores_number());
            inventories.setModify(HAS_MODIFY);
            inventories.setIsDel(0);
            inventories.setCreate_time(new Date());
            inventories.setUpdate_time(new Date());
            inventories.setInventory_confirm(0);
            inventories.setPlan_stock_show(0);
            boolean result = insertInventory(inventories);

            if(!result){
                throw new InventoryInsertException("库存插入失败!");
            }
        }

    }

    /**
     * 修改库存
     * */
    public boolean updateInventory(Inventories inventories){
        return inventoriesESMenager.updateInventory(inventories);
    }

    /**
     *查询盘点单是否为连续盘点
     */
    public boolean checkIsdoubleCheck(String pandian_num,Integer siteId){


        BPandianOrder order =  bPandianOrderMapper.getBPandianOrder(pandian_num,siteId);

        if(StringUtil.isEmpty(order)||StringUtil.isEmpty(order.getPandianPlan())){
            throw new NotFoundPandianOrderException("根据pandian_num查询BPandianOrder失败,pandian_num="+pandian_num);
        }

        return order.getPandianPlan().getPlanCheck()==0?true:false;
    }

    /**
     *
     * 判断盘点确认数量是否与库存数量一致
     * */
    public boolean checkNum(int inventoryId,Double actualNum){

        Inventories inventories = getInventoryByPrimaryKey(inventoryId);
        if(inventories==null){
            throw new NotFoundInventoryException("根据盘点库存ID查询商品失败");
        }

        if(inventories.getInventory_accounting()==null){
            return false;
        }

        return inventories.getInventory_accounting().equals(actualNum) ?true:false;

    }


    /**
     * 该商品编码的商品是否有其他店员盘点过
     * */
    public String haveInventory(InventoryConfirmParam param){

        String result = "";
        Inventories inventories = getInventories(param).get(0);

        boolean checkerIsNotEmpty = !(StringUtil.isEmpty(inventories.getInventory_checker()));
        if(!checkerIsNotEmpty)
            return result;
        boolean checkerIsNotSame = !(inventories.getInventory_checker().equals(param.getStoreAdminId()));

        if(checkerIsNotEmpty&&checkerIsNotSame){
            result = storeAdminExtMapper.getNameById2(inventories.getSite_id(),inventories.getInventory_checker());
        }

        return result;
    }


    /**
     * 该商品编码的商品是否已经有店员盘点确认过
     * */
    public String haveStoreAdminConfirm(InventoryConfirmParam param){

        String result = "";
        List<Inventories> inventories = getInventories(param);

        //查询结果中是否有没有盘点确认过的商品
        boolean hasStoreAdminConfirm = true;
        for(Inventories i: inventories){
            if( i.getInventory_confirm()==0){
                hasStoreAdminConfirm = false;
            }

        }


        if(hasStoreAdminConfirm){
            result = storeAdminExtMapper.getNameById2(inventories.get(0).getSite_id(),inventories.get(0).getInventory_checker());
        }

        return result;

    }

    /**
     * 店员查询盘点计划
     * */
    public List<PandainPlanMap> getPandianPlan(ClerkParam param){

        return bPandianOrderMapper.getBPandianOrders(param);
    }

    /**
     * 店员查询盘点数量
     * */
    public int getPandianPlanNum(String siteId,String storeId,String storeAdminId){

        ClerkParam param = new ClerkParam(Integer.parseInt(siteId),Integer.parseInt(storeId),Integer.parseInt(storeAdminId));
        List<PandainPlanMap> pandainPlanMaps = bPandianOrderMapper.getBPandianOrders(param);

        if(StringUtil.isEmpty(pandainPlanMaps)){
            return 0;
        }

        return pandainPlanMaps.size();
    }


    /**
     * 盘点人确认
     * */
    public void storeAdminConfirm(StoreAdminConfirmParam param){
        inventoriesESMenager.storeAdminConfirm(param);
    }




    private Inventories getInventoryByPrimaryKey(int inventoryId) {
        return inventoriesESMenager.findInventoryById(inventoryId);
    }


    private List<Inventories> getInventories(InventoryConfirmParam param){

        List<Inventories> inventories =  getInventories(getInventoryParam(param));

        if(StringUtil.isEmpty(inventories)){

            throw new NotFoundInventoryException("根据盘点编号和商品编码查询商品失败");
        }

        return inventories;
    }


    private InventoryParam getInventoryParam(InventoryConfirmParam param){

        InventoryParam result = new InventoryParam();
        result.setPandian_num(param.getPandian_num());
        result.setGoods_code(param.getGoods_code());
        result.setStoreId(param.getStoreId());
        result.setSiteId(param.getSiteId());

        return result;
    }


    public BPandianOrder getPandianOrde(String pandian_num,Integer siteId) {

        return bPandianOrderMapper.getBPandianOrder(pandian_num,siteId);
    }

    /**
     *
     * 使用链表的数据结构，维护盘点商品的盘点顺序
     *   1. 判断currentNode是否为空，如果为空执行2，不为空执行3
     *   2. 创建currentNode，currentNode.pre = nextIsNullNode.id
     *      插入currentNode
     *      更新nextIsNullNode.next = currentNode.id
     *      返回空的集合
     *  3. 判断currentNode.next 是否为空，为空执行4，不为空执行5
     *  4. 不做处理，返回空的集合
     *  5.
     *
     *    nextNode为null（数据库删除某系数据时会出现），删除currentNode，返回空集合
     *    preNode为null（第一个数据，或者数据库根据Id查询不到数据），nextNode.pre = null
     *    更新 preNode.next = nextNode.id;
     *    更新 nextNode.pre = preNode.id;
     *    更新 nextIsNullNode.next = currentNode.id
     *    更新 currentNode.pre = nextIsNullNode.id, currentNode.next = null
     *    返回下一个商品在盘点表中的信息
    * */
    @Async
    @Transactional
    public Future<List<Inventories>> pandianLinked(PandianLinkedDto linkedDto) {

        PandianLinkedNode currentNode =  bPandianLinkedMapper.findNodeByUniqueIndex(linkedDto);
        PandianLinkedNode nextIsNullNode = bPandianLinkedMapper.findnextIsNullNode(linkedDto);

        if(currentNode==null){

            currentNode = new PandianLinkedNode();
            currentNode.setSiteId(linkedDto.getSiteId());
            currentNode.setStoreId(linkedDto.getStoreId());
            currentNode.setGoodsCode(linkedDto.getGoodsCode());
            currentNode.setCreateTime(new Date());
            currentNode.setPre(StringUtil.isEmpty(nextIsNullNode)?null:nextIsNullNode.getId());
            currentNode.setStoreAdminId(linkedDto.getStoreAdminId());
            bPandianLinkedMapper.insert(currentNode);

            if(nextIsNullNode!=null){
                nextIsNullNode.setNext(currentNode.getId());
                bPandianLinkedMapper.update(nextIsNullNode);
            }

            return new AsyncResult(new ArrayList<>());
        }else if (currentNode.getNext()==null){

            return new AsyncResult(new ArrayList<>());
        }else{


            PandianLinkedNode preNode = bPandianLinkedMapper.selectByPrimaryKey(currentNode.getPre());
            PandianLinkedNode nextNode = bPandianLinkedMapper.selectByPrimaryKey(currentNode.getNext());
            boolean nextSameLast = nextNode.getId().equals(nextIsNullNode.getId());


            if(nextNode==null){

                bPandianLinkedMapper.deleteByPrimaryKey(currentNode.getId());
                return new AsyncResult(new ArrayList<>());
            }

            //如果最后一个和下一个是同一个商品时,将两个应用指向一个对象
            if(nextSameLast){
                nextNode = nextIsNullNode;
            }

            if(preNode==null){

                nextNode.setPre(null);
            }else{
                nextNode.setPre(preNode.getId());
                preNode.setNext(nextNode.getId());
            }


            nextIsNullNode.setNext(currentNode.getId());

            currentNode.setPre(nextIsNullNode.getId());
            currentNode.setNext(null);


            bPandianLinkedMapper.update(preNode);
            bPandianLinkedMapper.update(nextNode);
            bPandianLinkedMapper.update(nextIsNullNode);
            bPandianLinkedMapper.update(currentNode);


            return new AsyncResult(getInventories(getInventoryParam(linkedDto.getPandianNum(),nextNode)));
        }

    }


    private InventoryParam getInventoryParam(String pandianNum,PandianLinkedNode node){

        if(pandianNum==null||node.getSiteId()==null||node.getSiteId()==null||node.getGoodsCode()==null){
            throw new IllegalArgumentException(String.format("pandianNum:[%s],node:[%s] 为空",pandianNum,node));
        }
        InventoryParam result = new InventoryParam();
        result.setPandian_num(pandianNum);
        result.setSiteId(node.getSiteId());
        result.setStoreId(node.getStoreId());
        result.setGoods_code(node.getGoodsCode());
        return result;
    }

    public List<String> findStoreAdminByPandianNum(StatusParam param) {

        return inventoriesMapper.findStoreAdminByPandianNum(param);
    }


}
