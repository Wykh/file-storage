package com.example.file.vault.repository;

import com.example.file.vault.entity.FileEntity;
import com.example.file.vault.exception.EmptyFileListException;
import com.example.file.vault.exception.FileNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

// Arrange, act, assert
class FileSystemFileRepositoryTest {

    private FileSystemFileRepository repository;
    private UUID hardcodedId = UUID.fromString("1-1-1-1-1");

    @BeforeEach
    void init() {
        repository = new FileSystemFileRepository();
    }

    @Test
    void create_shouldCreateEntity_whenAllParamsOk() {
        // arrange
        String testName = "testName";
        String testExtension = "testExtension";
        String testComment = "testComment";
        byte[] testContent = new byte[100];

        // act
        FileEntity actual = repository.create(testName, testExtension, testComment, testContent);

        // assert
        assertNotNull(actual);
    }

    @Test
    void test_test() {
        // arrange
        String testName = "testName";
        String testExtension = "testExtension";
        String testComment = "testComment";
        byte[] testContent = new byte[100];

        // act
        FileEntity actual = repository.create(testName, testExtension, testComment, testContent);
        FileEntity expected = repository.create(testName, testExtension, testComment, testContent);
        UUID newid = UUID.fromString("1-1-1-1-1");
        System.out.println(newid.toString());
        actual.setId(newid);
        expected.setId(newid);

        // assert
        assertNotNull(actual);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void getAll_shouldGetAllEntities_whenRepositoryNotEmpty() {
        // arrange
        int elemsCount = 3;

        // act
        Map<UUID, FileEntity> actualMapByCreate = new HashMap<>();
        for (int i = 0; i < elemsCount; i++) {
            FileEntity fileEntity = repository.create(anyString(), anyString(), anyString(), new byte[10]);
            actualMapByCreate.put(fileEntity.getId(), fileEntity);
        }
        Map<UUID, FileEntity> actualMapByGetAll = repository.getAll();

        // assert
        assertEquals(elemsCount, actualMapByGetAll.size());
        assertEquals(actualMapByCreate, actualMapByGetAll);
    }

    @Test
    void getAll_shouldThrowException_whenRepositoryEmpty() {
        // act
        assertThrows(EmptyFileListException.class, () -> repository.getAll());
    }

    @Test
    void findById_shouldFindEntity_whenEntityExists() {
        FileEntity actual = repository.create(anyString(), anyString(), anyString(), new byte[10]);

        FileEntity foundEntity = repository.findById(actual.getId());

        assertEquals(foundEntity, actual);
    }

    @Test
    void findById_shouldThrowException_whenEntityMissing() {
        assertThrows(FileNotFoundException.class, () -> repository.findById(hardcodedId));
    }

    @Test
    void updateById_shouldUpdateEntity_whenEntityFound() {
        String newName = "updatedName";
        String newComment = "updatedComment";

        FileEntity actual = repository.create("testName", anyString(), "testComment", new byte[10]);
        FileEntity updatedActual = repository.updateById(actual.getId(), newName, newComment);

        assertEquals(actual, updatedActual);
        assertEquals(updatedActual.getName(), newName);
        assertEquals(updatedActual.getComment(), newComment);
    }

    @Test
    void deleteById_shouldDeleteEntity_whenEntityFound() {
        FileEntity actual = repository.create(anyString(), anyString(), anyString(), new byte[10]);

        FileEntity deletedEntity = repository.deleteById(actual.getId());
        assertThrows(FileNotFoundException.class, () -> repository.findById(deletedEntity.getId()));
    }
}