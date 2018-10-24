package com.jk51.session;


import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebListener;

@WebListener
public class ShiroSessionListener implements SessionListener {

    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private ShiroSessionService shiroSessionService;

    @Autowired
    private ShiroSessionDao sessionDao;

    @Override
    public void onStart( final Session session) {
        log.info("session {} onStart", session.getId());
    }

    @Override
    public void onStop( final Session session) {
        sessionDao.delete(session);
        shiroSessionService.sendUnCacheSessionMessage(session.getId());
        log.info("session {} onStop", session.getId());
    }

    @Override
    public void onExpiration( final Session session) {
        sessionDao.delete(session);
        shiroSessionService.sendUnCacheSessionMessage(session.getId());
        log.info("session {} onExpiration", session.getId());
    }

}
