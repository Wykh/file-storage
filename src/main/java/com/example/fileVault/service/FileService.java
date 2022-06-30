package com.example.fileVault.service;

import com.example.fileVault.entity.FileEntity;
import com.example.fileVault.model.FileDto;
import com.example.fileVault.model.FileModelNameAndId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FileService {

    public FileDto create(MultipartFile file, String comment);

    public List<FileDto> getAll();

    public FileDto get(UUID id);

    public FileEntity download(UUID id);

    public ResponseEntity<?> downloadZipBunch(UUID id);

    FileDto update(UUID id, String newFileName, String newComment);

    public FileDto delete(UUID id);

    List<FileModelNameAndId> getNamesAndIds();
}
