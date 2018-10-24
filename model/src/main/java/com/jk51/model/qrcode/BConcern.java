package com.jk51.model.qrcode;


import java.security.Timestamp;
import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: ChengShanyunduo
 * 创建日期: 2017-06-13
 * 修改记录:
 */
public class BConcern {

    private Integer id;
    private Integer siteId;
    private Integer typePk;
    private Integer type;
    private String openId;
    private String sceneStr;
    private Long createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public Integer getTypePk() {
        return typePk;
    }

    public void setTypePk(Integer typePk) {
        this.typePk = typePk;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getSceneStr() {
        return sceneStr;
    }

    public void setSceneStr(String sceneStr) {
        this.sceneStr = sceneStr;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
