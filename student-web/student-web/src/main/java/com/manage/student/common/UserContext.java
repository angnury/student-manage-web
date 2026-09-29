package com.manage.student.common;

/**
 * 当前请求的登录用户上下文。
 * <p>
 * 基于 {@link ThreadLocal} 保证线程隔离，让 Controller / Service 层都能直接拿到当前登录人，
 * 不必层层透传 HttpServletRequest。
 * <p>
 * <b>必须在请求结束时调用 {@link #clear()}</b>：Tomcat 使用线程池复用线程，
 * 不清理会把上一个请求的用户信息带到下一个请求，造成越权。
 * 清理动作由 {@code AuthInterceptor#afterCompletion} 负责。
 */
public class UserContext {

    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(String username, String role) {
        USERNAME.set(username);
        ROLE.set(role);
    }

    /** 当前登录用户名（学生为学号、教师为工号、管理员为 admin）。 */
    public static String getUsername() {
        return USERNAME.get();
    }

    /** 当前登录用户角色：ADMIN / TEACHER / STUDENT。 */
    public static String getRole() {
        return ROLE.get();
    }

    public static void clear() {
        USERNAME.remove();
        ROLE.remove();
    }
}
