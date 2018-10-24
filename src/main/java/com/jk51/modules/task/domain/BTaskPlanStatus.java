package com.jk51.modules.task.domain;

/**
 * 任务状态
 */
public enum BTaskPlanStatus {
    // 未开始
    NONSTART((byte)10),
    // 进行中
    STARTING((byte)20),
    // 已结束
    STOP((byte)30),
    // 删除
    DELETE((byte)40);

    private Byte value;

    BTaskPlanStatus(Byte i) {
        value = i;
    }

    public Byte getValue() {
        return value;
    }
}
