package com.bucket.thingstodobeforedie.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bucket_list_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BucketListItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false)
    private boolean completed;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column
    private String priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bucket_list_id", nullable = false)
    private BucketList bucketList;

    @Column
    private LocalDateTime completedAt;
    
}