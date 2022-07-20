package com.example.file.vault.service;

import com.example.file.vault.constants.FileVaultConstants;
import com.example.file.vault.dto.FileDto;
import com.example.file.vault.entity.FileEntity;
import com.example.file.vault.exception.BadFileTypeException;
import com.example.file.vault.exception.EmptyFileNameException;
import com.example.file.vault.exception.TooLargeFileSizeException;
import com.example.file.vault.repository.FileSystemFileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
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
        assertThatExceptionOfType(TooLargeFileSizeException.class)
                .isThrownBy(() -> fileService.upload(testMultipartFile, "Test comment"));
    }

    @Test
    void upload_Fail_shouldThrowException_whenBadFileName() {
        // TODO: Test png, img and other extensions
        MockMultipartFile testMultipartFile = new MockMultipartFile(
                "foo", "foo.", MediaType.ALL_VALUE, new byte[1]);

        assertThatExceptionOfType(BadFileTypeException.class)
                .isThrownBy(() -> fileService.upload(testMultipartFile, "Test comment"));
    }

    @Test
    void upload_Fail_shouldThrowException_whenFileNameIsEmpty() {
        MockMultipartFile testMultipartFile = new MockMultipartFile(
                "foo", "", MediaType.ALL_VALUE, new byte[1]);

        assertThatExceptionOfType(EmptyFileNameException.class)
                .isThrownBy(() -> fileService.upload(testMultipartFile, "Test comment"));
    }

    @Test
    void getEntity_Success_shouldRecordById_whenAllOk() {
        FileEntity found = mock(FileEntity.class);
        when(fileRepository.findById(any(UUID.class))).thenReturn(found);
        UUID randomId = UUID.randomUUID();

        FileEntity actual = fileService.getEntity(randomId);

        verify(fileRepository).findById(randomId);
        assertThat(actual).isNotNull();
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
    void getFilteredFiles_shouldGetFiles_whenOnlyNamePassed() {
        String nameToFind = "test";
        List<String> listOfGoodNames = Arrays.asList("test", "TEST", "nameOfTest");
        List<FileEntity> fileEntityList = Arrays.asList(
                FileEntity.builder().name(listOfGoodNames.get(0)).build(),
                FileEntity.builder().name(listOfGoodNames.get(1)).build(),
                FileEntity.builder().name(listOfGoodNames.get(2)).build(),
                FileEntity.builder().name("BadName").build()
        );
        Map<UUID, FileEntity> fileEntityMap = fileEntityList
                .stream()
                .collect(Collectors.toMap(FileEntity::getId, Function.identity()));
        when(fileRepository.getAll()).thenReturn(fileEntityMap);

        List<FileDto> filesFilteredByName =
                fileService.getFilteredFiles(nameToFind,
                        null, null,
                        null, null,
                        null);

        assertThat(filesFilteredByName.size()).isEqualTo(3);
        filesFilteredByName.forEach((model) -> assertThat(listOfGoodNames.contains(model.getName())).isTrue());
    }

    @Test
    void getFilteredFiles_shouldGetFiles_whenOnlyUploadDateFromPassed() {
        Date uploadDateFrom = new Date(1500);
        List<FileEntity> fileEntityList = Arrays.asList(
                FileEntity.builder().name("good").uploadDate(new Date(1500)).build(),
                FileEntity.builder().name("good").uploadDate(new Date(1501)).build(),
                FileEntity.builder().name("bad").uploadDate(new Date(999)).build(),
                FileEntity.builder().name("bad").uploadDate(new Date(1000)).build(),
                FileEntity.builder().name("bad").uploadDate(new Date(1250)).build()
        );
        Map<UUID, FileEntity> fileEntityMap = fileEntityList
                .stream()
                .collect(Collectors.toMap(FileEntity::getId, Function.identity()));
        when(fileRepository.getAll()).thenReturn(fileEntityMap);

        List<FileDto> filesFilteredByUploadDateRange =
                fileService.getFilteredFiles(null,
                        uploadDateFrom, null,
                        null, null,
                        null);

        assertThat(filesFilteredByUploadDateRange.size()).isEqualTo(2);
        filesFilteredByUploadDateRange.forEach(model -> assertThat(model.getName()).isEqualTo("good"));
    }

    @Test
    void getFilteredFiles_shouldGetFiles_whenOnlyUploadToFromPassed() {
        Date uploadDateTo = new Date(1000);
        List<FileEntity> fileEntityList = Arrays.asList(
                FileEntity.builder().name("good").uploadDate(new Date(999)).build(),
                FileEntity.builder().name("good").uploadDate(new Date(1000)).build(),
                FileEntity.builder().name("bad").uploadDate(new Date(1500)).build(),
                FileEntity.builder().name("bad").uploadDate(new Date(1501)).build(),
                FileEntity.builder().name("bad").uploadDate(new Date(1250)).build()
        );
        Map<UUID, FileEntity> fileEntityMap = fileEntityList
                .stream()
                .collect(Collectors.toMap(FileEntity::getId, Function.identity()));
        when(fileRepository.getAll()).thenReturn(fileEntityMap);

        List<FileDto> filesFilteredByUploadDateRange =
                fileService.getFilteredFiles(null,
                        null, uploadDateTo,
                        null, null,
                        null);

        assertThat(filesFilteredByUploadDateRange.size()).isEqualTo(2);
        filesFilteredByUploadDateRange.forEach(model -> assertThat(model.getName()).isEqualTo("good"));
    }

    @Test
    void getFilteredFiles_shouldGetFiles_whenOnlyUploadDatesPassed() {
        Date uploadDateFrom = new Date(1250);
        Date uploadDateTo = new Date(1300);
        List<FileEntity> fileEntityList = Arrays.asList(
                FileEntity.builder().name("good").uploadDate(new Date(1250)).build(),
                FileEntity.builder().name("good").uploadDate(new Date(1275)).build(),
                FileEntity.builder().name("good").uploadDate(new Date(1300)).build(),
                FileEntity.builder().name("bad").uploadDate(new Date(999)).build(),
                FileEntity.builder().name("bad").uploadDate(new Date(1000)).build(),
                FileEntity.builder().name("bad").uploadDate(new Date(1500)).build(),
                FileEntity.builder().name("bad").uploadDate(new Date(1501)).build()
        );
        Map<UUID, FileEntity> fileEntityMap = fileEntityList
                .stream()
                .collect(Collectors.toMap(FileEntity::getId, Function.identity()));
        when(fileRepository.getAll()).thenReturn(fileEntityMap);

        List<FileDto> filesFilteredByUploadDateRange =
                fileService.getFilteredFiles(null,
                        uploadDateFrom, uploadDateTo,
                        null, null,
                        null);

        assertThat(filesFilteredByUploadDateRange.size()).isEqualTo(3);
        filesFilteredByUploadDateRange
                .forEach(model -> assertThat(model.getName()).isEqualTo("good"));
    }

    @Test
    void getFilteredFiles_shouldGetFiles_whenOnlyExtensionsPassed() {
        List<String> extensionsToFind = Arrays.asList("png", "jpg");

        List<FileEntity> fileEntityList = Arrays.asList(
                FileEntity.builder().name("bad").extension("pngjpable").build(),
                FileEntity.builder().name("good").extension("png").build(),
                FileEntity.builder().name("good").extension("jpg").build()
        );
        Map<UUID, FileEntity> fileEntityMap = fileEntityList
                .stream()
                .collect(Collectors.toMap(FileEntity::getId, Function.identity()));
        when(fileRepository.getAll()).thenReturn(fileEntityMap);

        List<FileDto> filesFilteredByExtensions =
                fileService.getFilteredFiles(null,
                        null, null,
                        null, null,
                        extensionsToFind);

        assertThat(filesFilteredByExtensions.size()).isEqualTo(2);
        filesFilteredByExtensions
                .forEach(model -> assertThat(model.getName()).isEqualTo("good"));
    }

    @Test
    void getFilteredFiles_shouldGetFilteredFilesAsDtoList_whenAllParamsPassed() {
        String nameToFind = "test";
        Date uploadDateFrom = new Date(1000);
        Date uploadDateTo = new Date(1500);
        Date modifiedDateFrom = new Date(1250);
        Date modifiedDateTo = new Date(1750);
        List<String> extensionsToFind = Arrays.asList("png", "jpg");
        List<FileEntity> fileEntityList = Arrays.asList(
                FileEntity.builder().name("test1good").extension("jpg").modifiedDate(new Date(1300)).uploadDate(new Date(1270)).build(),
                FileEntity.builder().name("test1bad").extension("jpg").modifiedDate(new Date(1300)).uploadDate(new Date(900)).build(),
                FileEntity.builder().name("test1bad").extension("png").modifiedDate(new Date(1800)).uploadDate(new Date(1270)).build(),
                FileEntity.builder().name("test1bad").extension("wtf").modifiedDate(new Date(1300)).uploadDate(new Date(1270)).build(),
                FileEntity.builder().name("bad").extension("png").modifiedDate(new Date(1300)).uploadDate(new Date(1270)).build(),
                FileEntity.builder().name("bad").extension("jpg").modifiedDate(new Date(1300)).uploadDate(new Date(1270)).build()
        );
        Map<UUID, FileEntity> fileEntityMap = fileEntityList
                .stream()
                .collect(Collectors.toMap(FileEntity::getId, Function.identity()));
        when(fileRepository.getAll()).thenReturn(fileEntityMap);

        List<FileDto> filteredFiles =
                fileService.getFilteredFiles(nameToFind,
                        uploadDateFrom, uploadDateTo,
                        modifiedDateFrom, modifiedDateTo,
                        extensionsToFind);

        assertThat(filteredFiles.size()).isEqualTo(1);
        assertThat(filteredFiles.get(0).getName()).isEqualTo("test1good");
    }

    @Test
    void getFilteredFiles_shouldGetFilteredFilesAsDtoList_whenSomeDateIsMissing() {
        List<FileEntity> fileEntityList = Arrays.asList(
                FileEntity.builder().id(UUID.randomUUID()).name("test1").extension("jpg").modifiedDate(new Date(1300)).uploadDate(new Date(1270)).build(),
                FileEntity.builder().id(UUID.randomUUID()).name("test1").extension("jpg").modifiedDate(new Date(1300)).uploadDate(new Date(1300)).build(),
                FileEntity.builder().id(UUID.randomUUID()).name("test1").extension("png").modifiedDate(new Date(900)).uploadDate(new Date(900)).build(),
                FileEntity.builder().id(UUID.randomUUID()).name("test1").extension("wtf").modifiedDate(new Date(900)).uploadDate(new Date(900)).build(),
                FileEntity.builder().id(UUID.randomUUID()).name("wow").extension("png").modifiedDate(new Date(900)).uploadDate(new Date(900)).build(),
                FileEntity.builder().id(UUID.randomUUID()).name("bird").extension("jpg").modifiedDate(new Date(900)).uploadDate(new Date(900)).build()
        );
        Map<UUID, FileEntity> fileEntityMap = fileEntityList
                .stream()
                .collect(Collectors.toMap(FileEntity::getId, Function.identity()));
        when(fileRepository.getAll()).thenReturn(fileEntityMap);

        List<FileDto> filteredFiles =
                fileService.getFilteredFiles(null,
                        null, null,
                        null, null,
                        null);

        assertThat(fileEntityList.size()).isEqualTo(filteredFiles.size());
    }
}