package com.manage.student;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudentWebApplication {

	public static void main(String[] args) {
		// 临时生成 BCrypt 密文
		//String rawPassword = "123456";
		//String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword);
		//System.out.println("生成的密文: " + encodedPassword);

		SpringApplication.run(StudentWebApplication.class, args);
	}
}