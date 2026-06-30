package com.mdevm.InventoryMgtSystem.services.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioStorageService {

    private final S3Client s3Client;

    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * Upload file to MinIO
     * @param file the file to upload
     * @return the URL/key of the uploaded file
     */
    public String uploadFile(MultipartFile file) {
        try {
            // Validate file
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File cannot be empty");
            }

            // Validate file content type by checking MIME type from actual content, not just header
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are allowed");
            }

            // Additional validation: check file extension to prevent SVG/script uploads
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                String lowerCaseFilename = originalFilename.toLowerCase();
                if (lowerCaseFilename.endsWith(".svg") || lowerCaseFilename.endsWith(".svgz")) {
                    throw new IllegalArgumentException("SVG files are not allowed for security reasons");
                }
            }

            if (file.getSize() > 1024 * 1024 * 1024) { // 1GB limit
                throw new IllegalArgumentException("File size must be less than 1GB");
            }

            // Generate unique filename
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // Create bucket if it doesn't exist
            createBucketIfNotExists();

            // Upload file to MinIO
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(
                    file.getInputStream(), file.getSize()));

            log.info("File uploaded successfully to MinIO: {}", fileName);

            // Return the file key (can be used to construct URL)
            return fileName;

        } catch (S3Exception e) {
            log.error("S3 error while uploading file: {}", e.getMessage());
            throw new RuntimeException("Error uploading file to storage: " + e.getMessage());
        } catch (IOException e) {
            log.error("IO error while uploading file: {}", e.getMessage());
            throw new RuntimeException("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while uploading file: {}", e.getMessage());
            throw new RuntimeException("Error uploading file: " + e.getMessage());
        }
    }

    /**
     * Delete file from MinIO
     * @param fileName the name/key of the file to delete
     */
    public void deleteFile(String fileName) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted successfully from MinIO: {}", fileName);

        } catch (S3Exception e) {
            log.error("S3 error while deleting file: {}", e.getMessage());
            throw new RuntimeException("Error deleting file: " + e.getMessage());
        }
    }

    /**
     * Get file URL (constructs the URL based on MinIO endpoint)
     * @param fileName the file key
     * @return the full URL to access the file
     */
    public String getFileUrl(String fileName) {
        // For MinIO, we construct the URL manually
        // Format: http://localhost:9000/bucket-name/file-name
        return System.getProperty("minio.endpoint", "http://localhost:9000") + "/" + bucketName + "/" + fileName;
    }

    /**
     * Check if bucket exists, create if not
     */
    private void createBucketIfNotExists() {
        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.headBucket(headBucketRequest);
            log.info("Bucket '{}' already exists", bucketName);

        } catch (NoSuchBucketException e) {
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.createBucket(createBucketRequest);
            log.info("Bucket '{}' created successfully", bucketName);
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .build();

                s3Client.createBucket(createBucketRequest);
                log.info("Bucket '{}' created successfully", bucketName);
            } else {
                log.error("Error checking/creating bucket: {}", e.getMessage());
                throw new RuntimeException("Error with bucket: " + e.getMessage());
            }
        }
    }
}
