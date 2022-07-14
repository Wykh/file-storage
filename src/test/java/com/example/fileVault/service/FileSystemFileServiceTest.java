package com.example.fileVault.service;

import com.example.fileVault.constants.FileVaultConstants;
import com.example.fileVault.dto.FileDto;
import com.example.fileVault.dto.FileNameById;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
    void upload_Fail_shouldThrowException_whenFileNameIsEmpty() {
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

    @Test
    void getAll_Success_shouldGetAllRecords_whenAllOk() {
        Map<UUID, FileEntity> dtoListMock = (Map<UUID, FileEntity>) mock(Map.class);
        when(fileRepository.getAll()).thenReturn(dtoListMock);

        List<FileDto> actual = fileService.getAll();

        verify(fileRepository).getAll();
        assertNotNull(actual);
    }

    @Test
    void getDTO_Success_shouldRecordById_whenAllOk() {
        FileEntity found = mock(FileEntity.class);
        when(fileRepository.findById(any(UUID.class))).thenReturn(found);
        UUID randomId = UUID.randomUUID();

        FileDto actual = fileService.getDTO(randomId);

        verify(fileRepository).findById(randomId);
        assertNotNull(actual);
    }

    @Test
    void getNamesById_Success_shouldRecordById_whenAllOk() {
        Map<UUID, FileEntity> dtoListMock = (Map<UUID, FileEntity>) mock(Map.class);
        when(fileRepository.getAll()).thenReturn(dtoListMock);

        List<FileNameById> actual = fileService.getNamesById();

        verify(fileRepository).getAll();
        assertNotNull(actual);
    }

    @Test
    void getFilesFilteredByName_Success_shouldFindRecordByString_whenOneRecordIsFitting() {
        String substringToFind = "test";
        UUID testId1 = UUID.randomUUID();
        UUID testId2 = UUID.randomUUID();
        String testName1 = "myTestString";
        String testName2 = "myString";
        FileEntity testEntity1 = mock(FileEntity.class);
        FileEntity testEntity2 = mock(FileEntity.class);
        when(testEntity1.getId()).thenReturn(testId1);
        when(testEntity2.getId()).thenReturn(testId2);
        when(testEntity1.getName()).thenReturn(testName1);
        when(testEntity2.getName()).thenReturn(testName2);
        Map<UUID, FileEntity> dtoListTest = new HashMap<>();
        dtoListTest.put(testId1, testEntity1);
        dtoListTest.put(testId2, testEntity2);
        when(fileRepository.getAll()).thenReturn(dtoListTest);

        List<FileDto> filesFilteredByName = fileService.getFilesFilteredByName(substringToFind);

        verify(fileRepository).getAll();
        assertEquals(filesFilteredByName.size(), 1);
        assertEquals(filesFilteredByName.get(0).getName(), testName1);
    }

    @Test
    void getFilesFilteredByModifiedDateRange_Success_shouldFindRecordInRange_whenOneRecordIsFitting() {
        Date fromDateToFind = new Date(100);
        Date toDateToFind = new Date(1000);
        UUID testId1 = UUID.randomUUID();
        UUID testId2 = UUID.randomUUID();
        UUID testId3 = UUID.randomUUID();
        Date testModifiedDate1 = new Date(100);
        Date testModifiedDate2 = new Date(150);
        Date testModifiedDate3 = new Date(1000);
        FileEntity testEntity1 = mock(FileEntity.class);
        FileEntity testEntity2 = mock(FileEntity.class);
        FileEntity testEntity3 = mock(FileEntity.class);
        when(testEntity1.getId()).thenReturn(testId1);
        when(testEntity2.getId()).thenReturn(testId2);
        when(testEntity3.getId()).thenReturn(testId3);
        when(testEntity1.getModifiedDate()).thenReturn(testModifiedDate1);
        when(testEntity2.getModifiedDate()).thenReturn(testModifiedDate2);
        when(testEntity3.getModifiedDate()).thenReturn(testModifiedDate3);
        Map<UUID, FileEntity> dtoListTest = new HashMap<>();
        dtoListTest.put(testId1, testEntity1);
        dtoListTest.put(testId2, testEntity2);
        dtoListTest.put(testId3, testEntity3);
        when(fileRepository.getAll()).thenReturn(dtoListTest);

        List<FileDto> filesFilteredByModifiedDateRange = fileService.getFilesFilteredByModifiedDateRange(fromDateToFind, toDateToFind);

        assertEquals(filesFilteredByModifiedDateRange.size(), 1);
        assertEquals(filesFilteredByModifiedDateRange.get(0).getModifiedDate(), testModifiedDate2);
    }

    @Test
    void getFilesFilteredByUploadDateRange_Success_shouldFindRecordInRange_whenOneRecordIsFitting() {
        Date fromDateToFind = new Date(100);
        Date toDateToFind = new Date(1000);
        UUID testId1 = UUID.randomUUID();
        UUID testId2 = UUID.randomUUID();
        UUID testId3 = UUID.randomUUID();
        Date testUploadDate1 = new Date(100);
        Date testUploadDate2 = new Date(150);
        Date testUploadDate3 = new Date(1000);
        FileEntity testEntity1 = mock(FileEntity.class);
        FileEntity testEntity2 = mock(FileEntity.class);
        FileEntity testEntity3 = mock(FileEntity.class);
        when(testEntity1.getId()).thenReturn(testId1);
        when(testEntity2.getId()).thenReturn(testId2);
        when(testEntity3.getId()).thenReturn(testId3);
        when(testEntity1.getUploadDate()).thenReturn(testUploadDate1);
        when(testEntity2.getUploadDate()).thenReturn(testUploadDate2);
        when(testEntity3.getUploadDate()).thenReturn(testUploadDate3);
        Map<UUID, FileEntity> dtoListTest = new HashMap<>();
        dtoListTest.put(testId1, testEntity1);
        dtoListTest.put(testId2, testEntity2);
        dtoListTest.put(testId3, testEntity3);
        when(fileRepository.getAll()).thenReturn(dtoListTest);

        List<FileDto> filesFilteredByUploadDateRange = fileService.getFilesFilteredByUploadDateRange(fromDateToFind, toDateToFind);

        assertEquals(filesFilteredByUploadDateRange.size(), 1);
        assertEquals(filesFilteredByUploadDateRange.get(0).getUploadDate(), testUploadDate2);
    }

    @Test
    void getFilesFilteredByExtensions_Success_shouldFindFittingExtensions_whenExtensionsListIsNotEmpty() {
        List<String> extensionsToFind = new ArrayList<>();
        extensionsToFind.add("png");
        extensionsToFind.add("svg");
        extensionsToFind.add("mov");
        UUID testId1 = UUID.randomUUID();
        UUID testId2 = UUID.randomUUID();
        UUID testId3 = UUID.randomUUID();
        String testExtension1 = "png";
        String testExtension2 = "jpg";
        String testExtension3 = "mov";
        FileEntity testEntity1 = mock(FileEntity.class);
        FileEntity testEntity2 = mock(FileEntity.class);
        FileEntity testEntity3 = mock(FileEntity.class);
        when(testEntity1.getId()).thenReturn(testId1);
        when(testEntity2.getId()).thenReturn(testId2);
        when(testEntity3.getId()).thenReturn(testId3);
        when(testEntity1.getExtension()).thenReturn(testExtension1);
        when(testEntity2.getExtension()).thenReturn(testExtension2);
        when(testEntity3.getExtension()).thenReturn(testExtension3);
        Map<UUID, FileEntity> dtoListTest = new HashMap<>();
        dtoListTest.put(testId1, testEntity1);
        dtoListTest.put(testId2, testEntity2);
        dtoListTest.put(testId3, testEntity3);
        when(fileRepository.getAll()).thenReturn(dtoListTest);

        List<FileDto> filesFilteredByExtensions = fileService.getFilesFilteredByExtensions(extensionsToFind);

        assertEquals(filesFilteredByExtensions.size(), 2);
        filesFilteredByExtensions.forEach((fileDto) -> assertTrue(extensionsToFind.contains(fileDto.getExtension())));
    }

    @Test
    void getEntity_Success_shouldRecordById_whenAllOk() {
        FileEntity found = mock(FileEntity.class);
        when(fileRepository.findById(any(UUID.class))).thenReturn(found);
        UUID randomId = UUID.randomUUID();

        FileEntity actual = fileService.getEntity(randomId);

        verify(fileRepository).findById(randomId);
        assertNotNull(actual);
    }

    @Test
    void downloadZip() {
        // незнай как это тестировать
    }

    @Test
    void update_shouldUpdateFile_whenFileFound() {
        UUID randomId = UUID.randomUUID();
        String newFileName = "testNameNew";
        String newComment = "testCommentNew";
        when(fileRepository.updateById(any(UUID.class), anyString(), anyString())).thenReturn(fileEntityMock);

        FileDto actual = fileService.update(randomId, newFileName, newComment);

        verify(fileRepository).updateById(randomId, newFileName, newComment);
        assertNotNull(actual);
    }

    @Test
    void delete_shouldDeleteFile_whenFileFound() {
        UUID randomId = UUID.randomUUID();
        when(fileRepository.deleteById(any(UUID.class))).thenReturn(fileEntityMock);

        FileDto actual = fileService.delete(randomId);

        verify(fileRepository).deleteById(randomId);
        assertNotNull(actual);
    }
}