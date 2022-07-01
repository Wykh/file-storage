package com.example.fileVault.repository;

import com.example.fileVault.entity.FileEntity;
import com.example.fileVault.exception.EmptyFileListException;
import com.example.fileVault.exception.FileNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class FileSystemStorageRepository {

    // TODO: use Linked List or better Map -- ok

    private final Map<UUID, FileEntity> fileEntityMap = new HashMap<>();

    public FileEntity create(String name, String type, String comment, byte[] content) {
        // TODO: use lombok and @Builder -- ok

        FileEntity newFile = FileEntity.builder()
                .id(UUID.randomUUID())
                .name(name)
                .uploadDate(new Date())
                .modifiedDate(new Date())
                .comment(comment)
                .content(content)
                .size(content.length)
                .extension(type)
                .build();

        // TODO: убрать этот тип инициализации -- ok его тут больше нет
        fileEntityMap.put(newFile.getId(), newFile);
        return newFile;
    }

    public Map<UUID, FileEntity> getAll() {
        if (fileEntityMap.size() == 0) {
            throw new EmptyFileListException("File map is empty");
        }
        return fileEntityMap;
    }

    public FileEntity updateById(UUID id, String name, String comment) {
        // TODO: Do not update content, type -- ok

        FileEntity fileToUpdate = findById(id);
        fileToUpdate.setName(name);
        fileToUpdate.setComment(comment);
        fileToUpdate.setModifiedDate(new Date());
        return fileToUpdate;
    }

    // TODO: don't use uuid use id -- ok
    public FileEntity findById(UUID id) {
        FileEntity fileToUpdate = fileEntityMap.get(id);
        if (fileToUpdate == null) {
            throw new FileNotFoundException("file not found by id");
        }
        return fileToUpdate;
    }

    public FileEntity deleteById(UUID id) {
        FileEntity deletedEntity = fileEntityMap.remove(id);
        if (deletedEntity == null)
            throw new FileNotFoundException("Id to delete not found");
        return deletedEntity;
    }
}
