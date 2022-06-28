package com.example.fileVault.controller;

import com.example.fileVault.model.FileModel;
import com.example.fileVault.model.FileModelNameAndId;
import com.example.fileVault.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor // почему это работает, ведь вроде для работы Depencency Injection нужно всегда указывать @AutoWired даже над конструкторами
                         // а в сгенерированном конструкторе этой @Autowired не будет!
public class FileController {

    public final FileService fileService;

    // TODO: /api начало -- ok
    // TODO: /file - в единственном числе -- ok
    // TODO: Ошибки -- ok
    // Ошибки наследовать от RuntimeException
    // Ловить их ExceptionHandler и RestControllerAdvice

    // TODO: возвращать именя файло в и ID в отдельном методе /file/name -- ok
    // TODO: не возвращать конент файла -- ok
    // TODO: возвращать ссылку, но не захардкоженную -- нет
    // TODO: использовать lombok чтоб убрать анотации и ещё кое-что -- ok

    @GetMapping
    public ResponseEntity<List<FileModel>> getAllFiles() { // get or find использовать
        return ResponseEntity.ok(fileService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileModel> getOneFile(@PathVariable UUID id) {
        return ResponseEntity.ok(fileService.get(id));
    }

    @GetMapping("/name")
    public ResponseEntity<List<FileModelNameAndId>> getNamesAndIds() {
        return ResponseEntity.ok(fileService.getNamesAndIds());
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable UUID id) {
        HttpEntity<byte[]> entityToDownload = fileService.download(id);
        String headerValues = entityToDownload.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, headerValues).body(entityToDownload.getBody());
    }

    @PostMapping
    public ResponseEntity<FileModel> uploadFile(@RequestParam("file") MultipartFile file,
                                                @RequestParam("comment") String comment) { // прокидывать ещё комментарий к файлу
        FileModel newModel = fileService.create(file, comment);
        return ResponseEntity.ok(newModel);
    }

    // TODO: измениять только имя файла и комментарий к файлу -- ok
    @PutMapping("/{id}")
    public ResponseEntity<FileModel> updateFile(@PathVariable UUID id,
                                                @RequestParam("name") String newName,
                                                @RequestParam("comment") String newComment) {
        FileModel model = fileService.update(id, newName, newComment);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FileModel> deleteFile(@PathVariable UUID id) {
        FileModel deletedModel = fileService.delete(id);
        return ResponseEntity.ok(deletedModel);
    }
}
