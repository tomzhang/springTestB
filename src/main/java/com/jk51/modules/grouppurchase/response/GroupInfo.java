package com.jk51.modules.grouppurchase.response;

/**
 * Created by ztq on 2018/1/30
 * Description: 团购中团的状态信息
 */
@SuppressWarnings("unused")
public class GroupInfo {

    /**
     * 是否是团订单
     */
    private boolean isGroup;

    /**
     * 主状态
     * 11:开团成功（未付款）
     * 12:开团成功（付款）
     * 13:参团成功（未付款）
     * 14:参团成功（付款）
     * 21:拼团成功
     * 31:拼团取消（系统）
     * 32:拼团取消（商家）
     * 33:拼团取消（客户申请）
     */
    private Integer mainStatus;

    /**
     * 是否付款
     */
    private boolean isPay;

    /**
     * 是否退款
     */
    private boolean isRefund;

    /**
     * 团状态，表示的团的整体的状态
     * 10: 拼团中
     * 20: 拼团成功
     * 30: 拼团取消
     */
    private Integer groupStatus;

    /**
     * 表示该团信息是由团长订单查询而来还是团员查询而来
     */
    private boolean isHead;

    /**
     * 团长的团id
     */
    private Integer parentId;

    /**
     * 团结束的时间
     */
    private String groupEndDateTime = null;

    /**
     * 拼团成功，尚需几人
     */
    private Integer personNumToSuccess;


    /* -- getter & setter -- */

    public Integer getMainStatus() {
        return mainStatus;
    }

    public void setMainStatus(Integer mainStatus) {
        this.mainStatus = mainStatus;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public boolean isPay() {
        return isPay;
    }

    public void setPay(boolean pay) {
        isPay = pay;
    }

    public boolean isRefund() {
        return isRefund;
    }

    public void setRefund(boolean refund) {
        isRefund = refund;
    }

    public Integer getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(Integer groupStatus) {
        this.groupStatus = groupStatus;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setHead(boolean head) {
        isHead = head;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getGroupEndDateTime() {
        return groupEndDateTime;
    }

    public void setGroupEndDateTime(String groupEndDateTime) {
        this.groupEndDateTime = groupEndDateTime;
    }

    public Integer getPersonNumToSuccess() {
        return personNumToSuccess;
    }

    public void setPersonNumToSuccess(Integer personNumToSuccess) {
        this.personNumToSuccess = personNumToSuccess;
    }
}
