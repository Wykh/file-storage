package com.example.fileVault.util;

import com.example.fileVault.constants.FileVaultConstants;

public class FileSizeUtils {
    public static double toMB(long bytesCount) {
        return bytesCount * FileVaultConstants.BYTES_TO_MB_MULTIPLIER;
    }
}
