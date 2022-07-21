package com.example.filevault.service;

import com.example.filevault.constants.FileVaultConstants;
import com.example.filevault.controller.FilesFilterParams;
import com.example.filevault.dto.FileDto;
import com.example.filevault.entity.FileEntity;
import com.example.filevault.exception.BadFileTypeException;
import com.example.filevault.exception.EmptyFileNameException;
import com.example.filevault.exception.TooLargeFileSizeException;
import com.example.filevault.repository.FileSystemFileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

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
    void upload_Success_shouldUploadFile_whenAllIsOk()  {
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
                fileService.getFilteredFiles(
                        FilesFilterParams.builder().name(nameToFind).build()
                );

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
                fileService.getFilteredFiles(
                        FilesFilterParams.builder().uploadDateFrom(uploadDateFrom).build()
                );

        assertThat(filesFilteredByUploadDateRange.size()).isEqualTo(2);
        filesFilteredByUploadDateRange.forEach(model -> assertThat(model.getName()).isEqualTo("good"));
    }

    @Test
    void getFilteredFiles_shouldGetFiles_whenOnlyUploadDateToPassed() {
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
                fileService.getFilteredFiles(
                        FilesFilterParams.builder().uploadDateTo(uploadDateTo).build()
                );

        assertThat(filesFilteredByUploadDateRange.size()).isEqualTo(2);
        filesFilteredByUploadDateRange.forEach(model -> assertThat(model.getName()).isEqualTo("good"));
    }

    @Test
    void getFilteredFiles_shouldGetFiles_whenOnlyModifiedDateFromPassed() {
        Date modifiedDateFrom = new Date(1500);
        List<FileEntity> fileEntityList = Arrays.asList(
                FileEntity.builder().name("good").modifiedDate(new Date(1500)).build(),
                FileEntity.builder().name("good").modifiedDate(new Date(1501)).build(),
                FileEntity.builder().name("bad").modifiedDate(new Date(999)).build(),
                FileEntity.builder().name("bad").modifiedDate(new Date(1000)).build(),
                FileEntity.builder().name("bad").modifiedDate(new Date(1250)).build()
        );
        Map<UUID, FileEntity> fileEntityMap = fileEntityList
                .stream()
                .collect(Collectors.toMap(FileEntity::getId, Function.identity()));
        when(fileRepository.getAll()).thenReturn(fileEntityMap);

        List<FileDto> filesFilteredByModifiedDateRange =
                fileService.getFilteredFiles(
                        FilesFilterParams.builder().modifiedDateFrom(modifiedDateFrom).build()
                );

        assertThat(filesFilteredByModifiedDateRange.size()).isEqualTo(2);
        filesFilteredByModifiedDateRange.forEach(model -> assertThat(model.getName()).isEqualTo("good"));
    }

    @Test
    void getFilteredFiles_shouldGetFiles_whenOnlyModifiedDateToPassed() {
        Date modifiedDateTo = new Date(1000);
        List<FileEntity> fileEntityList = Arrays.asList(
                FileEntity.builder().name("good").modifiedDate(new Date(999)).build(),
                FileEntity.builder().name("good").modifiedDate(new Date(1000)).build(),
                FileEntity.builder().name("bad").modifiedDate(new Date(1500)).build(),
                FileEntity.builder().name("bad").modifiedDate(new Date(1501)).build(),
                FileEntity.builder().name("bad").modifiedDate(new Date(1250)).build()
        );
        Map<UUID, FileEntity> fileEntityMap = fileEntityList
                .stream()
                .collect(Collectors.toMap(FileEntity::getId, Function.identity()));
        when(fileRepository.getAll()).thenReturn(fileEntityMap);

        List<FileDto> filesFilteredByModifiedDateRange =
                fileService.getFilteredFiles(
                        FilesFilterParams.builder().modifiedDateTo(modifiedDateTo).build()
                );

        assertThat(filesFilteredByModifiedDateRange.size()).isEqualTo(2);
        filesFilteredByModifiedDateRange.forEach(model -> assertThat(model.getName()).isEqualTo("good"));
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
                fileService.getFilteredFiles(
                        FilesFilterParams.builder().uploadDateFrom(uploadDateFrom).uploadDateTo(uploadDateTo).build()
                );

        assertThat(filesFilteredByUploadDateRange.size()).isEqualTo(3);
        filesFilteredByUploadDateRange
                .forEach(model -> assertThat(model.getName()).isEqualTo("good"));
    }

    @Test
    void getFilteredFiles_shouldGetFiles_whenOnlyModifiedDatesPassed() {
        Date modifiedDateFrom = new Date(1250);
        Date modifiedDateTo = new Date(1300);
        List<FileEntity> fileEntityList = Arrays.asList(
                FileEntity.builder().name("good").modifiedDate(new Date(1250)).build(),
                FileEntity.builder().name("good").modifiedDate(new Date(1275)).build(),
                FileEntity.builder().name("good").modifiedDate(new Date(1300)).build(),
                FileEntity.builder().name("bad").modifiedDate(new Date(999)).build(),
                FileEntity.builder().name("bad").modifiedDate(new Date(1000)).build(),
                FileEntity.builder().name("bad").modifiedDate(new Date(1500)).build(),
                FileEntity.builder().name("bad").modifiedDate(new Date(1501)).build()
        );
        Map<UUID, FileEntity> fileEntityMap = fileEntityList
                .stream()
                .collect(Collectors.toMap(FileEntity::getId, Function.identity()));
        when(fileRepository.getAll()).thenReturn(fileEntityMap);

        List<FileDto> filesFilteredByModifiedDateRange =
                fileService.getFilteredFiles(
                        FilesFilterParams.builder().modifiedDateFrom(modifiedDateFrom).modifiedDateTo(modifiedDateTo).build()
                );

        assertThat(filesFilteredByModifiedDateRange.size()).isEqualTo(3);
        filesFilteredByModifiedDateRange
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
                fileService.getFilteredFiles(
                        FilesFilterParams.builder().extensions(extensionsToFind).build()
                );

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
                fileService.getFilteredFiles(
                        FilesFilterParams.builder()
                                .name(nameToFind)
                                .uploadDateFrom(uploadDateFrom)
                                .uploadDateTo(uploadDateTo)
                                .modifiedDateFrom(modifiedDateFrom)
                                .modifiedDateTo(modifiedDateTo)
                                .extensions(extensionsToFind)
                                .build()
                );

        assertThat(filteredFiles.size()).isEqualTo(1);
        assertThat(filteredFiles.get(0).getName()).isEqualTo("test1good");
    }

    @Test
    void getFilteredFiles_shouldGetFilteredFilesAsDtoList_whenNoParamsPassed() {
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
                fileService.getFilteredFiles(
                        FilesFilterParams.builder().build()
                );

        assertThat(fileEntityList.size()).isEqualTo(filteredFiles.size());
    }
}
