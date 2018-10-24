package com.jk51.modules.merchant.controller;

import com.jk51.model.merchant.SignContract;
import com.jk51.modules.merchant.mapper.SignContractMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 版权所有(C) 2018 上海伍壹健康科技有限公司
 * 描述：
 * 作者: XC
 * 创建日期: 2018-09-29 14:08
 * 修改记录:
 **/
@RestController
@RequestMapping("/sign_contract")
public class SignContractController {
    @Autowired
    private SignContractMapper signContractMapper;

    @GetMapping("/getById/{id}")
    public SignContract getById(@PathVariable Integer id){
        SignContract signContract = signContractMapper.getById(id);
        if (signContract==null){
            signContract = new SignContract();
        }
        return signContract;
    }
}
