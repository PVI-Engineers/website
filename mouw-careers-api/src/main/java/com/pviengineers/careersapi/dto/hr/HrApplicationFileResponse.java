package com.pviengineers.careersapi.dto.hr;

public record HrApplicationFileResponse(
        Long id,
        String category,
        String fileName,
        String contentType,
        Long fileSize
) {
}
