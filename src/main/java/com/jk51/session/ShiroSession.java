package com.jk51.session;

import org.apache.shiro.session.mgt.SimpleSession;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: shiro session
 * 作者: bluesx
 * 创建日期: 2017-04-17
 * 修改记录:
 */
public class ShiroSession extends SimpleSession implements Serializable {

    private boolean isChanged;

    public void setAttributekeys(Collection<Object> attributekeys) {
        this.attributekeys = attributekeys;
    }

    private Collection<Object> attributekeys;

    public ShiroSession() {
        super();
        this.setChanged(true);
    }

    public ShiroSession(String host) {
        super(host);
        this.setChanged(true);
    }


    @Override
    public void setId( final Serializable id) {
        super.setId(id);
        this.setChanged(true);
    }

    @Override
    public void setStopTimestamp( final Date stopTimestamp) {
        super.setStopTimestamp(stopTimestamp);
        this.setChanged(true);
    }

    @Override
    public void setExpired(final boolean expired) {
        super.setExpired(expired);
        this.setChanged(true);
    }

    @Override
    public void setTimeout(final long timeout) {
        super.setTimeout(timeout);
        this.setChanged(true);
    }

    @Override
    public void setHost( final String host) {
        super.setHost(host);
        this.setChanged(true);
    }

    @Override
    public void setAttributes( final Map<Object, Object> attributes) {
        super.setAttributes(attributes);
        this.setChanged(true);
    }

    @Override
    public void setLastAccessTime( final Date lastAccessTime) {
        if (getLastAccessTime() != null) {
            long last = getLastAccessTime().getTime();
            long now = lastAccessTime.getTime();
            //如果60s内访问 则不更新session,否则需要更新远端过期时间
            if ((last - now) / 1000 >= 60) {
                //发送通知
                //设置为已改变，更新到redis
                this.setChanged(true);
            }
        }
        super.setLastAccessTime(lastAccessTime);
    }


    @Override
    public void setAttribute( final Object key,
                              final Object value) {
        super.setAttribute(key, value);
        this.setChanged(true);
    }

    @Override
    public Object removeAttribute( final Object key) {
        this.setChanged(true);
        return super.removeAttribute(key);
    }

    @Override
    public void stop() {
        super.stop();
        this.setChanged(true);
    }

    @Override
    protected void expire() {
        this.stop();
        this.setExpired(true);
    }


    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(final boolean isChanged) {
        this.isChanged = isChanged;
    }
}
