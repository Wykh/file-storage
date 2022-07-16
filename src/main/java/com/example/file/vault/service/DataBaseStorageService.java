package com.example.file.vault.service;

import com.example.file.vault.constants.FileVaultConstants;
import com.example.file.vault.dto.FileDto;
import com.example.file.vault.dto.FileNameById;
import com.example.file.vault.entity.FileEntity;
import com.example.file.vault.exception.CantReadFileContentException;
import com.example.file.vault.exception.FileNotFoundException;
import com.example.file.vault.exception.TooLargeFileSizeException;
import com.example.file.vault.repository.DateBaseFileRepository;
import com.example.file.vault.util.FileSizeUtils;
import com.example.file.vault.util.FilenameUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class DataBaseStorageService implements FileService {

    private final DateBaseFileRepository fileRepository;

    public FileEntity create(String name, String type, String comment, byte[] content) {
        return FileEntity.builder()
                .id(UUID.randomUUID())
                .name(name)
                .uploadDate(new Date())
                .modifiedDate(new Date())
                .comment(comment)
                .content(content)
                .size(content.length)
                .extension(type)
                .build();
    }

    @Override
    public FileDto upload(MultipartFile file, String comment) {
        if (FileSizeUtils.toMB(file.getSize()) >= FileVaultConstants.MAX_FILE_SIZE_MB)
            throw new TooLargeFileSizeException("File Size Cant be more than " + FileVaultConstants.MAX_FILE_SIZE_MB + "MB");

        String fullFileName = file.getOriginalFilename();
        String fileName = FilenameUtils.getNameWithoutExtension(fullFileName);
        String fileExtension = FilenameUtils.getExtension(fullFileName);

        try {
            return FileDto.of(fileRepository.save(create(fileName, fileExtension, comment, file.getBytes())));
        } catch (IOException e) {
            throw new CantReadFileContentException(".getBytes() method fails", e);
        }
    }

    @Override
    public List<FileDto> getAll() {
        return fileRepository.findAll()
                .stream()
                .map(FileDto::of)
                .collect(Collectors.toList());
    }

    @Override
    public FileDto getDTO(UUID id) {
        Optional<FileEntity> optionalUser = fileRepository.findById(id);
        if (optionalUser.isEmpty())
            throw new FileNotFoundException("File not found :(");
        return FileDto.of(optionalUser.get());
    }

    @Override
    public FileEntity getEntity(UUID id) {
        Optional<FileEntity> optionalUser = fileRepository.findById(id);
        if (optionalUser.isEmpty())
            throw new FileNotFoundException("File not found :(");
        return optionalUser.get();
    }

    @Override
    public byte[] downloadZip(List<UUID> ids) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
            try (ZipOutputStream zipOut = new ZipOutputStream(bos);) {
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
                    try (ByteArrayInputStream bis = new ByteArrayInputStream(fileToDownload.getContent());) {
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
        Optional<FileEntity> foundFileEntity = fileRepository.findById(id);
        if (foundFileEntity.isEmpty())
            throw new FileNotFoundException("File not found. Cant update the id");
        FileEntity updatedFileEntity = foundFileEntity.get();
        updatedFileEntity.setName(newFileName);
        updatedFileEntity.setComment(newComment);
        fileRepository.save(updatedFileEntity);
        return FileDto.of(updatedFileEntity);

    }

    @Override
    public FileDto delete(UUID id) {
        Optional<FileEntity> foundFileEntity = fileRepository.findById(id);
        if (foundFileEntity.isEmpty())
            throw new FileNotFoundException("File not found. Cant update the id");
        fileRepository.deleteById(id);
        FileDto deletedDto = FileDto.of(foundFileEntity.get());
        deletedDto.setDownloadUrl("");
        return deletedDto;
    }

    @Override
    public List<FileNameById> getNamesById() {
        return fileRepository.findAll()
                .stream()
                .map(FileNameById::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FileDto> getFilesFilteredByName(String mask) {
        return fileRepository.findAll().stream()
                .map(FileDto::of)
                .filter(entity -> entity.getName().toLowerCase().contains(mask.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<FileDto> getFilesFilteredByModifiedDateRange(Date fromDate, Date toDate) {
        return fileRepository.findAll().stream()
                .map(FileDto::of)
                .filter(entity -> entity.getModifiedDate().after(fromDate) && entity.getModifiedDate().before(toDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<FileDto> getFilesFilteredByUploadDateRange(Date fromDate, Date toDate) {
        return fileRepository.findAll().stream()
                .map(FileDto::of)
                .filter(entity -> entity.getUploadDate().after(fromDate) && entity.getUploadDate().before(toDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<FileDto> getFilesFilteredByExtensions(List<String> extensions) {
        return fileRepository.findAll().stream()
                .map(FileDto::of)
                .filter(entity -> extensions.contains(entity.getExtension()))
                .collect(Collectors.toList());
    }
}
