package com.example.filevault.repository;

import com.example.filevault.entity.FileEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface DateBaseFileRepository extends JpaRepository<FileEntity, UUID>, JpaSpecificationExecutor<FileEntity> {
    List<FileEntity> findAll();
    public List<FileEntity> findAll(Specification<FileEntity> spec);
}
