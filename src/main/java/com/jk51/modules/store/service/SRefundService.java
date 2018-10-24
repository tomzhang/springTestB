package com.jk51.modules.store.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.model.order.Refund;
import com.jk51.model.order.response.RefundQueryReq;
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
 * 作者: baixiongfei
 * 创建日期: 2017/3/17
 * 修改记录:
 */
@Service
public class SRefundService {

    public static final Logger logger = LoggerFactory.getLogger(SRefundService.class);

    @Autowired
    private RefundMapper refundMapper;
   /* @Autowired
    private StoreAuthorityService storeAuthorityService;*/

    /**
     * 查询门店的退款列表(包括所有的退款状态)
     * @param req
     * @return
     */
    public List<Refund> getRefundList(RefundQueryReq req){

        return refundMapper.getRefundList(req);
    }


    /**
     * 查询门店的退款列表(包括所有的退款状态)
     * @param req
     * @return
     */
    public List<Refund> refundList(RefundQueryReq req){
        return refundMapper.refundList(req);
    }

    /**
     *
     * @param req
     * @return
     */
    public Refund getRefundById(RefundQueryReq req){
        return refundMapper.getRefundById(req);
    }


    /**
     * 根据商户ID和门店ID判断门店是否有退款权限
     * @param siteId
     * @param storeId
     * @return 有权限时返回null  无权限是返回错误信息
     */

    private Map<String, Object> judgeRefundAuth(Integer siteId, Integer storeId){
        Map<String, Object> mapResponse = new HashMap<>();
       /* if (siteId == null || storeId == null){
            mapResponse.put("status", 000);
            mapResponse.put("code", 102);
            mapResponse.put("message", "门店或者商户ID为空");
            return mapResponse;
        }
        try {
            String storeIds = storeAuthorityService.selectIdsByStorereFundPermissionFromMeta(siteId);
            String storeIdTemp = storeId.toString();
            if(StringUtils.isEmpty(storeIdTemp) || !storeIds.contains(storeIdTemp)){
                mapResponse.put("status", 000);//请求成功状态码值
                mapResponse.put("code", 101);//不具有权限时返回的码值
                mapResponse.put("message", "该门店不具有退款权限!");
                return mapResponse;
            }
        }catch (Exception e){
            logger.error("根据商户ID查询具有退款权限的门店出错===>>>{}", e.getMessage());
        }*/
        return mapResponse;
    }

}
