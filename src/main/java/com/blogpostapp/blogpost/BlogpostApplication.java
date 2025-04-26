package com.blogpostapp.blogpost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
@ComponentScan(basePackages = {"com.blogpostapp.blogpost", "com.blogpostapp.blogpost.config"})
@SpringBootApplication
public class BlogpostApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogpostApplication.class, args);
	}

}
