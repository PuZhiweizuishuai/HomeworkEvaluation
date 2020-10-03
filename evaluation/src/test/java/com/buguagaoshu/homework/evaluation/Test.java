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
        int x = "/api/uploads/courseware/9/201741010102/2020-10-03/82f033c6abd04af38c65a8049b08850e.jpg".lastIndexOf("/");
        System.out.println("/api/uploads/courseware/9/201741010102/2020-10-03/82f033c6abd04af38c65a8049b08850e.jpg".substring(x+1));

    }
}
