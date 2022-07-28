package com.example.filevault.service;

import com.example.filevault.constants.FileVaultConstants;
import com.example.filevault.specification.FilesFilterParams;
import com.example.filevault.dto.FileBytesAndNameById;
import com.example.filevault.dto.FileDto;
import com.example.filevault.dto.FileNameById;
import com.example.filevault.entity.FileEntity;
import com.example.filevault.exception.FileNotFoundException;
import com.example.filevault.exception.TooLargeFileSizeException;
import com.example.filevault.repository.DataBaseFileRepository;
import com.example.filevault.specification.FileSpecification;
import com.example.filevault.util.FileNameUtils;
import com.example.filevault.util.FileSizeUtils;
import com.example.filevault.util.FileWorkUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class DataBaseFileService implements FileService {

    private final DataBaseFileRepository fileRepository;
    private final Path rootLocation = Paths.get(FileVaultConstants.STORAGE_LOCATION);

    @Override
    public FileDto upload(MultipartFile file, String passedComment) {
        if (FileSizeUtils.toMB(file.getSize()) >= FileVaultConstants.MAX_FILE_SIZE_MB)
            throw new TooLargeFileSizeException("File Size Cant be more than " + FileVaultConstants.MAX_FILE_SIZE_MB + "MB");

        String fullFileName = file.getOriginalFilename();
        String fileName = FileNameUtils.getNameWithoutExtension(fullFileName);
        String fileExtension = FileNameUtils.getExtension(fullFileName);

        FileEntity fullFilledNewEntity = fileRepository.save(
                FileEntity.builder()
                        .name(fileName)
                        .extension(fileExtension)
                        .comment(passedComment)
                        .contentFolderPath(FileVaultConstants.STORAGE_LOCATION)
                        .size(file.getSize())
                        .build()
        );
        Path destinationFilePath = rootLocation.resolve(
                fullFilledNewEntity.getId().toString() + '.' + fileExtension);

        FileWorkUtils.saveFileToSystem(file, destinationFilePath);

        return FileDto.of(fullFilledNewEntity);
    }

    @Override
    public List<FileDto> getAll(FilesFilterParams filterParams) {
        return fileRepository.findAll(FileSpecification.getFilteredFiles(filterParams))
                .stream()
                .map(FileDto::of)
                .collect(Collectors.toList());
    }

    @Override
    public List<FileNameById> getNames() {
        return fileRepository.findAll()
                .stream()
                .map(FileNameById::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FileDto getDTOById(UUID id) {
        return FileDto.of(fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("File not found :("))
        );
    }

    @Override
    public FileBytesAndNameById getBytesAndNameById(UUID id) {
        FileEntity foundFileEntity = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("File not found :("));

        Path fileLocation = rootLocation.resolve(foundFileEntity.getId().toString() + '.' + foundFileEntity.getExtension());
        byte[] fileContent = FileWorkUtils.getFileContent(fileLocation);

        return FileBytesAndNameById.of(foundFileEntity, fileContent);
    }

    @Override
    public FileBytesAndNameById getZipBytesByIds(List<UUID> ids) {
        // TODO: stream output with byte chunks
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ZipOutputStream zipOut = new ZipOutputStream(bos)) {
                zipOut.setLevel(ZipOutputStream.STORED);

                Set<String> names = new HashSet<>();
                for (UUID id : ids) {
                    FileBytesAndNameById fileToDownload = getBytesAndNameById(id);

                    String fullFileName = FileNameUtils.getUniqueFileName(names, fileToDownload.getName(), fileToDownload.getExtension());
                    zipOut.putNextEntry(new ZipEntry(fullFileName));
                    zipOut.write(fileToDownload.getContent());
                }
            }
            return FileBytesAndNameById.of(FileVaultConstants.ZIP_ENTITY, bos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: custom exception
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
        return FileDto.of(fileRepository.save(updatedFileEntity));
    }

    @Override
    public FileDto delete(UUID id) {
        Optional<FileEntity> optionalFileEntity = fileRepository.findById(id);
        if (optionalFileEntity.isEmpty())
            throw new FileNotFoundException("File not found. Cant update the id");
        FileEntity foundFileEntity = optionalFileEntity.get();
        Path fileLocation = rootLocation.resolve(foundFileEntity.getId().toString() + '.' + foundFileEntity.getExtension());
        fileRepository.deleteById(id);
        try {
            Files.delete(fileLocation);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: custom exception
        }
        FileDto deletedDto = FileDto.of(foundFileEntity);
        deletedDto.setDownloadUrl("");
        return deletedDto;
    }
}
