package com.pviengineers.careersapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CareerApplicationPresignFileRequest {

    @NotBlank(message = "Client file ID is required")
    private String clientFileId;

    @NotBlank(message = "File category is required")
    private String category;

    @NotBlank(message = "File name is required")
    private String fileName;

    @NotBlank(message = "File content type is required")
    private String contentType;

    @NotNull(message = "File size is required")
    @Positive(message = "File size must be greater than zero")
    private Long sizeBytes;

    public String getClientFileId() {
        return clientFileId;
    }

    public void setClientFileId(String clientFileId) {
        this.clientFileId = clientFileId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }
}
