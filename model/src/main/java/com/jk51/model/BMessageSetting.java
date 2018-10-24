package com.jk51.model;

import java.util.Date;

public class BMessageSetting {
    private Integer id;

    private Integer siteId;

    private String messageType;

    private String messageTitle;

    private String messageIcon;

    private String messageSummary;

    private String messageContent;

    private String messageWhereabouts;

    private String notificationTitle;

    private String notificationText;

    private String notificationLogo;

    private String notificationLogoUrl;

    private Integer notificationRing;

    private Integer notificationVibrate;

    private Integer notificationClearable;

    private Integer mandatoryReminder;

    private Date createTime;

    private Date updateTime;

    private String ext;

    private Integer offLine;

    private Integer wifi;

    private String sound;

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

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType == null ? null : messageType.trim();
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle == null ? null : messageTitle.trim();
    }

    public String getMessageIcon() {
        return messageIcon;
    }

    public void setMessageIcon(String messageIcon) {
        this.messageIcon = messageIcon == null ? null : messageIcon.trim();
    }

    public String getMessageSummary() {
        return messageSummary;
    }

    public void setMessageSummary(String messageSummary) {
        this.messageSummary = messageSummary == null ? null : messageSummary.trim();
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent == null ? null : messageContent.trim();
    }

    public String getMessageWhereabouts() {
        return messageWhereabouts;
    }

    public void setMessageWhereabouts(String messageWhereabouts) {
        this.messageWhereabouts = messageWhereabouts == null ? null : messageWhereabouts.trim();
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle == null ? null : notificationTitle.trim();
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText == null ? null : notificationText.trim();
    }

    public String getNotificationLogo() {
        return notificationLogo;
    }

    public void setNotificationLogo(String notificationLogo) {
        this.notificationLogo = notificationLogo == null ? null : notificationLogo.trim();
    }

    public String getNotificationLogoUrl() {
        return notificationLogoUrl;
    }

    public void setNotificationLogoUrl(String notificationLogoUrl) {
        this.notificationLogoUrl = notificationLogoUrl == null ? null : notificationLogoUrl.trim();
    }

    public Integer getNotificationRing() {
        return notificationRing;
    }

    public void setNotificationRing(Integer notificationRing) {
        this.notificationRing = notificationRing;
    }

    public Integer getNotificationVibrate() {
        return notificationVibrate;
    }

    public void setNotificationVibrate(Integer notificationVibrate) {
        this.notificationVibrate = notificationVibrate;
    }

    public Integer getNotificationClearable() {
        return notificationClearable;
    }

    public void setNotificationClearable(Integer notificationClearable) {
        this.notificationClearable = notificationClearable;
    }

    public Integer getMandatoryReminder() {
        return mandatoryReminder;
    }

    public void setMandatoryReminder(Integer mandatoryReminder) {
        this.mandatoryReminder = mandatoryReminder;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext == null ? null : ext.trim();
    }

    public Integer getOffLine() {
        return offLine;
    }

    public void setOffLine(Integer offLine) {
        this.offLine = offLine;
    }

    public Integer getWifi() {
        return wifi;
    }

    public void setWifi(Integer wifi) {
        this.wifi = wifi;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    @Override
    public String toString() {
        return "BMessageSetting{" +
                "id=" + id +
                ", siteId=" + siteId +
                ", messageType='" + messageType + '\'' +
                ", messageTitle='" + messageTitle + '\'' +
                ", messageIcon='" + messageIcon + '\'' +
                ", messageSummary='" + messageSummary + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", messageWhereabouts='" + messageWhereabouts + '\'' +
                ", notificationTitle='" + notificationTitle + '\'' +
                ", notificationText='" + notificationText + '\'' +
                ", notificationLogo='" + notificationLogo + '\'' +
                ", notificationLogoUrl='" + notificationLogoUrl + '\'' +
                ", notificationRing=" + notificationRing +
                ", notificationVibrate=" + notificationVibrate +
                ", notificationClearable=" + notificationClearable +
                ", mandatoryReminder=" + mandatoryReminder +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", ext='" + ext + '\'' +
                ", offLine=" + offLine +
                ", wifi=" + wifi +
                ", sound='" + sound + '\'' +
                '}';
    }
}