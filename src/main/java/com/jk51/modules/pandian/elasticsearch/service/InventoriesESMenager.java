package com.jk51.modules.pandian.elasticsearch.service;

import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.communal.exceptionUtil.ExceptionUtil;
import com.jk51.model.*;
import com.jk51.modules.pandian.elasticsearch.dao.InventoriesDao;
import com.jk51.modules.pandian.elasticsearch.repositories.InventoryRepository;
import com.jk51.modules.pandian.elasticsearch.util.QueryUtil;
import com.jk51.modules.pandian.mapper.BInventoriesMapper;
import com.jk51.modules.pandian.mapper.InventoriesMapper;
import com.jk51.modules.pandian.param.*;
import com.jk51.modules.pandian.service.InventoriesManager;
import com.jk51.modules.pandian.service.PandianAsyncService;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Future;

import static com.jk51.modules.pandian.elasticsearch.util.Constant.INDEX_NAME;



/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/5/25
 * 修改记录:
 */
@Service
public class InventoriesESMenager {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InventoriesDao inventoriesDao;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private PandianAsyncService pandianAsyncService;

    /**
     * 库存查询
     */
    public List<Inventories> getInventories(InventoryParam param) {

        List<Inventories> result = new ArrayList<>();
        try{

            if(!StringUtil.isEmpty(param.getBar_code())){

               List<String> goodsCodes =  inventoriesDao.findGoodsCodeByBarCode(param.getBar_code(),param.getSiteId());
               if(!StringUtil.isEmpty(goodsCodes)){
                   param.setGoodsCodes(new HashSet(goodsCodes));
               }
            }

            if(!StringUtil.isEmpty(param.getGoods_code())){
                param.getGoodsCodes().add(param.getGoods_code());
            }


            List<Inventories> list = inventoryRepository.findInventories(param);
            for (Inventories i :list) {

                result.add(inventoriesDao.getInventories(i));
            }


        }catch (Exception e){

            logger.error("ES 盘点查询报错：param：{},报错信息：{}",param,ExceptionUtil.exceptionDetail(e));
        }

        return result;

    }

    /**
     * 库存查询
     */
    public List<Inventories> getInventoriesOr(InventoryParam param) {

        List<Inventories> result = new ArrayList<>();
        try{

            if(!StringUtil.isEmpty(param.getBar_code())){

                List<String> goodsCodes =  inventoriesDao.findGoodsCodeByBarCode(param.getBar_code(),param.getSiteId());
                if(!StringUtil.isEmpty(goodsCodes)){

                    param.setGoodsCodes(new HashSet<>(goodsCodes));
                }
            }

            if(!StringUtil.isEmpty(param.getGoods_code())){
                param.getGoodsCodes().add(param.getGoods_code());
            }

            if(StringUtil.isEmpty(param.getDrug_name())&&StringUtil.isEmpty(param.getGoodsCodes())){
                return result;
            }

            List<Inventories> list = inventoryRepository.findInventoriesOr(param);
            for (Inventories i :list) {

                result.add(inventoriesDao.getInventories(i));
            }


        }catch (Exception e){

            logger.error("ES 盘点查询报错：param：{},报错信息：{}",param,ExceptionUtil.exceptionDetail(e));
        }

        return result;

    }


    /**
     * 单条插入
     * */
    public boolean insert(Inventories inventories){

        boolean result = false;
        try{
          String id = inventoryRepository.index(inventories);
          if(!StringUtil.isEmpty(id)){
              result = true;
          }

            //异步插入数据库
            inventories.setEsId(Integer.parseInt(id));
            pandianAsyncService.insert2DB(inventories);

        }catch (Exception e){

            logger.error("盘点ES batchInsert报错：inventories:{},报错信息：{}",inventories,ExceptionUtil.exceptionDetail(e));
        }

        return result;
    }



    public Inventories findInventoryById(int inventoryId) {

       return inventoryRepository.findInventoryById(inventoryId);
    }

    public boolean updateInventory(Inventories inventories) {

       String result =  inventoryRepository.updateIndex(inventories);

       if(StringUtil.isEmpty(result)){
           return false;
       }

       inventories.setEsId(Integer.parseInt(result));
        pandianAsyncService.update2DB(inventories);

       return true;
    }





    @Async
    public Future<Page<Inventories>> hasNotCheckPageInfoforAsync(HasNotCheckInventories param){

        Page<Inventories> page = inventoryRepository.hasNotCheckPageInfo(param);

        return new AsyncResult(page);
    }

    @Async
    public Future<Page<Inventories>> hasNotCheckPageInfoforRepeat(HasNotCheckInventories param) {

        Page<Inventories> page = inventoryRepository.hasNotCheckPageInfoforRepeat(param);
        return new AsyncResult(page);
    }


    @Async
    public Future<Long> countHasNotCheckAsync(HasNotCheckInventories param){

        long total = inventoryRepository.getPandianDetailCount(param);
        return new AsyncResult(total);
    }

    @Async
    public Future<Long> countHasNotCheckforRepeat(HasNotCheckInventories param) {

        long total = inventoryRepository.getPandianDetailCountforRepeat(param);
        return new AsyncResult(total);
    }


    public List<Inventories> getHasDifferenceInventories(String pandian_num, int storeAdminId) {

        return inventoryRepository.getHasDifferenceInventories(pandian_num,storeAdminId);
    }

    public List<Inventories> repeatInventoryForCondition(RepeatInventoryForConditionParam param) {

        if(!StringUtil.isEmpty(param.getBar_code())){

            List<String> goodsCodes =  inventoriesDao.findGoodsCodeByBarCode(param.getBar_code(),param.getSiteId());
            if(!StringUtil.isEmpty(goodsCodes)){
                param.setGoodsCodes(new HashSet(goodsCodes));
            }

        }

        if(!StringUtil.isEmpty(param.getGoods_code())){
            param.getGoodsCodes().add(param.getGoods_code());
        }

        return inventoryRepository.repeatInventoryForCondition(param);
    }

    public Page<Inventories> getPandianDetail(PandianOrderDetailParam param) {

        return inventoryRepository.getPandianDetail(param);
    }


    public void storeAdminConfirm(StoreAdminConfirmParam param) {

         inventoryRepository.storeAdminConfirm(param);
        pandianAsyncService.storeAdminConfirm2DB(param);
    }



    public void insertInventory(List<Inventories> list){

        inventoryRepository.bulkIndex(list);
        pandianAsyncService.asyncInsert2DB(list);
    }



}
