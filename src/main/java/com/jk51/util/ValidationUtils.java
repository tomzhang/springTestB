package com.jk51.util;

import com.jk51.exception.BusinessLogicException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class ValidationUtils {
    public static void check(BindingResult bindingResult) throws BusinessLogicException {
        if (bindingResult.hasErrors()) {
            FieldError fe = bindingResult.getFieldError();
            String err = fe.getDefaultMessage();

            throw new BusinessLogicException(err);
        }
    }

    public static <T> void validate(T object, Class<?>... groups) throws BusinessLogicException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> validate = validator.validate(object, groups);
        if (validate.size() > 0) {
            throw new BusinessLogicException(validate.iterator().next().getMessage());
        }
    }
}
