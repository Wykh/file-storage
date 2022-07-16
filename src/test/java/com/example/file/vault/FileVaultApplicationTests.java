package com.example.file.vault;

import com.example.file.vault.controller.FileController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class FileVaultApplicationTests {

	@Autowired
	private FileController controller;

	@Test
	void contextLoads() {
		assertThat(controller).isNotNull();
	}

}
