package com.jk51.modules.account.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.model.account.models.ClassifiedStatistic;
import com.jk51.model.account.models.FinancesStatistic;
import com.jk51.model.account.requestParams.ClassifiedAccountParam;
import com.jk51.modules.account.mapper.ClassifiedStatisticMapper;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     : 结算汇总获取数据
 * 作者     : zhangduoduo
 * 创建日期 : 2017/3/11-03-11
 * 修改记录 :
 */
@RestController
@RequestMapping("/classified_statistic")
public class ClassifiedStatisticController {
        @Autowired
        private ClassifiedStatisticMapper classifiedStatisticMapper;
        private final Logger logger= LoggerFactory.getLogger(ClassifiedStatisticController.class);

    /**
     * 结算汇总
     * @return
     */
    @RequestMapping(value = "/product_list",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> queryClassifiedStatistic(@RequestBody ClassifiedAccountParam classifiedAccountParam) {
        PageHelper.startPage(classifiedAccountParam.getPageNum(), classifiedAccountParam.getPageSize());//开启分页
        List<FinancesStatistic> list = classifiedStatisticMapper.getClassifiedList(classifiedAccountParam);
        PageInfo<?> pageInfo = new PageInfo<>(list);
        Map<String, Object> map = new HashedMap();
        map.put("items",pageInfo.getList());
        map.put("page",pageInfo.getPageNum());
        map.put("pageSize",pageInfo.getPageSize());
        map.put("pages",pageInfo.getPages());
        map.put("total",pageInfo.getTotal());
        return map;
    }



}
