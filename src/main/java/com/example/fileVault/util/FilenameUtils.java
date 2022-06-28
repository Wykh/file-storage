package com.example.fileVault.util;

import com.example.fileVault.exception.BadFileTypeException;
import com.example.fileVault.exception.EmptyFileNameException;

// Class FilenameUtils in commons.apache.org
public class FilenameUtils {

    public static String getNameWithoutExtension(String fullFileName) {
        if (fullFileName.length() == 0) {
            throw new EmptyFileNameException("File name is empty");
        }

        int dotIndex = fullFileName.lastIndexOf('.');
        if (dotIndex == -1 || fullFileName.length() == dotIndex + 1) {
            throw new BadFileTypeException("Bad file type");
        }

        return fullFileName.substring(0, dotIndex);
    }

    public static String getExtension(String fullFileName) {
        if (fullFileName.length() == 0) {
            throw new EmptyFileNameException("File name is empty");
        }

        int dotIndex = fullFileName.lastIndexOf('.');
        if (dotIndex == -1 || fullFileName.length() == dotIndex + 1) {
            throw new BadFileTypeException("Bad file type");
        }

        return fullFileName.substring(dotIndex + 1);
    }

}
