package com.pviengineers.careersapi.dto;

import java.util.List;

public record CareerApplicationPresignResponse(
        List<CareerApplicationPresignFileResponse> files
) {
}
