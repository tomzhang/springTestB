package com.jk51.session;


import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component("shiroSessionRepository")
public class ShiroSessionRepository {
    private Logger log = LoggerFactory.getLogger(getClass());

    private static final String REDIS_SHIRO_SESSION = "shiro-session:";
    private static final int SESSION_VAL_TIME_SPAN = 1800;

    @Setter
    private String redisShiroSessionPrefix = REDIS_SHIRO_SESSION;

    @Setter
    private int redisShiroSessionTimeout = SESSION_VAL_TIME_SPAN;

    @Autowired
    private RedisTemplate<String, Session> redisTemplate;

    public void saveSession( final Session session) {
        try {
            redisTemplate.opsForValue()
                    .set(
                            buildRedisSessionKey(
                                    session.getId()
                            )
                            , session
                            , redisShiroSessionTimeout
                            , TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("save session to redis error,message:{}",e.getMessage());
        }
    }

    public void updateSession( final Session session) {
        try {
            redisTemplate.boundValueOps(
                    buildRedisSessionKey(
                            session.getId()
                    )
            ).set(session
                    , redisShiroSessionTimeout
                    , TimeUnit.SECONDS
            );
        } catch (Exception e) {
            log.error("update session error,message:{}", e.getMessage());
        }
    }

    public void refreshSession( final Serializable sessionId) {
        redisTemplate.expire(
                buildRedisSessionKey(sessionId)
                , redisShiroSessionTimeout
                , TimeUnit.SECONDS
        );
    }

    public void deleteSession( final Serializable id) {
        try {
            redisTemplate.delete(buildRedisSessionKey(id));
        } catch (Exception e) {
            log.error("delete session error,message:{}",e.getMessage());
        }
    }

    public Session getSession( final Serializable id) {
        Session session = null;
        try {
            session = redisTemplate.boundValueOps(buildRedisSessionKey(id)).get();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("get session error,message:{}",e.getMessage());
        }
        return session;
    }

    private String buildRedisSessionKey( final Serializable sessionId) {
        return redisShiroSessionPrefix + sessionId;
    }
}
