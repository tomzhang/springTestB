package com.jk51.model.order.response;

import java.util.ArrayList;
import java.util.List;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述: ERP库存查询接口相应数据体
 * 作者: wangzhengfei
 * 创建日期: 2017-03-26
 * 修改记录:
 */
public class StorageResponse {

    public static final String SUCCESS_CODE = "1";

    public static final String ERROR_CODE = "0";


    private String code;

    private String msg;

    private List<StorageInfo> info = new ArrayList<>();

    public class StorageInfo{

        private String UID;

        private String GOODSNO;

        private String SPEC;

        private String STATE;

        private String kcqty;

        public String getUID() {
            return UID;
        }

        public void setUID(String UID) {
            this.UID = UID;
        }

        public String getGOODSNO() {
            return GOODSNO;
        }

        public void setGOODSNO(String GOODSNO) {
            this.GOODSNO = GOODSNO;
        }

        public String getSPEC() {
            return SPEC;
        }

        public void setSPEC(String SPEC) {
            this.SPEC = SPEC;
        }

        public String getSTATE() {
            return STATE;
        }

        public void setSTATE(String STATE) {
            this.STATE = STATE;
        }

        public String getKcqty() {
            return kcqty;
        }

        public void setKcqty(String kcqty) {
            this.kcqty = kcqty;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<StorageInfo> getInfo() {
        return info;
    }

    public void setInfo(List<StorageInfo> info) {
        this.info = info;
    }
}
