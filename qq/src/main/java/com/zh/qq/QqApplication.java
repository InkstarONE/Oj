package com.zh.qq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
//扫描所有需要的包 包含自用工具类包 所在路径
@ComponentScan(basePackages = {"com.zh","org.n3r.idworker"})
//扫描mybatis 包路径
@MapperScan(basePackages = {"com.zh.qq.mapper"})
public class QqApplication {


	@Bean
	public SpringUtil getSpringUtil(){
		return new SpringUtil();
	}

	public static void main(String[] args) {
		SpringApplication.run(QqApplication.class, args);
	}
}
