package com.bucket.thingstodobeforedie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    /**
     * Upload a file to S3 bucket
     *
     * @param file The file to upload
     * @param directory The directory to store the file in
     * @return The URL of the uploaded file
     * @throws IOException if the file upload fails
     */
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID() + fileExtension;
        String key = directory + "/" + uniqueFilename;

        log.info("Uploading file {} to S3 bucket {} with key {}", originalFilename, bucketName, key);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(key)).toString();
    }

    /**
     * Get the file extension from a filename
     *
     * @param filename The filename
     * @return The file extension (including the dot)
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return filename.substring(dotIndex);
    }

    public void deleteFile(String fileKey) {
        log.info("Deleting file from S3: {}", fileKey);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        s3Client.deleteObject(deleteRequest);
        log.info("File deleted successfully from S3: {}", fileKey);
    }
}