package com.example.filevault.service;

import com.example.filevault.dto.FileBytesAndNameById;
import com.example.filevault.controller.FilesFilterParams;
import com.example.filevault.dto.FileDto;
import com.example.filevault.dto.FileNameById;
import com.example.filevault.entity.FileEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FileService {

    FileDto upload(MultipartFile file, String comment);

    List<FileDto> getAll();

    FileBytesAndNameById getBytesAndNameById(UUID id);

    FileDto getDTO(UUID id);

    FileEntity getEntity(UUID id);

    byte[] downloadZip(List<UUID> id);

    FileDto update(UUID id, String newFileName, String newComment);

    FileDto delete(UUID id);

    List<FileNameById> getNamesById();

    List<FileDto> getFilteredFiles(FilesFilterParams filterParams);

}
