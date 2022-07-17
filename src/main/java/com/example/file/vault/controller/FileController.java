package com.example.file.vault.controller;

import com.example.file.vault.constants.FileVaultConstants;
import com.example.file.vault.dto.FileBytesAndNameById;
import com.example.file.vault.dto.FileDto;
import com.example.file.vault.dto.FileNameById;
import com.example.file.vault.exception.EmptyGetParamException;
import com.example.file.vault.exception.IncorrectDateTypeFilterParamException;
import com.example.file.vault.exception.IncorrectFilterParamException;
import com.example.file.vault.service.FileService;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
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
public class FileController {

    public final FileService fileService;

    @GetMapping
    public ResponseEntity<List<FileDto>> getAllFiles(@RequestParam(required = false) String filter,
                                                     @RequestParam(required = false) String mask,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date from,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") Date to,
                                                     @RequestParam(required = false) String dateType, // modified or upload
                                                     @RequestParam(required = false) List<String> extensions) {
        if (filter == null) {
            return ResponseEntity.ok(fileService.getAll());
        }
        return switch (filter) {
            case "name" -> getFilesFilteredByName(mask);
            case "date" -> getFilesFilteredByDateRange(from, to, dateType);
            case "extension" -> getFilesFilteredByExtensions(extensions);
            default -> throw new IncorrectFilterParamException("Unknown filter param: " + filter);
        };
    }

    private ResponseEntity<List<FileDto>> getFilesFilteredByExtensions(List<String> extensions) {
        if (extensions == null)
            return ResponseEntity.ok(fileService.getAll());
        return ResponseEntity.ok(fileService.getFilesFilteredByExtensions(extensions));
    }

    private ResponseEntity<List<FileDto>> getFilesFilteredByDateRange(Date fromDate, Date toDate, String dateType) {
        System.out.println(fromDate);
        System.out.println(toDate);
        System.out.println(dateType);
        if (fromDate == null || toDate == null || dateType == null)
            throw new EmptyGetParamException("When get files filtered by date range one of params is null");
        return switch (dateType) {
            case "modified" -> ResponseEntity.ok(fileService.getFilesFilteredByModifiedDateRange(fromDate, toDate));
            case "upload" -> ResponseEntity.ok(fileService.getFilesFilteredByUploadDateRange(fromDate, toDate));
            default -> throw new IncorrectDateTypeFilterParamException("Unknown date type filter param: " + dateType);
        };
    }

    private ResponseEntity<List<FileDto>> getFilesFilteredByName(String mask) {
        if (Strings.isNullOrEmpty(mask))
            throw new EmptyGetParamException("Mask param is empty or null");
        return ResponseEntity.ok(fileService.getFilesFilteredByName(mask));
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
