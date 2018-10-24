package com.jk51.model.distribute;

import java.sql.Timestamp;

/**
 * Created by Administrator on 2017/2/9.
 */
public class OperationRecond {

    private int d_id;
    private int id;
    private int autding_status;
    private String remark;
    private String snapshot;
    private Timestamp update_time;

    public int getD_id() {
        return d_id;
    }

    public void setD_id(int d_id) {
        this.d_id = d_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAutding_status() {
        return autding_status;
    }

    public void setAutding_status(int autding_status) {
        this.autding_status = autding_status;
    }
    
    public String getRemark(){return this.remark;}
    public void setRemark(String remark){this.remark = remark;}
    
    public String getSnapshot(){return this.snapshot;}
    public void setSnapshot(String snapshot){this.snapshot = snapshot;}

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }
}
