package com.example.filevault.repository;

import com.example.filevault.entity.FileEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface DateBaseFileRepository extends CrudRepository<FileEntity, UUID> {
    List<FileEntity> findAll();
}
