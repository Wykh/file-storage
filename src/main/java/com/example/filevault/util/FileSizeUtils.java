package com.example.filevault.util;

import com.example.filevault.constants.FileVaultConstants;
import com.example.filevault.exception.CantReadFileContentException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class FileSizeUtils {
    public static double toMB(long bytesCount) {
        return bytesCount * FileVaultConstants.BYTES_TO_MB_MULTIPLIER;
    }

    public static int getMultipartFileSize(MultipartFile file) {
        try {
            return file.getBytes().length;
        } catch (IOException e) {
            throw new CantReadFileContentException(".getBytes() method fails", e);
        }
    }
}
