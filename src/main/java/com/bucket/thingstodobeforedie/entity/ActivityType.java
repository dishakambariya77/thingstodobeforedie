package com.bucket.thingstodobeforedie.entity;

/**
 * Types of user activities to track
 */
public enum ActivityType {
    // Bucket list activities
    BUCKET_LIST_CREATED,
    BUCKET_LIST_UPDATED,
    BUCKET_LIST_COMPLETED,
    BUCKET_LIST_DELETED,
    
    // Bucket list item activities
    BUCKET_ITEM_COMPLETED,
    BUCKET_ITEM_UPDATED,
    
    // Blog post activities
    BLOG_POST_CREATED,
    BLOG_POST_UPDATED,
    BLOG_POST_PUBLISHED,
    BLOG_POST_DELETED,
    
    // Comment activities
    COMMENT_ADDED,
    COMMENT_UPDATED,
    COMMENT_DELETED,
    
    // User profile activities
    PROFILE_IMAGE_UPDATED,
    
    // Social activities
    LIKED_BLOG_POST,
    UNLIKED_BLOG_POST
} 