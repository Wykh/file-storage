package com.example.fileVault.service;

import com.example.fileVault.model.FileModel;
import com.example.fileVault.model.FileModelNameAndId;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FileService {

    public FileModel create(MultipartFile file, String comment);

    public List<FileModel> getAll();

    public FileModel get(UUID id);

    public HttpEntity<byte[]> download(UUID id);

    public ResponseEntity<?> downloadZipBunch(UUID id);

    FileModel update(UUID id, String newFileName, String newComment);

    public FileModel delete(UUID id);

    List<FileModelNameAndId> getNamesAndIds();
}
