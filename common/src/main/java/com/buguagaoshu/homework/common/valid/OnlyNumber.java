package com.buguagaoshu.homework.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-29 18:48
 * 只能是数字
 * 负责对学号进行校验
 */
@Documented
@Constraint(
        validatedBy = {OnlyNumberConstraintValidator.class}
)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OnlyNumber {
    String message() default "{com.buguagaoshu.homework.common.OnlyNumber.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int max() default 20;
}
