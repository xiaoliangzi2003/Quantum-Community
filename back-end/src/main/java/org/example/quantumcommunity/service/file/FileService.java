package org.example.quantumcommunity.service.file;

import java.io.InputStream;

/**
 * @author xiaol
 */

public interface FileService {
    void uploadFile(String objectName, InputStream inputStream);
    String getFileContent(String objectName);

    boolean isFileExist(String objectName);

    void deleteFile(String objectName);
}
