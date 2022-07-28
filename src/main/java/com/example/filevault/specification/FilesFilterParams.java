package com.example.filevault.specification;

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
}
