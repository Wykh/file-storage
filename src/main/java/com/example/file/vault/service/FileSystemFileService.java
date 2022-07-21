package com.example.file.vault.service;

import com.example.file.vault.constants.FileVaultConstants;
import com.example.file.vault.controller.FilesFilterParams;
import com.example.file.vault.dto.FileDto;
import com.example.file.vault.dto.FileNameById;
import com.example.file.vault.entity.FileEntity;
import com.example.file.vault.exception.CantReadFileContentException;
import com.example.file.vault.exception.TooLargeFileSizeException;
import com.example.file.vault.repository.FileSystemFileRepository;
import com.example.file.vault.util.FilenameUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class FileSystemFileService implements FileService {

    private final FileSystemFileRepository fileRepository;

    @Override
    public FileDto upload(MultipartFile file, String comment) {
        if (file.getSize() >= FileVaultConstants.MAX_FILE_SIZE_BYTES)
            throw new TooLargeFileSizeException("File Size Cant be more than " + FileVaultConstants.MAX_FILE_SIZE_MB + "MB");

        String fullFileName = file.getOriginalFilename();
        String fileName = FilenameUtils.getNameWithoutExtension(fullFileName);
        String fileExtension = FilenameUtils.getExtension(fullFileName);

        try {
            return FileDto.of(fileRepository.create(fileName, fileExtension, comment, file.getBytes()));
        } catch (IOException e) {
            throw new CantReadFileContentException(".getBytes() method fails", e);
        }
    }

    @Override
    public List<FileDto> getAll() {
        return fileRepository.getAll().values().stream()
                .map(FileDto::of)
                .collect(Collectors.toList());
    }

    @Override
    public FileDto getDTO(UUID id) {
        return FileDto.of(fileRepository.findById(id));
    }

    @Override
    public List<FileNameById> getNamesById() {
        return fileRepository.getAll().values().stream()
                .map(FileNameById::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FileDto> getFilteredFiles(FilesFilterParams filterParams) {

        return fileRepository.getAll().values().stream()
                .filter(entity -> filterParams.getName() == null || entity.getName().toLowerCase().contains(filterParams.getName().toLowerCase()))
                .filter(entity -> filterParams.getUploadDateFrom() == null || !entity.getUploadDate().before(filterParams.getUploadDateFrom()))
                .filter(entity -> filterParams.getUploadDateTo() == null ||  !entity.getUploadDate().after(filterParams.getUploadDateTo()))
                .filter(entity -> filterParams.getModifiedDateFrom() == null || !entity.getModifiedDate().before(filterParams.getModifiedDateFrom()))
                .filter(entity -> filterParams.getModifiedDateTo() == null || !entity.getModifiedDate().after(filterParams.getModifiedDateTo()))
                .filter(entity -> filterParams.getExtensions() == null || filterParams.getExtensions().contains(entity.getExtension()))
                .map(FileDto::of)
                .collect(Collectors.toList());
    }

    @Override
    public FileEntity getEntity(UUID id) {
        return fileRepository.findById(id);
    }

    @Override
    public byte[] downloadZip(List<UUID> ids) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ZipOutputStream zipOut = new ZipOutputStream(bos)) {
                zipOut.setLevel(ZipOutputStream.STORED);

                Set<String> names = new HashSet<>();
                for (UUID id : ids) {
                    FileEntity fileToDownload = getEntity(id);

                    String fullFileName = fileToDownload.getName() + '.' + fileToDownload.getExtension();
                    int count = 0;
                    while (names.contains(fullFileName)) {
                        fullFileName = fileToDownload.getName() + "_" + ++count + '.' + fileToDownload.getExtension();
                    }
                    names.add(fullFileName);

                    zipOut.putNextEntry(new ZipEntry(fullFileName));
                    try (ByteArrayInputStream bis = new ByteArrayInputStream(fileToDownload.getContent())) {
                        byte[] bytes = new byte[1024];
                        int length;
                        while ((length = bis.read(bytes)) >= 0) {
                            zipOut.write(bytes, 0, length);
                        }
                    }
                }
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileDto update(UUID id, String newFileName, String newComment) {
        return FileDto.of(fileRepository.updateById(id, newFileName, newComment));
    }

    @Override
    public FileDto delete(UUID id) {
        FileDto deletedModel = FileDto.of(fileRepository.deleteById(id));
        deletedModel.setDownloadUrl("");
        return deletedModel;
    }
}
