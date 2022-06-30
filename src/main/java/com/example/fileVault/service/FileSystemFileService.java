package com.example.fileVault.service;

import com.example.fileVault.entity.FileEntity;
import com.example.fileVault.exception.*;
import com.example.fileVault.dto.FileDto;
import com.example.fileVault.dto.FileNameById;
import com.example.fileVault.repository.FileSystemStorageRepo;
import com.example.fileVault.util.FilenameUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileSystemFileService implements FileService {

    private final FileSystemStorageRepo storageRepo;

    @Override
    public FileDto create(MultipartFile file, String comment) {
        String fullFileName = file.getOriginalFilename();
        assert fullFileName != null;
        String fileName = FilenameUtils.getNameWithoutExtension(fullFileName);
        String fileExtension = FilenameUtils.getExtension(fullFileName);

        // Тут .getBytes() кидает IOException, это нормально его в RuntimeException преобразовывать таким образом?
        try {
            return FileDto.toDTO(storageRepo.create(fileName, fileExtension, comment, file.getBytes()));
        } catch (IOException e) {
            throw new CantReadFileContentException(".getBytes() method fails", e);
        }
    }

    @Override
    public List<FileDto> getAll() throws EmptyFileListException {
        return storageRepo.getAll().stream().map(FileDto::toDTO).collect(Collectors.toList());
    }

    @Override
    public FileDto get(UUID id) throws FileNotFoundException {
        return FileDto.toDTO(storageRepo.findByUUID(id));
    }

    @Override
    public List<FileNameById> getNamesById() {
        return storageRepo.getAll().stream().map(FileNameById::toDTO).collect(Collectors.toList());
    }

    // TODO: rid of return Response here -- ok
    @Override
    public FileEntity download(UUID id) throws FileNotFoundException {
        // TODO: HttpEntity + Header here -- ok

        return storageRepo.findByUUID(id);
    }

    @Override
    public ResponseEntity<?> downloadZipBunch(UUID id) {
        return null;
    }

    @Override
    public FileDto update(UUID id, String newFileName, String newComment) {
        return FileDto.toDTO(storageRepo.updateByUUID(id, newFileName, newComment));
    }

    @Override
    public FileDto delete(UUID id) throws FileNotFoundException {
        // TODO: return DTO without download uri -- ok
        FileDto deletedModel = FileDto.toDTO(storageRepo.findByUUID(id));
        storageRepo.deleteByUUID(id);
        deletedModel.setModifiedDate(new Date());
        deletedModel.setDownloadUrl("");
        return deletedModel;
    }



    // TODO: split into two functions -- ok
    // TODO: pass to utils -- ok


}
