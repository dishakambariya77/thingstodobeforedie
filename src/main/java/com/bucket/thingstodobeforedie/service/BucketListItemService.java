package com.bucket.thingstodobeforedie.service;

import com.bucket.thingstodobeforedie.dto.BucketListItemResponse;
import com.bucket.thingstodobeforedie.dto.BucketListItemRequest;
import com.bucket.thingstodobeforedie.dto.PagedResponse;
import com.bucket.thingstodobeforedie.entity.BucketList;
import com.bucket.thingstodobeforedie.entity.BucketListItem;
import com.bucket.thingstodobeforedie.entity.User;
import com.bucket.thingstodobeforedie.exception.ResourceNotFoundException;
import com.bucket.thingstodobeforedie.repository.BucketListItemRepository;
import com.bucket.thingstodobeforedie.repository.BucketListRepository;
import com.bucket.thingstodobeforedie.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BucketListItemService {

    private final BucketListItemRepository bucketListItemRepository;
    private final BucketListRepository bucketListRepository;
    private final CurrentUser currentUser;

    /**
     * Create a new bucket list item
     */
    public BucketListItemResponse createBucketListItem(Long bucketListId, BucketListItemRequest request) {
        User user = currentUser.getUser();

        // Find the bucket list and verify it belongs to the current user
        BucketList bucketList = bucketListRepository.findById(bucketListId)
                .orElseThrow(() -> new ResourceNotFoundException("Bucket list not found with id: " + bucketListId));

        if (!bucketList.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Bucket list not found with id: " + bucketListId);
        }


        BucketListItem item = BucketListItem.builder()
                .name(request.name())
                .description(request.description())
                .completed(false)
                .deadline(request.deadline())
                .priority(request.priority())
                .bucketList(bucketList)
                .notes(request.notes())
                .build();

        bucketListItemRepository.save(item);
        return mapToRecord(item);
    }

    /**
     * Get bucket list item by id
     */
    public BucketListItemResponse getBucketListItemById(Long id) {
        User user = currentUser.getUser();
        
        BucketListItem item = bucketListItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bucket list item not found with id: " + id));
        
        // Verify the item belongs to the current user
        if (!item.getBucketList().getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Bucket list item not found with id: " + id);
        }
        
        return mapToRecord(item);
    }

    /**
     * Get all items for a specific bucket list with pagination
     */
    public PagedResponse<BucketListItemResponse> getItemsByBucketListId(Long bucketListId, int page, int size) {
        validatePageNumberAndSize(page, size);
        User user = currentUser.getUser();
        
        // Verify the bucket list exists and belongs to the current user
        BucketList bucketList = bucketListRepository.findById(bucketListId)
                .orElseThrow(() -> new ResourceNotFoundException("Bucket list not found with id: " + bucketListId));
        
        if (!bucketList.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Bucket list not found with id: " + bucketListId);
        }
        
        // Create pageable instance
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        
        Page<BucketListItem> items = bucketListItemRepository.findByBucketListId(bucketListId, pageable);
        
        if (items.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), items.getNumber(),
                    items.getSize(), items.getTotalElements(), items.getTotalPages(), items.isLast());
        }
        
        List<BucketListItemResponse> itemRecords = items.map(this::mapToRecord).getContent();
        
        return new PagedResponse<>(itemRecords, items.getNumber(),
                items.getSize(), items.getTotalElements(), items.getTotalPages(), items.isLast());
    }

    /**
     * Map BucketListItem entity to BucketListItemRecord DTO
     */
    private BucketListItemResponse mapToRecord(BucketListItem item) {
        return BucketListItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .completed(item.isCompleted())
                .deadline(item.getDeadline())
                .completedAt(item.getCompletedAt())
                .priority(item.getPriority())
                .bucketListId(item.getBucketList().getId())
                .bucketListName(item.getBucketList().getName())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    /**
     * Validate page number and size
     */
    private void validatePageNumberAndSize(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be less than zero.");
        }

        if (size < 1) {
            throw new IllegalArgumentException("Size must be greater than zero.");
        }

        if (size > 100) {
            throw new IllegalArgumentException("Page size must not be greater than 100.");
        }
    }
} 