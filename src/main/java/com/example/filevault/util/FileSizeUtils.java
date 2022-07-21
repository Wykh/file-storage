package com.example.filevault.util;

import com.example.filevault.constants.FileVaultConstants;

public class FileSizeUtils {
    public static double toMB(long bytesCount) {
        return bytesCount * FileVaultConstants.BYTES_TO_MB_MULTIPLIER;
    }
}
