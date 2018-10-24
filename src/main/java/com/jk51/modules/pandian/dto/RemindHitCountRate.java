package com.jk51.modules.pandian.dto;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/7/2
 * 修改记录:
 */
public class RemindHitCountRate {

    private Integer sameNum;
    private Integer notSameNum;
    private Integer notRemindNum;

    public Integer getSameNum() {
        return sameNum;
    }

    public void setSameNum(Integer sameNum) {
        this.sameNum = sameNum;
    }

    public Integer getNotSameNum() {
        return notSameNum;
    }

    public void setNotSameNum(Integer notSameNum) {
        this.notSameNum = notSameNum;
    }

    public Integer getNotRemindNum() {
        return notRemindNum;
    }

    public void setNotRemindNum(Integer notRemindNum) {
        this.notRemindNum = notRemindNum;
    }
}
