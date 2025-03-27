package com.bucket.thingstodobeforedie.controller;

import com.bucket.thingstodobeforedie.dto.ApiResponse;
import com.bucket.thingstodobeforedie.dto.ImageUploadResponse;
import com.bucket.thingstodobeforedie.service.BlogImageService;
import com.bucket.thingstodobeforedie.service.BucketListImageService;
import com.bucket.thingstodobeforedie.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class ImageController {

    private final BucketListImageService bucketListImageService;
    private final BlogImageService blogImageService;
    private final UserService userService;

    /**
     * Upload an image for a bucket list
     * 
     * @param bucketListId the ID of the bucket list
     * @param file the image file to upload
     * @return response with the URL of the uploaded image
     */
    @PostMapping(value = "/{bucketListId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadBucketListImage(
            @PathVariable Long bucketListId,
            @RequestPart("file") MultipartFile file) {
        
        ImageUploadResponse response = bucketListImageService.uploadBucketListImage(bucketListId, file);
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", response));
    }

    @PostMapping(value = "/{blogId}/featured-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadBlogPostImage(
            @PathVariable Long blogId,
            @RequestPart("file") MultipartFile file) {

        ImageUploadResponse response = blogImageService.uploadBlogPostImage(blogId, file);
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", response));
    }

    @PostMapping(value = "/{userId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadUserProfileImage(
            @PathVariable Long userId,
            @RequestPart("file") MultipartFile file) {

        ImageUploadResponse response = userService.uploadUserProfileImage(userId, file);
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", response));
    }
} 