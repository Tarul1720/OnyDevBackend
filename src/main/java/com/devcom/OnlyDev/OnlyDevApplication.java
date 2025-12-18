package com.devcom.OnlyDev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OnlyDevApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlyDevApplication.class, args);
	}

}
