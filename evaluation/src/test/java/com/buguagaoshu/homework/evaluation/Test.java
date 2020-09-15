package com.buguagaoshu.homework.evaluation;

import com.buguagaoshu.homework.evaluation.utils.InviteCodeUtil;
import com.buguagaoshu.homework.evaluation.utils.TimeUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-03 22:27
 */
public class Test {
    public static void main(String[] args) throws ParseException {
        List<String> list = new ArrayList<>();
        update(list);
        System.out.println(list);
    }

    public static void update(List<String> list) {
        list = new ArrayList<>();
        list.add("123");
        list.add("456");
    }
}
