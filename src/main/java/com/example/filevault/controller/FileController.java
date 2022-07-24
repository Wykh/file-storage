package com.example.filevault.controller;

import com.example.filevault.constants.FileVaultConstants;
import com.example.filevault.dto.FileBytesAndNameById;
import com.example.filevault.dto.FileDto;
import com.example.filevault.dto.FileNameById;
import com.example.filevault.service.DataBaseStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
//@Api(value = "Swagger2DemoRestController", description = "REST APIs related to Student Entity!!!!")
public class FileController {

    public final DataBaseStorageService fileService;

    @Tag(name = "Upload")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FileDto.class)
            ),
            description = "Return model for uploaded file"
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDto> uploadFile(
            @Parameter (description = "Select file to upload using form")
            @RequestParam("file")
            MultipartFile file,
            @RequestParam("comment")
            String comment) { // прокидывать ещё комментарий к файлу
        return ResponseEntity.ok(fileService.upload(file, comment));
    }

    @Tag(name = "Multiple files")
    @Operation(summary = "Get list of files with filtering")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = FileDto.class))
            )
    )
    @GetMapping
    public ResponseEntity<List<FileDto>> getAllFiles(
            @RequestParam(required = false)
            String name,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = FileVaultConstants.DATE_FORMAT)
            Date uploadDateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = FileVaultConstants.DATE_FORMAT)
            Date uploadDateTo,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = FileVaultConstants.DATE_FORMAT)
            Date modifiedDateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = FileVaultConstants.DATE_FORMAT)
            Date modifiedDateTo,
            @Parameter(description = "List of extensions. For example: `png,svg,jpg`")
            @RequestParam(required = false)
            List<String> extensions) {
        FilesFilterParams filterParams = FilesFilterParams.builder()
                .name(name)
                .uploadDateFrom(uploadDateFrom).uploadDateTo(uploadDateTo)
                .modifiedDateFrom(modifiedDateFrom).modifiedDateTo(modifiedDateTo)
                .extensions(extensions)
                .build();
        return ResponseEntity.ok(fileService.getFilteredFiles(filterParams));
    }

    @Tag(name = "Multiple files")
    @Operation(summary = "Get list of files with only name and id")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = FileNameById.class))
            )
    )
    @GetMapping("/name")
    public ResponseEntity<List<FileNameById>> getNamesById() {
        return ResponseEntity.ok(fileService.getNamesById());
    }

    @Tag(name = "Single file")
    @Operation(summary = "Get one file by id")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FileDto.class)
            )
    )
    @GetMapping("/{id}")
    public ResponseEntity<FileDto> getOneFile(@PathVariable UUID id) {
        return ResponseEntity.ok(fileService.getDTO(id));
    }

    @Tag(name = "Single file")
    @Operation(summary = "Update one file by id")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FileDto.class)
            )
    )
    @PutMapping("/{id}")
    public ResponseEntity<FileDto> updateFile(@PathVariable UUID id,
                                              @RequestParam("name") String newName,
                                              @RequestParam("comment") String newComment) {
        return ResponseEntity.ok(fileService.update(id, newName, newComment));
    }

    @Tag(name = "Single file")
    @Operation(summary = "Delete one file by id")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FileDto.class)
            )
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<FileDto> deleteFile(@PathVariable UUID id) {
        return ResponseEntity.ok(fileService.delete(id));
    }

    @Tag(name = "Download")
    @Operation(summary = "Download one file by id")
    @GetMapping(value = "/download/{id}")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE),
            description = "Attachment to download"
    )
    public HttpEntity<byte[]> downloadFile(@PathVariable UUID id) {
        FileBytesAndNameById fileToDownload = fileService.getBytesAndNameById(id);
        String fullFileName = fileToDownload.getName() + '.' + fileToDownload.getExtension();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fullFileName);

        return new HttpEntity<>(fileToDownload.getContent(), responseHeaders);

    }

    @Tag(name = "Download")
    @Operation(summary = "Download multiple files using the ids")
    @GetMapping("/download/zip")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE),
            description = "Attachment to download"
    )
    public HttpEntity<byte[]> downloadZip(@RequestParam List<UUID> ids) {
        byte[] zipContent = fileService.downloadZip(ids);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + FileVaultConstants.ZIP_NAME);

        return new HttpEntity<>(zipContent, responseHeaders);
    }
}
