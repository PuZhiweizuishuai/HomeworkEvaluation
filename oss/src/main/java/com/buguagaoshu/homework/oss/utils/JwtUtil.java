package com.buguagaoshu.homework.oss.utils;

import com.buguagaoshu.homework.oss.config.TokenAuthenticationHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-06 15:47
 * jwt 工具类
 * 用来获取当前登陆用户
 */
public class JwtUtil {
    public static Claims getNowLoginUser(HttpServletRequest request, String key) {
        Cookie cookie = WebUtils.getCookie(request, TokenAuthenticationHelper.COOKIE_TOKEN);
        if (cookie != null) {
            return parseJWT(key, cookie.getValue());
        }
        return null;
    }


    /**
     * @param key 解析的 KEY
     * @param jwtStr 解析的文本
     * */
    public static Claims parseJWT(String key, String jwtStr) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jwtStr)
                .getBody();
    }


}
