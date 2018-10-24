package com.jk51.modules.account.service;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.interceptor.LoginInterceptor;
import com.jk51.model.account.models.Account;
import com.jk51.model.account.models.AccountCommissionRate;
import com.jk51.model.account.models.PayPlatform;
import com.jk51.model.account.requestParams.CommissionParam;
import com.jk51.modules.account.constants.AccountConstants;
import com.jk51.modules.account.mapper.AccountCommissionRateMapper;
import com.jk51.modules.account.mapper.PayPlatformMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * filename :com.jk51.modules.account.goodsService.
 * author   :zw
 * date     :2017/3/15
 * Update   :
 */
@Service
public class CommissionSettingService {
    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
    @Autowired
    private AccountCommissionRateMapper accountCommissionRateMapper;
    @Autowired
    private PayPlatformMapper payPlatformMapper;

    @Transactional
    public ReturnDto settingCommission(CommissionParam commissionParam) {
        try {
            if(getCommissionRatById(commissionParam.getSite_id()) > 0 ){
                //update
                accountCommissionRateMapper.updateAccount(processAccountCommissionRateBin(commissionParam));
            }else{
                accountCommissionRateMapper.addAccount(processAccountCommissionRateBin(commissionParam));
            }
            processPayPlatformBin(commissionParam).forEach(p->{
                Integer id = getPayPlatformBySiteIdAndPayType(p.getSite_id(),p.getPay_type());
                if(id > 0){
                    //update
                    p.setId(id);
                    payPlatformMapper.updatePayPlatform(p);
                }else{
                    payPlatformMapper.addPayPlatform(p);
                }

            });
        }catch (Exception e){
            logger.error("站点id："+commissionParam.getSite_id() + "佣金规则创建失败，原因："+e);
            return ReturnDto.buildFailedReturnDto("commission setting filed");
        }
        return ReturnDto.buildSuccessReturnDto("commission setting success");
    }
    private int getCommissionRatById(Integer site_id){
        AccountCommissionRate accountCommissionRate = accountCommissionRateMapper.getCommissionRatById(site_id);
        if(null == accountCommissionRate){
            return 0;
        }else{
            return accountCommissionRate.getSite_id();
        }
    }
    private int getPayPlatformBySiteIdAndPayType(Integer site_id,String pay_type){
       PayPlatform payPlatform = payPlatformMapper.getPayPlatformById(site_id,pay_type);
        if(null == payPlatform){
            return 0;
        }else{
            return payPlatform.getId();
        }
    }

    public List<PayPlatform> processPayPlatformBin(CommissionParam commissionParam){

        List<PayPlatform> list = new ArrayList<>();

        if(commissionParam.getWx_collection_fee() >= 0){
            PayPlatform payPlatform = new PayPlatform();
            payPlatform.setSite_id(commissionParam.getSite_id());
            payPlatform.setPay_type(AccountConstants.PAY_TYPE_WX);
            payPlatform.setProcedure_fee(commissionParam.getWx_collection_fee());
            list.add(payPlatform);
        }
        if(commissionParam.getAli_collection_fee() >= 0){
            PayPlatform payPlatform = new PayPlatform();
            payPlatform.setSite_id(commissionParam.getSite_id());
            payPlatform.setPay_type(AccountConstants.PAY_TYPE_ALI);
            payPlatform.setProcedure_fee(commissionParam.getAli_collection_fee());
            list.add(payPlatform);
        }
        if(commissionParam.getCash_collection_fee() >= 0){
            PayPlatform payPlatform = new PayPlatform();
            payPlatform.setSite_id(commissionParam.getSite_id());
            payPlatform.setPay_type(AccountConstants.PAY_TYPE_CASH);
            payPlatform.setProcedure_fee(commissionParam.getCash_collection_fee());
            list.add(payPlatform);
        }
        if(commissionParam.getHealth_insurance_collection_fee() >= 0){
            PayPlatform payPlatform = new PayPlatform();
            payPlatform.setSite_id(commissionParam.getSite_id());
            payPlatform.setPay_type(AccountConstants.PAY_TYPE_HEALTH_INSURANCE);
            payPlatform.setProcedure_fee(commissionParam.getHealth_insurance_collection_fee());
            list.add(payPlatform);
        }
        if(commissionParam.getUnionPay_collection_fee() >= 0){
            PayPlatform payPlatform = new PayPlatform();
            payPlatform.setSite_id(commissionParam.getSite_id());
            payPlatform.setPay_type(AccountConstants.PAY_TYPE_UNION_PAY);
            payPlatform.setProcedure_fee(commissionParam.getUnionPay_collection_fee());
            list.add(payPlatform);
        }
        return list;
    }
    public AccountCommissionRate processAccountCommissionRateBin(CommissionParam commissionParam){
        AccountCommissionRate accountCommissionRate = new AccountCommissionRate();
        accountCommissionRate.setSite_id(commissionParam.getSite_id());
        accountCommissionRate.setDirect_purchase_rate(commissionParam.getDirect_purchase_fee());
        accountCommissionRate.setDistributor_rate(commissionParam.getDistributor_fee());
        accountCommissionRate.setShipping_fee_rate(commissionParam.getDelivery_fee());
       return accountCommissionRate;
    }
}
