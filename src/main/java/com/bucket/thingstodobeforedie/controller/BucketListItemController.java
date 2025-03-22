/*
package com.bucket.thingstodobeforedie.controller;

import com.bucket.thingstodobeforedie.dto.BucketListItemCreateRequest;
import com.bucket.thingstodobeforedie.dto.BucketListItemRecord;
import com.bucket.thingstodobeforedie.dto.BucketListItemUpdateRequest;
import com.bucket.thingstodobeforedie.dto.PagedResponse;
import com.bucket.thingstodobeforedie.service.BucketListItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bucket-list-items")
@RequiredArgsConstructor
public class BucketListItemController {

    private final BucketListItemService bucketListItemService;

    */
/**
     * Create a new bucket list item
     *//*

    @PostMapping
    public ResponseEntity<BucketListItemRecord> createBucketListItem(@Valid @RequestBody BucketListItemCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bucketListItemService.createBucketListItem(request));
    }

    */
/**
     * Get bucket list item by id
     *//*

    @GetMapping("/{id}")
    public ResponseEntity<BucketListItemRecord> getBucketListItemById(@PathVariable Long id) {
        return ResponseEntity.ok(bucketListItemService.getBucketListItemById(id));
    }

    */
/**
     * Get all items for a specific bucket list with pagination
     *//*

    @GetMapping("/bucket-list/{bucketListId}")
    public ResponseEntity<PagedResponse<BucketListItemRecord>> getItemsByBucketListId(
            @PathVariable Long bucketListId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(bucketListItemService.getItemsByBucketListId(bucketListId, page, size));
    }

    */
/**
     * Update bucket list item
     *//*

    @PutMapping("/{id}")
    public ResponseEntity<BucketListItemRecord> updateBucketListItem(
            @PathVariable Long id,
            @Valid @RequestBody BucketListItemUpdateRequest request) {
        return ResponseEntity.ok(bucketListItemService.updateBucketListItem(id, request));
    }

    */
/**
     * Delete bucket list item
     *//*

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBucketListItem(@PathVariable Long id) {
        bucketListItemService.deleteBucketListItem(id);
        return ResponseEntity.noContent().build();
    }

    */
/**
     * Toggle completion status of a bucket list item
     *//*

    @PutMapping("/{id}/toggle-completion")
    public ResponseEntity<BucketListItemRecord> toggleItemCompletion(@PathVariable Long id) {
        return ResponseEntity.ok(bucketListItemService.toggleItemCompletion(id));
    }
} */
