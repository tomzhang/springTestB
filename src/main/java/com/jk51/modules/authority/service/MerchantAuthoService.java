package com.jk51.modules.authority.service;

import com.jk51.modules.authority.mapper.MerchantAuhtoMapper;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 商家授权
 *
 * @auhter zy
 * @create 2017-08-04 14:49
 */
@Service
public class MerchantAuthoService {

    @Autowired
    private MerchantAuhtoMapper merchantAuhtoMapper;

    public List<Map<String,Object>> selectMerchantList(Map<String,Object> map) {
        List<Map<String,Object>> resultList = merchantAuhtoMapper.selectMerchantList(map);
        return resultList;
    }


    @Transactional
    public Map<String,Object> insertAuthoLog(Map<String,Object> map) {
        //生成授权码 授权码为///开头的20个数字及大写字母的组合；
        String str = "0123456789ABCDEFGHIJKLNMOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        Map<String,Object> resultMap = new HashedMap();
        sb.append("///");
        for(int i = 0; i < 20; i++) {
            sb.append(str.charAt(random.nextInt(20)));
        }
        map.put("authoCode",sb.toString());
        Integer result = merchantAuhtoMapper.insertAuthoLog(map);
        if(result != null && result == 1) {
            resultMap.put("result","success");
            resultMap.put("authoCode",sb.toString());
            //查询截止有效时间
            String finishTime = merchantAuhtoMapper.queryAuthoLog(Integer.valueOf(map.get("siteId").toString()));
            resultMap.put("finishTime",finishTime);
        }else {
            resultMap.put("result","fail");
        }
        return resultMap;
    }

    public Map<String,Object> getAuthoLog(String pwd) {
        return merchantAuhtoMapper.getAuthoLog(pwd);
    }

    public List<Map<String,Object>> getStoresListBySiteId(Integer siteId,String storeName) {
        return merchantAuhtoMapper.getStoresListBySiteId(siteId,storeName);
    }
}
