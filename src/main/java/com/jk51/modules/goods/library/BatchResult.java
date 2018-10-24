package com.jk51.modules.goods.library;

import com.jk51.commons.string.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * batch add/update
 */
public class BatchResult {
    private Integer successNum;

    private Integer failNum;

    private Integer marryFailNum;

    private Integer marrySuccessNum;

    private Integer repeatMarryNum;

    private List<Map<String, String>> errorList = new ArrayList();

    public Integer getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(Integer successNum) {
        this.successNum = successNum;
    }

    public Integer getFailNum() {
        return failNum;
    }

    public List<Map<String, String>> getErrorList() {
        return errorList;
    }

    public void setFailNum(Integer failNum) {
        this.failNum = failNum;
    }

    public void addError(String key, Object value, String errorMsg) {
        Map error = new HashMap();
        error.put(key, value);

        String name = "";
        if (!StringUtil.isEmpty(errorMsg)) {
            int index = errorMsg.indexOf(":");
            if (index != -1) {
                name = errorMsg.substring(0, index);
                errorMsg = errorMsg.substring(index + 1, errorMsg.length());
            }
        }
        error.put("reason", errorMsg);
        error.put("name", name);
        error.put("hard_msg", errorMsg);
//        String format = "商品编号%d操作失败,%s";

//        String error = String.format(format, goodsId, errorMsg);

        errorList.add(error);
    }

    public void setErrorList(List<Map<String, String>> errorList) {
        this.errorList = errorList;
    }

    public void addError(int goodsId, String errorMsg) {
        addError("goods_id", String.valueOf(goodsId), errorMsg);
    }

    public Integer getMarryFailNum() {
        return marryFailNum;
    }

    public void setMarryFailNum(Integer marryFailNum) {
        this.marryFailNum = marryFailNum;
    }

    public Integer getRepeatMarryNum() {
        return repeatMarryNum;
    }

    public void setRepeatMarryNum(Integer repeatMarryNum) {
        this.repeatMarryNum = repeatMarryNum;
    }

    public Integer getMarrySuccessNum() {
        return marrySuccessNum;
    }

    public void setMarrySuccessNum(Integer marrySuccessNum) {
        this.marrySuccessNum = marrySuccessNum;
    }
}
