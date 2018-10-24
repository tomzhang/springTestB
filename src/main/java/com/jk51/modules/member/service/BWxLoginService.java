package com.jk51.modules.member.service;

import com.jk51.model.login.BLogin;
import com.jk51.modules.member.mapper.BWxLoginMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: dumingliang
 * 创建日期: 2017-05-21
 * 修改记录:
 */
@Service
public class BWxLoginService {
    @Autowired
    BWxLoginMapper bWxLoginMapper;

    @Transactional
    public Integer loginLog(BLogin bLogin) {
        return bWxLoginMapper.insertLog(bLogin);
    }
}
