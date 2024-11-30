package com.example.Proyecto_CALIDAD;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = "com.example.Proyecto_CALIDAD.Clases")
public class ProyectoCalidadApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoCalidadApplication.class, args);
	}

	@Bean
	public CommandLineRunner generatePassword() {
		return args -> {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			String rawPassword = "clave123";
			String encodedPassword = encoder.encode(rawPassword);
			System.out.println("Contraseña encriptada: " + encodedPassword);
		};
	}
}
