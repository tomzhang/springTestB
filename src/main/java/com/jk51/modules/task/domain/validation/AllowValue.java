package com.jk51.modules.task.domain.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = AllowValueValidation.class)
@Documented
@Target( { ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowValue {
    byte[] value() default {};
    String message () default "值不正确";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
