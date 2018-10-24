package com.jk51.modules.shiro;

import com.jk51.model.role.Manager;
import com.jk51.model.role.Role;
import com.jk51.modules.authority.mapper.ManagerMapper;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen
 * 创建日期: 2017-02-23
 * 修改记录:
 */
public class MyShiroRealm extends AuthorizingRealm {

    private static final Logger log = LoggerFactory.getLogger(MyShiroRealm.class);
    @Autowired
    private ManagerMapper managerMapper;


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        log.info("shiro权限认证");
        String loginName = (String) super.getAvailablePrincipal(principals);
        Manager manager = managerMapper.selectByName(loginName);


        if (null != manager) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            //放入用户角色集合
            info.setRoles(null);
            List<Role> roleList = null;

            //修改部分
            for (Role role :
                    roleList) {
                //添加资源的集合
                //map.keySet()
                info.addStringPermission(role.getPermissions());
            }

            return info;
        }
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        //UsernamePasswordToken对象用来存放提交的登录信息
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        log.info("验证当前Subject时获取到token为：" + ReflectionToStringBuilder.toString(token, ToStringStyle.MULTI_LINE_STYLE));

        //查出是否有此用户
        Manager manager = managerMapper.selectByName(token.getUsername());

        String password = new String(token.getPassword());

        if (manager != null) {
            // 若存在，将此用户存放到登录认证info中，无需自己做密码对比，Shiro会为我们进行密码对比校验
            return new SimpleAuthenticationInfo(manager.getUsername(), manager.getPassword(), getName());
        }


        return null;
    }
}
