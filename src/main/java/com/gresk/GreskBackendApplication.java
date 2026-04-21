package com.gresk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.gresk.modules.user.infrastructure.adapters.external.spotify")
public class GreskBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GreskBackendApplication.class, args);
	}

}
