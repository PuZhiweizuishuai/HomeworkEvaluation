package com.buguagaoshu.homework.oss.filter;

import com.buguagaoshu.homework.oss.config.TokenAuthenticationHelper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2021-03-10 17:41
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication authentication = TokenAuthenticationHelper.getAuthentication(httpServletRequest);
            // 对用 token 获取到的用户进行校验
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException |
                SignatureException | IllegalArgumentException e) {
            // 清除过期的 TOKEN
            Cookie cookie = WebUtils.getCookie(httpServletRequest, TokenAuthenticationHelper.COOKIE_TOKEN);
            if (cookie != null) {
                cookie.setValue(null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                httpServletResponse.addCookie(cookie);
            }
            // 传递 登录已过期信息
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired，登陆已过期");
        }
    }
}
