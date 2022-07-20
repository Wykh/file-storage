package com.example.file.vault.repository;

import com.example.file.vault.entity.FileEntity;
import com.example.file.vault.exception.EmptyFileListException;
import com.example.file.vault.exception.FileNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


// Arrange, act, assert
@ExtendWith(MockitoExtension.class)
class FileSystemFileRepositoryTest {

    private FileSystemFileRepository repository;
    private final UUID hardcodedId = UUID.fromString("1-1-1-1-1");

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
        assertThat(actual).isNotNull();
    }

    @Test
    void getAll_shouldGetAllEntities_whenRepositoryNotEmpty() {
        // arrange
        int elemsCount = 3;

        // act
        Map<UUID, FileEntity> actualMapByCreate = new HashMap<>();
        for (int i = 0; i < elemsCount; i++) {
            FileEntity fileEntity = repository.create("name", "extension", "comment", new byte[10]);
            actualMapByCreate.put(fileEntity.getId(), fileEntity);
        }
        Map<UUID, FileEntity> actualMapByGetAll = repository.getAll();

        // assert
        assertThat(actualMapByGetAll.size()).isEqualTo(elemsCount);
        assertThat(actualMapByGetAll).isEqualTo(actualMapByCreate);
    }

    @Test
    void getAll_shouldThrowException_whenRepositoryEmpty() {
        // act
        assertThatExceptionOfType(EmptyFileListException.class)
                .isThrownBy(() -> repository.getAll());
    }

    @Test
    void findById_shouldFindEntity_whenEntityExists() {
        FileEntity actual = repository.create("name", "extension", "comment", new byte[10]);

        FileEntity foundEntity = repository.findById(actual.getId());

        assertThat(foundEntity).isEqualTo(actual);
    }

    @Test
    void findById_shouldThrowException_whenEntityMissing() {
        assertThatExceptionOfType(FileNotFoundException.class)
                .isThrownBy(() -> repository.findById(hardcodedId));
    }

    @Test
    void updateById_shouldUpdateEntity_whenEntityFound() throws InterruptedException {
        String extension = "testExtension";
        byte[] content = new byte[1];
        String newName = "updatedName";
        String newComment = "updatedComment";

        FileEntity expected = FileEntity.builder()
                .name(newName)
                .extension(extension)
                .comment(newComment)
                .size(1)
                .content(content)
                .build();

        FileEntity actual = repository.create("oldName", extension, "oldComment", content);
        Date actualModifiedDateBeforeUpdate = actual.getModifiedDate();
        Thread.sleep(1); // иначе даты до и после могут быть равны, если выполнение пройдёт слишком быстро
        FileEntity updatedActual = repository.updateById(actual.getId(), newName, newComment);
        Date actualModifiedDateAfterUpdate = updatedActual.getModifiedDate();

        assertThat(actualModifiedDateAfterUpdate).isAfter(actualModifiedDateBeforeUpdate);
        assertThat(updatedActual)
                .usingRecursiveComparison()
                .ignoringFields("id", "modifiedDate", "uploadDate")
                .isEqualTo(expected);
    }

    @Test
    void deleteById_shouldDeleteEntity_whenEntityFound() {
        FileEntity actual = repository.create("name", "extension", "comment", new byte[10]);
        FileEntity deletedEntity = repository.deleteById(actual.getId());
        assertThatExceptionOfType(FileNotFoundException.class)
                .isThrownBy(() -> repository.findById(deletedEntity.getId()));
    }
}