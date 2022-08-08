package com.example.filevault;

import com.example.filevault.config.jwt.JwtConfig;
import com.example.filevault.constants.FileVaultConstants;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
@EnableConfigurationProperties(JwtConfig.class)
public class FileVaultApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileVaultApplication.class, args);
	}

	@Bean
	CommandLineRunner init() {
		return (args) -> {
			try {
				Files.createDirectories(Paths.get(FileVaultConstants.STORAGE_LOCATION));
			} catch (IOException e) {
				throw new RuntimeException(e); // TODO: custom exception
			}
		};
	}

}
