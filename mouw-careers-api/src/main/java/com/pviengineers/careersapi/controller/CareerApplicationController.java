package com.pviengineers.careersapi.controller;

import com.pviengineers.careersapi.dto.CareerApplicationRequest;
import com.pviengineers.careersapi.dto.CareerApplicationPresignRequest;
import com.pviengineers.careersapi.dto.CareerApplicationPresignResponse;
import com.pviengineers.careersapi.dto.CareerApplicationResponse;
import com.pviengineers.careersapi.service.CareerApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/careers/applications")
public class CareerApplicationController {

    private final CareerApplicationService service;

    public CareerApplicationController(CareerApplicationService service) {
        this.service = service;
    }

    @PostMapping("/presign")
    public ResponseEntity<CareerApplicationPresignResponse> presignUploads(
            @Valid @RequestBody CareerApplicationPresignRequest request
    ) {
        return ResponseEntity.ok(service.presignUploads(request));
    }

    @PostMapping
    public ResponseEntity<CareerApplicationResponse> submit(
            @Valid @RequestBody CareerApplicationRequest request
    ) {
        return ResponseEntity.ok(service.submit(request));
    }
}
