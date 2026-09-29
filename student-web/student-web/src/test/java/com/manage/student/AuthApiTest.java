package com.manage.student;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 认证与权限接口测试。
 * <p>
 * 覆盖：登录成功 / 密码错误 / 无 token 401 / 越权 403 / 管理员分页查询。
 * 需要本地 MySQL 已启动且 student_web 库已导入 student_web.sql。
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthApiTest {

    @Autowired
    private MockMvc mockMvc;

    private String loginAndGetToken(String username, String password) throws Exception {
        String body = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.data.token");
    }

    /** 1. 管理员登录成功，返回 token */
    @Test
    void loginSuccess() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    /** 2. 密码错误，返回 HTTP 401 + 业务码 401 */
    @Test
    void loginWrongPassword() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    /** 3. 不带 token 访问受保护接口，返回 HTTP 401 */
    @Test
    void accessWithoutToken() throws Exception {
        mockMvc.perform(get("/api/admin/students"))
                .andExpect(status().isUnauthorized());
    }

    /** 4. 学生 token 访问管理员接口，返回 HTTP 403（越权防护） */
    @Test
    void studentCannotAccessAdminApi() throws Exception {
        String token = loginAndGetToken("202401", "123456");

        mockMvc.perform(get("/api/admin/students")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    /** 5. 管理员分页查询正常返回 */
    @Test
    void adminCanListStudents() throws Exception {
        String token = loginAndGetToken("admin", "123456");

        mockMvc.perform(get("/api/admin/students")
                        .param("page", "1").param("size", "5")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    /** 6. 教师可查成绩，学生不可（成绩接口限制 TEACHER/ADMIN） */
    @Test
    void studentCannotAccessTeacherApi() throws Exception {
        String token = loginAndGetToken("202401", "123456");

        mockMvc.perform(get("/api/teacher/students/202401/scores")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
