package com.example.filevault.dto;

import com.example.filevault.entity.FileEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class FileDto {
    private UUID id;
    private Boolean isPublic;
    private String name;
    private String comment;
    private String owner;
    private String extension;
    private LocalDateTime uploadDate;
    private LocalDateTime modifiedDate;
    private String downloadUrl;
    private Long size;

    public static FileDto of(FileEntity entity) {
        return FileDto.builder()
                .id(entity.getId())
                .owner(entity.getUser().getName())
                .isPublic(entity.isPublic())
                .uploadDate(entity.getUploadDate())
                .modifiedDate(entity.getModifiedDate())
                .extension(entity.getExtension())
                .name(entity.getName())
                .size(entity.getSize())
                .comment(entity.getComment())
                .downloadUrl("/api/file/download/" + entity.getId()).build();
    }
}
