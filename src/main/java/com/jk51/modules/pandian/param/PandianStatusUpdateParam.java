package com.jk51.modules.pandian.param;

import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-11-03
 * 修改记录:
 */
public class PandianStatusUpdateParam {

    private List<StatusParam> statusParams;
    private Integer to_status;
    private Integer operateType;// 1.商家admin操作、2.商家manager操作、3.门店店员操作

    public List<StatusParam> getStatusParams() {
        return statusParams;
    }

    public void setStatusParams(List<StatusParam> statusParams) {
        this.statusParams = statusParams;
    }

    public Integer getTo_status() {
        return to_status;
    }

    public void setTo_status(Integer to_status) {
        this.to_status = to_status;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }
}
