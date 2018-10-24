package com.jk51.modules.wechatcardbag.service;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.commons.string.StringUtil;
import com.jk51.modules.appInterface.mapper.BMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author zhaoyang
 * @version 1.00
 * @ClassName CreateCardService
 * @Description 微信卡包功能
 * @Date 2018-05-23 10:36
 */
@Service
public class CreateCardService {



    @Autowired
    BMemberMapper bMemberMapper;


    //根据openId查询是否有会员记录
    public ReturnDto queryIsHaveLog(Map<String, Object> parameterMap) {
        Map<String,Object> memb = bMemberMapper.queryIsMember(parameterMap);
        ReturnDto returnDto = null;
        if (StringUtil.isEmpty(memb)) {
            returnDto =  ReturnDto.buildSuccessReturnDto("NO");
        }else {
            returnDto =  ReturnDto.buildSuccessReturnDto("YES");
        }
        return returnDto;
    }

    public String queryMemberCardId(Integer siteId) {
        String cardId = bMemberMapper.queryMemberCardId(siteId);
        return cardId;
    }
}
