package com.example.file.vault.service;

import com.example.file.vault.repository.FileSystemFileRepository;
import com.example.file.vault.constants.FileVaultConstants;
import com.example.file.vault.dto.FileDto;
import com.example.file.vault.dto.FileNameById;
import com.example.file.vault.entity.FileEntity;
import com.example.file.vault.exception.BadFileTypeException;
import com.example.file.vault.exception.EmptyFileNameException;
import com.example.file.vault.exception.TooLargeFileSizeException;
import com.example.file.vault.util.FilenameUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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

    @Test
    void upload_Success_shouldUploadFile_whenAllIsOk() throws IOException {
        // arrange
        String testName = "tname";
        String testExtension = "textension";
        String testComment = "tcomment";
        long testedSize = FileVaultConstants.MAX_FILE_SIZE_BYTES - 1;
        byte[] testContent = new byte[(int) testedSize];

        FileEntity testFileEntity = FileEntity.builder()
                .name(testName)
                .extension(testExtension)
                .comment(testComment)
                .size(testedSize)
                .content(testContent)
                .id(UUID.randomUUID())
                .modifiedDate(new Date())
                .uploadDate(new Date())
                .build();
        FileDto expectedFileDto = FileDto.of(testFileEntity);
        MockMultipartFile testMultipartFile = new MockMultipartFile(
                testName, testName + '.' + testExtension, MediaType.ALL_VALUE, testContent);

        when(fileRepository.create(testName, testExtension, testComment, testContent)).thenReturn(testFileEntity);

        //act
        FileDto actualFileDto = fileService.upload(testMultipartFile, testComment);

        // assert
        verify(fileRepository).create(testName, testExtension, testComment, testContent);
        assertThat(actualFileDto)
                .usingRecursiveComparison()
                .isEqualTo(expectedFileDto);
    }

    @Test
    void upload_Fail_shouldThrowException_whenFileSizeMoreThanMaxSize() {
        // arrange
        String testName = "tname";
        long tooMuchMB = FileVaultConstants.MAX_FILE_SIZE_BYTES + 1;
        byte[] testContent = new byte[(int) tooMuchMB];

        MockMultipartFile testMultipartFile = new MockMultipartFile(testName, testContent);

        // act, assert
        Assertions.assertThrows(TooLargeFileSizeException.class,
                () -> fileService.upload(testMultipartFile, "Test comment"));
    }

    @Test
    void upload_Fail_shouldThrowException_whenBadFileName() {
        // TODO: Test png, img and other extensions
        MockMultipartFile testMultipartFile = new MockMultipartFile(
                "foo", "foo.", MediaType.ALL_VALUE, new byte[1]);

        Assertions.assertThrows(BadFileTypeException.class, () ->
                fileService.upload(testMultipartFile, "Test comment"));
    }

    @Test
    void upload_Fail_shouldThrowException_whenFileNameIsEmpty() {
        MockMultipartFile testMultipartFile = new MockMultipartFile(
                "foo", "", MediaType.ALL_VALUE, new byte[1]);

        Assertions.assertThrows(EmptyFileNameException.class, () ->
                fileService.upload(testMultipartFile, "Test comment"));
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
        UUID idSample = UUID.fromString("1-1-1-1-1");
        String newFileName = "newName";
        String newComment = "newComment";
        FileEntity testFileEntity = FileEntity.builder()
                .name(newFileName)
                .extension("test")
                .comment(newFileName)
                .size(1)
                .content(new byte[1])
                .id(idSample)
                .modifiedDate(new Date())
                .uploadDate(new Date())
                .build();
        FileDto expectedFileDto = FileDto.of(testFileEntity);
        when(fileRepository.updateById(idSample, newFileName, newComment)).thenReturn(testFileEntity);

        FileDto actualFileDto = fileService.update(idSample, newFileName, newComment);

        assertThat(actualFileDto)
                .usingRecursiveComparison()
                .isEqualTo(expectedFileDto);
    }

    @Test
    void delete_shouldDeleteFile_whenFileFound() {
        UUID idSample = UUID.fromString("1-1-1-1-1");
        FileEntity testFileEntity = FileEntity.builder()
                .name("testName")
                .extension("test")
                .comment("testComment")
                .size(1)
                .content(new byte[1])
                .id(idSample)
                .modifiedDate(new Date())
                .uploadDate(new Date())
                .build();
        FileDto expectedFileDto = FileDto.of(testFileEntity);
        expectedFileDto.setDownloadUrl("");
        when(fileRepository.deleteById(idSample)).thenReturn(testFileEntity);

        FileDto actualFileDto = fileService.delete(idSample);

        assertThat(actualFileDto)
                .usingRecursiveComparison()
                .isEqualTo(expectedFileDto);
    }
}