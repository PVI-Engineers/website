package com.pviengineers.careersapi.controller;

import com.pviengineers.careersapi.dto.hr.HrApplicationResponse;
import com.pviengineers.careersapi.dto.hr.HrApplicationFileResponse;
import com.pviengineers.careersapi.model.CareerApplication;
import com.pviengineers.careersapi.model.CareerApplicationFile;
import com.pviengineers.careersapi.repository.CareerApplicationFileRepository;
import com.pviengineers.careersapi.repository.CareerApplicationRepository;
import com.pviengineers.careersapi.service.ResumeStorageService;
import java.util.List;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/hr")
public class HrPortalController {

    private final CareerApplicationRepository applicationRepository;
    private final CareerApplicationFileRepository fileRepository;
    private final ResumeStorageService resumeStorageService;

    public HrPortalController(
            CareerApplicationRepository applicationRepository,
            CareerApplicationFileRepository fileRepository,
            ResumeStorageService resumeStorageService
    ) {
        this.applicationRepository = applicationRepository;
        this.fileRepository = fileRepository;
        this.resumeStorageService = resumeStorageService;
    }

    @GetMapping("/applications")
    public ResponseEntity<List<HrApplicationResponse>> getApplications() {
        List<HrApplicationResponse> payload = applicationRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toHrResponse)
                .toList();
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/applications/{id}/resume")
    public ResponseEntity<ByteArrayResource> downloadResume(@PathVariable Long id) {
        CareerApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Application not found."));

        byte[] resumeBytes = resolveResumeBytes(application);
        return buildDownloadResponse(
                application.getResumeFileName(),
                application.getResumeContentType(),
                application.getResumeFileSize(),
                resumeBytes
        );
    }

    @GetMapping("/applications/{applicationId}/files/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadApplicationFile(
            @PathVariable Long applicationId,
            @PathVariable Long fileId
    ) {
        CareerApplicationFile file = fileRepository.findByIdAndApplicationId(fileId, applicationId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Application file not found."));
        byte[] fileBytes = resumeStorageService.loadFile(file.getStorageKey());

        return buildDownloadResponse(
                file.getFileName(),
                file.getContentType(),
                file.getFileSize(),
                fileBytes
        );
    }

    private HrApplicationResponse toHrResponse(CareerApplication app) {
        return new HrApplicationResponse(
                app.getId(),
                app.getApplicationRef(),
                app.getJobId(),
                app.getJobRole(),
                app.getFirstName(),
                app.getLastName(),
                app.getEmail(),
                app.getPhone(),
                app.getCurrentLocation(),
                app.getWillingToRelocate(),
                app.getWorkAuthorization(),
                app.getCurrentCompany(),
                app.getCurrentDesignation(),
                app.getTotalExperience(),
                app.getRelevantExperience(),
                app.getHighestQualification(),
                app.getSpecialization(),
                app.getGraduationYear(),
                app.getCurrentCtc(),
                app.getExpectedCtc(),
                app.getNoticePeriod(),
                app.getAvailableFrom(),
                app.getLinkedin(),
                app.getPortfolio(),
                app.getKeySkills(),
                app.getWhyJoin(),
                app.getAdditionalInfo(),
                app.getConsentPrivacy(),
                app.getConsentBackground(),
                app.getResumeFileName(),
                app.getResumeContentType(),
                app.getResumeFileSize(),
                app.getFiles()
                        .stream()
                        .map(file -> new HrApplicationFileResponse(
                                file.getId(),
                                file.getCategory(),
                                file.getFileName(),
                                file.getContentType(),
                                file.getFileSize()
                        ))
                        .toList(),
                app.getCreatedAt()
        );
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "resume";
        }

        return fileName.replaceAll("[\\r\\n\"]", "_");
    }

    private byte[] resolveResumeBytes(CareerApplication application) {
        String storageKey = application.getResumeStorageKey();
        if (storageKey != null && !storageKey.isBlank()) {
            try {
                return resumeStorageService.loadFile(storageKey);
            } catch (RuntimeException ignored) {
                // Fallback for legacy records or temporary S3 access issues.
            }
        }

        if (application.getResumeData() != null && application.getResumeData().length > 0) {
            return application.getResumeData();
        }

        throw new ResponseStatusException(NOT_FOUND, "Resume file not found for this application.");
    }

    private ResponseEntity<ByteArrayResource> buildDownloadResponse(
            String fileName,
            String contentType,
            Long fileSize,
            byte[] fileBytes
    ) {
        String safeFilename = sanitizeFileName(fileName);
        ByteArrayResource resource = new ByteArrayResource(fileBytes);
        MediaType mediaType = resolveMediaType(contentType);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(fileSize == null ? fileBytes.length : fileSize)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(safeFilename).build().toString()
                )
                .body(resource);
    }

    private MediaType resolveMediaType(String contentType) {
        try {
            return contentType == null || contentType.isBlank()
                    ? MediaType.APPLICATION_OCTET_STREAM
                    : MediaType.parseMediaType(contentType);
        } catch (InvalidMediaTypeException ex) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
