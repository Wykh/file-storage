package com.example.filevault.service;

import com.example.filevault.config.security.UserRole;
import com.example.filevault.constants.FileVaultConstants;
import com.example.filevault.dto.FileBytesAndNameById;
import com.example.filevault.dto.FileDto;
import com.example.filevault.dto.FileNameById;
import com.example.filevault.dto.FileUpdatableFieldsById;
import com.example.filevault.entity.FileEntity;
import com.example.filevault.entity.UserEntity;
import com.example.filevault.exception.*;
import com.example.filevault.repository.FileRepository;
import com.example.filevault.specification.FilesFilterParams;
import com.example.filevault.util.FileNameUtils;
import com.example.filevault.util.FileSizeUtils;
import com.example.filevault.util.FileWorkUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.example.filevault.config.security.UserPermission.CHANGE_FILE_ACCESS;
import static com.example.filevault.config.security.UserPermission.DELETE_PUBLIC_FILE;
import static com.example.filevault.specification.FileSpecification.getFilteredFiles;
import static com.example.filevault.util.UserWorkUtils.getCurrentUserName;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final UserService userService;
    private final Path rootLocation = Paths.get(FileVaultConstants.STORAGE_LOCATION);

    @Override
    public FileDto upload(MultipartFile file, String passedComment) {
        if (FileSizeUtils.toMB(file.getSize()) >= FileVaultConstants.MAX_FILE_SIZE_MB)
            throw new TooLargeFileSizeException("File Size Cant be more than " + FileVaultConstants.MAX_FILE_SIZE_MB + "MB");

        String fullFileName = file.getOriginalFilename();
        String fileName = FileNameUtils.getNameWithoutExtension(fullFileName);
        String fileExtension = FileNameUtils.getExtension(fullFileName);

        UserEntity currentUser = userService.getOne(getCurrentUserName());
        FileEntity fullFilledNewEntity = fileRepository.save(
                FileEntity.builder()
                        .name(fileName)
                        .extension(fileExtension)
                        .comment(passedComment)
                        .contentFolderPath(FileVaultConstants.STORAGE_LOCATION)
                        .size(file.getSize())
                        .user(currentUser)
                        .isPublic(false)
                        .build()
        );
        Path destinationFilePath = rootLocation.resolve(
                fullFilledNewEntity.getId().toString() + '.' + fileExtension);

        FileWorkUtils.saveFileToSystem(file, destinationFilePath);

        return FileDto.of(fullFilledNewEntity);
    }

    @Override
    public List<FileDto> getAll(FilesFilterParams filterParams) {
        return getFileEntityStream(filterParams)
                .map(FileDto::of)
                .collect(Collectors.toList());
    }

    @Override
    public List<FileNameById> getNames() {
        return getFileEntityStream(new FilesFilterParams())
                .map(FileNameById::toDTO)
                .collect(Collectors.toList());
    }

    private Stream<FileEntity> getFileEntityStream(FilesFilterParams filterParams) {
        UserEntity currentUser = userService.getOne(getCurrentUserName());
        return fileRepository.findAll(getFilteredFiles(filterParams, currentUser)).stream();
    }

    @Override
    public FileDto getDTOById(UUID id) {
        FileEntity foundEntity = getFileEntity(id);
        UserEntity currentUser = userService.getOne(getCurrentUserName());
        if (currentUser.getId().equals(foundEntity.getUser().getId()) || foundEntity.isPublic()) {
            return FileDto.of(foundEntity);
        }
        throw new ForbiddenException("You have no permission to get the file"); // TODO: custom exception
    }

    @Override
    public FileBytesAndNameById getBytesAndNameById(UUID id) {
        FileEntity foundEntity = getFileEntity(id);
        UserEntity currentUser = userService.getOne(getCurrentUserName());
        if (currentUser.getId().equals(foundEntity.getUser().getId()) || foundEntity.isPublic()) {
            Path fileLocation = rootLocation.resolve(foundEntity.getId().toString() + '.' + foundEntity.getExtension());
            byte[] fileContent = FileWorkUtils.getFileContent(fileLocation);

            return FileBytesAndNameById.of(foundEntity, fileContent);
        }
        throw new ForbiddenException("You have no permission to download the file");
    }

    @Override
    public FileBytesAndNameById getZipBytesByIds(List<UUID> ids) {
        // TODO: stream output with byte chunks
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            try (ZipOutputStream zipOut = new ZipOutputStream(bos)) {
                zipOut.setLevel(ZipOutputStream.STORED);

                Set<String> names = new HashSet<>();
                for (UUID id : ids) {
                    FileBytesAndNameById fileToDownload = getBytesAndNameById(id);

                    String fullFileName = FileNameUtils.getUniqueFileName(names, fileToDownload.getName(), fileToDownload.getExtension());
                    zipOut.putNextEntry(new ZipEntry(fullFileName));
                    zipOut.write(fileToDownload.getContent());
                }
            }
            return FileBytesAndNameById.of(FileVaultConstants.ZIP_ENTITY, bos.toByteArray());
        } catch (IOException e) {
            throw new CantCreateZipFileException("Bad when creating zip file", e);
        }
    }

    @Override
    public FileDto update(FileUpdatableFieldsById fileToUpdate) {
        UUID id = fileToUpdate.getId();
        String newFileName = fileToUpdate.getName();
        String newComment = fileToUpdate.getComment();
        Boolean isPublic = fileToUpdate.getIsPublic();
        FileEntity foundEntity = getFileEntity(id);
        UserEntity currentUser = userService.getOne(getCurrentUserName());
        UserRole currentUserRole = UserRole.valueOf(currentUser.getRole().getName());
        if (currentUser.getId().equals(foundEntity.getUser().getId())) {
            if (newFileName != null)
                foundEntity.setName(newFileName);
            if (newComment != null)
                foundEntity.setComment(newComment);
            if (isPublic != null)
                foundEntity.setPublic(isPublic);
            return FileDto.of(fileRepository.save(foundEntity));
        }
        if (foundEntity.isPublic() && currentUserRole.getPermissions().contains(CHANGE_FILE_ACCESS)) {
            if (isPublic != null)
                foundEntity.setPublic(isPublic);
            return FileDto.of(fileRepository.save(foundEntity));
        }
        throw new ForbiddenException("You have no permission to update the file"); // TODO: custom exception
    }

    @Override
    public FileDto delete(UUID id) {
        FileEntity foundEntity = getFileEntity(id);
        UserEntity currentUser = userService.getOne(getCurrentUserName());
        UserRole currentUserRole = UserRole.valueOf(currentUser.getRole().getName());
        if (currentUser.getId().equals(foundEntity.getUser().getId()) ||
                foundEntity.isPublic() && currentUserRole.getPermissions().contains(DELETE_PUBLIC_FILE)) {
            Path fileLocation = rootLocation.resolve(
                    foundEntity.getId().toString() + '.' + foundEntity.getExtension());
            fileRepository.deleteById(id);
            try {
                Files.delete(fileLocation);
            } catch (IOException e) {
                throw new DeletingFileNotExistsInStorageException("Can't delete file because is not exists", e);
            }
            FileDto deletedDto = FileDto.of(foundEntity);
            deletedDto.setDownloadUrl("");
            return deletedDto;
        }
        throw new ForbiddenException("You have no permission to delete the file");
    }

    private FileEntity getFileEntity(UUID id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("File was deleted or never was existed"));
    }
}
