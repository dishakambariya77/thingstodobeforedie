package com.bucket.thingstodobeforedie.repository;

import com.bucket.thingstodobeforedie.entity.BucketList;
import com.bucket.thingstodobeforedie.entity.BucketListItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BucketListItemRepository extends JpaRepository<BucketListItem, Long> {

    Page<BucketListItem> findByBucketListOrderByCreatedAtDesc(BucketList bucketList, Pageable pageable);

    @Query("SELECT b FROM BucketListItem b WHERE b.bucketList = :bucketList AND " +
           "LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<BucketListItem> searchBucketListItems(BucketList bucketList, String searchTerm, Pageable pageable);

    /**
     * Find all items for a specific bucket list with pagination
     */
    Page<BucketListItem> findByBucketListId(Long bucketListId, Pageable pageable);
    
    /**
     * Count the number of completed items in a bucket list
     */
    long countByBucketListIdAndCompletedTrue(Long bucketListId);
    
    /**
     * Delete all items in a bucket list
     */
    void deleteByBucketListId(Long bucketListId);

    List<BucketListItem> findByBucketListId(Long bucketListId);

} 