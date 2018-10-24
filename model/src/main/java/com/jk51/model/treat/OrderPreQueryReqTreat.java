package com.jk51.model.treat;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: 预约订单(商品)查询的请求参数
 * 作者: baixiongfei
 * 创建日期: 2017/3/11
 * 修改记录:
 */
public class OrderPreQueryReqTreat {

    private String siteId;

    private String mobile;

    private String goodsName;

    private Integer pageNum=1;//当前第几页，默认为第一页

    private Integer pageSize=10;//默认每页多少条数据，默认每页10条

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "OrderPreQueryReq{" +
            "siteId='" + siteId + '\'' +
            ", mobile='" + mobile + '\'' +
            ", goodsName='" + goodsName + '\'' +
            ", pageNum=" + pageNum +
            ", pageSize=" + pageSize +
            '}';
    }
}
