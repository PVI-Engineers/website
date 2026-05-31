package com.pviengineers.careersapi.dto;

public record CareerApplicationPresignFileResponse(
        String clientFileId,
        String category,
        String fileName,
        String contentType,
        Long sizeBytes,
        String storageKey,
        String uploadUrl,
        String method
) {
}
