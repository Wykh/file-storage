package com.example.fileVault.service;

import com.example.fileVault.entity.FileEntity;
import com.example.fileVault.exception.BadFileTypeException;
import com.example.fileVault.exception.EmptyFileListException;
import com.example.fileVault.exception.EmptyFileNameException;
import com.example.fileVault.exception.FileNotFoundException;
import com.example.fileVault.repository.FileSystemStorageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class FileSystemStorageService implements StorageService {

    @Autowired
    private FileSystemStorageRepo storageRepo;

    @Override
    public UUID create(MultipartFile file) throws EmptyFileNameException, BadFileTypeException, IOException {
        String fullFileName = file.getOriginalFilename();
        List<String> nameAndType = getNameAndType(fullFileName);
        String fileName = nameAndType.get(0);
        String fileType = nameAndType.get(1);
        return storageRepo.create(fileName, fileType, file.getBytes());
    }

    @Override
    public List<FileEntity> readAll() throws EmptyFileListException {
        return storageRepo.readAll();
    }

    @Override
    public FileEntity readOne(UUID id) throws FileNotFoundException {
        return storageRepo.findByUUID(id);
    }

    @Override
    public ResponseEntity<?> downloadOne(UUID id) throws FileNotFoundException {
        FileEntity fileToServe = storageRepo.findByUUID(id);
        Resource file = new ByteArrayResource(fileToServe.getContent());
        System.out.println("Filename method of resource: " + file.getFilename());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileToServe.getName() + "." + fileToServe.getType() + "\"").body(file);
    }

    @Override
    public ResponseEntity<?> downloadBrunch(UUID id) {
        return null;
    }

    @Override
    public Date update(UUID id, MultipartFile file) throws FileNotFoundException, EmptyFileNameException, BadFileTypeException, IOException {
        String fullFileName = file.getOriginalFilename();
        List<String> nameAndType = getNameAndType(fullFileName);
        String newFileName = nameAndType.get(0);
        String newFileType = nameAndType.get(1);
        byte[] newContent = file.getBytes();

        return storageRepo.updateByUUID(id, newFileName, newFileType, newContent);
    }

    @Override
    public UUID delete(UUID id) throws FileNotFoundException {
        return storageRepo.deleteByUUID(id);
    }

    private static List<String> getNameAndType(String fileName) throws EmptyFileNameException, BadFileTypeException {
        List<String> result = new ArrayList<>();
        if (fileName.length() == 0) {
            throw new EmptyFileNameException("File name is empty");
        }

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            result.add(fileName);
            result.add("");
            return result;
        }

        result.add(fileName.substring(0, dotIndex));
        if (fileName.length() == dotIndex + 1)
            throw new BadFileTypeException("Bad file type"); // exception if name.
        result.add(fileName.substring(dotIndex + 1));
        return result;
    }
}
