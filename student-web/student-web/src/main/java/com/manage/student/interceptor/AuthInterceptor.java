package com.manage.student.interceptor;

import com.manage.student.common.RequireRole;
import com.manage.student.common.UserContext;
import com.manage.student.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * 登录鉴权 + 接口级角色校验拦截器。
 * <p>
 * 职责：
 * <ol>
 *     <li>从 Authorization 头取出 Bearer Token 并校验签名 / 有效期；</li>
 *     <li>把用户名与角色写入 {@link UserContext}，供后续业务代码使用；</li>
 *     <li>读取目标方法或类上的 {@link RequireRole}，角色不匹配返回 403。</li>
 * </ol>
 * 放行规则由 {@code WebConfig} 配置（当前只放行 /api/auth/login）。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 非 Controller 方法（静态资源、错误页等）直接放行
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 1. 取 Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return reject(response, 401, "未登录或Token无效");
        }

        String token = authHeader.substring(7);
        String username;
        String role;
        try {
            username = jwtUtil.getUsernameFromToken(token);
            role = jwtUtil.getRoleFromToken(token);
        } catch (Exception e) {
            // Token 被篡改、签名不匹配、已过期都会抛异常，统一按 401 处理。
            // 注意：不能只靠 isTokenExpired()，签名错误时它会直接抛异常而不是返回 false。
            return reject(response, 401, "Token已过期或无效");
        }

        // 2. 写入当前请求上下文
        UserContext.set(username, role);

        // 3. 角色校验：方法上的注解优先，其次取类上的
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }
        if (requireRole != null
                && Arrays.stream(requireRole.value()).noneMatch(r -> r.equals(role))) {
            return reject(response, 403, "无权限访问该接口");
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // Tomcat 线程池会复用线程，必须清理 ThreadLocal，否则会串用户数据
        UserContext.clear();
    }

    private boolean reject(HttpServletResponse response, int code, String message) throws Exception {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":" + code + ",\"message\":\"" + message + "\"}");
        return false;
    }
}
