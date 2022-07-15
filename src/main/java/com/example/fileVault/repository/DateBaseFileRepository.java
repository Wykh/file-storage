package com.example.fileVault.repository;

import com.example.fileVault.entity.FileEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface DateBaseFileRepository extends CrudRepository<FileEntity, UUID> {
    // TODO: in 3 part
    List<FileEntity> findAll();
}
