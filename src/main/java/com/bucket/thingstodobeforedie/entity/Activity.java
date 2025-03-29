package com.bucket.thingstodobeforedie.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_activities")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Activity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType activityType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityIcon activityIcon;
    
    @Column(nullable = false)
    private String text;
    
    // Additional metadata as JSON
    @Column(columnDefinition = "TEXT")
    private String metadata;
} 