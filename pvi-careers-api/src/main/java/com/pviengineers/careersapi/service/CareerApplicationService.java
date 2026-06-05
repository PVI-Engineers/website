package com.pviengineers.careersapi.service;

import com.pviengineers.careersapi.dto.CareerApplicationFileReferenceRequest;
import com.pviengineers.careersapi.dto.CareerApplicationPresignFileRequest;
import com.pviengineers.careersapi.dto.CareerApplicationPresignFileResponse;
import com.pviengineers.careersapi.dto.CareerApplicationPresignRequest;
import com.pviengineers.careersapi.dto.CareerApplicationPresignResponse;
import com.pviengineers.careersapi.dto.CareerApplicationRequest;
import com.pviengineers.careersapi.dto.CareerApplicationResponse;
import com.pviengineers.careersapi.model.CareerApplication;
import com.pviengineers.careersapi.model.CareerApplicationFile;
import com.pviengineers.careersapi.repository.CareerApplicationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;

@Service
public class CareerApplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CareerApplicationService.class);
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".pdf", ".doc", ".docx", ".png", ".jpg", ".jpeg");
    private static final Set<String> ALLOWED_FILE_CATEGORIES = Set.of("resume", "supporting");
    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024;
    private static final long MAX_TOTAL_UPLOAD_BYTES = 9L * 1024 * 1024;
    private static final int MAX_FILE_COUNT = 4;

    private final CareerApplicationRepository repository;
    private final JavaMailSender mailSender;
    private final ResumeStorageService resumeStorageService;
    private final Optional<SesClient> sesClient;

    @Value("${app.mail.company-address}")
    private String companyMailAddress;

    @Value("${app.mail.from-address}")
    private String fromAddress;

    @Value("${app.mail.provider:smtp}")
    private String mailProvider;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    public CareerApplicationService(
            CareerApplicationRepository repository,
            JavaMailSender mailSender,
            ResumeStorageService resumeStorageService,
            ObjectProvider<SesClient> sesClientProvider
    ) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.resumeStorageService = resumeStorageService;
        this.sesClient = Optional.ofNullable(sesClientProvider.getIfAvailable());
    }

    public CareerApplicationPresignResponse presignUploads(CareerApplicationPresignRequest request) {
        ensureS3StorageForPresignedUpload();
        List<CareerApplicationPresignFileRequest> requestedFiles = request.getFiles();
        validateRequestedFileCount(requestedFiles.size());

        long totalBytes = 0L;
        List<CareerApplicationPresignFileResponse> targets = new ArrayList<>();

        for (CareerApplicationPresignFileRequest file : requestedFiles) {
            String category = normalizeCategory(file.getCategory());
            validateFileMetadata(file.getFileName(), file.getContentType(), file.getSizeBytes(), category);
            totalBytes += file.getSizeBytes();
            if (totalBytes > MAX_TOTAL_UPLOAD_BYTES) {
                throw new IllegalArgumentException("Combined upload size must be less than 9 MB.");
            }

            ResumeStorageService.PresignedUploadTarget target = resumeStorageService.createPresignedUpload(
                    category,
                    file.getClientFileId().trim(),
                    file.getFileName().trim(),
                    file.getContentType().trim()
            );

            targets.add(new CareerApplicationPresignFileResponse(
                    target.clientFileId(),
                    category,
                    file.getFileName().trim(),
                    file.getContentType().trim(),
                    file.getSizeBytes(),
                    target.storageKey(),
                    target.uploadUrl(),
                    target.method()
            ));
        }

        long resumeCount = targets.stream().filter(file -> "resume".equals(file.category())).count();
        if (resumeCount != 1) {
            throw new IllegalArgumentException("Exactly one resume file is required.");
        }

        return new CareerApplicationPresignResponse(targets);
    }

    @Transactional
    public CareerApplicationResponse submit(CareerApplicationRequest request) {
        ensureMailConfiguration();
        List<SubmittedFile> submittedFiles = validateAndNormalizeSubmittedFiles(request.getFiles());
        SubmittedFile resumeFile = submittedFiles.stream()
                .filter(file -> "resume".equals(file.category()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Resume file is required."));

        ensureUploadedObjectsExist(submittedFiles);

        CareerApplication entity = new CareerApplication();
        entity.setApplicationRef(generateApplicationRef());
        entity.setJobId(request.getJobId().trim());
        entity.setJobRole(request.getJobRole().trim());
        entity.setFirstName(request.getFirstName().trim());
        entity.setLastName(request.getLastName().trim());
        entity.setEmail(request.getEmail().trim().toLowerCase(Locale.ROOT));
        entity.setPhone(request.getPhone().trim());
        entity.setCurrentLocation(request.getCurrentLocation().trim());
        entity.setWillingToRelocate(request.getWillingToRelocate().trim());
        entity.setWorkAuthorization(request.getWorkAuthorization().trim());
        entity.setCurrentCompany(request.getCurrentCompany().trim());
        entity.setCurrentDesignation(request.getCurrentDesignation().trim());
        entity.setTotalExperience(request.getTotalExperience().trim());
        entity.setRelevantExperience(request.getRelevantExperience().trim());
        entity.setHighestQualification(request.getHighestQualification().trim());
        entity.setSpecialization(request.getSpecialization().trim());
        entity.setGraduationYear(request.getGraduationYear().trim());
        entity.setCurrentCtc(request.getCurrentCtc().trim());
        entity.setExpectedCtc(request.getExpectedCtc().trim());
        entity.setNoticePeriod(request.getNoticePeriod().trim());
        entity.setAvailableFrom(request.getAvailableFrom().trim());
        entity.setLinkedin(request.getLinkedin().trim());
        entity.setPortfolio(request.getPortfolio() == null ? "" : request.getPortfolio().trim());
        entity.setKeySkills(request.getKeySkills().trim());
        entity.setWhyJoin(request.getWhyJoin().trim());
        entity.setAdditionalInfo(request.getAdditionalInfo() == null ? "" : request.getAdditionalInfo().trim());
        entity.setConsentPrivacy(Boolean.TRUE.equals(request.getConsentPrivacy()));
        entity.setConsentBackground(Boolean.TRUE.equals(request.getConsentBackground()));
        entity.setResumeFileName(resumeFile.fileName());
        entity.setResumeContentType(resumeFile.contentType());
        entity.setResumeFileSize(resumeFile.sizeBytes());
        entity.setResumeStorageKey(resumeFile.storageKey());
        // Keep a non-null payload for backward compatibility with older DB schemas
        // where resume_data may still be NOT NULL from previous deployments.
        entity.setResumeData(new byte[0]);

        for (SubmittedFile submittedFile : submittedFiles) {
            CareerApplicationFile fileEntity = new CareerApplicationFile();
            fileEntity.setCategory(submittedFile.category());
            fileEntity.setFileName(submittedFile.fileName());
            fileEntity.setContentType(submittedFile.contentType());
            fileEntity.setFileSize(submittedFile.sizeBytes());
            fileEntity.setStorageKey(submittedFile.storageKey());
            entity.addFile(fileEntity);
        }

        CareerApplication saved = repository.save(entity);

        dispatchNotifications(saved);

        return new CareerApplicationResponse(
                saved.getApplicationRef(),
                "Application submitted successfully."
        );
    }

    private void dispatchNotifications(CareerApplication application) {
        try {
            sendCompanyNotification(application);
        } catch (Exception ex) {
            LOGGER.error(
                    "Application {} saved, but company notification email failed.",
                    application.getApplicationRef(),
                    ex
            );
        }

        try {
            sendApplicantAcknowledgement(application);
        } catch (Exception ex) {
            LOGGER.error(
                    "Application {} saved, but applicant acknowledgement email failed.",
                    application.getApplicationRef(),
                    ex
            );
        }
    }

    private String generateApplicationRef() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase(Locale.ROOT);
        return "PVI-APP-" + datePart + "-" + randomPart;
    }

    private void ensureMailConfiguration() {
        if (resolveFromAddress().isBlank()) {
            throw new IllegalArgumentException("Mail sender is not configured. Set MAIL_FROM or MAIL_USERNAME.");
        }

        if (companyMailAddress == null || companyMailAddress.isBlank()) {
            throw new IllegalArgumentException("Company recipient email is not configured. Set COMPANY_MAIL_TO.");
        }

        if (isSesProvider() && sesClient.isEmpty()) {
            throw new IllegalArgumentException("SES mail provider is selected but SES client is unavailable.");
        }
    }

    private void ensureS3StorageForPresignedUpload() {
        if (!resumeStorageService.isS3StorageEnabled()) {
            throw new IllegalArgumentException("Pre-signed upload flow requires APP_STORAGE_PROVIDER=s3.");
        }
    }

    private void validateRequestedFileCount(int fileCount) {
        if (fileCount < 1) {
            throw new IllegalArgumentException("At least one file is required.");
        }

        if (fileCount > MAX_FILE_COUNT) {
            throw new IllegalArgumentException("You can upload up to " + MAX_FILE_COUNT + " files.");
        }
    }

    private void validateFileMetadata(String fileName, String contentType, Long sizeBytes, String category) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name is missing.");
        }

        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("File content type is missing.");
        }

        if (sizeBytes == null || sizeBytes <= 0) {
            throw new IllegalArgumentException("File size must be greater than zero.");
        }

        if (sizeBytes > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("Each file must be less than 5 MB.");
        }

        if (!ALLOWED_FILE_CATEGORIES.contains(category)) {
            throw new IllegalArgumentException("File category must be either resume or supporting.");
        }

        String lowerCaseName = fileName.toLowerCase(Locale.ROOT);
        boolean hasValidExtension = ALLOWED_EXTENSIONS.stream().anyMatch(lowerCaseName::endsWith);
        if (!hasValidExtension) {
            throw new IllegalArgumentException("Files must be PDF, DOC, DOCX, PNG, JPG, or JPEG.");
        }
    }

    private String normalizeCategory(String category) {
        if (category == null) {
            return "";
        }
        return category.trim().toLowerCase(Locale.ROOT);
    }

    private List<SubmittedFile> validateAndNormalizeSubmittedFiles(List<CareerApplicationFileReferenceRequest> files) {
        validateRequestedFileCount(files == null ? 0 : files.size());
        ensureS3StorageForPresignedUpload();

        long totalBytes = 0L;
        long resumeCount = 0L;
        Set<String> uniqueStorageKeys = new HashSet<>();
        Set<String> uniqueClientFileIds = new HashSet<>();
        List<SubmittedFile> normalized = new ArrayList<>();

        for (CareerApplicationFileReferenceRequest file : files) {
            String category = normalizeCategory(file.getCategory());
            validateFileMetadata(file.getFileName(), file.getContentType(), file.getSizeBytes(), category);
            if (file.getClientFileId() == null || file.getClientFileId().isBlank()) {
                throw new IllegalArgumentException("Client file ID is required.");
            }
            if (file.getStorageKey() == null || file.getStorageKey().isBlank()) {
                throw new IllegalArgumentException("Storage key is required.");
            }

            String cleanedClientId = file.getClientFileId().trim();
            String cleanedStorageKey = file.getStorageKey().trim();
            String cleanedFileName = file.getFileName().trim();
            String cleanedContentType = file.getContentType().trim();

            if (!uniqueClientFileIds.add(cleanedClientId)) {
                throw new IllegalArgumentException("Duplicate files are not allowed.");
            }

            if (!uniqueStorageKeys.add(cleanedStorageKey)) {
                throw new IllegalArgumentException("Duplicate storage keys are not allowed.");
            }

            if (!resumeStorageService.isManagedStorageKey(cleanedStorageKey)) {
                throw new IllegalArgumentException("Invalid file storage key.");
            }

            totalBytes += file.getSizeBytes();
            if (totalBytes > MAX_TOTAL_UPLOAD_BYTES) {
                throw new IllegalArgumentException("Combined upload size must be less than 9 MB.");
            }

            if ("resume".equals(category)) {
                resumeCount++;
            }

            normalized.add(new SubmittedFile(
                    cleanedClientId,
                    category,
                    cleanedFileName,
                    cleanedContentType,
                    file.getSizeBytes(),
                    cleanedStorageKey
            ));
        }

        if (resumeCount != 1) {
            throw new IllegalArgumentException("Exactly one resume file is required.");
        }

        return normalized;
    }

    private void ensureUploadedObjectsExist(List<SubmittedFile> files) {
        for (SubmittedFile file : files) {
            if (!resumeStorageService.objectExists(file.storageKey())) {
                throw new IllegalArgumentException("Uploaded file missing in storage: " + file.fileName());
            }
        }
    }

    private void sendCompanyNotification(CareerApplication application) {
        try {
            String senderAddress = resolveFromAddress();
            MimeMessage message = createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(senderAddress);
            helper.setTo(companyMailAddress);
            helper.setReplyTo(application.getEmail());
            helper.setSubject("New Career Application | " + application.getJobRole() + " | " + application.getApplicationRef());
            helper.setText(buildCompanyMailBody(application), false);

            for (CareerApplicationFile file : application.getFiles()) {
                byte[] fileBytes = resumeStorageService.loadFile(file.getStorageKey());
                helper.addAttachment(
                        file.getFileName(),
                        new ByteArrayResource(fileBytes),
                        file.getContentType()
                );
            }

            dispatchMimeMessage(message);
        } catch (Exception ex) {
            throw new RuntimeException("Application saved, but failed to send company notification email.", ex);
        }
    }

    private void sendApplicantAcknowledgement(CareerApplication application) {
        try {
            String senderAddress = resolveFromAddress();
            MimeMessage message = createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(senderAddress);
            helper.setTo(application.getEmail());
            helper.setSubject("PVI ENGINEERS | Application Received | " + application.getJobRole());
            helper.setText(buildApplicantMailBody(application), false);
            dispatchMimeMessage(message);
        } catch (Exception ex) {
            throw new RuntimeException("Application saved, but failed to send applicant acknowledgement email.", ex);
        }
    }

    private MimeMessage createMimeMessage() {
        if (isSesProvider()) {
            return new MimeMessage(Session.getInstance(new Properties()));
        }

        return mailSender.createMimeMessage();
    }

    private void dispatchMimeMessage(MimeMessage message) throws MessagingException, IOException {
        if (!isSesProvider()) {
            mailSender.send(message);
            return;
        }

        SesClient client = sesClient.orElseThrow(
                () -> new IllegalArgumentException("SES mail provider is selected but SES client is unavailable.")
        );

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            message.writeTo(outputStream);
            RawMessage rawMessage = RawMessage.builder()
                    .data(SdkBytes.fromByteArray(outputStream.toByteArray()))
                    .build();

            SendRawEmailRequest request = SendRawEmailRequest.builder()
                    .rawMessage(rawMessage)
                    .build();

            client.sendRawEmail(request);
        }
    }

    private boolean isSesProvider() {
        return "ses".equalsIgnoreCase(mailProvider);
    }

    private String resolveFromAddress() {
        if (fromAddress != null && !fromAddress.isBlank()) {
            return fromAddress.trim();
        }

        return mailUsername == null ? "" : mailUsername.trim();
    }

    private String buildCompanyMailBody(CareerApplication app) {
        return """
                A new candidate has applied.

                Application Reference: %s
                Job ID: %s
                Job Role: %s

                Candidate Details
                -----------------
                Name: %s %s
                Email: %s
                Phone: %s
                Current Location: %s
                Willing to Relocate: %s
                Work Authorization: %s

                Professional Profile
                --------------------
                Current Company: %s
                Current Designation: %s
                Total Experience: %s
                Relevant Experience: %s
                Highest Qualification: %s
                Specialization: %s
                Graduation Year: %s
                Key Skills: %s

                Compensation & Availability
                --------------------------
                Current CTC: %s
                Expected CTC: %s
                Notice Period: %s
                Available From: %s

                Professional Links
                ------------------
                LinkedIn: %s
                Portfolio: %s

                Candidate Statement
                -------------------
                Why Join: %s
                Additional Info: %s

                Consents
                --------
                Privacy Consent: %s
                Background Verification Consent: %s

                Uploaded Attachments
                --------------------
                %s
                """.formatted(
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
                app.getKeySkills(),
                app.getCurrentCtc(),
                app.getExpectedCtc(),
                app.getNoticePeriod(),
                app.getAvailableFrom(),
                app.getLinkedin(),
                app.getPortfolio(),
                app.getWhyJoin(),
                app.getAdditionalInfo(),
                app.getConsentPrivacy(),
                app.getConsentBackground(),
                formatFileList(app.getFiles())
        );
    }

    private String buildApplicantMailBody(CareerApplication app) {
        return """
                Dear %s %s,

                Thank you for applying to PVI ENGINEERS for the position of %s.

                We have received your application successfully.
                Application Reference: %s

                Our hiring team will review your profile and reach out if your experience matches the role requirements.

                Regards,
                HR Team
                PVI ENGINEERS
                """.formatted(
                app.getFirstName(),
                app.getLastName(),
                app.getJobRole(),
                app.getApplicationRef()
        );
    }

    private String formatFileList(List<CareerApplicationFile> files) {
        if (files == null || files.isEmpty()) {
            return "- None";
        }

        return files.stream()
                .map(file -> "- %s (%s, %s)"
                        .formatted(file.getFileName(), file.getCategory(), formatSize(file.getFileSize())))
                .collect(Collectors.joining("\n"));
    }

    private String formatSize(Long sizeBytes) {
        if (sizeBytes == null || sizeBytes <= 0) {
            return "unknown size";
        }

        double sizeInMb = sizeBytes / (1024d * 1024d);
        return String.format(Locale.ROOT, "%.2f MB", sizeInMb);
    }

    private record SubmittedFile(
            String clientFileId,
            String category,
            String fileName,
            String contentType,
            long sizeBytes,
            String storageKey
    ) {
    }
}
