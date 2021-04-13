package com.buguagaoshu.homework.evaluation.filter;

import com.buguagaoshu.homework.common.domain.ResponseDetails;
import com.buguagaoshu.homework.evaluation.config.TokenAuthenticationHelper;
import com.buguagaoshu.homework.evaluation.config.WebConstant;
import com.buguagaoshu.homework.evaluation.model.User;
import com.buguagaoshu.homework.evaluation.service.UserLoginLogService;
import com.buguagaoshu.homework.evaluation.service.VerifyCodeService;
import com.buguagaoshu.homework.evaluation.vo.LoginDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Pu Zhiwei {@literal puzhiweipuzhiwei@foxmail.com}
 * create          2019-11-25 21:57
 * JWT 登陆过滤器
 */
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final UserLoginLogService userLoginLogService;

    private final VerifyCodeService verifyCodeService;

    /**
     * @param defaultFilterProcessesUrl 配置要过滤的地址，即登陆地址
     * @param authenticationManager 认证管理器，校验身份时会用到
     * @param userLoginLogService 保存用户登录日志
     *
     * */
    public JwtLoginFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager, UserLoginLogService userLoginLogService, VerifyCodeService verifyCodeService) {
        super(new AntPathRequestMatcher(defaultFilterProcessesUrl));
        // 为 AbstractAuthenticationProcessingFilter 中的属性赋值
        setAuthenticationManager(authenticationManager);
        this.userLoginLogService = userLoginLogService;
        this.verifyCodeService = verifyCodeService;
    }



    /**
     * 提取用户账号密码进行验证
     * */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        // 获取 User 对象
        // readValue 第一个参数 输入流，第二个参数 要转换的对象
        User user = new ObjectMapper().readValue(httpServletRequest.getInputStream(), User.class);
        // 验证码验证
        verifyCodeService.verify(WebConstant.VERIFY_CODE_KEY, user.getVerifyCode(), httpServletRequest.getSession());
        // 对 html 标签进行转义，防止 XSS 攻击
        String userId = user.getUserId();
        userId = HtmlUtils.htmlEscape(userId);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userId,
                user.getPassword(),
                user.getAuthorities()
        );
        // 添加验证的附加信息
        // 包括验证码信息和是否记住我
        token.setDetails(new LoginDetails(user.getRememberMe(), user.getVerifyCode()));
        // 进行登陆验证
        return getAuthenticationManager().authenticate(token);
    }

    /**
     * 登陆成功回调
     * */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // 登陆成功
        // TODO 限制登陆错误次数

        // 写入登录日志
        User user = (User) authResult.getPrincipal();
        userLoginLogService.saveUserLoginLog(request, user.getUserId());
        TokenAuthenticationHelper.addAuthentication(request, response ,authResult);
    }

    /**
     * 登陆失败回调
     * */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        // 错误请求次数加 1
        // 向前端写入数据
        ResponseDetails responseDetails = ResponseDetails.ok(HttpStatus.UNAUTHORIZED.value(), failed.getLocalizedMessage());
        responseDetails.put("error", failed.getLocalizedMessage());
        responseDetails.put("path", request.getServletPath());

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(responseDetails));
        out.flush();
        out.close();
    }
}
