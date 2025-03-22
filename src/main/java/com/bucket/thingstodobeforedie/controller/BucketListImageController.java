package com.bucket.thingstodobeforedie.controller;

import com.bucket.thingstodobeforedie.dto.ApiResponse;
import com.bucket.thingstodobeforedie.dto.ImageUploadResponse;
import com.bucket.thingstodobeforedie.service.BucketListImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/bucket-lists")
@RequiredArgsConstructor
public class BucketListImageController {

    private final BucketListImageService bucketListImageService;

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
            @RequestParam("file") MultipartFile file) {
        
        ImageUploadResponse response = bucketListImageService.uploadBucketListImage(bucketListId, file);
        return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", response));
    }
} 