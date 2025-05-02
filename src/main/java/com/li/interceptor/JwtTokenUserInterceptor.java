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

@Slf4j
@Component
public class JwtTokenUserInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private ThreadLocalUtil threadLocal;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            log.info("拦截到的不是动态方法，直接放行");
            return true;
        }
        log.info("拦截到的动态方法，开始校验token");
        String token = request.getHeader(jwtProperties.getUserTokenName());
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long id = claims.get(JwtClaimsConstant.USER_ID, Long.class);
            String name = claims.get(JwtClaimsConstant.USER_NAME, String.class);
            log.info("用户id: {} 用户名字: {}", id, name);
            threadLocal.set(token); // 将token放入ThreadLocal中
            return true;
        } catch (Exception e) {
            response.setStatus(StatusCodeConstant.USER_UNAUTHORIZED);
            log.error("token校验失败");
            throw new RuntimeException(MessageConstant.USER_NOT_LOGIN);
        }
    }

//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        return HandlerInterceptor.super.preHandle(request, response, handler);
//    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        threadLocal.remove(); // 清除ThreadLocal中存储的token
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
