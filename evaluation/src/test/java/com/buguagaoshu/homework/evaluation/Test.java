package com.buguagaoshu.homework.evaluation;

import com.buguagaoshu.homework.evaluation.utils.JwtUtil;

import java.text.ParseException;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-03 22:27
 */
public class Test {
    public static void main(String[] args) throws ParseException {
        String s = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiLokrLoh7TlqIEiLCJqdGkiOiIyMDE3NDEwMTAxMDIiLCJhdXRob3JpdGllcyI6IlJPTEVfQURNSU4sIiwiZXhwIjoxNjE1OTgzMzg2fQ.Oy7kX6ccrnWi5MrMYl9hmv5EIZEEdMZydK2u_1xz12TgGnGqX8L7m9GA2gYmZ0nRyl_NIlA0TpPHpqcH8js0ww";

        String st = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiLokrLoh7TlqIEiLCJqdGkiOiIyMDE3NDEwMTAxMDIiLCJhdXRob3JpdGllcyI6IlJPTEVfQURNSU4sIiwiZXhwIjoxNjE1OTgzMzg2fQ.Oy7kX6ccrnWi5MrMYl9hmv5EIZEEdMZydK2u_1xz12TgGnGqX8L7m9GA2gYmZ0nRyl_NIlA0TpPHpqcH8js0ww";
        System.out.println(JwtUtil.parseJWT(st, "TEST_KEY"));
    }
}
