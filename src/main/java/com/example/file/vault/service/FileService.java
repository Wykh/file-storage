package com.example.file.vault.service;

import com.example.file.vault.dto.FileDto;
import com.example.file.vault.dto.FileNameById;
import com.example.file.vault.entity.FileEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
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

    List<FileDto> getFilesFilteredByName(String mask);

    List<FileDto> getFilesFilteredByModifiedDateRange(Date fromDate, Date toDate);
    List<FileDto> getFilesFilteredByUploadDateRange(Date fromDate, Date toDate);

    List<FileDto> getFilesFilteredByExtensions(List<String> extensions);
}
