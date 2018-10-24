package com.jk51.modules.marketing.service;

import com.jk51.model.BMarketingMember;
import com.jk51.model.BMarketingMemberExt;
import com.jk51.modules.marketing.mapper.BMarketingMemberExtMapper;
import com.jk51.modules.marketing.mapper.BMarketingMemberMapper;
import com.jk51.modules.marketing.request.MarketingMemberParm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2018/3/19
 * 修改记录:
 */
@Service
public class MarketingMemberService {

    @Autowired
    private BMarketingMemberMapper bMarketingMemberMapper;
    @Autowired
    private BMarketingMemberExtMapper bMarketingMemberExtMapper;


    public List<Map> getLst(MarketingMemberParm parms) {
        return bMarketingMemberMapper.getLst(parms);
    }

    public Integer changeStatus(Integer siteId, Integer id, Integer status,String remark) {
        return bMarketingMemberExtMapper.changeStatus(siteId,id,status,remark);
    }
}
