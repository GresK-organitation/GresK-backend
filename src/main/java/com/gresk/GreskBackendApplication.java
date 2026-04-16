package com.gresk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@EnableCaching // <--- AÑADE ESTO PARA QUE FUNCIONE EL CACHÉ
public class GreskBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(GreskBackendApplication.class, args);
	}

}
