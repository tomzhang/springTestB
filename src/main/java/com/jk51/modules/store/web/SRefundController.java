package com.jk51.modules.store.web;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.parameter.ParameterUtil;
import com.jk51.model.order.Refund;
import com.jk51.model.order.response.RefundQueryReq;
import com.jk51.modules.store.service.SRefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: zhangkuncheng
 * 创建日期: 2017-03-22
 * 修改记录:
 */
@Controller
@RequestMapping("/store")
public class SRefundController {

    @Autowired
    private SRefundService sRefundService;

    /**
     * 门店后台查询用户退款列表
     * @param
     * @return
     */

    @RequestMapping(value = "/refund/getRefundList")
    @ResponseBody
    public List<Refund> getRefundList(HttpServletRequest request) {
        //Map<String,Object> result = new HashMap<String,Object>();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        RefundQueryReq req= null;
        List<Refund> list = null;
        try {
            String str = JacksonUtils.mapToJson(param);
            req = JacksonUtils.json2pojo(str, RefundQueryReq.class);
            list  = sRefundService.getRefundList(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 门店后台查询用户退款列表
     * @param
     * @return
     */

    @RequestMapping(value = "/refund/getRefundList1")
    @ResponseBody
    public PageInfo<?> getRefundList1(HttpServletRequest request) {
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        RefundQueryReq req= null;
        List<Refund> list = null;
        try {
            String str = JacksonUtils.mapToJson(param);
            req = JacksonUtils.json2pojo(str, RefundQueryReq.class);
            PageHelper.startPage(req.getPageNum(), req.getPageSize());
            list  = sRefundService.getRefundList(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PageInfo<?> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    //搜索
    @RequestMapping(value = "refund/refundList")
    @ResponseBody
    public List<Refund> refundList(HttpServletRequest request) {
        //Map<String,Object> result = new HashMap<String,Object>();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        RefundQueryReq req= null;
        List<Refund> list = null;
        try {
            String str = JacksonUtils.mapToJson(param);
            req = JacksonUtils.json2pojo(str, RefundQueryReq.class);
            list  = sRefundService.refundList(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //根据ID查看
    @RequestMapping(value = "refund/getRefundById")
    @ResponseBody
    public Refund getRefundById(HttpServletRequest request) {
        //Map<String,Object> result = new HashMap<String,Object>();
        Map<String,Object> param = ParameterUtil.getParameterMap(request);
        RefundQueryReq req= null;
        Refund refund = null;
        try {
            String str = JacksonUtils.mapToJson(param);
            req = JacksonUtils.json2pojo(str, RefundQueryReq.class);
            refund = sRefundService.getRefundById(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return refund;
    }

}
