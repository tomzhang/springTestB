package com.jk51.modules.account.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.model.account.requestParams.PreSettlementParam;
import com.jk51.model.order.Refund;
import com.jk51.modules.account.service.PreSettlementService;
import com.jk51.modules.account.service.SettlementDetailAndTradesService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 财务预算
 * 作者: chen_pt
 * 创建日期: 2017/7/25
 * 修改记录:
 */
@Controller
@RequestMapping("account")
public class PreSettlementController {

    private static final Logger logger = LoggerFactory.getLogger(PreSettlementController.class);

    @Autowired private PreSettlementService preSettlementService;


    /**
     * 预结算列表
     */
    @RequestMapping(value="/preSettlement")
    public @ResponseBody Map<String, Object> querySettlementLog(@RequestBody PreSettlementParam parm) {
        Map<String, Object> map = new HashedMap();
        PageHelper.startPage(parm.getPageNum(), parm.getPageSize(), parm.isCount());//开启分页
        List<Map<String, Object>> list = preSettlementService.getPreSettlementLst(parm);

        PageInfo<?> pageInfo = new PageInfo<>(list);
        if(pageInfo.getList().size()>0){
            map.put("items", pageInfo.getList());
            map.put("page", pageInfo.getPageNum());
            map.put("pageSize", pageInfo.getPageSize());
            //如果不需要统计总行数,总页数不返回参数
            if (parm.isCount()) {
                map.put("pages", pageInfo.getPages());
                map.put("total", pageInfo.getTotal());
            }
        }

        return map;
    }




}
