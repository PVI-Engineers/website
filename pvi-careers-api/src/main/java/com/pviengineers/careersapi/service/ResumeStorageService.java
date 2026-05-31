package com.pviengineers.careersapi.service;

import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Service
public class ResumeStorageService {

    private final Optional<S3Client> s3Client;
    private final Optional<S3Presigner> s3Presigner;

    @Value("${app.storage.provider:s3}")
    private String storageProvider;

    @Value("${app.storage.s3.bucket:}")
    private String s3BucketName;

    @Value("${app.storage.s3.key-prefix:resumes}")
    private String s3KeyPrefix;

    @Value("${app.storage.s3.presign-expiration-seconds:900}")
    private long presignExpirationSeconds;

    public ResumeStorageService(
            ObjectProvider<S3Client> s3ClientProvider,
            ObjectProvider<S3Presigner> s3PresignerProvider
    ) {
        this.s3Client = Optional.ofNullable(s3ClientProvider.getIfAvailable());
        this.s3Presigner = Optional.ofNullable(s3PresignerProvider.getIfAvailable());
    }

    public boolean isS3StorageEnabled() {
        return "s3".equalsIgnoreCase(storageProvider);
    }

    public String storeResume(
            String applicationRef,
            String originalFileName,
            String contentType,
            byte[] resumeBytes
    ) {
        if (!isS3StorageEnabled()) {
            return null;
        }

        S3Client client = s3Client.orElseThrow(
                () -> new IllegalArgumentException("S3 storage is enabled but S3 client is not configured.")
        );

        if (s3BucketName == null || s3BucketName.isBlank()) {
            throw new IllegalArgumentException("S3 bucket is not configured. Set APP_STORAGE_S3_BUCKET.");
        }

        String safeFileName = sanitizeFileName(originalFileName, "resume");
        String key = "%s/%s/%s-%s"
                .formatted(
                        normalizedPrefix(),
                        applicationRef,
                        UUID.randomUUID().toString().replace("-", ""),
                        safeFileName
                );

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3BucketName)
                .key(key)
                .contentType(contentType)
                .build();

        client.putObject(putObjectRequest, RequestBody.fromBytes(resumeBytes));
        return key;
    }

    public byte[] loadResume(String storageKey) {
        return loadFile(storageKey);
    }

    public byte[] loadFile(String storageKey) {
        if (!isS3StorageEnabled()) {
            throw new IllegalArgumentException("S3 storage is not enabled.");
        }

        S3Client client = s3Client.orElseThrow(
                () -> new IllegalArgumentException("S3 storage is enabled but S3 client is not configured.")
        );

        if (s3BucketName == null || s3BucketName.isBlank()) {
            throw new IllegalArgumentException("S3 bucket is not configured. Set APP_STORAGE_S3_BUCKET.");
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3BucketName)
                .key(storageKey)
                .build();

        ResponseBytes<GetObjectResponse> responseBytes = client.getObjectAsBytes(getObjectRequest);
        return responseBytes.asByteArray();
    }

    public PresignedUploadTarget createPresignedUpload(
            String category,
            String clientFileId,
            String originalFileName,
            String contentType
    ) {
        if (!isS3StorageEnabled()) {
            throw new IllegalArgumentException("Pre-signed uploads require S3 storage provider.");
        }

        S3Presigner presigner = s3Presigner.orElseThrow(
                () -> new IllegalArgumentException("S3 pre-signer is not configured.")
        );

        if (s3BucketName == null || s3BucketName.isBlank()) {
            throw new IllegalArgumentException("S3 bucket is not configured. Set APP_STORAGE_S3_BUCKET.");
        }

        String safeCategory = sanitizeToken(category, "supporting");
        String safeFileName = sanitizeFileName(originalFileName, "file");
        String key = "%s/applications/uploads/%s/%s-%s"
                .formatted(
                        normalizedPrefix(),
                        safeCategory,
                        UUID.randomUUID().toString().replace("-", ""),
                        safeFileName
                );

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3BucketName)
                .key(key)
                .contentType(contentType)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presign -> presign
                .signatureDuration(Duration.ofSeconds(Math.max(presignExpirationSeconds, 300)))
                .putObjectRequest(putObjectRequest)
        );

        return new PresignedUploadTarget(
                clientFileId,
                category,
                originalFileName,
                contentType,
                key,
                presignedRequest.url().toString(),
                "PUT"
        );
    }

    public boolean objectExists(String storageKey) {
        if (!isS3StorageEnabled()) {
            return false;
        }

        S3Client client = s3Client.orElseThrow(
                () -> new IllegalArgumentException("S3 storage is enabled but S3 client is not configured.")
        );

        try {
            client.headObject(HeadObjectRequest.builder()
                    .bucket(s3BucketName)
                    .key(storageKey)
                    .build());
            return true;
        } catch (S3Exception ex) {
            if (ex.statusCode() == 404) {
                return false;
            }
            throw ex;
        }
    }

    public boolean isManagedStorageKey(String storageKey) {
        if (storageKey == null || storageKey.isBlank()) {
            return false;
        }
        String normalized = normalizedPrefix() + "/applications/uploads/";
        return storageKey.startsWith(normalized);
    }

    private String sanitizeFileName(String fileName, String fallbackName) {
        if (fileName == null || fileName.isBlank()) {
            return fallbackName;
        }

        String sanitized = fileName
                .replace("\\", "-")
                .replace("/", "-")
                .replaceAll("[\\r\\n\"]", "_")
                .trim();

        if (sanitized.isBlank()) {
            return fallbackName;
        }

        return sanitized.toLowerCase(Locale.ROOT);
    }

    private String sanitizeToken(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        String normalized = value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_-]", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");

        return normalized.isBlank() ? fallback : normalized;
    }

    private String normalizedPrefix() {
        String prefix = s3KeyPrefix == null || s3KeyPrefix.isBlank() ? "resumes" : s3KeyPrefix.trim();
        return prefix.endsWith("/") ? prefix.substring(0, prefix.length() - 1) : prefix;
    }

    public record PresignedUploadTarget(
            String clientFileId,
            String category,
            String fileName,
            String contentType,
            String storageKey,
            String uploadUrl,
            String method
    ) {
    }
}
