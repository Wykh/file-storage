package com.example.fileVault.entity;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
public class FileEntity {
    private UUID id;
    private Date uploadDate;
    private Date modifiedDate;
    private String extension;
    private String name;
    private byte[] content;
    private long size;
    private String comment;

}
