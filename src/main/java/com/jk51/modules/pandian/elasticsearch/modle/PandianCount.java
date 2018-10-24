package com.jk51.modules.pandian.elasticsearch.modle;




import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2018/6/6
 * 修改记录:
 */

public class PandianCount {

    private List<StoreCount> storeCountList;
    private String pandianNum;

    public List<StoreCount> getStoreCountList() {
        return storeCountList;
    }

    public void setStoreCountList(List<StoreCount> storeCountList) {
        this.storeCountList = storeCountList;
    }

    public String getPandianNum() {
        return pandianNum;
    }

    public void setPandianNum(String pandianNum) {
        this.pandianNum = pandianNum;
    }
}
