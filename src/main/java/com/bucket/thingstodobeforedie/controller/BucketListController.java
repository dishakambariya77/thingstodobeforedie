package com.bucket.thingstodobeforedie.controller;

import com.bucket.thingstodobeforedie.dto.BucketListRequest;
import com.bucket.thingstodobeforedie.dto.BucketListResponse;
import com.bucket.thingstodobeforedie.dto.PagedResponse;
import com.bucket.thingstodobeforedie.dto.BucketListItemRequest;
import com.bucket.thingstodobeforedie.dto.BucketListItemResponse;
import com.bucket.thingstodobeforedie.dto.ApiResponse;
import com.bucket.thingstodobeforedie.service.BucketListService;
import com.bucket.thingstodobeforedie.service.BucketListItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bucket-lists")
@RequiredArgsConstructor
public class BucketListController {

    private final BucketListService bucketListService;
    private final BucketListItemService bucketListItemService;

    /**
     * Create a new bucket list
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BucketListResponse>> createBucketList(@Valid @RequestBody BucketListRequest request) {
        BucketListResponse bucketList = bucketListService.createBucketList(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bucket list created successfully", bucketList));
    }

    /**
     * Get bucket list by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BucketListResponse>> getBucketListById(@PathVariable Long id) {
        BucketListResponse bucketList = bucketListService.getBucketListById(id);
        return ResponseEntity.ok(ApiResponse.success(bucketList));
    }

    /**
     * Get all bucket lists for current user with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<BucketListResponse>>> getBucketLists(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PagedResponse<BucketListResponse> response = bucketListService.getBucketLists(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get bucket lists by category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<PagedResponse<BucketListResponse>>> getBucketListsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PagedResponse<BucketListResponse> response = bucketListService.getBucketListsByCategory(categoryId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update bucket list
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BucketListResponse>> updateBucketList(
            @PathVariable Long id,
            @Valid @RequestBody BucketListRequest request) {
        BucketListResponse bucketList = bucketListService.updateBucketList(id, request);
        return ResponseEntity.ok(ApiResponse.success("Bucket list updated successfully", bucketList));
    }

    /**
     * Delete bucket list
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBucketList(@PathVariable Long id) {
        bucketListService.deleteBucketList(id);
        return ResponseEntity.ok(ApiResponse.success("Bucket list deleted successfully", null));
    }

    /**
     * Get all items for a specific bucket list with pagination
     */
    @GetMapping("/{bucketListId}/items")
    public ResponseEntity<ApiResponse<PagedResponse<BucketListItemResponse>>> getBucketListItems(
            @PathVariable Long bucketListId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PagedResponse<BucketListItemResponse> response = bucketListItemService.getItemsByBucketListId(bucketListId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Add a new item to a bucket list
     */
    @PostMapping("/{bucketListId}/items")
    public ResponseEntity<ApiResponse<BucketListItemResponse>> addBucketListItem(
            @PathVariable Long bucketListId,
            @Valid @RequestBody BucketListItemRequest request) {
        BucketListItemResponse item = bucketListItemService.createBucketListItem(bucketListId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Item added to bucket list", item));
    }
    
    /**
     * Toggle completion status of a bucket list item
     */
    @PutMapping("/{bucketListId}/items/{itemId}/toggle-completion")
    public ResponseEntity<ApiResponse<BucketListResponse>> toggleItemCompletion(
            @PathVariable Long bucketListId,
            @PathVariable Long itemId) {
        BucketListResponse item = bucketListService.toggleItemCompletion(bucketListId,itemId);
        return ResponseEntity.ok(ApiResponse.success("Item completion status toggled", item));
    }

//    @GetMapping("/{bucketListId}")
//    public ResponseEntity<PagedResponse<BucketListItemRecord>> getItemsByBucketListId(
//            @PathVariable Long bucketListId,
//            @RequestParam(value = "page", defaultValue = "0") int page,
//            @RequestParam(value = "size", defaultValue = "10") int size) {
//        return ResponseEntity.ok(bucketListItemService.getItemsByBucketListId(bucketListId, page, size));
//    }
} 