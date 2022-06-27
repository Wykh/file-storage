package com.example.fileVault.controller;

import com.example.fileVault.entity.FileEntity;
import com.example.fileVault.exception.BadFileTypeException;
import com.example.fileVault.exception.EmptyFileListException;
import com.example.fileVault.exception.EmptyFileNameException;
import com.example.fileVault.exception.FileNotFoundException;
import com.example.fileVault.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    StorageService storageService;

    @GetMapping
    public ResponseEntity<?> readAllFiles() {
        try {
            List<FileEntity>  allFiles = storageService.readAll();
            return ResponseEntity.ok(allFiles);
        } catch (EmptyFileListException e) {
            return ResponseEntity.badRequest().body("Error reading all files: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> readOneFile(@PathVariable UUID id) {
        try {
            FileEntity file = storageService.readOne(id);
            return ResponseEntity.ok(file);
        } catch (FileNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> serveFile(@PathVariable UUID id) { // тут ваще какая-то магия, он сам заполняет UUID
        try {
            return storageService.downloadOne(id);
        } catch (FileNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createFile(@RequestParam("file") MultipartFile file) {
        try {
            storageService.create(file);
            return ResponseEntity.ok("File is saved!");
        } catch (EmptyFileNameException | BadFileTypeException | IOException e) {
            return ResponseEntity.badRequest().body("File is not saved! Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFile(@PathVariable UUID id,
                                        @RequestParam("file") MultipartFile file) {
        try {
            Date modifiedDate = storageService.update(id, file);
            return ResponseEntity.ok("File with UUID " + id + " successfully updated at " + modifiedDate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable UUID id) {
        try {
            UUID deletedUUID = storageService.delete(id);
            return ResponseEntity.ok("Deleted file with UUID" + deletedUUID);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
