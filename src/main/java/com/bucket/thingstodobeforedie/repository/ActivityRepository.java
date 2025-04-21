package com.bucket.thingstodobeforedie.repository;

import com.bucket.thingstodobeforedie.entity.Activity;
import com.bucket.thingstodobeforedie.entity.ActivityType;
import com.bucket.thingstodobeforedie.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    
    /**
     * Find activities by user with pagination
     */
    Page<Activity> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    /**
     * Find activities by user and activity type with pagination
     */
    Page<Activity> findByUserAndActivityTypeOrderByCreatedAtDesc(User user, ActivityType activityType, Pageable pageable);
    
    /**
     * Find recent activities by user with a limit
     */
    List<Activity> findTop10ByUserOrderByCreatedAtDesc(User user);

} 