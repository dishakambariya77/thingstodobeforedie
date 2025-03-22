package com.bucket.thingstodobeforedie.repository;

import com.bucket.thingstodobeforedie.entity.BucketList;
import com.bucket.thingstodobeforedie.entity.Category;
import com.bucket.thingstodobeforedie.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BucketListRepository extends JpaRepository<BucketList, Long> {
    
    Page<BucketList> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    Page<BucketList> findByUserAndCategoryOrderByCreatedAtDesc(User user, Category category, Pageable pageable);
    
    Page<BucketList> findByCategoryOrderByCreatedAtDesc(Category category, Pageable pageable);
    
    @Query("SELECT b FROM BucketList b WHERE b.user = :user AND " +
           "LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<BucketList> searchBucketLists(User user, String searchTerm, Pageable pageable);
    
    List<BucketList> findTop5ByUserOrderByCreatedAtDesc(User user);
    
    List<BucketList> findTop5ByCategoryOrderByCreatedAtDesc(Category category);

    /**
     * Find all bucket lists for a specific user with pagination
     */
    Page<BucketList> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Find all bucket lists for a specific user and category with pagination
     */
    Page<BucketList> findByUserIdAndCategoryId(Long userId, Long categoryId, Pageable pageable);
    
    /**
     * Check if a bucket list with the given id belongs to the specified user
     */
    boolean existsByIdAndUserId(Long id, Long userId);
} 