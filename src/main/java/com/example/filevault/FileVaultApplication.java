package com.example.filevault;

import com.example.filevault.config.jwt.JwtConfig;
import com.example.filevault.constants.FileVaultConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
@EnableConfigurationProperties(JwtConfig.class)
public class FileVaultApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileVaultApplication.class, args);
	}

	@Bean
	public ObjectMapper defaultMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		return objectMapper;
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
