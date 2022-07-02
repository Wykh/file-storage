package com.example.fileVault.service;

import com.example.fileVault.entity.FileEntity;
import com.example.fileVault.dto.FileDto;
import com.example.fileVault.dto.FileNameById;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FileService {

    FileDto create(MultipartFile file, String comment);

    List<FileDto> getAll();

    FileDto get(UUID id);

    FileEntity download(UUID id);

    ResponseEntity<?> downloadZipBunch(UUID id);

    FileDto update(UUID id, String newFileName, String newComment);

    FileDto delete(UUID id);

    List<FileNameById> getNamesById();
}
