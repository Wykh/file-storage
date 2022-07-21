package com.example.filevault.repository;

import com.example.filevault.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface DateBaseFileRepository extends JpaRepository<FileEntity, UUID> {
    List<FileEntity> findAll();
}
