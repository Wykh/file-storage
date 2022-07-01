package com.example.fileVault.service;

import com.example.fileVault.entity.FileEntity;
import com.example.fileVault.dto.FileDto;
import com.example.fileVault.dto.FileNameById;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FileService {

    FileDto upload(MultipartFile file, String comment);

    List<FileDto> getAll();

    FileDto getDTO(UUID id);

    FileEntity getEntity(UUID id);

    byte[] downloadZip(List<UUID> id);

    FileDto update(UUID id, String newFileName, String newComment);

    FileDto delete(UUID id);

    List<FileNameById> getNamesById();
}
