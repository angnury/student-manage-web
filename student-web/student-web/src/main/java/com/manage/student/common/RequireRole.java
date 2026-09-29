package com.manage.student.common;

import java.lang.annotation.*;

/**
 * 接口级角色控制注解。
 * <p>
 * 可以标注在类上（整个 Controller 生效）或方法上（优先级更高）。
 * 由 {@code AuthInterceptor} 在请求进入 Controller 前读取并校验。
 *
 * <pre>
 * &#64;RequireRole("ADMIN")
 * &#64;RequireRole({"TEACHER", "ADMIN"})
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {

    /** 允许访问该接口的角色列表，命中任意一个即通过。 */
    String[] value();
}
