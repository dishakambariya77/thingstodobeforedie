package com.bucket.thingstodobeforedie.repository;

import com.bucket.thingstodobeforedie.entity.BlogPost;
import com.bucket.thingstodobeforedie.entity.Like;
import com.bucket.thingstodobeforedie.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    Optional<Like> findByUserAndBlogPost(User user, BlogPost blogPost);
    
    boolean existsByUserAndBlogPost(User user, BlogPost blogPost);
    
    long countByBlogPost(BlogPost blogPost);
    
    void deleteByUserAndBlogPost(User user, BlogPost blogPost);
} 