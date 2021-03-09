package com.buguagaoshu.homework.evaluation.config;

import com.buguagaoshu.homework.evaluation.filter.JwtAuthenticationFilter;
import com.buguagaoshu.homework.evaluation.filter.JwtLoginFilter;
import com.buguagaoshu.homework.evaluation.service.UserLoginLogService;
import com.buguagaoshu.homework.evaluation.service.UserRoleService;
import com.buguagaoshu.homework.evaluation.service.UserService;
import com.buguagaoshu.homework.evaluation.service.VerifyCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2020-06-04 13:21
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private UserService userService;

    private UserRoleService userRoleService;

    private UserLoginLogService userLoginLogService;

    private VerifyCodeService verifyCodeService;

    @Autowired
    public void setVerifyCodeService(VerifyCodeService verifyCodeService) {
        this.verifyCodeService = verifyCodeService;
    }

    @Autowired
    public void setUserLoginLogService(UserLoginLogService userLoginLogService) {
        this.userLoginLogService = userLoginLogService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setUserRoleService(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    /**
     * 开放访问的请求
     */
    private final static String[] PERMIT_ALL_MAPPING = {
//            "/index/**",
//            "/verifyImage",
//            "/course/tag/list",
//            "/curriculum/list",
//            "/curriculum/info/**",
//            "/index/**",
//            "/uploads/file/**",
//            "/logout"
            "/**"
    };



    /**
     * 跨域配置
     * "4291d7da9005377ec9aec4a71ea837f"
     * Access-Token
     * http://127.0.0.1:8000/list/user-list
     */
    //@Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // 允许跨域访问的 URL
        List<String> allowedOriginsUrl = new ArrayList<>();
//        allowedOriginsUrl.add("http://localhost:9527");
//        allowedOriginsUrl.add("http://127.0.0.1:9527");
        allowedOriginsUrl.add("*");
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // 设置允许跨域访问的 URL
        config.setAllowedOrigins(allowedOriginsUrl);
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(PERMIT_ALL_MAPPING)
                .permitAll()
                // 需要管理员权限才可以访问的地址
                .and()
                // 添加过滤器链,前一个参数过滤器， 后一个参数过滤器添加的地方
                // 登陆过滤器
                .addFilterBefore(
                        new JwtLoginFilter("/login",
                                authenticationManager(),
                                userLoginLogService,
                                verifyCodeService
                        ),
                        UsernamePasswordAuthenticationFilter.class
                )
                // 请求过滤器
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                // 开启跨域
                .cors()
                .and()
                // 开启 csrf 验证
                .csrf()
                .ignoringAntMatchers(
                        "/index/**",
                        "/verifyImage",
                        "/register",
                        "/course/tag/list",
                        "/curriculum/list",
                        "/curriculum/info/**",
                        "/upload/file",
                        "/upload/courseware",
                        "/service/danmakus/check"
                )
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .headers()
                .frameOptions()
                // 设置同域名下页面可嵌套
                .sameOrigin();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .authenticationProvider(daoAuthenticationProvider());
    }


    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setHideUserNotFoundExceptions(false);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        provider.setUserDetailsService(new CustomUserDetailsService(userService, userRoleService, new BCryptPasswordEncoder()));
        return provider;
    }


}
