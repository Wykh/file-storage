package com.example.fileVault.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
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
    @GeneratedValue
    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID id;
    private Date uploadDate;
    private Date modifiedDate;
    private String extension;
    private String name;
    @Lob
    private byte[] content;
    private long size;
    private String comment;

}
