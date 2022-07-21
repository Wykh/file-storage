package com.example.filevault.entity;

import com.example.filevault.constants.FileVaultConstants;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class FileEntity {
    // TODO: Set toString, equals, and hashCode - https://stackoverflow.com/questions/34241718/lombok-builder-and-jpa-default-constructor
    @Id
    @Builder.Default
    @Type(type="org.hibernate.type.UUIDCharType")
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
    private String contentFolderPath = FileVaultConstants.STORAGE_LOCATION;
    @Builder.Default
    private long size = 0;
    @Builder.Default
    private String comment = "";
}
