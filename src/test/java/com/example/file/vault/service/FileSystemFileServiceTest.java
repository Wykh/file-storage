package com.example.file.vault.service;

import com.example.file.vault.constants.FileVaultConstants;
import com.example.file.vault.dto.FileDto;
import com.example.file.vault.entity.FileEntity;
import com.example.file.vault.exception.BadFileTypeException;
import com.example.file.vault.exception.EmptyFileNameException;
import com.example.file.vault.exception.TooLargeFileSizeException;
import com.example.file.vault.repository.FileSystemFileRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

        assertThrows(BadFileTypeException.class, () ->
                fileService.upload(testMultipartFile, "Test comment"));
    }

    @Test
    void upload_Fail_shouldThrowException_whenFileNameIsEmpty() {
        MockMultipartFile testMultipartFile = new MockMultipartFile(
                "foo", "", MediaType.ALL_VALUE, new byte[1]);

        assertThrows(EmptyFileNameException.class, () ->
                fileService.upload(testMultipartFile, "Test comment"));
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

    @Test
    void getFilesFilteredByName_shouldGetFilteredFiles_whenGoodParamsPassed() {
        String nameToFind = "test";
        List<String> listOfGoodNames = Arrays.asList("test", "TEST", "nameOfTest");
        List<FileEntity> fileEntityList = Arrays.asList(
                FileEntity.builder().name(listOfGoodNames.get(0)).build(),
                FileEntity.builder().name(listOfGoodNames.get(1)).build(),
                FileEntity.builder().name(listOfGoodNames.get(2)).build(),
                FileEntity.builder().name("BadName").build()
        );

        List<FileEntity> filesFilteredByName = fileService.getFilesFilteredByName(nameToFind, fileEntityList);

        assertEquals(filesFilteredByName.size(), 3);
        filesFilteredByName.forEach((entity) -> assertTrue(listOfGoodNames.contains(entity.getName())));
    }

    @Test
    void getFilesFilteredByUploadDateRange_shouldGetFilteredFiles_whenTwoDatesAreNotNull() {
        Date uploadDateFrom = new Date(1000);
        Date uploadDateTo = new Date(1500);
        List<FileEntity> fileEntityList = Arrays.asList(
                FileEntity.builder().name("bad").uploadDate(new Date(999)).build(),
                FileEntity.builder().name("bad").uploadDate(new Date(1501)).build(),
                FileEntity.builder().name("bad").uploadDate(new Date(1000)).build(),
                FileEntity.builder().name("bad").uploadDate(new Date(1500)).build(),
                FileEntity.builder().name("good").uploadDate(new Date(1250)).build()
        );

        List<FileEntity> filesFilteredByUploadDateRange =
                fileService.getFilesFilteredByUploadDateRange(uploadDateFrom, uploadDateTo, fileEntityList);

        assertEquals(filesFilteredByUploadDateRange.size(), 1);
        assertEquals(filesFilteredByUploadDateRange.get(0).getName(), "good");
    }

    @Test
    void getFilesFilteredByModifiedDateRange_shouldGetFilteredFiles_whenTwoDatesAreNotNull() {
        Date modifiedDateFrom = new Date(1000);
        Date modifiedDateTo = new Date(1500);
        List<FileEntity> fileEntityList = Arrays.asList(
                FileEntity.builder().name("bad").modifiedDate(new Date(999)).build(),
                FileEntity.builder().name("bad").modifiedDate(new Date(1501)).build(),
                FileEntity.builder().name("bad").modifiedDate(new Date(1000)).build(),
                FileEntity.builder().name("bad").modifiedDate(new Date(1500)).build(),
                FileEntity.builder().name("good").modifiedDate(new Date(1250)).build()
        );

        List<FileEntity> filesFilteredByModifiedDateRange =
                fileService.getFilesFilteredByModifiedDateRange(modifiedDateFrom, modifiedDateTo, fileEntityList);

        assertEquals(filesFilteredByModifiedDateRange.size(), 1);
        assertEquals(filesFilteredByModifiedDateRange.get(0).getName(), "good");
    }

    @Test
    void getFilesFilteredByExtensions_shouldGetFilteredFiles_whenExtensionsAreNotNull() {
        List<String> extensionsToFind = Arrays.asList("png", "jpg");
        List<FileEntity> fileEntityList = Arrays.asList(
                FileEntity.builder().name("bad").extension("pngarable").build(),
                FileEntity.builder().name("good").extension("png").build(),
                FileEntity.builder().name("good").extension("jpg").build()
        );

        List<FileEntity> filesFilteredByExtensions =
                fileService.getFilesFilteredByExtensions(extensionsToFind, fileEntityList);

        assertEquals(filesFilteredByExtensions.size(), 2);
        filesFilteredByExtensions.forEach((entity -> assertTrue(extensionsToFind.contains(entity.getExtension()))));
    }


    @Test
    void getFilteredFiles_shouldGetFilteredFilesAsDtoList_whenAllParamsIsOk() {
        String nameToFind = "test";
        Date uploadDateFrom = new Date(1000);
        Date uploadDateTo = new Date(1500);
        Date modifiedDateFrom = new Date(1250);
        Date modifiedDateTo = new Date(1750);
        List<String> extensionsToFind = Arrays.asList("png", "jpg");
        List<FileEntity> fileEntityList = Arrays.asList(
                FileEntity.builder().id(UUID.randomUUID()).name("test1good").extension("jpg").modifiedDate(new Date(1300)).uploadDate(new Date(1270)).build(),
                FileEntity.builder().id(UUID.randomUUID()).name("test1bad").extension("jpg").modifiedDate(new Date(1300)).uploadDate(new Date(900)).build(),
                FileEntity.builder().id(UUID.randomUUID()).name("test1bad").extension("png").modifiedDate(new Date(1800)).uploadDate(new Date(1270)).build(),
                FileEntity.builder().id(UUID.randomUUID()).name("test1bad").extension("wtf").modifiedDate(new Date(1300)).uploadDate(new Date(1270)).build(),
                FileEntity.builder().id(UUID.randomUUID()).name("bad").extension("png").modifiedDate(new Date(1300)).uploadDate(new Date(1270)).build(),
                FileEntity.builder().id(UUID.randomUUID()).name("bad").extension("jpg").modifiedDate(new Date(1300)).uploadDate(new Date(1270)).build()
        );
        Map<UUID, FileEntity> fileEntityMap = new HashMap<>();
        fileEntityList.forEach((entity -> fileEntityMap.put(entity.getId(), entity)));
        when(fileRepository.getAll()).thenReturn(fileEntityMap);

        List<FileDto> filteredFiles =
                fileService.getFilteredFiles(nameToFind,
                        uploadDateFrom, uploadDateTo,
                        modifiedDateFrom, modifiedDateTo,
                        extensionsToFind);

        assertEquals(1, filteredFiles.size());
        assertEquals("test1good", filteredFiles.get(0).getName());
    }

    @Test
    void getFilteredFiles_shouldGetFilteredFilesAsDtoList_whenSomeDateIsMissing() {
        String nameToFind = "test";
        Date uploadDateFrom = new Date(1000);
        Date uploadDateTo = null;
        Date modifiedDateFrom = null;
        Date modifiedDateTo = null;
        List<String> extensionsToFind = Arrays.asList("png", "jpg");
        List<FileEntity> fileEntityList = Arrays.asList(
                FileEntity.builder().id(UUID.randomUUID()).name("test1good").extension("jpg").modifiedDate(new Date(1300)).uploadDate(new Date(1270)).build(),
                FileEntity.builder().id(UUID.randomUUID()).name("test1good").extension("jpg").modifiedDate(new Date(1300)).uploadDate(new Date(1300)).build(),
                FileEntity.builder().id(UUID.randomUUID()).name("test1bad").extension("png").modifiedDate(new Date(900)).uploadDate(new Date(900)).build(),
                FileEntity.builder().id(UUID.randomUUID()).name("test1bad").extension("wtf").modifiedDate(new Date(900)).uploadDate(new Date(900)).build(),
                FileEntity.builder().id(UUID.randomUUID()).name("bad").extension("png").modifiedDate(new Date(900)).uploadDate(new Date(900)).build(),
                FileEntity.builder().id(UUID.randomUUID()).name("bad").extension("jpg").modifiedDate(new Date(900)).uploadDate(new Date(900)).build()
        );
        Map<UUID, FileEntity> fileEntityMap = new HashMap<>();
        fileEntityList.forEach((entity -> fileEntityMap.put(entity.getId(), entity)));
        when(fileRepository.getAll()).thenReturn(fileEntityMap);

        List<FileDto> filteredFiles =
                fileService.getFilteredFiles(nameToFind,
                        uploadDateFrom, uploadDateTo,
                        modifiedDateFrom, modifiedDateTo,
                        extensionsToFind);

        assertEquals(2, filteredFiles.size());
        filteredFiles.forEach(entity -> assertEquals("test1good", entity.getName()));
    }
}