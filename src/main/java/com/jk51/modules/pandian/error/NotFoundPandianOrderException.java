package com.jk51.modules.pandian.error;

/**
 * 版权所有(C) 2017 上海伍壹健康科技有限公司
 * 描述:
 * 作者: gaojie
 * 创建日期: 2017-10-27
 * 修改记录:
 */
public class NotFoundPandianOrderException extends RuntimeException {

    public NotFoundPandianOrderException(String message){
        super(message);
    }
}
