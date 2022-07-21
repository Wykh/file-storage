package com.example.file.vault.util;

import com.example.file.vault.exception.BadFileTypeException;
import com.example.file.vault.exception.EmptyFileNameException;
import com.google.common.base.Strings;

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
