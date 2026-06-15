package com.pviengineers.careersapi.controller;

import com.pviengineers.careersapi.dto.ContactInquiryRequest;
import com.pviengineers.careersapi.dto.ContactInquiryResponse;
import com.pviengineers.careersapi.service.ContactInquiryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact/inquiries")
public class ContactInquiryController {

    private final ContactInquiryService service;

    public ContactInquiryController(ContactInquiryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ContactInquiryResponse> submit(
            @Valid @RequestBody ContactInquiryRequest request
    ) {
        return ResponseEntity.ok(service.submit(request));
    }
}
