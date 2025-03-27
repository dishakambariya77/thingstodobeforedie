package com.bucket.thingstodobeforedie.repository;

import com.bucket.thingstodobeforedie.entity.BlogPost;
import com.bucket.thingstodobeforedie.entity.Comment;
import com.bucket.thingstodobeforedie.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    Page<Comment> findByBlogPostOrderByCreatedAtDesc(BlogPost blogPost, Pageable pageable);
    
    Page<Comment> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    long countByBlogPost(BlogPost blogPost);
} 