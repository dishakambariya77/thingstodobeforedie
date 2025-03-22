package com.bucket.thingstodobeforedie.controller;

import com.bucket.thingstodobeforedie.dto.BucketListRequest;
import com.bucket.thingstodobeforedie.dto.BucketListRecord;
import com.bucket.thingstodobeforedie.dto.PagedResponse;
import com.bucket.thingstodobeforedie.dto.BucketListItemRequest;
import com.bucket.thingstodobeforedie.dto.BucketListItemRecord;
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
    public ResponseEntity<ApiResponse<BucketListRecord>> createBucketList(@Valid @RequestBody BucketListRequest request) {
        BucketListRecord bucketList = bucketListService.createBucketList(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bucket list created successfully", bucketList));
    }

    /**
     * Get bucket list by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BucketListRecord>> getBucketListById(@PathVariable Long id) {
        BucketListRecord bucketList = bucketListService.getBucketListById(id);
        return ResponseEntity.ok(ApiResponse.success(bucketList));
    }

    /**
     * Get all bucket lists for current user with pagination
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<BucketListRecord>>> getBucketLists(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PagedResponse<BucketListRecord> response = bucketListService.getBucketLists(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get bucket lists by category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<PagedResponse<BucketListRecord>>> getBucketListsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PagedResponse<BucketListRecord> response = bucketListService.getBucketListsByCategory(categoryId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update bucket list
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BucketListRecord>> updateBucketList(
            @PathVariable Long id,
            @Valid @RequestBody BucketListRequest request) {
        BucketListRecord bucketList = bucketListService.updateBucketList(id, request);
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
    public ResponseEntity<ApiResponse<PagedResponse<BucketListItemRecord>>> getBucketListItems(
            @PathVariable Long bucketListId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PagedResponse<BucketListItemRecord> response = bucketListItemService.getItemsByBucketListId(bucketListId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Add a new item to a bucket list
     */
    @PostMapping("/{bucketListId}/items")
    public ResponseEntity<ApiResponse<BucketListItemRecord>> addBucketListItem(
            @PathVariable Long bucketListId,
            @Valid @RequestBody BucketListItemRequest request) {
        BucketListItemRecord item = bucketListItemService.createBucketListItem(bucketListId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Item added to bucket list", item));
    }
    
//    /**
//     * Update a bucket list item
//     */
//    @PutMapping("/{bucketListId}/items/{itemId}")
//    public ResponseEntity<ApiResponse<BucketListItemRecord>> updateBucketListItem(
//            @PathVariable Long bucketListId,
//            @PathVariable Long itemId,
//            @Valid @RequestBody BucketListRequest request) {
//        BucketListItemRecord item = bucketListItemService.updateBucketListItem(itemId, request);
//        return ResponseEntity.ok(ApiResponse.success("Item updated successfully", item));
//    }
//
//    /**
//     * Delete a bucket list item
//     */
//    @DeleteMapping("/{bucketListId}/items/{itemId}")
//    public ResponseEntity<ApiResponse<Void>> deleteBucketListItem(
//            @PathVariable Long bucketListId,
//            @PathVariable Long itemId) {
//        bucketListItemService.deleteBucketListItem(itemId);
//        return ResponseEntity.ok(ApiResponse.success("Item deleted successfully", null));
//    }
    
    /**
     * Toggle completion status of a bucket list item
     */
    @PutMapping("/{bucketListId}/items/{itemId}/toggle-completion")
    public ResponseEntity<ApiResponse<BucketListItemRecord>> toggleItemCompletion(
            @PathVariable Long bucketListId,
            @PathVariable Long itemId) {
        BucketListItemRecord item = bucketListItemService.toggleItemCompletion(itemId);
        return ResponseEntity.ok(ApiResponse.success("Item completion status toggled", item));
    }
} 