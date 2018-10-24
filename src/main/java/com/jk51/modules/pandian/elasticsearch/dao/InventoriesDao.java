package com.jk51.modules.pandian.elasticsearch.dao;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.BInventories;
import com.jk51.model.BPandianPlan;
import com.jk51.model.Inventories;
import com.jk51.model.StoreAdminExt;
import com.jk51.modules.index.mapper.StoreAdminExtMapper;
import com.jk51.modules.pandian.elasticsearch.mapper.InventoriesExtMapper;
import com.jk51.modules.pandian.mapper.BInventoriesMapper;
import com.jk51.modules.pandian.mapper.BPandianPlanMapper;
import com.jk51.modules.pandian.param.InventoryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/5/28
 * 修改记录:
 */
@Service
public class InventoriesDao {

    @Autowired
    private BInventoriesMapper bInventoriesMapper;
    @Autowired
    private InventoriesExtMapper inventoriesExtMapper;
    @Autowired
    private StoreAdminExtMapper storeAdminExtMapper;
    @Autowired
    private BPandianPlanMapper pandianPlanMapper;


    /*@Async
    public void insertByList(List<BInventories> list){
        bInventoriesMapper.insertByList(list);
    }*/

    public List<String> findGoodsCodeByBarCode(String barCode,Integer siteId){

        return inventoriesExtMapper.findGoodsCodeByBarCode(siteId,barCode);
    }

    public Inventories getInventories(Inventories i){

        Map<String,String> goodsMap = inventoriesExtMapper.queryByGoodsCode(i.getGoods_code(),i.getSite_id().toString());
        BPandianPlan pandianPlan = pandianPlanMapper.selectByPrimaryKey(i.getSite_id(),i.getPlan_id());

        if(!StringUtil.isEmpty(i.getInventory_checker())){
            StoreAdminExt storeAdminExt = storeAdminExtMapper.getStoreAdminExtBySiteIdAndStoreAdminId(i.getSite_id(),i.getInventory_checker()).get(0);
            i.setCheckerName(storeAdminExt.getName());
        }


        i.setPlan_stock_show(pandianPlan.getPlanStockShow());
        i.setHash(StringUtil.isEmpty(goodsMap)?null:goodsMap.get("hash"));

        return i;
    }
}
