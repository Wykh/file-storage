package com.example.fileVault.service;

import com.example.fileVault.dto.FileDto;
import com.example.fileVault.dto.FileNameById;
import com.example.fileVault.entity.FileEntity;
import com.example.fileVault.exception.CantReadFileContentException;
import com.example.fileVault.exception.CloseStreamException;
import com.example.fileVault.exception.FileNotFoundException;
import com.example.fileVault.exception.TooLargeFileSizeException;
import com.example.fileVault.repository.FileSystemStorageRepository;
import com.example.fileVault.util.FilenameUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class FileSystemFileService implements FileService {

    private final FileSystemStorageRepository fileRepository;
    private final int MAX_FILE_SIZE_MB = 15;

    @Override
    public FileDto upload(MultipartFile file, String comment) {
        if (file.getSize() * 0.00000095367432 >= MAX_FILE_SIZE_MB)
            throw new TooLargeFileSizeException("File Size Cant be more than " + MAX_FILE_SIZE_MB + "MB");

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
        return fileRepository.getAll().values().stream().map(FileDto::of).collect(Collectors.toList());
    }

    @Override
    public FileDto getDTO(UUID id) {
        return FileDto.of(fileRepository.findById(id));
    }

    @Override
    public List<FileNameById> getNamesById() {
        return fileRepository.getAll().values().stream().map(FileNameById::toDTO).collect(Collectors.toList());
    }

    @Override
    public FileEntity getEntity(UUID id) {
        return fileRepository.findById(id);
    }

    @Override
    public byte[] downloadZip(List<UUID> ids) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(bos);) {
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
    public FileDto delete(UUID id) throws FileNotFoundException {
        FileDto deletedModel = FileDto.of(fileRepository.deleteById(id));
        deletedModel.setModifiedDate(new Date());
        deletedModel.setDownloadUrl("");
        return deletedModel;
    }
}
