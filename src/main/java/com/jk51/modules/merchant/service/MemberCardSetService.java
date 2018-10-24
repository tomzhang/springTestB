package com.jk51.modules.merchant.service;

import com.jk51.model.merchant.MemberCardSet;
import com.jk51.modules.merchant.mapper.MemberCardSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen_pt
 * 创建日期: 2017/11/2
 * 修改记录:
 */
@Service
public class MemberCardSetService {

    @Autowired
    private MemberCardSetMapper cardSetMapper;


    public MemberCardSet getBySiteId(Integer siteId) {
        return cardSetMapper.getBySiteId(siteId);
    }

    public Integer updCardSet(MemberCardSet cardSet) {
        return cardSetMapper.updCardSet(cardSet);
    }

    public Integer addCardSet(MemberCardSet cardSet) {
        return cardSetMapper.addCardSet(cardSet);
    }
}
