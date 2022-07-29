package com.example.filevault.specification;

import com.example.filevault.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class FilesFilterParams {
    String name;
    Date uploadDateFrom;
    Date uploadDateTo;
    Date modifiedDateFrom;
    Date modifiedDateTo;
    List<String> extensions;
    String ownerFileUsername;
    Long ownerFileId;
    UserEntity owner;
}
