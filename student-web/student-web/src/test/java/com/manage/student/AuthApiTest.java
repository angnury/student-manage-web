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
 * 认证、权限与业务接口测试。
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

    // ---------------- 认证 ----------------

    /** 1. 管理员登录成功，返回 token 与角色 */
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

    // ---------------- 鉴权与越权 ----------------

    /** 3. 不带 token 访问受保护接口，返回 HTTP 401 */
    @Test
    void accessWithoutToken() throws Exception {
        mockMvc.perform(get("/api/admin/students"))
                .andExpect(status().isUnauthorized());
    }

    /** 4. 学生 token 访问管理员接口，返回 HTTP 403 */
    @Test
    void studentCannotAccessAdminApi() throws Exception {
        String token = loginAndGetToken("202401", "123456");
        mockMvc.perform(get("/api/admin/students").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    /** 5. 学生 token 访问教师接口，返回 HTTP 403 */
    @Test
    void studentCannotAccessTeacherApi() throws Exception {
        String token = loginAndGetToken("202401", "123456");
        mockMvc.perform(get("/api/teacher/students/202401/scores").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    /** 6. 管理员 token 访问学生自助接口，返回 HTTP 403 */
    @Test
    void adminCannotAccessStudentSelfApi() throws Exception {
        String token = loginAndGetToken("admin", "123456");
        mockMvc.perform(get("/api/student/my/info").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    // ---------------- 管理员业务 ----------------

    /** 7. 管理员分页查询学生 */
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

    /** 8. 工作台概览返回计数 */
    @Test
    void overviewReturnsCounts() throws Exception {
        String token = loginAndGetToken("admin", "123456");
        mockMvc.perform(get("/api/admin/statistics/overview").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.studentCount").isNumber())
                .andExpect(jsonPath("$.data.classCount").isNumber());
    }

    // ---------------- 学生自助 ----------------

    /** 9. 学生可查询自己的个人信息（sid 来自 token，不接受前端传参） */
    @Test
    void studentCanGetMyInfo() throws Exception {
        String token = loginAndGetToken("202401", "123456");
        mockMvc.perform(get("/api/student/my/info").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.sid").value("202401"));
    }

    /** 10. 学生可查询自己的成绩 */
    @Test
    void studentCanGetMyScores() throws Exception {
        String token = loginAndGetToken("202401", "123456");
        mockMvc.perform(get("/api/student/my/scores").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    // ---------------- 教师业务 ----------------

    /** 11. 教师按班级查询学员（成绩管理页多条件查询） */
    @Test
    void teacherCanQueryStudentsByClass() throws Exception {
        String token = loginAndGetToken("T001", "123456");
        mockMvc.perform(get("/api/teacher/students")
                        .param("className", "计科")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].student").exists())
                .andExpect(jsonPath("$.data[0].scores").isArray());
    }

    /** 12. 教师可获取课程列表 */
    @Test
    void teacherCanListCourses() throws Exception {
        String token = loginAndGetToken("T001", "123456");
        mockMvc.perform(get("/api/teacher/courses").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    /** 13. 查询不存在的学员返回业务码 404（前端据此弹窗提示） */
    @Test
    void queryNotExistStudentReturns404() throws Exception {
        String token = loginAndGetToken("T001", "123456");
        mockMvc.perform(get("/api/teacher/students/999999/scores")
                        .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.code").value(404));
    }
}
