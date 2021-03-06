package com.buguagaoshu.homework.common.search;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-31 14:51
 */
public class SearchTableNameConstant {
    public static final String EVENT_TYPE_KEY = "_eventType";

    public static final String TABLE_NAME_KEY ="_tableName";

    public static final String ARTICLE = "article";

    public static final String QUESTIONS = "questions";

    public static final String CURRICULUM = "curriculum";

    public static final String USER = "user";

    private static final String[] tableName = {
            "article",
            "questions",
            "curriculum",
            "user"
    };


    public static boolean notLoginSearch(String index) {
        return CURRICULUM.equals(index) || ARTICLE.equals(index) || USER.equals(index);
    }


    public static boolean hasTable(String name) {
        for (String s : tableName) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
