package com.buguagaoshu.homework.evaluation;

import com.buguagaoshu.homework.evaluation.utils.JwtUtil;

import java.text.ParseException;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-03 22:27
 */
public class Test {
    public static void main(String[] args) throws ParseException {
        String url = "/api/uploads/courseware/9/201741010102/2021-03-03/a9754b8016fc4130975315be0bd3fe5f.mp4";
        int n = url.lastIndexOf("/");
        System.out.println(url.substring(n+1));

        System.out.println(url.substring(5, url.lastIndexOf("/")));
    }
}
