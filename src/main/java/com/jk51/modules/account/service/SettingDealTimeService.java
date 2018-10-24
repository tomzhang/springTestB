package com.jk51.modules.account.service;

import com.alibaba.druid.util.StringUtils;
import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.string.StringUtil;
import com.jk51.model.YbMeta;
import com.jk51.model.account.requestParams.DealTimeParam;
import com.jk51.modules.account.constants.AccountConstants;
import com.jk51.modules.merchant.mapper.YbMetaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * filename :com.jk51.modules.account.goodsService.
 * author   :zw
 * date     :2017/3/17
 * Update   :
 */
@Service
public class SettingDealTimeService {
    @Autowired
    private YbMetaMapper ybMetaMapper;

    @Transactional
    public ReturnDto settingDealTime(DealTimeParam dealTimeParam) {
        try {
            processYbMetaBin(dealTimeParam).forEach(p -> {
                YbMeta ybMeta = ybMetaMapper.findFirst(p.getSiteId(),p.getMetaKey());
                if(null == ybMeta){
                    ybMetaMapper.save(p);
                }else{
                    ybMetaMapper.update(p);
                }
            });
        } catch (Exception e) {
            return ReturnDto.buildFailedReturnDto("meta create failed, message:" + e);
        }
        return ReturnDto.buildSuccessReturnDto("meta rule create success");
    }

    private List<YbMeta> processYbMetaBin(DealTimeParam dealTimeParam) {
        List<YbMeta> list = new ArrayList<>();

        if (dealTimeParam.getTrades_auto_close_time() > 0) {
            YbMeta ybMeta = new YbMeta();
            ybMeta.setSiteId(dealTimeParam.getSite_id());
            ybMeta.setMetaKey(AccountConstants.META_KEY_CLOSE);
            ybMeta.setMetaVal(String.valueOf(dealTimeParam.getTrades_auto_close_time()));
            list.add(ybMeta);
        }
        if (dealTimeParam.getTrades_auto_confirm_time() > 0) {
            YbMeta ybMeta = new YbMeta();
            ybMeta.setSiteId(dealTimeParam.getSite_id());
            ybMeta.setMetaKey(AccountConstants.META_KEY_AFFIRM);
            ybMeta.setMetaVal(String.valueOf(dealTimeParam.getTrades_auto_confirm_time()));
            list.add(ybMeta);
        }
        if (dealTimeParam.getTrades_allow_refund_time() > 0) {
            YbMeta ybMeta = new YbMeta();
            ybMeta.setSiteId(dealTimeParam.getSite_id());
            ybMeta.setMetaKey(AccountConstants.META_KEY_FINISH);
            ybMeta.setMetaVal(String.valueOf(dealTimeParam.getTrades_allow_refund_time()));
            list.add(ybMeta);
        }
        return list;
    }
    public YbMeta getDealTime(Integer siteId,String ruleKey){
        YbMeta ybMeta = ybMetaMapper.findFirst(siteId,ruleKey);
        return ybMeta;
    }

}
