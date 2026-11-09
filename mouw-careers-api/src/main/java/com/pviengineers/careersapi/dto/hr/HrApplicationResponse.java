package com.pviengineers.careersapi.dto.hr;

import java.time.LocalDateTime;
import java.util.List;

public record HrApplicationResponse(
        Long id,
        String applicationRef,
        String jobId,
        String jobRole,
        String firstName,
        String lastName,
        String email,
        String phone,
        String currentLocation,
        String willingToRelocate,
        String workAuthorization,
        String currentCompany,
        String currentDesignation,
        String totalExperience,
        String relevantExperience,
        String highestQualification,
        String specialization,
        String graduationYear,
        String currentCtc,
        String expectedCtc,
        String noticePeriod,
        String availableFrom,
        String linkedin,
        String portfolio,
        String keySkills,
        String whyJoin,
        String additionalInfo,
        Boolean consentPrivacy,
        Boolean consentBackground,
        String resumeFileName,
        String resumeContentType,
        Long resumeFileSize,
        List<HrApplicationFileResponse> files,
        LocalDateTime createdAt
) {
}
