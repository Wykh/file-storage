package com.example.fileVault.util;

import com.example.fileVault.exception.BadFileTypeException;
import com.example.fileVault.exception.EmptyFileNameException;
import com.google.common.base.Strings;
import org.springframework.util.StringUtils;

// Class FilenameUtils in commons.apache.org
public class FilenameUtils {

    private static int getLastPointIndexWithErrorChecking(String fullFileName) {
        if (Strings.isNullOrEmpty(fullFileName)) {
            throw new EmptyFileNameException("File name is empty or null");
        }

        int dotIndex = fullFileName.lastIndexOf('.');
        if (dotIndex == -1 || fullFileName.length() == dotIndex + 1) {
            throw new BadFileTypeException("Bad file type");
        }
        return dotIndex;
    }

    public static String getNameWithoutExtension(String fullFileName) {
        int dotIndex = getLastPointIndexWithErrorChecking(fullFileName);

        return fullFileName.substring(0, dotIndex);
    }

    public static String getExtension(String fullFileName) {
        int dotIndex = getLastPointIndexWithErrorChecking(fullFileName);

        return fullFileName.substring(dotIndex + 1);
    }

    public static String getFullFileName(String fileName, String fileExtension) {
        return fileName + "." + fileExtension;
    }
}
