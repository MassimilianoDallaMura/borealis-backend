package com.borealis.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class BackendApplication extends SpringBootServletInitializer {

	// Questo metodo è necessario per l'avvio in un container Servlet esterno (es. Tomcat)
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BackendApplication.class);
	}

	// Questo è il metodo main per l'avvio locale come applicazione stand-alone
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}
}
