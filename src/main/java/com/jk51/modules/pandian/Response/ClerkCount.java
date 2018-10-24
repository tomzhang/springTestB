package com.jk51.modules.pandian.Response;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/8/15
 * 修改记录:
 */
public class ClerkCount {

    private String pandianNum;
    private String status;

    private Long total;
    private Long more;
    private Long less;
    private Long same;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getMore() {
        return more;
    }

    public void setMore(Long more) {
        this.more = more;
    }

    public Long getLess() {
        return less;
    }

    public void setLess(Long less) {
        this.less = less;
    }

    public Long getSame() {
        return same;
    }

    public void setSame(Long same) {
        this.same = same;
    }

    public String getPandianNum() {
        return pandianNum;
    }

    public void setPandianNum(String pandianNum) {
        this.pandianNum = pandianNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
