package com.example.fileVault.service;

import com.example.fileVault.constants.FileVaultConstants;
import com.example.fileVault.dto.FileDto;
import com.example.fileVault.entity.FileEntity;
import com.example.fileVault.exception.BadFileTypeException;
import com.example.fileVault.exception.EmptyFileNameException;
import com.example.fileVault.exception.TooLargeFileSizeException;
import com.example.fileVault.repository.FileSystemFileRepository;
import com.example.fileVault.util.FilenameUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class FileSystemFileServiceTest {

    @Autowired
    FileService fileService;

    @MockBean
    FileSystemFileRepository fileRepository;

    @Test
    void uploadSuccessByFileSizeTest() {
        // given
        long testMB = FileVaultConstants.MB_TO_BYTES_MULTIPLIER * FileVaultConstants.MAX_FILE_SIZE_MB - 1;

        FileDto mockedFileDto = Mockito.mock(FileDto.class);
        MultipartFile mockMultipartFile = Mockito.mock(MultipartFile.class);
        Mockito.when(mockMultipartFile.getSize()).thenReturn(testMB);

        // when
        try (MockedStatic<FileDto> dtoMockedStatic = Mockito.mockStatic(FileDto.class);
             MockedStatic<FilenameUtils> utilsMockedStatic = Mockito.mockStatic(FilenameUtils.class)) {

            dtoMockedStatic.when(() -> FileDto.of(Mockito.any())).thenReturn(mockedFileDto);
            utilsMockedStatic.when(() -> FilenameUtils.getNameWithoutExtension(Mockito.anyString())).thenReturn("test");
            utilsMockedStatic.when(() -> FilenameUtils.getExtension(Mockito.anyString())).thenReturn("test");

            fileService.upload(mockMultipartFile, "Test comment");
        }
    }

    @Test
    void uploadFailByFileSizeTest() {
        // given
        long testMB = FileVaultConstants.MB_TO_BYTES_MULTIPLIER * FileVaultConstants.MAX_FILE_SIZE_MB + 1;

        FileDto mockedResultFileDto = Mockito.mock(FileDto.class);
        MultipartFile mockMultipartFile = Mockito.mock(MultipartFile.class);
        Mockito.when(mockMultipartFile.getSize()).thenReturn(testMB);

        // when
        try (MockedStatic<FileDto> dtoMockedStatic = Mockito.mockStatic(FileDto.class);
             MockedStatic<FilenameUtils> utilsMockedStatic = Mockito.mockStatic(FilenameUtils.class)) {

            dtoMockedStatic.when(() -> FileDto.of(Mockito.any())).thenReturn(mockedResultFileDto);
            utilsMockedStatic.when(() -> FilenameUtils.getNameWithoutExtension(Mockito.anyString())).thenReturn("test");
            utilsMockedStatic.when(() -> FilenameUtils.getExtension(Mockito.anyString())).thenReturn("test");

            // then
            Assertions.assertThrows(TooLargeFileSizeException.class,
                    () -> fileService.upload(mockMultipartFile, "Test comment"));
        }
    }

    @Test
    void uploadSuccessByFileNameTest() {
        // TODO: Test png, img and other extensions

        // given
        FileDto mockedResultFileDto = Mockito.mock(FileDto.class);
        MultipartFile mockMultipartFile = Mockito.mock(MultipartFile.class);
        Mockito.when(mockMultipartFile.getSize()).thenReturn(0L);
        Mockito.when(mockMultipartFile.getOriginalFilename()).thenReturn("test.test");

        // when
        try (MockedStatic<FileDto> dtoMockedStatic = Mockito.mockStatic(FileDto.class)) {
            dtoMockedStatic.when(() -> FileDto.of(Mockito.any())).thenReturn(mockedResultFileDto);

            fileService.upload(mockMultipartFile, "Test comment");

            // then
            dtoMockedStatic.verify(() -> FileDto.of(Mockito.any()));
        }
    }

    @Test
    void uploadFailByFileNameTest() {
        // TODO: Test png, img and other extensions

        // given
        FileDto mockedResultFileDto = Mockito.mock(FileDto.class);
        MultipartFile mockMultipartFile = Mockito.mock(MultipartFile.class);
        Mockito.when(mockMultipartFile.getSize()).thenReturn(0L);
        Mockito.when(mockMultipartFile.getOriginalFilename()).thenReturn("test");

        // when
        try (MockedStatic<FileDto> dtoMockedStatic = Mockito.mockStatic(FileDto.class)) {
            dtoMockedStatic.when(() -> FileDto.of(Mockito.any())).thenReturn(mockedResultFileDto);

            Assertions.assertThrows(BadFileTypeException.class, () ->
                    fileService.upload(mockMultipartFile, "Test comment"));

            // then
            dtoMockedStatic.verify(() -> FileDto.of(Mockito.any()), Mockito.times(0));

        }
    }

    @Test
    void uploadFailByFileNameEmptyTest() {
        // given
        FileDto mockedResultFileDto = Mockito.mock(FileDto.class);
        MultipartFile mockMultipartFile = Mockito.mock(MultipartFile.class);
        Mockito.when(mockMultipartFile.getSize()).thenReturn(0L);
        Mockito.when(mockMultipartFile.getOriginalFilename()).thenReturn("");

        // when
        try (MockedStatic<FileDto> dtoMockedStatic = Mockito.mockStatic(FileDto.class)) {
            dtoMockedStatic.when(() -> FileDto.of(Mockito.any())).thenReturn(mockedResultFileDto);

            Assertions.assertThrows(EmptyFileNameException.class, () ->
                    fileService.upload(mockMultipartFile, "Test comment"));

            // then
            dtoMockedStatic.verify(() -> FileDto.of(Mockito.any()), Mockito.times(0));

        }
    }

    @Test
    void uploadSuccessByReturnedValue() {
        // given
        String testName = "tname";
        String testExtension = "textension";
        String testComment = "tcomment";
        byte[] testContent = new byte[100];

        MultipartFile mockMultipartFile = new MockMultipartFile(testName + '.' + testExtension,
                testName + '.' + testExtension, MediaType.TEXT_PLAIN_VALUE, testContent);

        UUID randomId = UUID.randomUUID();
        Date uploadDate = new Date();
        Date modifiedDate = new Date();
        FileEntity entityExample = FileEntity.builder()
                .id(randomId)
                .uploadDate(uploadDate)
                .modifiedDate(modifiedDate)
                .extension(testExtension)
                .name(testName)
                .content(testContent)
                .size(testContent.length)
                .comment(testComment)
                .build();

        Mockito.when(fileRepository.create(testName, testExtension, testComment, testContent))
                .thenReturn(entityExample);

        // when
        FileDto uploadDtoResult = fileService.upload(mockMultipartFile, testComment);

        // then
        assertThat(uploadDtoResult.getId()).isEqualTo(randomId);
        assertThat(uploadDtoResult.getUploadDate()).isEqualTo(uploadDate);
        assertThat(uploadDtoResult.getModifiedDate()).isEqualTo(modifiedDate);
        assertThat(uploadDtoResult.getExtension()).isEqualTo(testExtension);
        assertThat(uploadDtoResult.getName()).isEqualTo(testName);
        assertThat(uploadDtoResult.getSize()).isEqualTo(testContent.length);
        assertThat(uploadDtoResult.getComment()).isEqualTo(testComment);
        assertThat(uploadDtoResult.getDownloadUrl()).isNotNull();
        assertThat(uploadDtoResult.getDownloadUrl()).contains(randomId.toString());
    }

}