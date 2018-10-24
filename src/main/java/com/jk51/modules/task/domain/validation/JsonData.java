package com.jk51.modules.task.domain.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = JsonDataValidation.class)
@Documented
@Target( { ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonData {
    String message () default "值是非法的json值";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
