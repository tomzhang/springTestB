package com.jk51.interceptor;

import com.jk51.commons.dto.ReturnDto;
import com.jk51.exception.BusinessLogicException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 异常处理
 */
@RestController
@ControllerAdvice
public class RestExceptionInterceptor {
    @ExceptionHandler(BusinessLogicException.class)
    @ResponseBody
    public ReturnDto businessLogicException(BusinessLogicException e) {
        return ReturnDto.buildFailedReturnDto(e.getMessage());
    }
}
