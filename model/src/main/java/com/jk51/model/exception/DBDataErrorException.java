package com.jk51.model.exception;

/**
 * <br/><br/>
 * 版权所有(C) 2017 上海伍壹健康科技有限公司            <br/>
 * 作者: zhutianqiong                               <br/>
 * 创建日期: 2017/10/26                                <br/>
 * 修改记录:                                         <br/>
 */
public class DBDataErrorException extends Exception {
    private static final long serialVersionUID = -1L;

    public DBDataErrorException() {
    }

    public DBDataErrorException(String message) {
        super(message);
    }

    public DBDataErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBDataErrorException(Throwable cause) {
        super(cause);
    }
}
