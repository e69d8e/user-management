package com.li.interceptor;

import com.li.common.constant.JwtClaimsConstant;
import com.li.common.constant.MessageConstant;
import com.li.common.constant.StatusCodeConstant;
import com.li.common.properties.JwtProperties;
import com.li.common.utils.JwtUtil;
import com.li.common.utils.ThreadLocalUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private ThreadLocalUtil threadLocal;

    public JwtTokenAdminInterceptor(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        threadLocal.remove(); // 清除ThreadLocal中存储的token
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            log.info("拦截到的不是动态方法，直接放行");
            return true;
        }
        log.info("拦截到的动态方法，开始校验token");
        String token = request.getHeader(jwtProperties.getAdminTokenName());
        log.info("token: {}", token);
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            threadLocal.set(token);
            Long adminId = claims.get(JwtClaimsConstant.ADMIN_ID, Long.class);
            String adminName = claims.get(JwtClaimsConstant.ADMIN_NAME, String.class);
            log.info("管理员id: {} 管理员名字: {}", adminId, adminName);
            return true;
        } catch (Exception e) {
            response.setStatus(StatusCodeConstant.ADMIN_UNAUTHORIZED);
            log.error("token校验失败");
            throw new RuntimeException(MessageConstant.ADMIN_NOT_LOGIN);
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }
}
