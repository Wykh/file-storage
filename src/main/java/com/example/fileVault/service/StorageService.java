package com.example.fileVault.service;

import com.example.fileVault.entity.FileEntity;
import com.example.fileVault.exception.BadFileTypeException;
import com.example.fileVault.exception.EmptyFileListException;
import com.example.fileVault.exception.EmptyFileNameException;
import com.example.fileVault.exception.FileNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface StorageService {

    public UUID create(MultipartFile file) throws EmptyFileNameException, BadFileTypeException, IOException;

    public List<FileEntity> readAll() throws EmptyFileListException;

    public FileEntity readOne(UUID id) throws FileNotFoundException;

    public ResponseEntity<?> downloadOne(UUID id) throws FileNotFoundException;

    public ResponseEntity<?> downloadBrunch(UUID id);

    public Date update(UUID id, MultipartFile file) throws FileNotFoundException, EmptyFileNameException, BadFileTypeException, IOException;

    public UUID delete(UUID id) throws FileNotFoundException;
}
