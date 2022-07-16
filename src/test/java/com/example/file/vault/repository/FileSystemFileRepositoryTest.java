package com.example.file.vault.repository;

import com.example.file.vault.entity.FileEntity;
import com.example.file.vault.exception.EmptyFileListException;
import com.example.file.vault.exception.FileNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

// Arrange, act, assert
class FileSystemFileRepositoryTest {

    private FileSystemFileRepository repository;

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
    void getAll_shouldGetAllEntities_whenRepositoryNotEmpty() {
        // arrange
        int elemsCount = 3;

        // act
        for (int i = 0; i < elemsCount; i++) {
            repository.create(anyString(), anyString(), anyString(), new byte[10]);
        }
        Map<UUID, FileEntity> actualMap = repository.getAll();

        // assert
        assertEquals(elemsCount, actualMap.size());
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
        assertThrows(FileNotFoundException.class, () -> repository.findById(UUID.randomUUID()));
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