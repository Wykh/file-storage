package com.example.filevault.dto;

import com.example.filevault.entity.FileEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
public class FileDto {
    private UUID id;
    private Date uploadDate;
    private Date modifiedDate;
    private String extension;
    private String name;
    private Long size;
    private String comment;
    private String downloadUrl;

    public static FileDto of(FileEntity entity) {
        return FileDto.builder()
                .id(entity.getId())
                .uploadDate(entity.getUploadDate())
                .modifiedDate(entity.getModifiedDate())
                .extension(entity.getExtension())
                .name(entity.getName())
                .size(entity.getSize())
                .comment(entity.getComment())
                .downloadUrl("/api/file/download/" + entity.getId()).build();
    }
}
