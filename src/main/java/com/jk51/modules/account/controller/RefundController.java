package com.jk51.modules.account.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jk51.model.account.models.PayDataImport;
import com.jk51.model.account.requestParams.PayDataImportParams;
import com.jk51.model.account.requestParams.RefundParams;
import com.jk51.model.order.Refund;
import com.jk51.modules.trades.mapper.RefundMapper;
import com.jk51.modules.trades.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     :
 * 作者     : zhangduoduo
 * 创建日期 : 2017/3/14-03-14
 * 修改记录 :
 */
@Controller
@RequestMapping("refund")
public class RefundController {

    @Autowired
    private RefundMapper refundMapper;

    @RequestMapping(value = "/refund_list",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> queryPayList(RefundParams refundParams,
                                            @RequestParam(value = "page", defaultValue = "1") Integer page,
                                            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize) {
        Map<String, Object> map = null;
        Page pageInfo = PageHelper.startPage(page, pageSize);//开启分页
        List<Refund> list = refundMapper.queryRefundList(refundParams);
        map.put("items",list);
        map.put("page",pageInfo.toPageInfo());
        return map;
    }

}
