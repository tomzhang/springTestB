package com.jk51.commons.ccprest.result;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: chen
 * 创建日期: 2017-02-20
 * 修改记录:
 */
public class BaseResult {

    private Integer code;
    private String msg;

    public BaseResult(){};

    public BaseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static BaseResult success(){
        return new BaseResult(BaseResultEnum.SUCCESS.getCode(),BaseResultEnum.SUCCESS.getMsg());
    }
    public static BaseResult success(String msg){
        return new BaseResult(BaseResultEnum.SUCCESS.getCode(),msg);
    }
    public static BaseResult failed(){
        return new BaseResult(BaseResultEnum.FAILED.getCode(),BaseResultEnum.FAILED.getMsg());
    }
    public static BaseResult failed(String msg){
        return new BaseResult(BaseResultEnum.FAILED.getCode(),BaseResultEnum.FAILED.getMsg());
    }

    public Integer getCode(){
        return code;
    }
    @Override
    public String toString() {
        return "Message{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }


}
