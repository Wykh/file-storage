package com.example.file.vault.util;

import com.example.file.vault.constants.FileVaultConstants;

public class FileSizeUtils {
    public static double toMB(long bytesCount) {
        return bytesCount * FileVaultConstants.BYTES_TO_MB_MULTIPLIER;
    }
}
