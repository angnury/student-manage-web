package com.manage.student;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.manage.student.mapper")  // 添加这一行
public class StudentWebApplication {
	public static void main(String[] args) {
		SpringApplication.run(StudentWebApplication.class, args);
	}
}