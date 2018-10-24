package com.jk51.modules.task.domain;

/**
 * 任务状态
 */
public enum BTaskStatus {
    // 未开始
    NONSTART((byte)10),
    // 进行中
    STARTING((byte)20),
    // 已结束
    STOP((byte)30),
    // 软删除
    SOFT_DELETE((byte)40);

    private byte value;

    BTaskStatus(byte i) {
        value = i;
    }

    public byte getValue() {
        return value;
    }
}
