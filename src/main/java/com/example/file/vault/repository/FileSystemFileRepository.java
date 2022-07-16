package com.example.file.vault.repository;

import com.example.file.vault.entity.FileEntity;
import com.example.file.vault.exception.EmptyFileListException;
import com.example.file.vault.exception.FileNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class FileSystemFileRepository {

    private final Map<UUID, FileEntity> fileEntityMap = new HashMap<>();

    public FileEntity create(String name, String type, String comment, byte[] content) {
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
        FileEntity fileToUpdate = findById(id);
        fileToUpdate.setName(name);
        fileToUpdate.setComment(comment);
        fileToUpdate.setModifiedDate(new Date());
        return fileToUpdate;
    }

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
