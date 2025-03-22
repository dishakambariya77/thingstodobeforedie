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
        // Get current user
        User currentUser = userService.getCurrentUser();

        // Find the bucket list
        BucketList bucketList = bucketListRepository.findById(bucketListId)
                .orElseThrow(() -> new ResourceNotFoundException("Bucket List not found with id: " + bucketListId));

        // Check if the bucket list belongs to the current user
        if (!bucketList.getUser().getId().equals(currentUser.getId())) {
            throw new AuthenticationException("You don't have permission to upload an image for this bucket list");
        }

        try {
            // Construct directory path: bucket-lists/{userId}/{bucketListId}
            String directory = String.format("bucket-lists/%d/%d", currentUser.getId(), bucketListId);
            
            // Upload the file to S3
            String imageUrl = s3Service.uploadFile(file, directory);
            
            // Update the bucket list with the image URL
            bucketList.setImageUrl(imageUrl);
            bucketListRepository.save(bucketList);
            
            return ImageUploadResponse.builder()
                    .imageUrl(imageUrl)
                    .message("Image uploaded successfully")
                    .bucketListId(bucketListId)
                    .build();
            
        } catch (IOException e) {
            log.error("Failed to upload image for bucket list {}: {}", bucketListId, e.getMessage());
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }
    }
} 