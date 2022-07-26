package com.example.filevault.util;

import com.example.filevault.dto.FileBytesAndNameById;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipOutputStream;

public class FileWorkUtils {
    public static void saveFileToSystem(MultipartFile file, Path destinationFilePath) {
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: create custom exception
        }
    }

    public static byte[] getFileContent(Path fileLocation) {
        try {
            return Files.readAllBytes(fileLocation);
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO: custom exception
        }
    }

    private void extracted(ZipOutputStream zipOut, FileBytesAndNameById fileToDownload) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(fileToDownload.getContent())) {
            byte[] bytes = new byte[1024];
            int length;
            while ((length = bis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }
}
