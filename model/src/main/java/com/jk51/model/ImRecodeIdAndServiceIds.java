package com.jk51.model;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-07-14
 * 修改记录:
 */
public class ImRecodeIdAndServiceIds {

    //b_im_recode 主键
    private Integer msg_id;

    //b_im_service 主键
    private Integer imServiceId;

    public Integer getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(Integer msg_id) {
        this.msg_id = msg_id;
    }

    public Integer getImServiceId() {
        return imServiceId;
    }

    public void setImServiceId(Integer imServiceId) {
        this.imServiceId = imServiceId;
    }

    @Override
    public String toString() {
        return "ImRecodeIdAndServiceIds{" +
                "msg_id=" + msg_id +
                ", imServiceId=" + imServiceId +
                '}';
    }
}
