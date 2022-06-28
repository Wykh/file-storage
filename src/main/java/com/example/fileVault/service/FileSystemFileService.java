package com.example.fileVault.service;

import com.example.fileVault.entity.FileEntity;
import com.example.fileVault.exception.*;
import com.example.fileVault.model.FileModel;
import com.example.fileVault.model.FileModelNameAndId;
import com.example.fileVault.repository.FileSystemStorageRepo;
import com.example.fileVault.util.FilenameUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
    public FileModel create(MultipartFile file, String comment) {
        String fullFileName = file.getOriginalFilename();
        assert fullFileName != null;
        String fileName = FilenameUtils.getNameWithoutExtension(fullFileName);
        String fileExtension = FilenameUtils.getExtension(fullFileName);
        long fileSize = file.getSize(); // а это вообще нужно передавать в функцию create, если мы можем получить размер из file.getBytes().length?

        // Тут .getBytes() кидает IOException, это нормально его в RuntimeException преобразовывать таким образом?
        try {
            return FileModel.toModel(storageRepo.create(fileName, fileExtension, comment, file.getBytes(), fileSize));
        } catch (IOException e) {
            throw new CantReadFileContentException(".getBytes() method fails");
        }
    }

    @Override
    public List<FileModel> getAll() throws EmptyFileListException {
        return storageRepo.getAll().stream().map(FileModel::toModel).collect(Collectors.toList());
    }

    @Override
    public FileModel get(UUID id) throws FileNotFoundException {
        return FileModel.toModel(storageRepo.findByUUID(id));
    }

    @Override
    public List<FileModelNameAndId> getNamesAndIds() {
        return storageRepo.getAll().stream().map(FileModelNameAndId::toModel).collect(Collectors.toList());
    }

    // TODO: rid of return Response here -- ok
    @Override
    public HttpEntity<byte[]> download(UUID id) throws FileNotFoundException {
        // TODO: HttpEntity + Header here -- ok
        FileEntity fileToDownload = storageRepo.findByUUID(id);
        String fullFileName = fileToDownload.getName() + '.' + fileToDownload.getExtension();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fullFileName + "\"");

        return new HttpEntity<>(fileToDownload.getContent(), responseHeaders);
    }

    @Override
    public ResponseEntity<?> downloadZipBunch(UUID id) {
        return null;
    }

    @Override
    public FileModel update(UUID id, String newFileName, String newComment) {
        return FileModel.toModel(storageRepo.updateByUUID(id, newFileName, newComment));
    }

    @Override
    public FileModel delete(UUID id) throws FileNotFoundException {
        // TODO: return DTO without download uri -- ok
        FileModel deletedModel = FileModel.toModel(storageRepo.findByUUID(id));
        storageRepo.deleteByUUID(id);
        deletedModel.setModifiedDate(new Date());
        deletedModel.setWebUrl("DELETED");
        return deletedModel;
    }



    // TODO: split into two functions -- ok
    // TODO: pass to utils -- ok


}
