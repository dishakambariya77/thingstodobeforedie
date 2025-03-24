package com.bucket.thingstodobeforedie.service;

import com.bucket.thingstodobeforedie.dto.ImageUploadResponse;
import com.bucket.thingstodobeforedie.entity.BucketList;
import com.bucket.thingstodobeforedie.entity.User;
import com.bucket.thingstodobeforedie.exception.AuthenticationException;
import com.bucket.thingstodobeforedie.exception.ResourceNotFoundException;
import com.bucket.thingstodobeforedie.repository.BucketListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BucketListImageService {

    private final BucketListRepository bucketListRepository;
    private final UserService userService;
    private final S3Service s3Service;

    /**
     * Upload a bucket list image
     *
     * @param bucketListId The ID of the bucket list
     * @param file The image file to upload
     * @return The image upload response
     */
    @Transactional
    public ImageUploadResponse uploadBucketListImage(Long bucketListId, MultipartFile file) {
        User currentUser = userService.getCurrentUser();

        BucketList bucketList = bucketListRepository.findById(bucketListId)
                .orElseThrow(() -> new ResourceNotFoundException("Bucket List not found with id: " + bucketListId));

        if (!bucketList.getUser().getId().equals(currentUser.getId())) {
            throw new AuthenticationException("You don't have permission to upload an image for this bucket list");
        }

        try {
            String directory = String.format("bucket-lists/%d/%d", currentUser.getId(), bucketListId);

            // Step 1: Get old image URL before uploading
            String oldImageUrl = bucketList.getImageUrl();

            // Step 2: Upload new image
            String newImageUrl = s3Service.uploadFile(file, directory);

            // Step 3: Delete old image from S3 if it exists
            if (oldImageUrl != null) {
                String oldImageKey = oldImageUrl.substring(oldImageUrl.indexOf("bucket-lists"));
                s3Service.deleteFile(oldImageKey);
            }

            // Step 4: Update DB with new image URL
            bucketList.setImageUrl(newImageUrl);
            bucketListRepository.save(bucketList);

            return ImageUploadResponse.builder()
                    .imageUrl(newImageUrl)
                    .message("Image uploaded successfully")
                    .bucketListId(bucketListId)
                    .build();

        } catch (IOException e) {
            log.error("Failed to upload image for bucket list {}: {}", bucketListId, e.getMessage());
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }

} 