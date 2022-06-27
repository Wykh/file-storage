package com.example.fileVault.repository;

import com.example.fileVault.entity.FileEntity;
import com.example.fileVault.exception.EmptyFileListException;
import com.example.fileVault.exception.FileNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class FileSystemStorageRepo {

    List<FileEntity> fileEntityList = new ArrayList<>();

    public UUID create(String name, String type, byte[] content) {
        UUID randUUID = UUID.randomUUID();
        fileEntityList.add(new FileEntity() {
            {
                setId(randUUID);
                setName(name);
                setUploadDate(new Date());
                setModifiedDate(new Date());
                setContent(content);
                setType(type);
            }
        });
        return randUUID;
    }

    public List<FileEntity> readAll() throws EmptyFileListException {
        if (fileEntityList.size() == 0) {
            throw new EmptyFileListException("File list is empty");
        }
        return fileEntityList;
    }

    public Date updateByUUID(UUID id, String name, String type, byte[] newContent) throws FileNotFoundException {
        FileEntity fileToUpdate = findByUUID(id);
        fileToUpdate.setContent(newContent);
        fileToUpdate.setName(name);
        fileToUpdate.setType(type);
        fileToUpdate.setModifiedDate(new Date());
        return fileToUpdate.getModifiedDate();
    }

    public FileEntity findByUUID(UUID uuid) throws FileNotFoundException {
        for (FileEntity file :
                this.fileEntityList) {
            System.out.println(file.getId());
            System.out.println(uuid);
            System.out.println();
            if (file.getId().equals(uuid)) {
                return file;
            }
        }
        throw new FileNotFoundException("file not found by uuid");
    }

    public UUID deleteByUUID(UUID uuid) throws FileNotFoundException {
        for (int i = 0; i < fileEntityList.size(); i++) {
            if (fileEntityList.get(i).getId().equals(uuid)) {
                UUID deletedUUID = fileEntityList.get(i).getId();
                fileEntityList.remove(i);
                return deletedUUID;
            }
        }
        throw new FileNotFoundException("Nothing to delete");
    }

}
