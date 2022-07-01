package com.example.fileVault.repository;

import com.example.fileVault.entity.FileEntity;
import com.example.fileVault.exception.EmptyFileListException;
import com.example.fileVault.exception.FileNotFoundException;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FileSystemStorageRepository {

    // TODO: use Linked List or better Map

    private final List<FileEntity> fileEntityList = new LinkedList<>();

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
        fileEntityList.add(newFile);
        return newFile;
    }

    public List<FileEntity> getAll() {
        if (fileEntityList.size() == 0) {
            throw new EmptyFileListException("File list is empty");
        }
        return fileEntityList;
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
        for (FileEntity file :
                this.fileEntityList) {

            //TODO: use @slf4j from lombok to log
            System.out.println(file.getId());
            System.out.println(id);
            System.out.println();
            if (file.getId().equals(id)) {
                return file;
            }
        }
        throw new FileNotFoundException("file not found by id");
    }

    public UUID deleteById(UUID id) {
        if (fileEntityList.removeIf(entity -> entity.getId().equals(id)))
            return id;
        throw new FileNotFoundException("Nothing to delete");
    }

}
