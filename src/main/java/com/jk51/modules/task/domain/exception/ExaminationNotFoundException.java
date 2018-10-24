package com.jk51.modules.task.domain.exception;

/**
 * 试卷不存在
 */
public class ExaminationNotFoundException extends RuntimeException {

    public ExaminationNotFoundException(Integer id) {
        super("试卷" + id +"不存在");
    }
}
