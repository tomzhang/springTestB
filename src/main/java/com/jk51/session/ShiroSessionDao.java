package com.jk51.session;


import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: shiro session写入redis
 * 作者: bluesx
 * 创建日期: 2017-04-17
 * 修改记录:
 */
@Slf4j
public class ShiroSessionDao extends CachingSessionDAO {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private ShiroSessionRepository sessionRepository;

    @Override
    public Session readSession( final Serializable sessionId) throws UnknownSessionException {
        Session session = getCachedSession(sessionId);
        if (session == null || session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY) == null) {
            session = this.doReadSession(sessionId);
            if (session == null) {
                log.error("There is no session with id [" + sessionId + "]");
                throw new UnknownSessionException("There is no session with id [" + sessionId + "]");
            } else {
                cache(session, session.getId());
            }
        }
        return session;
    }

    @Override
    protected Session doReadSession( final Serializable sessionId) {
        log.debug("begin doReadSession {} ", sessionId);
        Session session = null;
        try {
            session = sessionRepository.getSession(sessionId);
            if (session != null) {
                sessionRepository.refreshSession(sessionId);
                log.info("sessionId {} name {} read", sessionId, session.getClass().getName());
            }
        } catch (Exception e) {
            log.warn("reading Session fail,message:{}", e.getMessage());
        }
        return session;
    }

    public Session doReadSessionWithoutExpire( final Serializable sessionId) {
        Session session = null;
        try {
            session = sessionRepository.getSession(sessionId);
        } catch (Exception e) {
            log.warn("readding Session fail,message:{}", e.getMessage());
        }
        return session;
    }

    @Override
    protected Serializable doCreate( final Session session) {
        Serializable sessionId = this.generateSessionId(session);
        assignSessionId(session, sessionId);
        try {
            sessionRepository.saveSession(session);
            log.info("sessionId {} name {} created", sessionId, session.getClass().getName());
        } catch (Exception e) {
            log.error("creating Session fail,message:{}", e.getMessage());
        }
        return sessionId;
    }

    @Override
    protected void doUpdate( final Session session) {
        try {
            if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid()) {
                return;
            }
        } catch (Exception e) {
            log.error("ValidatingSession error,message:{}",e.getMessage());
        }
        try {
            if (session instanceof Session) {
                SimpleSession ss =  (SimpleSession)session;
                ss.setLastAccessTime(new Date());
                sessionRepository.updateSession(session);
                log.info("sessionId {} name {} updated", session.getId(), session.getClass().getName());
            } else {
                log.error("sessionId {} name {} updated fail", session.getId(), session.getClass().getName());
            }
        } catch (Exception e) {
            log.error("updating Session fail,message:{}", e.getMessage());
        }
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        this.doUpdate(session);
    }

    @Override
    public void doDelete( final Session session) {
        log.debug("begin doDelete {} ", session);
        try {
            sessionRepository.deleteSession(session.getId());
            this.unCache(session.getId());
            log.debug("shiro session id {} deleted", session.getId());
        } catch (Exception e) {
            log.error("deleting session failed:{}", e.getMessage());
        }
    }

    public void unCache( final Serializable sessionId) {
        try {
            Session session = super.getCachedSession(sessionId);
            super.uncache(session);
            log.debug("Session id {} in local cache invalid", sessionId);
        } catch (Exception e) {
            log.error("deleting session in local cache failed,message:{}", e.getMessage());
        }
    }

    public Collection<Session> getEhCacheActiveSessions() {
        return super.getActiveSessions();
    }

}
