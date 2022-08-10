package com.example.filevault.specification;

import com.example.filevault.entity.UserEntity;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilesFilterParams {
    String name;
    LocalDateTime uploadDateFrom;
    LocalDateTime uploadDateTo;
    LocalDateTime modifiedDateFrom;
    LocalDateTime modifiedDateTo;
    List<String> extensions;
}
