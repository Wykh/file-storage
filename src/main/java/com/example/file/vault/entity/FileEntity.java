package com.example.file.vault.entity;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
public class FileEntity {
    @Builder.Default
    private UUID id = UUID.randomUUID();
    @Builder.Default
    private Date uploadDate = new Date();
    @Builder.Default
    private Date modifiedDate = new Date();
    @Builder.Default
    private String extension = "";
    @Builder.Default
    private String name = "";
    @Builder.Default
    private byte[] content = new byte[0];
    @Builder.Default
    private long size = 0;
    @Builder.Default
    private String comment = "";
}
