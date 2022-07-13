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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileSystemFileServiceTest {

    @Mock
    FileSystemFileRepository fileRepository;

    @InjectMocks
    FileSystemFileService fileService;

    @Mock
    FileDto fileDtoMock;

    @Mock
    FileEntity fileEntityMock;

    @Mock
    MultipartFile multipartFileMock;

    @Test
    void upload_Success_shouldUploadFile_whenAllIsOk() throws IOException {
        // arrange
        String testName = "tname";
        String testExtension = "textension";
        String testComment = "tcomment";
        long testedSize = FileVaultConstants.MB_TO_BYTES_MULTIPLIER * FileVaultConstants.MAX_FILE_SIZE_MB - 1;
        byte[] testContent = new byte[(int) testedSize];

        try (MockedStatic<FileDto> dtoMockedStatic = mockStatic(FileDto.class)) {
            dtoMockedStatic.when(() -> FileDto.of(any(FileEntity.class))).thenReturn(fileDtoMock);
            when(fileRepository.create(anyString(), anyString(), anyString(), any())).thenReturn(fileEntityMock);
            when(multipartFileMock.getSize()).thenReturn(testedSize);
            when(multipartFileMock.getBytes()).thenReturn(testContent);
            when(multipartFileMock.getOriginalFilename()).thenReturn(testName + "." + testExtension);

            // act
            fileService.upload(multipartFileMock, testComment);

            // assert
            verify(fileRepository).create(testName, testExtension, testComment, testContent);
            dtoMockedStatic.verify(() -> FileDto.of(fileEntityMock));
            verify(multipartFileMock).getSize();
            assertEquals(testedSize, testContent.length);
        }
    }

    @Test
    void upload_Fail_shouldThrowException_whenFileSizeMoreThanMaxSize() {
        // arrange
        long tooMuchMB = FileVaultConstants.MB_TO_BYTES_MULTIPLIER * FileVaultConstants.MAX_FILE_SIZE_MB + 1;
        try (MockedStatic<FileDto> dtoMockedStatic = mockStatic(FileDto.class);
             MockedStatic<FilenameUtils> utilsMockedStatic = mockStatic(FilenameUtils.class)) {

            dtoMockedStatic.when(() -> FileDto.of(any())).thenReturn(fileDtoMock);
            utilsMockedStatic.when(() -> FilenameUtils.getNameWithoutExtension(anyString())).thenReturn("test");
            utilsMockedStatic.when(() -> FilenameUtils.getExtension(anyString())).thenReturn("test");
            when(multipartFileMock.getSize()).thenReturn(tooMuchMB);

            // act, assert
            Assertions.assertThrows(TooLargeFileSizeException.class,
                    () -> fileService.upload(multipartFileMock, "Test comment"));
        }
    }

    @Test
    void upload_Fail_shouldThrowException_whenBadFileName() {
        // TODO: Test png, img and other extensions

        // arrange
        try (MockedStatic<FileDto> dtoMockedStatic = mockStatic(FileDto.class)) {
            dtoMockedStatic.when(() -> FileDto.of(any())).thenReturn(fileDtoMock);
            when(multipartFileMock.getSize()).thenReturn(0L);
            when(multipartFileMock.getOriginalFilename()).thenReturn("test");

            // act, assert
            Assertions.assertThrows(BadFileTypeException.class, () ->
                    fileService.upload(multipartFileMock, "Test comment"));
            dtoMockedStatic.verify(() -> FileDto.of(any()), times(0));
        }
    }

    @Test
    void uploadFailByFileNameEmptyTest() {
        // arrange
        try (MockedStatic<FileDto> dtoMockedStatic = mockStatic(FileDto.class)) {
            dtoMockedStatic.when(() -> FileDto.of(any())).thenReturn(fileDtoMock);
            when(multipartFileMock.getSize()).thenReturn(0L);
            when(multipartFileMock.getOriginalFilename()).thenReturn("");

            // act, assert
            Assertions.assertThrows(EmptyFileNameException.class, () ->
                    fileService.upload(multipartFileMock, "Test comment"));
            dtoMockedStatic.verify(() -> FileDto.of(any()), times(0));
        }
    }
}