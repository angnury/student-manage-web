-- =====================================================================
-- 学生综合信息管理系统 · Web 版数据库脚本 (student_web)
-- 适用：Spring Boot 3 + MyBatis-Plus 升级版（任务书 M1）
-- 说明：认证与业务档案分离；日期规范化；外键列建索引
-- 默认账号密码均为 123456（BCrypt，前缀 $2a$ 兼容 Spring Security）
-- 可重复执行（先删后建）
-- =====================================================================

DROP DATABASE IF EXISTS student_web;
CREATE DATABASE student_web DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE student_web;

-- ---------------------------------------------------------------------
-- 1. 认证用户表（登录主体，与业务档案分离）
--    username：学生=学号 / 教师=工号 / 管理员=admin
-- ---------------------------------------------------------------------
CREATE TABLE sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username    VARCHAR(20)  NOT NULL                COMMENT '登录账号',
    password    VARCHAR(100) NOT NULL                COMMENT 'BCrypt 密文',
    role        VARCHAR(10)  NOT NULL DEFAULT 'STUDENT'
                              COMMENT '角色: STUDENT/TEACHER/ADMIN',
    status      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态: 1启用 0禁用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB COMMENT = '登录用户表';

-- ---------------------------------------------------------------------
-- 2. 学生学籍表（不再存密码）
-- ---------------------------------------------------------------------
CREATE TABLE student (
    sid         VARCHAR(20) NOT NULL COMMENT '学号',
    sname       VARCHAR(20) NOT NULL COMMENT '姓名',
    sex         VARCHAR(4)           COMMENT '性别',
    sclass      VARCHAR(30)          COMMENT '班级',
    birthday    DATE                 COMMENT '出生日期',
    jg          VARCHAR(50)          COMMENT '籍贯',
    phone       VARCHAR(11)          COMMENT '手机号',
    enter_time  DATE                 COMMENT '入学时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (sid),
    KEY idx_sclass (sclass),
    KEY idx_sname (sname)
) ENGINE = InnoDB COMMENT = '学生学籍表';

-- ---------------------------------------------------------------------
-- 3. 教师表（不再存密码）
-- ---------------------------------------------------------------------
CREATE TABLE teacher (
    tid         VARCHAR(20) NOT NULL COMMENT '工号',
    tname       VARCHAR(20) NOT NULL COMMENT '姓名',
    zc          VARCHAR(10)          COMMENT '职称',
    phone       VARCHAR(11)          COMMENT '联系电话',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (tid)
) ENGINE = InnoDB COMMENT = '教师表';

-- ---------------------------------------------------------------------
-- 4. 课程表
-- ---------------------------------------------------------------------
CREATE TABLE course (
    cid    VARCHAR(20) NOT NULL COMMENT '课程编号',
    cname  VARCHAR(50) NOT NULL COMMENT '课程名',
    credit INT                   COMMENT '学分',
    tid    VARCHAR(20)           COMMENT '授课教师工号',
    PRIMARY KEY (cid),
    KEY idx_tid (tid),
    CONSTRAINT fk_course_teacher FOREIGN KEY (tid) REFERENCES teacher (tid)
) ENGINE = InnoDB COMMENT = '课程表';

-- ---------------------------------------------------------------------
-- 5. 成绩表（主键含学期，同课不同学期可共存；total 由后端计算）
-- ---------------------------------------------------------------------
CREATE TABLE score (
    sid     VARCHAR(20) NOT NULL COMMENT '学号',
    cid     VARCHAR(20) NOT NULL COMMENT '课程编号',
    term    VARCHAR(20) NOT NULL COMMENT '学期, 如 2026秋',
    usual   INT         NOT NULL DEFAULT 0 COMMENT '平时分',
    final   INT         NOT NULL DEFAULT 0 COMMENT '期末分',
    total   INT                  COMMENT '总分(后端计算: usual*0.4+final*0.6)',
    PRIMARY KEY (sid, cid, term),
    KEY idx_sid (sid),
    KEY idx_cid (cid),
    CONSTRAINT fk_score_student FOREIGN KEY (sid) REFERENCES student (sid),
    CONSTRAINT fk_score_course  FOREIGN KEY (cid) REFERENCES course (cid)
) ENGINE = InnoDB COMMENT = '成绩表';

-- ---------------------------------------------------------------------
-- 6. 课表
-- ---------------------------------------------------------------------
CREATE TABLE timetable (
    id      BIGINT      NOT NULL AUTO_INCREMENT,
    cid     VARCHAR(20) NOT NULL COMMENT '课程编号',
    tid     VARCHAR(20) NOT NULL COMMENT '授课教师工号',
    week    VARCHAR(10)          COMMENT '星期, 如 周一',
    section VARCHAR(10)          COMMENT '节次, 如 1-2节',
    room    VARCHAR(30)          COMMENT '教室',
    PRIMARY KEY (id),
    UNIQUE KEY uk_cid_week_section (cid, week, section),
    KEY idx_tid (tid),
    CONSTRAINT fk_tt_course FOREIGN KEY (cid) REFERENCES course (cid),
    CONSTRAINT fk_tt_teacher FOREIGN KEY (tid) REFERENCES teacher (tid)
) ENGINE = InnoDB COMMENT = '课表';

-- ---------------------------------------------------------------------
-- 7. 奖惩记录表
-- ---------------------------------------------------------------------
CREATE TABLE reward_punish (
    id      BIGINT      NOT NULL AUTO_INCREMENT,
    sid     VARCHAR(20) NOT NULL COMMENT '学生学号',
    type    VARCHAR(10) NOT NULL COMMENT '类型: 奖励/处分',
    content VARCHAR(200)         COMMENT '事由',
    r_time  DATE                 COMMENT '发生日期',
    remark  VARCHAR(100)         COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_sid (sid),
    CONSTRAINT fk_rp_student FOREIGN KEY (sid) REFERENCES student (sid)
) ENGINE = InnoDB COMMENT = '奖惩记录表';

-- =====================================================================
-- 初始化数据（密码统一 123456 的 BCrypt 密文）
-- =====================================================================

INSERT INTO sys_user (username, password, role) VALUES
('admin',  '$2a$10$BRA.BUeYDw9dTZmRajZXDu5r0iyMKOKp1E108l02gnTNWmgDxrpd2', 'ADMIN'),
('202401', '$2a$10$BRA.BUeYDw9dTZmRajZXDu5r0iyMKOKp1E108l02gnTNWmgDxrpd2', 'STUDENT'),
('202402', '$2a$10$BRA.BUeYDw9dTZmRajZXDu5r0iyMKOKp1E108l02gnTNWmgDxrpd2', 'STUDENT'),
('T001',   '$2a$10$BRA.BUeYDw9dTZmRajZXDu5r0iyMKOKp1E108l02gnTNWmgDxrpd2', 'TEACHER'),
('T002',   '$2a$10$BRA.BUeYDw9dTZmRajZXDu5r0iyMKOKp1E108l02gnTNWmgDxrpd2', 'TEACHER');

INSERT INTO student (sid, sname, sex, sclass, birthday, jg, phone, enter_time) VALUES
('202401', '张三', '男', '计科2班', '2004-01-01', '广东东莞', '13800138000', '2024-09-01'),
('202402', '李四', '女', '计科2班', '2004-02-02', '广东深圳', '13900139000', '2024-09-01');

INSERT INTO teacher (tid, tname, zc) VALUES
('T001', '王老师', '教授'),
('T002', '李老师', '讲师');

INSERT INTO course (cid, cname, credit, tid) VALUES
('C001', '数据库原理', 5, 'T001'),
('C002', 'Java程序设计', 4, 'T002'),
('C003', '数据结构', 4, 'T001');

INSERT INTO score (sid, cid, term, usual, final, total) VALUES
('202401', 'C001', '2026秋', 80, 85, 83),
('202401', 'C002', '2026秋', 78, 82, 80),
('202402', 'C001', '2026秋', 90, 92, 91),
('202402', 'C002', '2026秋', 65, 60, 62);

INSERT INTO timetable (cid, tid, week, section, room) VALUES
('C001', 'T001', '周一', '1-2节', '1号教学楼201'),
('C002', 'T002', '周三', '3-4节', '1号教学楼302'),
('C003', 'T001', '周五', '5-6节', '1号教学楼205');

INSERT INTO reward_punish (sid, type, content, r_time, remark) VALUES
('202401', '奖励', '校级三好学生', '2026-06-01', '无');

-- =====================================================================
-- 可选：扩充演示数据（分页/统计图表有数据才好看）
-- =====================================================================
INSERT INTO student (sid, sname, sex, sclass, birthday, jg, phone, enter_time) VALUES
('202403', '王五', '男', '计科1班', '2004-03-03', '湖南长沙', '13700137000', '2024-09-01'),
('202404', '赵六', '女', '计科1班', '2004-04-04', '四川成都', '13600136000', '2024-09-01'),
('202405', '钱七', '男', '计科2班', '2003-12-05', '湖北武汉', '13500135000', '2024-09-01'),
('202406', '孙八', '女', '计科2班', '2004-06-06', '福建厦门', '13400134000', '2024-09-01'),
('202407', '周九', '男', '软工1班', '2004-07-07', '浙江杭州', '13300133000', '2024-09-01'),
('202408', '吴十', '女', '软工1班', '2004-08-08', '江苏南京', '13200132000', '2024-09-01'),
('202409', '郑一', '男', '软工1班', '2003-10-09', '山东青岛', '13100131000', '2024-09-01'),
('202410', '冯二', '女', '软工1班', '2004-10-10', '广东广州', '13000130000', '2024-09-01');

INSERT INTO sys_user (username, password, role) SELECT sid, '$2a$10$BRA.BUeYDw9dTZmRajZXDu5r0iyMKOKp1E108l02gnTNWmgDxrpd2', 'STUDENT' FROM student WHERE sid BETWEEN '202403' AND '202410';

INSERT INTO score (sid, cid, term, usual, final, total)
SELECT s.sid, c.cid, '2026秋',
       CAST(RAND()*20+70 AS UNSIGNED), CAST(RAND()*30+60 AS UNSIGNED), NULL
FROM student s CROSS JOIN course c
WHERE s.sid BETWEEN '202403' AND '202410' AND c.cid IN ('C001','C002');

-- 按后端同款规则回填总分，保证与平时/期末一致
UPDATE score SET total = ROUND(usual * 0.4 + final * 0.6)
WHERE sid BETWEEN '202403' AND '202410';
