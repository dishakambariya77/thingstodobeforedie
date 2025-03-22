package com.bucket.thingstodobeforedie.service;

import com.bucket.thingstodobeforedie.dto.BucketListRequest;
import com.bucket.thingstodobeforedie.dto.BucketListItemRecord;
import com.bucket.thingstodobeforedie.dto.BucketListRecord;
import com.bucket.thingstodobeforedie.dto.CategoryRecord;
import com.bucket.thingstodobeforedie.dto.PagedResponse;
import com.bucket.thingstodobeforedie.entity.BucketList;
import com.bucket.thingstodobeforedie.entity.BucketListItem;
import com.bucket.thingstodobeforedie.entity.Category;
import com.bucket.thingstodobeforedie.entity.User;
import com.bucket.thingstodobeforedie.exception.ResourceNotFoundException;
import com.bucket.thingstodobeforedie.repository.BucketListItemRepository;
import com.bucket.thingstodobeforedie.repository.BucketListRepository;
import com.bucket.thingstodobeforedie.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BucketListService {

    private final UserService userService;
    private final BucketListRepository bucketListRepository;
    private final BucketListItemRepository bucketListItemRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Create a new bucket list
     */
    @Transactional
    public BucketListRecord createBucketList(BucketListRequest request) {
        User currentUser = userService.getCurrentUser();
        
        Category category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.categoryId()));
        }

        BucketList bucketList = BucketList.builder()
                .name(request.name())
                .description(request.description())
                .user(currentUser)
                .category(category)
                .build();

        BucketList savedBucketList = bucketListRepository.save(bucketList);
        
        return mapToRecord(savedBucketList);
    }

    /**
     * Get bucket list by id
     */
    @Transactional(readOnly = true)
    public BucketListRecord getBucketListById(Long id) {
        User currentUser = userService.getCurrentUser();
        
        BucketList bucketList = bucketListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bucket List", "id", id));
        
        // Check if the bucket list belongs to the current user
        if (!bucketList.getUser().getId().equals(currentUser.getId())) {
            throw new com.bucket.thingstodobeforedie.exception.AuthenticationException("You don't have permission to access this bucket list");
        }
        
        return mapToRecord(bucketList);
    }

    /**
     * Get all bucket lists for current user with pagination
     */
    @Transactional(readOnly = true)
    public PagedResponse<BucketListRecord> getBucketLists(int page, int size) {
        User currentUser = userService.getCurrentUser();
        
        validatePageNumberAndSize(page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<BucketList> bucketLists = bucketListRepository.findByUserOrderByCreatedAtDesc(currentUser, pageable);
        
        if (bucketLists.getNumberOfElements() == 0) {
            return new PagedResponse<>(
                    Collections.emptyList(), 
                    bucketLists.getNumber(),
                    bucketLists.getSize(), 
                    bucketLists.getTotalElements(), 
                    bucketLists.getTotalPages(), 
                    bucketLists.isLast());
        }
        
        List<BucketListRecord> bucketListRecords = bucketLists.getContent().stream()
                .map(this::mapToRecord)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
                bucketListRecords, 
                bucketLists.getNumber(),
                bucketLists.getSize(), 
                bucketLists.getTotalElements(), 
                bucketLists.getTotalPages(), 
                bucketLists.isLast());
    }

    /**
     * Get bucket lists by category
     */
    @Transactional(readOnly = true)
    public PagedResponse<BucketListRecord> getBucketListsByCategory(Long categoryId, int page, int size) {
        User currentUser = userService.getCurrentUser();
        
        validatePageNumberAndSize(page, size);
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<BucketList> bucketLists = bucketListRepository.findByUserAndCategoryOrderByCreatedAtDesc(currentUser, category, pageable);
        
        if (bucketLists.getNumberOfElements() == 0) {
            return new PagedResponse<>(
                    Collections.emptyList(), 
                    bucketLists.getNumber(),
                    bucketLists.getSize(), 
                    bucketLists.getTotalElements(), 
                    bucketLists.getTotalPages(), 
                    bucketLists.isLast());
        }
        
        List<BucketListRecord> bucketListRecords = bucketLists.getContent().stream()
                .map(this::mapToRecord)
                .collect(Collectors.toList());
        
        return new PagedResponse<>(
                bucketListRecords, 
                bucketLists.getNumber(),
                bucketLists.getSize(), 
                bucketLists.getTotalElements(), 
                bucketLists.getTotalPages(), 
                bucketLists.isLast());
    }

    /**
     * Update bucket list
     */
    @Transactional
    public BucketListRecord updateBucketList(Long id, BucketListRequest request) {
        User currentUser = userService.getCurrentUser();
        
        BucketList bucketList = bucketListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bucket List", "id", id));
        
        // Check if the bucket list belongs to the current user
        if (!bucketList.getUser().getId().equals(currentUser.getId())) {
            throw new com.bucket.thingstodobeforedie.exception.AuthenticationException("You don't have permission to update this bucket list");
        }
        
        // Update name if provided
        if (request.name() != null) {
            bucketList.setName(request.name());
        }
        
        // Update description if provided
        if (request.description() != null) {
            bucketList.setDescription(request.description());
        }
        
        // Update category if provided
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.categoryId()));
            bucketList.setCategory(category);
        }
        
        BucketList updatedBucketList = bucketListRepository.save(bucketList);
        
        return mapToRecord(updatedBucketList);
    }

    /**
     * Delete bucket list
     */
    @Transactional
    public void deleteBucketList(Long id) {
        User currentUser = userService.getCurrentUser();
        
        BucketList bucketList = bucketListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bucket List", "id", id));
        
        // Check if the bucket list belongs to the current user
        if (!bucketList.getUser().getId().equals(currentUser.getId())) {
            throw new com.bucket.thingstodobeforedie.exception.AuthenticationException("You don't have permission to delete this bucket list");
        }
        
        bucketListRepository.delete(bucketList);
    }

    /**
     * Map BucketList entity to BucketListRecord DTO
     */
    private BucketListRecord mapToRecord(BucketList bucketList) {
        // Count completed and total items
        long completedItemsCount = bucketListItemRepository.countByBucketListIdAndCompletedTrue(bucketList.getId());
        long totalItemsCount = bucketListItemRepository.countByBucketListId(bucketList.getId());
        
        // Map category if exists
        CategoryRecord categoryRecord = null;
        if (bucketList.getCategory() != null) {
            Category category = bucketList.getCategory();
            categoryRecord = new CategoryRecord(
                    category.getId(),
                    category.getName(),
                    category.getDescription(),
                    category.getIcon(),
                    category.getType(),
                    category.getCreatedAt(),
                    category.getUpdatedAt()
            );
        }
        
        // Get items for this bucket list
        List<BucketListItem> items = bucketList.getBucketListItems();
        List<BucketListItemRecord> itemRecords = Collections.emptyList();
        
        if (items != null && !items.isEmpty()) {
            itemRecords = items.stream()
                    .map(item -> BucketListItemRecord.builder()
                            .id(item.getId())
                            .name(item.getName())
                            .description(item.getDescription())
                            .completed(item.isCompleted())
                            .deadline(item.getDeadline())
                            .completedAt(item.getCompletedAt())
                            .priority(item.getPriority())
                            .bucketListId(bucketList.getId())
                            .bucketListName(bucketList.getName())
                            .createdAt(item.getCreatedAt())
                            .updatedAt(item.getUpdatedAt())
                            .build())
                    .collect(Collectors.toList());
        }
        
        return new BucketListRecord(
                bucketList.getId(),
                bucketList.getName(),
                bucketList.getDescription(),
                bucketList.getImageUrl(),
                bucketList.getUser().getId(),
                categoryRecord,
                itemRecords,
                (int) completedItemsCount,
                (int) totalItemsCount,
                bucketList.getCreatedAt(),
                bucketList.getUpdatedAt()
        );
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