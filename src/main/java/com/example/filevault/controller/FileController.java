package com.example.filevault.controller;

import com.example.filevault.constants.FileVaultConstants;
import com.example.filevault.dto.FileBytesAndNameById;
import com.example.filevault.dto.FileDto;
import com.example.filevault.dto.FileNameById;
import com.example.filevault.service.DataBaseStorageService;
import com.example.filevault.service.FileSystemFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    public final DataBaseStorageService fileService;

    @GetMapping
    public ResponseEntity<List<FileDto>> getAllFiles(@RequestParam(required = false) String name,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = FileVaultConstants.DATE_FORMAT) Date uploadDateFrom,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = FileVaultConstants.DATE_FORMAT) Date uploadDateTo,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = FileVaultConstants.DATE_FORMAT) Date modifiedDateFrom,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = FileVaultConstants.DATE_FORMAT) Date modifiedDateTo,
                                                     @RequestParam(required = false) List<String> extensions) {
        FilesFilterParams filterParams = FilesFilterParams.builder()
                .name(name)
                .uploadDateFrom(uploadDateFrom).uploadDateTo(uploadDateTo)
                .modifiedDateFrom(modifiedDateFrom).modifiedDateTo(modifiedDateTo)
                .extensions(extensions)
                .build();
        return ResponseEntity.ok(fileService.getFilteredFiles(filterParams));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileDto> getOneFile(@PathVariable UUID id) {
        return ResponseEntity.ok(fileService.getDTO(id));
    }

    @GetMapping("/name")
    public ResponseEntity<List<FileNameById>> getNamesById() {
        return ResponseEntity.ok(fileService.getNamesById());
    }

    @GetMapping("/download/{id}")
    public HttpEntity<byte[]> downloadFile(@PathVariable UUID id) {
        FileBytesAndNameById fileToDownload = fileService.getBytesAndNameById(id);
        String fullFileName = fileToDownload.getName() + '.' + fileToDownload.getExtension();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fullFileName);

        return new HttpEntity<>(fileToDownload.getContent(), responseHeaders);

    }

    @GetMapping("/download/zip")
    public HttpEntity<byte[]> downloadZip(@RequestParam List<UUID> ids) {
        byte[] zipContent = fileService.downloadZip(ids);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + FileVaultConstants.ZIP_NAME);

        return new HttpEntity<>(zipContent, responseHeaders);
    }

    @PostMapping
    public ResponseEntity<FileDto> uploadFile(@RequestParam("file") MultipartFile file,
                                              @RequestParam("comment") String comment) { // прокидывать ещё комментарий к файлу
        return ResponseEntity.ok(fileService.upload(file, comment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FileDto> updateFile(@PathVariable UUID id,
                                              @RequestParam("name") String newName,
                                              @RequestParam("comment") String newComment) {
        return ResponseEntity.ok(fileService.update(id, newName, newComment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FileDto> deleteFile(@PathVariable UUID id) {
        return ResponseEntity.ok(fileService.delete(id));
    }
}
