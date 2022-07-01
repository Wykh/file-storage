package com.example.fileVault.service;

import com.example.fileVault.dto.FileDto;
import com.example.fileVault.dto.FileNameById;
import com.example.fileVault.entity.FileEntity;
import com.example.fileVault.exception.*;
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
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(bos);
        zipOut.setLevel(ZipOutputStream.STORED);

        Set<String> names = new HashSet<>();
        int count = 0;

        for (UUID id : ids) {
            FileEntity fileToDownload = getEntity(id);
            String fullFileName = fileToDownload.getName() + '.' + fileToDownload.getExtension();
            while (names.contains(fullFileName))
            {
                fullFileName = fileToDownload.getName() + ++count + '.' + fileToDownload.getExtension();
            }
            names.add(fullFileName);
            count = 0;

            ByteArrayInputStream bis = new ByteArrayInputStream(fileToDownload.getContent());
            ZipEntry zipEntry = new ZipEntry(fullFileName);

            try {
                zipOut.putNextEntry(zipEntry);
            } catch (IOException e) {
                throw new PutNextEntryToZipException("Can't put next entry to Zip ", e); // TODO: Make custom exception -- ok
            }

            try {
                byte[] bytes = new byte[1024];
                int length;
                while ((length = bis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
            } catch (IOException e) {
                throw new ReadWriteStreamException("Can't write bytes to zip stream", e); // TODO: Make custom exception -- ok
            }

            try {
                bis.close();
            } catch (IOException e) {
                throw new CloseStreamException("Can't close ByteArrayInputStream", e);
            }
        }
        try {
            zipOut.close();
        } catch (IOException e) {
            throw new CloseStreamException("Can't close ZipOutputStream", e);
        }

        try {
            bos.close();
        } catch (IOException e) {
            throw new CloseStreamException("Can't close ByteArrayOutputStream", e);
        }

        return bos.toByteArray();
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
