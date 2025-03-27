package com.bucket.thingstodobeforedie.repository;

import com.bucket.thingstodobeforedie.entity.BlogPost;
import com.bucket.thingstodobeforedie.entity.BlogStatus;
import com.bucket.thingstodobeforedie.entity.Category;
import com.bucket.thingstodobeforedie.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    
    Page<BlogPost> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    Page<BlogPost> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    Page<BlogPost> findByCategoryOrderByCreatedAtDesc(Category category, Pageable pageable);
    
    @Query("SELECT b FROM BlogPost b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<BlogPost> searchBlogPosts(String searchTerm, Pageable pageable);
    
    List<BlogPost> findTop5ByOrderByCreatedAtDesc();
    
    // New methods for trending blogs
    
    Page<BlogPost> findByStatusOrderByViewsDesc(BlogStatus status, Pageable pageable);
    
    @Modifying
    @Query("UPDATE BlogPost b SET b.views = b.views + 1 WHERE b.id = :blogId")
    void incrementViews(Long blogId);
    
    List<BlogPost> findTop5ByStatusOrderByViewsDesc(BlogStatus status);
    
    @Query("SELECT b FROM BlogPost b WHERE b.status = :status ORDER BY b.views DESC, b.createdAt DESC")
    Page<BlogPost> findTrendingBlogs(BlogStatus status, Pageable pageable);

    @Query("SELECT c.name, COUNT(b) FROM BlogPost b JOIN b.category c GROUP BY c.name")
    List<Object[]> getCategoryWiseBlogCount();

    /**
     * Count blog posts by user ID
     */
    long countByUserId(Long userId);
} 