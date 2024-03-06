package com.scenic.rownezcoreservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(servers = {@Server(url = "/", description = "default server url")})
public class RownezCoreServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RownezCoreServiceApplication.class, args);
	}
}
