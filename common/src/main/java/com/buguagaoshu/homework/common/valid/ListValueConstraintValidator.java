package com.buguagaoshu.homework.common.valid;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-04-09 20:19
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {
    private Set<Integer> set = new HashSet<>();

    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] values = constraintAnnotation.value();
        for(int i : values) {
            set.add(i);
        }
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return set.contains(integer);
    }
}
