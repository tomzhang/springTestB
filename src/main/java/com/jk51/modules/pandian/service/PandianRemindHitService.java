package com.jk51.modules.pandian.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.Inventories;
import com.jk51.model.InventorySortNotHasRemind;
import com.jk51.model.InventorySortRemindHit;
import com.jk51.modules.pandian.elasticsearch.repositories.InventoryRepository;
import com.jk51.modules.pandian.mapper.InventorySortNotHasRemindMapper;
import com.jk51.modules.pandian.mapper.InventorySortRemindHitMapper;
import com.jk51.modules.pandian.param.InventoryParam;
import com.jk51.modules.pandian.param.InventorySortRemindHitMassege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.jk51.modules.pandian.util.Constant.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/7/3
 * 修改记录:
 */
@Service
public class PandianRemindHitService {

    private static final String NEXT_GOODS_CODE_REDIS_KEY = "nextGoodsCode";

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private PandianOrderRedisManager pandianOrderRedisManager;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private InventorySortRemindHitMapper sortRemindHitMapper;
    @Autowired
    private InventorySortNotHasRemindMapper sortNotHasRemindMapper;

    @Transactional
    public void recodeInfo(InventorySortRemindHitMassege sortRemindHitMassege){

        InventorySortRemindHit sortRemindHit =  getInventorySortRemindHit(sortRemindHitMassege);
        InventorySortNotHasRemind sortNotHasRemind = getInventorySortNotHasRemind(sortRemindHitMassege);
        sortRemindHitMapper.delete(sortRemindHit);
        sortNotHasRemindMapper.delete(sortNotHasRemind);

        int num = sortRemindHitMapper.insert(sortRemindHit);
        int num2 = sortNotHasRemindMapper.insert(sortNotHasRemind);

        if(num==0 || num2==0){
            throw new RuntimeException("插入数据失败");
        }

        storageNextGoodsCode(sortRemindHitMassege);
    }



    public void storageNextGoodsCode(InventorySortRemindHitMassege sortRemindHitMassege){

        String key = getNextRedisKey(sortRemindHitMassege);
        String value = StringUtil.isEmpty(sortRemindHitMassege.getNextGoodsCode())?"":sortRemindHitMassege.getNextGoodsCode();

        if(!sortRemindHitMassege.isHasRemind()){
            value = "";
        }
        redisTemplate.opsForValue().set(key,value);

    }

    private InventorySortRemindHit getInventorySortRemindHit(InventorySortRemindHitMassege massege){

        InventorySortRemindHit result = new InventorySortRemindHit();

        String currentGoodsCode = massege.getDto().getCurrentGoodsCode();
        String remindGoodsCode = getRedisValue(massege);

        result.setPandianNum(massege.getDto().getPandianNum());
        result.setSiteId(massege.getDto().getSiteId());
        result.setStoreId(massege.getDto().getStoreId());
        result.setStoreAdminId( massege.getDto().getCurrentStoreAdminId());
        result.setCurrentGoodsCode(currentGoodsCode);
        if(StringUtil.isEmpty(remindGoodsCode)){
            result.setType(NOT_REMIND);
            result.setRemindGoodsCode(remindGoodsCode);
        }else {
            result.setType(currentGoodsCode.equals(remindGoodsCode)?REMIND_SAME:REMIND_NOT_SAME);
            result.setRemindGoodsCode(remindGoodsCode);
        }
        result.setCreateTime(new Date());
        result.setUpdateTime(new Date());

        return result;
    }

    private InventorySortNotHasRemind getInventorySortNotHasRemind(InventorySortRemindHitMassege massege){

        InventorySortNotHasRemind result = new InventorySortNotHasRemind();

        String currentGoodsCode = massege.getDto().getCurrentGoodsCode();
        String nextGoodsCode = massege.getNextGoodsCode();

        result.setPandianNum(massege.getDto().getPandianNum());
        result.setSiteId(massege.getDto().getSiteId());
        result.setStoreId(massege.getDto().getStoreId());
        result.setStoreAdminId( massege.getDto().getCurrentStoreAdminId());
        result.setCurrentGoodsCode(currentGoodsCode);
        result.setNextGoodsCode(nextGoodsCode);
        result.setType(getSortNotHasRemindType(massege));
        result.setCreateTime(new Date());
        result.setUpdateTime(new Date());

        return result;
    }

    private int getSortNotHasRemindType(InventorySortRemindHitMassege massege){

        if(StringUtil.isEmpty(massege.getNextGoodsCode())){

            boolean currentHasInRedis = pandianOrderRedisManager.currentGoodsCodeHasInRedis(massege);

            if(currentHasInRedis){
                return NEXT_NOT_HAVE_INFO_IN_REIDS;
            }

            return CURRENT_NOT_HAVE_IN_REDIS;
        }

        List<Inventories> list = inventoryRepository.findInventories(getInventoryParam(massege));

        if(StringUtil.isEmpty(list)){
            return NEXT_NOT_HAVE_INFO_IN_INVENTORY;
        }

        Inventories inventory = list.get(0);
        if(!StringUtil.isEmpty(inventory.getInventory_checker())){
            return NEXT_CHECKED;
        }

        return HAS_REMIND;

    }

    private InventoryParam getInventoryParam(InventorySortRemindHitMassege massege){

        InventoryParam param = new InventoryParam();
        param.setPandian_num(massege.getDto().getPandianNum());
        param.setSiteId(massege.getDto().getSiteId());
        param.setStoreId(massege.getDto().getStoreId());
        param.setGoods_code(massege.getNextGoodsCode());

        return param;
    }

    private String getRedisValue(InventorySortRemindHitMassege massege){
        String kye = getNextRedisKey(massege);
        String value =  redisTemplate.opsForValue().get(kye);

        if(value == null){
            value = "";
        }
        return value;
    }

    public String getNextRedisKey(InventorySortRemindHitMassege sortRemindHitMassege){

        StringBuilder builder = new StringBuilder(NEXT_GOODS_CODE_REDIS_KEY);
        builder.append("_");
        builder.append(sortRemindHitMassege.getDto().getSiteId());
        builder.append("_");
        builder.append(sortRemindHitMassege.getDto().getStoreId());
        builder.append("_");
        builder.append(sortRemindHitMassege.getDto().getCurrentStoreAdminId());

        return builder.toString();

    }
}
