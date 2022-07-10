package com.example.fileVault.controller;

import com.example.fileVault.dto.FileDto;
import com.example.fileVault.dto.FileNameById;
import com.example.fileVault.entity.FileEntity;
import com.example.fileVault.repository.FileSystemFileRepository;
import com.example.fileVault.service.FileService;
import com.example.fileVault.service.FileSystemFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WebFluxTest(controllers = {FileController.class})
class FileControllerTest {

    @MockBean
    private FileService fileService;

    @Test
    public void getAllFiles(@Autowired WebTestClient client) {

    }

    @Test
    public void saveAndLoad() {
        fileService = new FileSystemFileService(new FileSystemFileRepository());

        fileService.upload(new MockMultipartFile("foo", "foo.txt", MediaType.TEXT_PLAIN_VALUE,
                "Hello, World".getBytes()), "Test");
        List<FileNameById> ids = fileService.getNamesById();
        System.out.println(ids.get(0).getId());
        assertThat(fileService.getEntity(ids.get(0).getId())).isNotNull();
    }

    @Test
    void downloadFile() {

    }

    @Test
    void downloadZip() {
    }

    @Test
    void uploadFile() {
    }
}