package com.jk51.modules.balance.service;

import com.jk51.commons.string.StringUtil;
import com.jk51.model.balance.BaseFeeSet;
import com.jk51.modules.balance.mapper.BaseFeeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2018/5/11
 * 修改记录:
 */
@Service
public class BaseFeeService {
    private static Logger logger = LoggerFactory.getLogger(BaseFeeService.class);

    @Autowired
    private BaseFeeMapper baseFeeMapper;



    public BaseFeeSet getBaseFee(Integer siteId, Integer id) {
        return baseFeeMapper.getBaseFee(siteId,id);
    }

    public List<BaseFeeSet> getBaseFeeLst(Integer siteId) {
        return baseFeeMapper.getBaseFeeLst(siteId);
    }

    public Integer addBaseFee(BaseFeeSet record) {
        return baseFeeMapper.addBaseFee(record);
    }

    public Integer updBaseFee(BaseFeeSet record) {
        return baseFeeMapper.updBaseFee(record);
    }

    public Integer delBaseFee(Integer siteId, Integer id) {
        return baseFeeMapper.delBaseFee(siteId,id);
    }


    /**
     * 根据订单组合code获取收取的费率
     * 支付方式：
     * 微信：100公众号  110扫码支付  120刷卡支付  130app支付   140H5支付
     * 支付宝：200 手机网站支付  210 当面付  220  app支付
     * 其他：300现金  310医保卡
     * 配送方式：
     * 150送货上门  160门店自提  170门店直销
     * 下单场景 110PC商城 120微商城 130门店助手app 140门店后台 150分销app  160支付宝商城
     * @param siteId
     * @param scene          场景
     * @param deliveryType   配送方式
     * @param payType        支付方式
     * @return
     */
    public Map getBaseFeeByCode(Integer siteId, String scene, String deliveryType, String payType){
        Map map = new HashMap();
        map.put("result",0);//0不收
        try {
            //参数必传
            if(StringUtil.isEmpty(siteId)||StringUtil.isEmpty(scene)||StringUtil.isEmpty(deliveryType)||StringUtil.isEmpty(payType)){
                return map;
            }
            StringBuilder code = new StringBuilder();
            code.append(scene);
            code.append(deliveryType);
            code.append(payType);
            List<BaseFeeSet> baseFeeSetList = getBaseFeeLst(siteId);
            List<String> stringList;
            for(BaseFeeSet baseFeeSet : baseFeeSetList){
                stringList = str2list(baseFeeSet.getScene(),baseFeeSet.getDeliveryType(),baseFeeSet.getPayType());
                int count = Collections.frequency(stringList,code.toString());
                if (count>0){
                    if(baseFeeSet.getFeeType()==0){
                        map.put("feeRate",-baseFeeSet.getFeeRate());
                    }else{
                        map.put("feeRate",baseFeeSet.getFeeRate());
                    }
                    map.put("feeRule",baseFeeSet.getFeeRule());//费用规则（0-按订单实付金额，不含运费；1-按订单实付金额，含运费）
                    //退单规则 0:退单不返还 1:退单返还-全额返  2:退单返还-比例返还
                    map.put("refuseRule","0".equals(baseFeeSet.getRefuseRule())?0:baseFeeSet.getRefuseRule().split("-")[1].equals("0")?1:2);
                    map.put("result",1);
                    return map;
                }
                continue;
            }
            return map;
        }catch (Exception e){
            logger.error("获取佣金失败:{}",e.getMessage());
            return map;
        }
    }

    /**
     *  重组三个数组生成一个list
     * @param a  格式（1,2,3）
     * @param b  同上
     * @param c  同上
     * @return
     */
    public List<String> str2list(String a,String b,String c){
        List<String> lst = new ArrayList<>();
        String[] aArr = a.split(",");
        String[] bArr = b.split(",");
        String[] cArr = c.split(",");
        String x = "";
        String y = "";
        String z = "";
        List<String> list = new ArrayList<>();
        for (int j = 0; j < aArr.length; j++) {
            for (int k = 0; k < bArr.length; k++) {
                for (int h = 0; h < cArr.length; h++) {
                    x = aArr[j];
                    y = bArr[k];
                    z = cArr[h];
                    lst.add(x+y+z);
                }
            }
        }
        return lst;
    }


}
