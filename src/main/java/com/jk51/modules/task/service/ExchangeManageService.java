package com.jk51.modules.task.service;

import com.github.pagehelper.PageHelper;
import com.jk51.modules.task.mapper.YbJkexcrecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guosheng on 2017/9/29.
 */
@Service
public class ExchangeManageService {
    @Autowired
    YbJkexcrecordMapper ybJkexcrecordMapper;

    public List<Map<String,Object>> getexchangeManageList(HashMap queryMap, int pageNum, int pageSize) {
//        if (!Objects.isNull(queryMap.get("endtime")) && !String.valueOf(queryMap.get("endtime")).equals("")){
//            String end = String.valueOf(queryMap.get("endtime")) + " 23:59:59";
//            queryMap.put("endtime", end);
//        }
        PageHelper.startPage(pageNum, pageSize);
        List<Map<String,Object>> exchangeManageList=ybJkexcrecordMapper.selectByMap(queryMap);
        return exchangeManageList;
    }

    public boolean changeStatus(Integer id) {
         Integer count=ybJkexcrecordMapper.changeStatusById(id);
         if(count==1){
             return true;
         }
         return false;
    }

    public boolean updateStatus(Integer id) {
        Integer count=ybJkexcrecordMapper.updateStatusById(id);
        if(count==1){
            return true;
        }
        return false;
    }
}
