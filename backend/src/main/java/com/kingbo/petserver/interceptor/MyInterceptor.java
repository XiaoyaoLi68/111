package com.kingbo.petserver.interceptor;

import com.kingbo.myjwtutilsstarter.MyJwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class MyInterceptor implements HandlerInterceptor {

    @Resource
    private MyJwtUtils myJwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (request.getMethod().toUpperCase().equals("OPTIONS")) {
            return true;
        }
        // 从请求头里拿 Token
        String token = request.getHeader("token"); // 前端要把 token 放在这个 header 里

        // 校验 Token 是否合法
        if (token == null || token.isEmpty()) {
            response.setStatus(401); // 401 未授权
            return false;
        }

        try {
            // 使用 Hutool 验证签名 verify
            Map<String, Object> map = myJwtUtils.parseToken(token);
            if (map == null) {
                response.setStatus(401);
                return false;
            }
            return true; // 放行
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }
}
