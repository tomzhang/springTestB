package com.jk51.model.promotions.activity;

import com.jk51.model.promotions.PromotionsActivity;

/**
 * {@link  PromotionsActivity}中的showRule字段对应的实体类
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/8/10                                <br/>
 * 修改记录:                                         <br/>
 */
public class ShowRule {
    /**
     * 是否显示
     * 0 代表 不显示
     * 1 代表 显示
     */
    private Integer isShow;

    /**
     * 活动类型
     * 0 代表 普通活动，不强制提醒
     * 1 代表 主题活动，强制弹窗提醒
     */
    private Integer forcePopup;

    /**
     * 是否是从主题转变成普通活动的表示
     * 0 代表否
     * 1 代表是
     */
    private Integer isTransformFromThemaActivity;

    /**
     * 是否在打开商城首页时弹窗
     * 0 代表 不弹窗
     * 1 代表 活动期间只提醒一次
     * 2 代表 每天（间隔24消失）提醒一次
     */
    private Integer popupAtHomePage;

    /**
     * 是否在登录之后弹窗
     * 0 代表 不弹窗
     * 1 代表 活动期间只提醒一次
     * 2 代表 每天（间隔24消失）提醒一次
     */
    private Integer popupWhenLogin;

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    public Integer getForcePopup() {
        return forcePopup;
    }

    public void setForcePopup(Integer forcePopup) {
        this.forcePopup = forcePopup;
    }

    public Integer getPopupAtHomePage() {
        return popupAtHomePage;
    }

    public void setPopupAtHomePage(Integer popupAtHomePage) {
        this.popupAtHomePage = popupAtHomePage;
    }

    public Integer getPopupWhenLogin() {
        return popupWhenLogin;
    }

    public void setPopupWhenLogin(Integer popupWhenLogin) {
        this.popupWhenLogin = popupWhenLogin;
    }

    public Integer getIsTransformFromThemaActivity() {
        return isTransformFromThemaActivity;
    }

    public void setIsTransformFromThemaActivity(Integer isTransformFromThemaActivity) {
        this.isTransformFromThemaActivity = isTransformFromThemaActivity;
    }

    @Override
    public String toString() {
        return "ShowRule{" +
                "isShow=" + isShow +
                ", forcePopup=" + forcePopup +
                ", isTransformFromThemaActivity=" + isTransformFromThemaActivity +
                ", popupAtHomePage=" + popupAtHomePage +
                ", popupWhenLogin=" + popupWhenLogin +
                '}';
    }
}
