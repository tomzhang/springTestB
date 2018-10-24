package com.jk51.modules.store.service;

import com.jk51.commons.http.HttpClient;
import com.jk51.commons.json.JacksonUtils;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.order.Refund;
import com.jk51.model.order.response.RefundQueryReq;
import com.jk51.modules.persistence.mapper.BTradesMapper;
import com.jk51.modules.trades.mapper.RefundMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 门店后台查询用户退款列表
 * 作者: zhangkuncheng
 * 创建日期: 2017/5/16
 * 修改记录:
 */
@Service
public class BTradesService {

    public static final Logger logger = LoggerFactory.getLogger(SRefundService.class);

    @Autowired
    private BTradesMapper bTradesMapper;

    /**
     * 查询门店后台各订单量
     *
     * @param siteId
     * @param storeId
     * @return
     */
    public Map<String, Object> orderCount(String siteId, Integer storeId) {
        Map<String, Object> result = new HashMap<>();
        if (!StringUtil.isEmpty(siteId) && storeId != null) {
            result.put("allCount", bTradesMapper.getStoreOrderAll(siteId, storeId, null));//获取门店全部订单
            result.put("beihuoCount", bTradesMapper.getStoreOrderAll(siteId, storeId, "beihuo"));//获取门店全部待备货订单
            result.put("fahuoCount", bTradesMapper.getStoreOrderAll(siteId, storeId, "fahuo"));//获取门店全部发货订单
            result.put("zitiCount", bTradesMapper.getStoreOrderAll(siteId, storeId, "ziti"));//获取门店全部待自提订单
            result.put("zhigouCount", bTradesMapper.getStoreOrderAll(siteId, storeId, "zhigou"));//获取门店全部待自提订单
        }
        return result;
    }
}
