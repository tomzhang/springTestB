package com.jk51.model.coupon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by javen73 on 2018/4/18.
 */
public class ResultDto {
    private static Logger logger = LoggerFactory.getLogger(ResultDto.class);
    private Boolean result;
    private String message;

    public static ResultDto buildResultDto(Boolean result, String message){
        return new ResultDto(result,message);
    }
    public static ResultDto buildResultDto(Boolean result){
        return new ResultDto(result,null);
    }

    public ResultDto(Boolean result, String message) {
        this.result = result;
        this.message = message;
        if(!result){
            logger.error("错误信息:{}",message);
        }
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
