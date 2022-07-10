package com.example.fileVault;

import com.example.fileVault.controller.FileController;
import com.example.fileVault.repository.FileSystemFileRepository;
import com.example.fileVault.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class FileVaultApplicationTests {

	@Autowired
	private FileService fileService;

	@Autowired
	private FileController controller;

	@Test
	void contextLoads() {
		assertThat(controller).isNotNull();
	}

}
