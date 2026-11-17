package filecrypt.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model class representing an entry in the 'operations' database table.
 */
public class OperationRecord {
    private int id;
    private String timestamp;
    private String fileName;
    private String cipherType;
    private String operationMode; // ENCRYPT or DECRYPT
    private String keyUsed;
    private String fileSize;
    private String status;

    public OperationRecord() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public OperationRecord(int id, String timestamp, String fileName, String cipherType, 
                           String operationMode, String keyUsed, String fileSize, String status) {
        this.id = id;
        this.timestamp = timestamp;
        this.fileName = fileName;
        this.cipherType = cipherType;
        this.operationMode = operationMode;
        this.keyUsed = keyUsed;
        this.fileSize = fileSize;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCipherType() {
        return cipherType;
    }

    public void setCipherType(String cipherType) {
        this.cipherType = cipherType;
    }

    public String getOperationMode() {
        return operationMode;
    }

    public void setOperationMode(String operationMode) {
        this.operationMode = operationMode;
    }

    public String getKeyUsed() {
        return keyUsed;
    }

    public void setKeyUsed(String keyUsed) {
        this.keyUsed = keyUsed;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
