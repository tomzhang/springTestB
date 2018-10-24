package com.jk51.modules.task.domain.validation;

import org.apache.commons.lang.ArrayUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class AllowValueValidation implements ConstraintValidator<AllowValue, Byte> {
    private byte[] allowValue;

    @Override
    public void initialize(AllowValue constraintAnnotation) {
        allowValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Byte value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            return true;
        }

        if (ArrayUtils.contains(allowValue, value)) {
            return true;
        }

        return false;
    }
}
