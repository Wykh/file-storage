package com.example.filevault.repository;

import com.example.filevault.entity.FileEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface DataBaseFileRepository extends JpaRepository<FileEntity, UUID>, JpaSpecificationExecutor<FileEntity> {

}
