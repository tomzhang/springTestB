package com.jk51.modules.schedule;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 任务执行状态，分为运行
 * 作者: wangzhengfei
 * 创建日期: 2017-02-21
 * 修改记录:
 */
public enum ExecuteStatus {

    RUNNING(1),

    FINISHED(0);

    private Integer status;

    ExecuteStatus(Integer status) {
        this.status = status;
    }

    public Integer getValue(){
        return status;
    }
}
