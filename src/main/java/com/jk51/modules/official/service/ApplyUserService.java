package com.jk51.modules.official.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jk51.commons.des.EncryptUtils;
import com.jk51.model.goods.PageData;
import com.jk51.model.official.ApplyUser;
import com.jk51.modules.appInterface.util.SendSMS;
import com.jk51.modules.distribution.result.Resultful;
import com.jk51.modules.official.mapper.ApplyUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;


@Service
public class ApplyUserService {

    @Autowired
    private ApplyUserMapper applyUserMapper;
    @Autowired
    private SendSMS sendSMS;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public String doPassword(String password) {
        //加密
        String pwd = "";
        try {
            pwd = EncryptUtils.encryptToSHA(EncryptUtils.encryptToSHA(password));
        } catch (NoSuchAlgorithmException e) {
            logger.error("加密错误");
        }
        return pwd;
    }

    public Resultful login(String uphone, String upwd){
        ApplyUser applyUser = applyUserMapper.login(uphone,doPassword(upwd));
        if (null == applyUser) {
            return Resultful.buildFailedResult("账户或密码有误");
        }
        return Resultful.buildSuccessResult(applyUser);
    }

    //验证码登录
    public Resultful loginByCode(String uphone){
        ApplyUser applyUser = applyUserMapper.sendCode(uphone);
        return Resultful.buildSuccessResult(applyUser);
    }

    public  Resultful registUser(ApplyUser applyUser){
        applyUser.setUpwd(doPassword(applyUser.getUpwd()));
        int a = applyUserMapper.registUser(applyUser);
        if (a!=1){
            return Resultful.buildFailedResult("注册失败");
        }
        return Resultful.buildSuccessResult(null);
    }

    public Resultful sendCode(String uphone,String code){
        ApplyUser applyUser = applyUserMapper.sendCode(uphone);
        if (null == applyUser){
            return Resultful.buildFailedResult("账户不存在");
        }
        int status = sendSMS.sendSMS(0,uphone,code,180);
        if(status==0){
            return Resultful.buildSuccessResult(null);
        }
        return Resultful.buildFailedResult("验证码发送异常");
    }

    public Resultful sendRegistCode(String uphone,String code){
        ApplyUser applyUser = applyUserMapper.sendCode(uphone);
        if (null==applyUser){
            int status = sendSMS.sendSMS(0,uphone,code,180);
            if(status==0){
                return Resultful.buildSuccessResult(null);
            }
            return Resultful.buildFailedResult("验证码发送异常");
        }
        return Resultful.buildFailedResult("账户已经存在");
    }

    public PageInfo<?> queryUser(Map<String, Object> params, Integer page, Integer pageSize){
        PageHelper.startPage(page, pageSize);
        List<PageData> list = applyUserMapper.queryUser(params);
        return new PageInfo<>(list);
    }

}
