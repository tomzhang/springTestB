package com.jk51.modules.goods.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/24.
 */
public class GoodsImportResponse {
    private int successNum;

    private List<Map> errorDetail = new ArrayList<>();

    public int getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(int successNum) {
        this.successNum = successNum;
    }

    public List<Map> getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(List<Map> errorDetail) {
        this.errorDetail = errorDetail;
    }

    public void addError(int rownum, String message) {
        Map detail = new HashMap();
        detail.put("rownum", message);
        detail.put("message", message);
        errorDetail.add(detail);
    }
}
