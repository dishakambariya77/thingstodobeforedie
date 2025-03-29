package com.bucket.thingstodobeforedie.service;

import com.bucket.thingstodobeforedie.dto.*;
import com.bucket.thingstodobeforedie.entity.*;
import com.bucket.thingstodobeforedie.exception.ResourceNotFoundException;
import com.bucket.thingstodobeforedie.repository.BucketListItemRepository;
import com.bucket.thingstodobeforedie.repository.BucketListRepository;
import com.bucket.thingstodobeforedie.repository.CategoryRepository;
import com.bucket.thingstodobeforedie.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BucketListService {

    private final UserService userService;
    private final BucketListRepository bucketListRepository;
    private final BucketListItemRepository bucketListItemRepository;
    private final CategoryRepository categoryRepository;
    private final CurrentUser currentUser;
    private final ActivityService activityService;

    /**
     * Create a new bucket list
     */
    @Transactional
    public BucketListRecord createBucketList(BucketListRequest request) {
        User user = currentUser.getUser();
        
        Category category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.categoryId()));
        }
        String tags = null;

        if(!request.tags().isEmpty()){
            tags = String.join(",", request.tags());
        }

        BucketList bucketList = BucketList.builder()
                .name(request.name())
                .description(request.description())
                .user(user)
                .category(category)
                .tags(tags)
                .status(BucketStatus.ACTIVE)
                .build();

        BucketList savedBucketList = bucketListRepository.save(bucketList);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("bucketListName", savedBucketList.getName());
        activityService.trackActivity(
                user,
                ActivityType.BUCKET_LIST_CREATED,
                String.format("Added new goal \"%s\"", bucketList.getName()),
                ActivityIcon.BUCKET_LIST_CREATED,
                metadata
        );
        
        if (request.bucketItems() != null && !request.bucketItems().isEmpty()) {
            List<BucketListItem> bucketItems = request.bucketItems().stream()
                    .map(item -> BucketListItem.builder()
                            .name(item.name())
                            .description(item.description())
                            .completed(item.completed())
                            .notes(item.notes())
                            .deadline(item.deadline())
                            .priority(item.priority())
                            .bucketList(savedBucketList)
                            .build())
                    .collect(Collectors.toList());

            bucketListItemRepository.saveAll(bucketItems);
            savedBucketList.setBucketListItems(bucketItems);
        }
        
        return mapToRecord(savedBucketList);
    }

    /**
     * Get bucket list by id
     */
    @Transactional(readOnly = true)
    public BucketListRecord getBucketListById(Long id) {
        User user = currentUser.getUser();
        
        BucketList bucketList = bucketListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bucket List", "id", id));
        
        // Check if the bucket list belongs to the current user
        if (!bucketList.getUser().getId().equals(user.getId())) {
            throw new com.bucket.thingstodobeforedie.exception.AuthenticationException("You don't have permission to access this bucket list");
        }
        
        return mapToRecord(bucketList);
    }

    /**
     * Get all bucket lists for current user with pagination
     */
    @Transactional(readOnly = true)
    public PagedResponse<BucketListRecord> getBucketLists(int page, int size) {
        User user = currentUser.getUser();
        
        validatePageNumberAndSize(page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<BucketList> bucketLists = bucketListRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        
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
        User user = currentUser.getUser();
        
        validatePageNumberAndSize(page, size);
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<BucketList> bucketLists = bucketListRepository.findByUserAndCategoryOrderByCreatedAtDesc(user, category, pageable);
        
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
        User user = currentUser.getUser();

        BucketList bucketList = bucketListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bucket List", "id", id));

        // Check if the bucket list belongs to the current user
        if (!bucketList.getUser().getId().equals(user.getId())) {
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

        // Update tags if provided
        if (request.tags() != null && !request.tags().isEmpty()) {
            String tags = String.join(",", request.tags());
            bucketList.setTags(tags);
        }

        // Update bucket list items if provided
        if (request.bucketItems() != null && !request.bucketItems().isEmpty()) {
            // Fetch existing items
            List<BucketListItem> existingItems = bucketListItemRepository.findByBucketListId(id);

            // Map incoming request items by name for comparison
            Map<String, BucketListItemRequest> incomingItemsMap = request.bucketItems().stream()
                    .collect(Collectors.toMap(BucketListItemRequest::name, item -> item));

            // Process updates and deletions
            existingItems.forEach(existingItem -> {
                if (incomingItemsMap.containsKey(existingItem.getName())) {
                    BucketListItemRequest incomingItem = incomingItemsMap.get(existingItem.getName());
                    existingItem.setDescription(incomingItem.description());
                    existingItem.setNotes(incomingItem.notes());
                    existingItem.setDeadline(incomingItem.deadline());
                    existingItem.setPriority(incomingItem.priority());
                } else {
                    bucketListItemRepository.delete(existingItem); // Remove items not in the update request
                }
            });

            // Add new items
            List<BucketListItem> newItems = request.bucketItems().stream()
                    .filter(item -> existingItems.stream().noneMatch(existingItem -> existingItem.getName().equals(item.name())))
                    .map(item -> BucketListItem.builder()
                            .name(item.name())
                            .description(item.description())
                            .notes(item.notes())
                            .deadline(item.deadline())
                            .priority(item.priority())
                            .bucketList(bucketList)
                            .build())
                    .collect(Collectors.toList());

            bucketListItemRepository.saveAll(newItems);
        }

        BucketList updatedBucketList = bucketListRepository.save(bucketList);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("bucketListName", updatedBucketList.getName());
        activityService.trackActivity(
                user,
                ActivityType.BUCKET_LIST_UPDATED,
                String.format("Updated progress on \"%s\"", bucketList.getName()),
                ActivityIcon.BUCKET_LIST_UPDATED,
                metadata
        );

        return mapToRecord(updatedBucketList);
    }


    /**
     * Delete bucket list
     */
    @Transactional
    public void deleteBucketList(Long id) {
        User user = currentUser.getUser();
        
        BucketList bucketList = bucketListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bucket List", "id", id));
        
        // Check if the bucket list belongs to the current user
        if (!bucketList.getUser().getId().equals(user.getId())) {
            throw new com.bucket.thingstodobeforedie.exception.AuthenticationException("You don't have permission to delete this bucket list");
        }

        // Track the bucket list deletion activity before deletion
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("bucketListName", bucketList.getName());
        activityService.trackActivity(
                user,
                ActivityType.BUCKET_LIST_DELETED,
                String.format("Removed goal: \"%s\"", bucketList.getName()),
                ActivityIcon.BUCKET_LIST_DELETED,
                metadata
        );
        
        bucketListRepository.delete(bucketList);
    }

    /**
     * Toggle completion status of a bucket list item
     */
    @Transactional
    public BucketListRecord toggleItemCompletion(Long bucketListId, Long itemId) {
        User user = currentUser.getUser();

        BucketListItem item = bucketListItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Bucket list item not found with id: " + itemId));

        // Verify the item belongs to the current user
        if (!item.getBucketList().getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Bucket list item not found with id: " + itemId);
        }

        // Toggle completion status
        item.setCompleted(!item.isCompleted());

        // Update completedAt date
        if (item.isCompleted()) {
            item.setCompletedAt(LocalDateTime.now());

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("bucketListName", item.getBucketList().getName());
            metadata.put("bucketListItemName", item.getName());

            activityService.trackActivity(
                    user,
                    ActivityType.BUCKET_ITEM_COMPLETED,
                    String.format("Completed \"%s\"", item.getName()),
                    ActivityIcon.BUCKET_ITEM_COMPLETED,
                    metadata
            );
        } else {
            item.setCompletedAt(null);

            // Track un-completion activity
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("bucketListName", item.getBucketList().getName());
            metadata.put("bucketListItemName", item.getName());

            activityService.trackActivity(
                    user,
                    ActivityType.BUCKET_ITEM_UPDATED,
                    String.format("Marked bucket list item as not completed: \"%s\"", item.getName()),
                    ActivityIcon.BUCKET_ITEM_UPDATED,
                    metadata
            );
        }

        item.setUpdatedAt(LocalDateTime.now());
        bucketListItemRepository.save(item);

        // Check if all items in the bucket list are completed
        BucketList bucketList = item.getBucketList();
        boolean allCompleted = bucketList.getBucketListItems().stream().allMatch(BucketListItem::isCompleted);

        if (allCompleted) {
            bucketList.setStatus(BucketStatus.COMPLETED);

            // Track bucket list completion
            activityService.trackActivity(
                    user,
                    ActivityType.BUCKET_LIST_COMPLETED,
                    String.format("Completed the bucket list \"%s\"", bucketList.getName()),
                    ActivityIcon.BUCKET_LIST_COMPLETED,
                    Map.of("bucketListName", bucketList.getName())
            );
        } else {
            bucketList.setStatus(BucketStatus.ACTIVE);
        }

        bucketListRepository.save(bucketList);

        return getBucketListById(bucketListId);
    }

    /**
     * Map BucketList entity to BucketListRecord DTO
     */
    private BucketListRecord mapToRecord(BucketList bucketList) {
        // Count completed and total items
        int completedItemsCount = (int) bucketList.getBucketListItems().stream()
                .filter(BucketListItem::isCompleted) // Count only completed items
                .count();

        List<String> tagList = bucketList.getTags() != null ?
                List.of(bucketList.getTags().split(",")) :
                List.of();

        // Map category if exists
        CategoryRecord categoryRecord = null;
        if (bucketList.getCategory() != null) {
            Category category = bucketList.getCategory();
            categoryRecord = new CategoryRecord(
                    category.getId(),
                    category.getName(),
                    category.getDescription(),
                    category.getIconUrl(),
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

        int totalItems = bucketList.getBucketListItems().size();

        int progressPercentage = totalItems > 0 ? (completedItemsCount * 100) / totalItems : 0;

        LocalDateTime bucketDeadlineTime = null;
        if (!items.isEmpty()) {
            bucketDeadlineTime = items.stream()
                    .filter(item -> item.getDeadline() != null) // Exclude null deadlines
                    .max(Comparator.comparing(BucketListItem::getDeadline))
                    .map(BucketListItem::getDeadline)
                    .orElse(null);
        }

        return new BucketListRecord(
                bucketList.getId(),
                bucketList.getName(),
                bucketList.getDescription(),
                bucketList.getImageUrl(),
                bucketList.getUser().getId(),
                tagList,
                categoryRecord,
                itemRecords,
                completedItemsCount,
                totalItems,
                bucketList.getCreatedAt(),
                bucketList.getUpdatedAt(),
                progressPercentage,
                bucketDeadlineTime
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

    /**
     * Get recent ongoing bucket list items for the specified user
     * 
     * @param userId User ID to fetch bucket list items for
     * @param limit Number of items to return
     * @return List of recent ongoing bucket list items
     */
    @Transactional(readOnly = true)
    public List<BucketListRecord> getRecentOngoingBucketListItems(Long userId, int limit) {
        User user = userService.getUserById(userId);
        
        Pageable pageable = PageRequest.of(0, limit, Sort.Direction.DESC, "createdAt");
        Page<BucketList> ongoingBucketLists = bucketListRepository.findByUserAndStatusOrderByCreatedAtDesc(
                user, BucketStatus.ACTIVE, pageable);
        
        return ongoingBucketLists.getContent().stream()
                .map(this::mapToRecord)
                .collect(Collectors.toList());
    }
} 