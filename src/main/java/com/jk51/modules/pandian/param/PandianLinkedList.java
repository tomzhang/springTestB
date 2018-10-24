package com.jk51.modules.pandian.param;

import java.util.Date;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/4/26
 * 修改记录:
 */
public class PandianLinkedList {

    private Integer id;
    private Integer siteId;
    private Integer storeId;
    private String goodsCode;

    //前一个盘点的商品的pandianOrderId
    private Integer pre;

    //下一个盘点的商品的pandianOrderId
    private Integer next;

    private Date createTime;
    private Date updateTime;
}
