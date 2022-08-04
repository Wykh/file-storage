package com.example.filevault.util;

import com.example.filevault.exception.BadFileTypeException;
import com.example.filevault.exception.EmptyFileNameException;
import com.google.common.base.Strings;

import java.util.Set;

// Class FilenameUtils in commons.apache.org
public class FileNameUtils {

    private static int getLastPointIndexWithErrorChecking(String fullFileName) {
        if (Strings.isNullOrEmpty(fullFileName)) {
            throw new EmptyFileNameException("File can't be saved. File's name is empty or null");
        }

        int dotIndex = fullFileName.lastIndexOf('.');
        if (dotIndex == -1 || fullFileName.length() == dotIndex + 1) {
            throw new BadFileTypeException("File can't be saved. Bad file type");
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

    public static String getUniqueFileName(Set<String> names, String name, String extension) {
        String fullFileName = name + '.' + extension;
        int count = 0;
        while (names.contains(fullFileName)) {
            fullFileName = name + "_" + ++count + '.' + extension;
        }
        names.add(fullFileName);
        return fullFileName;
    }

    public static String getFullFileName(String fileName, String fileExtension) {
        return fileName + "." + fileExtension;
    }
}
