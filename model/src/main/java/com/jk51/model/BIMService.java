package com.jk51.model;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:聊天异常完整的服务记录表
 * 作者: gaojie
 * 创建日期: 2017-04-13
 * 修改记录:
 */
public class BIMService {

    private Integer id;
    private String sender;
    private String receiver;
    private Date first_reply_time; //店员首次答复时间
    private Date start_time; //会员聊天发起时间
    private Date race_time; //店员首次答复时间
    private Date im_end_time; //聊天结束时间
    private Date create_time;
    private Date update_time;
    private Integer im_end_type; //聊天结束类型 1=会员评价结束 2=店员超时 3=会员超时
    private Integer evaluate; //会员评价 1=满意 2=一般 3=不满意
    private Integer race_status; //是否有抢答 1=有 0=无
    private Integer im_type; //'1为文字咨询，2为一键呼叫',
    private Integer site_id;
    private Integer store_id;


    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Integer getStore_id() {
        return store_id;
    }

    public void setStore_id(Integer store_id) {
        this.store_id = store_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Date getFirst_reply_time() {
        return first_reply_time;
    }

    public void setFirst_reply_time(Date first_reply_time) {
        this.first_reply_time = first_reply_time;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getRace_time() {
        return race_time;
    }

    public void setRace_time(Date race_time) {
        this.race_time = race_time;
    }

    public Date getIm_end_time() {
        return im_end_time;
    }

    public void setIm_end_time(Date im_end_time) {
        this.im_end_time = im_end_time;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Integer getIm_end_type() {
        return im_end_type;
    }

    public void setIm_end_type(Integer im_end_type) {
        this.im_end_type = im_end_type;
    }

    public Integer getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(Integer evaluate) {
        this.evaluate = evaluate;
    }

    public Integer getRace_status() {
        return race_status;
    }

    public void setRace_status(Integer race_status) {
        this.race_status = race_status;
    }


    public Integer getIm_type() {
        return im_type;
    }

    public void setIm_type(Integer im_type) {
        this.im_type = im_type;
    }
}
