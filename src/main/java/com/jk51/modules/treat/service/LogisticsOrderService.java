package com.jk51.modules.treat.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.model.BLogisticsOrder;
import com.jk51.modules.treat.mapper.LogisticsOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: ChengShanyunduo
 * 创建日期: 2017-03-19
 * 修改记录:
 */
@Service
public class LogisticsOrderService {
    @Autowired
    LogisticsOrderMapper logisticsOrderMapper;

    private static final Logger log = LoggerFactory.getLogger(LogisticsOrderService.class);

    public PageInfo<BLogisticsOrder> getLogisticsOrder(Integer page, Integer pageSize, Map<String, Object> map){
        PageHelper.startPage(page, pageSize);//开启分页
        List<BLogisticsOrder>  logisticsOrderList= this.logisticsOrderMapper.getLogisticsOrder(map);
        return new PageInfo<>(logisticsOrderList);

    }

    public Boolean  insertLogistics (BLogisticsOrder bLogisticsOrder){
        if (bLogisticsOrder.getSiteId()==null || bLogisticsOrder.getOrderNumber()==null || bLogisticsOrder.getLogisticsId()==null ||
            bLogisticsOrder.getLogisticsName()==null || bLogisticsOrder.getSiteId()==null){
            return false;
        }
        Integer count = logisticsOrderMapper.insertLogistics(bLogisticsOrder);
        if (count != 0) {
            return true;
        }else {
            return false;
        }
    }

    public List<String> getLogisticsName (){
        return logisticsOrderMapper.getLogisticsName();
    }

    public PageInfo<BLogisticsOrder> getLogisticsOrderAccount(Integer page, Integer pageSize, Map<String, Object> map){
        PageHelper.startPage(page, pageSize);//开启分页
        List<BLogisticsOrder>  logisticsOrderList= this.logisticsOrderMapper.getLogisticsOrderAccount(map);
        return new PageInfo<>(logisticsOrderList);

    }
}
