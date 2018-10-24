package com.jk51.modules.schedule;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 执行结果状态，成功失败
 * 作者: wangzhengfei
 * 创建日期: 2017-02-22
 * 修改记录:
 */
public enum ExecuteResultStatus {

    /**
     * 未知
     */
    UNKNOWN(0),

    /**
     * 成功
     */
    SUCCESS(1),

    /**
     * 失败
     */
    FAIL(2);

    private Integer status;

    ExecuteResultStatus(Integer status) {
        this.status = status;
    }

    public Integer getValue(){
        return status;
    }
}
