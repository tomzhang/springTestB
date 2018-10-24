package com.jk51.model.goods;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 商品同步信息表
 * 作者: aaron（zhangchenchen）
 * 创建日期: 2017-07-25 19:59
 * 修改记录:
 */
public class BGoodsSynchroLog {

    private Integer id;
    private Integer site_id;
    private Integer goods_id;
    private Integer yb_goods_id;
    private Integer operate_type;
    private Integer operate_status;
    private Integer synchro_type;
    private Integer synchro_status;
    private Date create_time;
    private Date update_time;

    public BGoodsSynchroLog() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSite_id() {
        return site_id;
    }

    public void setSite_id(Integer site_id) {
        this.site_id = site_id;
    }

    public Integer getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(Integer goods_id) {
        this.goods_id = goods_id;
    }

    public Integer getYb_goods_id() {
        return yb_goods_id;
    }

    public void setYb_goods_id(Integer yb_goods_id) {
        this.yb_goods_id = yb_goods_id;
    }

    public Integer getOperate_type() {
        return operate_type;
    }

    public void setOperate_type(Integer operate_type) {
        this.operate_type = operate_type;
    }

    public Integer getOperate_status() {
        return operate_status;
    }

    public void setOperate_status(Integer operate_status) {
        this.operate_status = operate_status;
    }

    public Integer getSynchro_type() {
        return synchro_type;
    }

    public void setSynchro_type(Integer synchro_type) {
        this.synchro_type = synchro_type;
    }

    public Integer getSynchro_status() {
        return synchro_status;
    }

    public void setSynchro_status(Integer synchro_status) {
        this.synchro_status = synchro_status;
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
}
