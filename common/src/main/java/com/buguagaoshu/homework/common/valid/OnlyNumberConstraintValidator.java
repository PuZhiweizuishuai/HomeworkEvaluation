package com.buguagaoshu.homework.common.valid;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-09-29 18:49
 * 所提交的值都必须是数字
 */
public class OnlyNumberConstraintValidator implements ConstraintValidator<OnlyNumber, String> {

    private int max;

    @Override
    public void initialize(OnlyNumber constraintAnnotation) {
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isEmpty(s)) {
            return false;
        }
        int length = s.length();
        if (length > max) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            try {
                Integer.parseInt(String.valueOf(s.charAt(i)));
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}
