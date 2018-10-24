package com.jk51.modules.pandian.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.Inventories;
import com.jk51.modules.pandian.dto.PandianSortRedisDto;
import com.jk51.modules.pandian.elasticsearch.repositories.InventoryRepository;
import com.jk51.modules.pandian.job.InventorySortRemindHitJob;
import com.jk51.modules.pandian.param.InventoryParam;
import com.jk51.modules.pandian.param.InventorySortRemindHitMassege;
import com.jk51.modules.pandian.param.StatusParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/5/9
 * 修改记录:
 */

/**
 * 盘点的顺序用分两步实现
 *
 * 1.在盘点确认时，查询当前商品的下一个商品的信息，添加当前商品信息到当前店员的有序集合
 * 2.盘点门店审核时，合并该盘点单当前门店所有店员参加记录的盘点迅速，生成门店的盘点顺序集合，删除店员的集合
 *
 * */
@Component
public class PandianOrderRedisManager {

    private Logger logger = LoggerFactory.getLogger(PandianOrderRedisManager.class);
    private static final String STORE_ZSET_PREFIX = "pd_store_zset_";
    private static final String STORE_ZSET_COPY_PREFIX = "pd_store_zset_copy";
    private static final String STORE_ADMIN_ZSET_PREFIX = "pd_store_admin_zset_";

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private InventoriesManager inventoriesManager;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private PandianAsyncService pandianAsyncService;



    /**
     *查询当前盘点确认的下一个goods_code
     * 1.在门店zset中查询 currentGoodsCode的下一个nextgoodsCode
     * 2.添加currentGoodsCode 到当前店员的zset
     * 3.根据nextgoodsCode查询库存表，nextgoodsCode为空时返回空
     *
     */
    public List<Inventories> pandianSort(PandianSortRedisDto dto) throws ExecutionException, InterruptedException {

        checkParame(dto);

        String nextGoodsCode = getNextGoodsCode(dto);

        //添加currentGoodsCode到当前店员的zset最后
        String storeAdminZsetKey= getStoreAdminZsetKey(dto.getSiteId(),dto.getStoreId(),dto.getCurrentStoreAdminId());
        zsetAddMember2Last(dto,storeAdminZsetKey);


        List<Inventories> result = new ArrayList<>();
        if(!StringUtil.isEmpty(nextGoodsCode)){
            result =  getResult(dto,nextGoodsCode).get();
        }

        InventorySortRemindHitJob.sendMessage(getMessage(dto,nextGoodsCode,result));

        return result;
    }




    /**
     * 合并盘点时创建的店员zset，生成一个门店的zset
     *
     * 每次审核都会将该次盘点的顺序添加到门店的zse中（zset会覆盖重复的顺序）
     * */
    public void mergeZset(StatusParam param) {

        //获取参与了该盘点单的店员zsetKey 集合
        List<String> storeAdminKeys = getStoreAdminKeys(param);

        //合并店员zset到门店zset
        unionStoreZset(param,storeAdminKeys);


        //删除店员zset
        deleteZset(storeAdminKeys);
    }

    public void mergeZsetForAsync(){
        List<String> storeAdminKeys = new ArrayList<>();

        storeAdminKeys.add(getStoreAdminZsetKey(100166,1228,6607));
        storeAdminKeys.add(getStoreAdminZsetKey(100166,1228,7120));
        storeAdminKeys.add(getStoreAdminZsetKey(100166,1228,12804));



        StatusParam param = new StatusParam();
        param.setSiteId(100166);
        param.setStoreId(1228);

        //合并店员zset到门店zset
        unionStoreZset(param,storeAdminKeys);

    }


    @Async
    public Future<List<Inventories>> getResult(PandianSortRedisDto dto,String goodsCode){
       return new AsyncResult(inventoryRepository.getNextNotCheckerInventories(getNextNotCheckerParam(dto,goodsCode))) ;
    }



    private InventoryParam getNextNotCheckerParam(PandianSortRedisDto dto, String goodsCode){


        InventoryParam result = new InventoryParam();
        result.setPandian_num(dto.getPandianNum());
        result.setSiteId(dto.getSiteId());
        result.setStoreId(dto.getStoreId());
        result.setGoods_code(goodsCode);
        return result;
    }



    private String getStoreAdminZsetKey(Integer siteId,Integer soteId,Integer storeAdminId){

        StringBuilder sb = new StringBuilder();
        sb.append(STORE_ADMIN_ZSET_PREFIX);
        sb.append(siteId);
        sb.append("_");
        sb.append(soteId);
        sb.append("_");
        sb.append(storeAdminId);

        return sb.toString();
    }



    /**
     * 查询zset中currentGoodsCode的下一个goodsCode
     * */
    private String getNextGoodsCode(PandianSortRedisDto dto){


        String result = "";

        String storeZsetKey = getStoreZsetKey(dto.getSiteId(),dto.getStoreId());
        Long index = redisTemplate.opsForZSet().rank(storeZsetKey,dto.getCurrentGoodsCode());

        if(index!=null){

            Long nextIndex  = index+1;
            Iterator iterator = redisTemplate.opsForZSet().range(storeZsetKey,nextIndex,nextIndex).iterator();
            if(iterator.hasNext()){
                result = (String) iterator.next();
            }
        }

        return result;
    }

    public boolean currentGoodsCodeHasInRedis(InventorySortRemindHitMassege massege){

        boolean result = false;

        String storeZsetKey = getStoreZsetKey(massege.getDto().getSiteId(),massege.getDto().getStoreId());
        Long index = redisTemplate.opsForZSet().rank(storeZsetKey,massege.getDto().getCurrentGoodsCode());

        if(!StringUtil.isEmpty(index)){
            result = true;
        }

        return result;
    }



    /**
     * 参数验证
     * */
    private void checkParame(PandianSortRedisDto dto){

        if(dto.getSiteId()==null || dto.getStoreId()==null || dto.getCurrentStoreAdminId()==null || dto.getCurrentGoodsCode()==null || dto.getPandianNum()==null){


            throw new IllegalArgumentException("参数不能为空："+dto);
        }
    }

    private static final int DEF_DIV_SCALE = 11;
    /**
     * 商品添加到对应有序集合中
     * 添加的scope为了实现每个店员对应的zset不重复，由storeAdminId+(mem_count/10000000)
     * */
    private void zsetAddMember2Last(PandianSortRedisDto dto, String key) {

        //商户盘点计数器值
        Double  counterValue = redisTemplate.opsForValue().increment(getSiteCounterKey(dto.getSiteId()),0.0000000001D);

        Double score = new BigDecimal(dto.getCurrentStoreAdminId()).add(new BigDecimal(counterValue)).setScale(10,RoundingMode.HALF_UP).doubleValue();
        redisTemplate.opsForZSet().add(key,dto.getCurrentGoodsCode(),score);

        //异步记录日志
        pandianAsyncService.asyncUpdateInventoryLog(dto,score);

        pandianAsyncService.asyncUpdateInventory(dto,score);
    }



    //商户盘点计数器
    private String getSiteCounterKey(Integer siteId){

        return "site_counter_"+siteId;
    }

    /**
     * 删除店员的zset
     *
     * */
    private void deleteZset(List<String> storeAdminKeys){

        redisTemplate.delete(storeAdminKeys);
    }

    /**
     *  合并有序集合storeZsetKey，storeAdminKeys 到 storeZsetKey
     * */
    private void unionStoreZset(StatusParam param,List<String> storeAdminKeys){

        String storeZsetKey = getStoreZsetKey(param.getSiteId(),param.getStoreId());

      /*  redisTemplate.rename(storeZsetKey,storeZsetCopyKey);
        redisTemplate.opsForZSet().unionAndStore(storeZsetCopyKey,storeAdminKeys,storeZsetKey);*/

        for(String key:storeAdminKeys){
            redisTemplate.opsForZSet().add(storeZsetKey,redisTemplate.opsForZSet().rangeWithScores(key,0,-1));
        }
    }

    /**
     * 获取该盘点单，在该门店中参与的店员，并将其转化为店员的zsetKey 集合
     * */
    private List<String> getStoreAdminKeys(StatusParam param){

        List<String> result = new ArrayList<>();

        List<String> storeAdminIds = inventoriesManager.findStoreAdminByPandianNum(param);

        for(String id :storeAdminIds){

            String key = getStoreAdminZsetKey(param.getSiteId(),param.getStoreId(),Integer.parseInt(id));
            result.add(key);
        }

        return result;

    }


    private String getStoreZsetKey(Integer siteId,Integer storeId){
        StringBuilder sb = new StringBuilder();
        sb.append(STORE_ZSET_PREFIX);
        sb.append(siteId);
        sb.append("_");
        sb.append(storeId);

        return sb.toString();
    }

    private String getStoreZsetCopyKey(Integer siteId,Integer storeId){
        StringBuilder sb = new StringBuilder();
        sb.append(STORE_ZSET_COPY_PREFIX);
        sb.append(siteId);
        sb.append("_");
        sb.append(storeId);

        return sb.toString();
    }

    private InventorySortRemindHitMassege getMessage(PandianSortRedisDto dto,String nextGoodsCode,List<Inventories> resultList){
        InventorySortRemindHitMassege result = new InventorySortRemindHitMassege();
        result.setDto(dto);
        result.setNextGoodsCode(nextGoodsCode);

        if(StringUtil.isEmpty(resultList)){
            result.setHasRemind(false);
        }else{
            result.setHasRemind(true);
        }

        return result;
    }
}
