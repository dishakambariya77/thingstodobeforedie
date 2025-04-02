package com.bucket.thingstodobeforedie.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for tracking and preventing duplicate views on blog posts.
 * Uses an in-memory cache with expiration for simplicity, but could be replaced
 * with a distributed cache like Redis for production environments.
 */
@Service
public class ViewTrackingService {

    // Map to store view records: key = blog:viewerId, value = timestamp
    private final Map<String, LocalDateTime> viewRecords = new ConcurrentHashMap<>();
    
    // Default TTL for view records (24 hours)
    private static final long VIEW_TTL_HOURS = 24;
    
    /**
     * Check if a viewer has recently viewed a blog post
     * 
     * @param blogId the ID of the blog post
     * @param viewerIdentifier unique identifier for the viewer
     * @return true if the viewer has viewed this post within the TTL period, false otherwise
     */
    public boolean hasRecentView(Long blogId, String viewerIdentifier) {
        if (viewerIdentifier == null || viewerIdentifier.isEmpty()) {
            return false;
        }
        
        String viewKey = createViewKey(blogId, viewerIdentifier);
        LocalDateTime viewTime = viewRecords.get(viewKey);
        
        // Clean up expired records periodically (simplified approach)
        if (Math.random() < 0.01) { // 1% chance to clean up on each check
            cleanupExpiredRecords();
        }
        
        // If record exists and hasn't expired
        return viewTime != null && viewTime.plusHours(VIEW_TTL_HOURS).isAfter(LocalDateTime.now());
    }
    
    /**
     * Record a new view for a blog post
     * 
     * @param blogId the ID of the blog post
     * @param viewerIdentifier unique identifier for the viewer
     */
    public void recordView(Long blogId, String viewerIdentifier) {
        if (viewerIdentifier == null || viewerIdentifier.isEmpty()) {
            return;
        }
        
        String viewKey = createViewKey(blogId, viewerIdentifier);
        viewRecords.put(viewKey, LocalDateTime.now());
    }
    
    /**
     * Create a unique key for storing view records
     */
    private String createViewKey(Long blogId, String viewerIdentifier) {
        return "blog:" + blogId + ":" + viewerIdentifier;
    }
    
    /**
     * Remove expired view records from the cache
     */
    private void cleanupExpiredRecords() {
        LocalDateTime expirationThreshold = LocalDateTime.now().minusHours(VIEW_TTL_HOURS);
        viewRecords.entrySet().removeIf(entry -> entry.getValue().isBefore(expirationThreshold));
    }
} 