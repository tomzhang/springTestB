package com.jk51.modules.login.service;

import com.jk51.commons.message.ReturnDto;
import com.jk51.commons.des.EncryptUtils;
import com.jk51.model.BManager;
import com.jk51.model.StoreAdmin;
import com.jk51.model.official.ApplyUser;
import com.jk51.model.order.Member;
import com.jk51.modules.authority.mapper.ManagerMapper;
import com.jk51.modules.index.mapper.StoreAdminMapper;
import com.jk51.modules.official.mapper.ApplyUserMapper;
import com.jk51.modules.trades.mapper.MemberMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * 版本所有（C）2017 上海伍壹健康科技有限公司
 * 描述     :
 * 作者     : zhangduoduo
 * 创建日期 : 2017/2/27-02-27
 * 修改记录 :
 */
@Service
public class LoginService {
    /**
     * 商家登录的方法
     *
     * @param username
     * @param password
     * @return
     */
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private ManagerMapper managerMapper;
    @Autowired
    private StoreAdminMapper storeAdminMapper;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String doPassword(String password) {
        //加密
        String password2 = null;
        try {
            password2 = EncryptUtils.encryptToSHA(EncryptUtils.encryptToSHA(password));
        } catch (NoSuchAlgorithmException e) {
            logger.error("加密错误");
        }
        return password2;
    }

    public ReturnDto login(Integer siteId,String username, String password) {
        BManager loginManager = managerMapper.findUserAndPasswordById(siteId,username, doPassword(password));
        if (null == loginManager) {
            return ReturnDto.buildFailedReturnDto("登陆失败");
         }
        return ReturnDto.buildSuccessReturnDto("登陆成功");
    }

    public ReturnDto storeLogin(Integer siteId,String username, String password) {
        StoreAdmin loginStore = storeAdminMapper.findUserAndPasswordByStoreId(siteId,username, doPassword(password));
        if (null == loginStore) {
            return ReturnDto.buildFailedReturnDto("登陆失败");
        }
        return ReturnDto.buildSuccessReturnDto("登陆成功");
    }



}
