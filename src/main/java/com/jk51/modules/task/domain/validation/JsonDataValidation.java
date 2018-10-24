package com.jk51.modules.task.domain.validation;

import com.alibaba.fastjson.JSONObject;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class JsonDataValidation implements ConstraintValidator<JsonData, String> {

    @Override
    public void initialize(JsonData constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            if (Objects.isNull(value)) {
                return true;
            }
            Object o = JSONObject.parse(value);
            Objects.requireNonNull(o);

            return true;
        } catch (Exception e) {}

        return false;
    }
}
