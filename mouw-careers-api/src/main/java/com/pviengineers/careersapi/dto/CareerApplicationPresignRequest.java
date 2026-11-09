package com.pviengineers.careersapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class CareerApplicationPresignRequest {

    @NotEmpty(message = "At least one file is required for upload.")
    @Valid
    private List<CareerApplicationPresignFileRequest> files;

    public List<CareerApplicationPresignFileRequest> getFiles() {
        return files;
    }

    public void setFiles(List<CareerApplicationPresignFileRequest> files) {
        this.files = files;
    }
}
