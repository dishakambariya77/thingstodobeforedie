package com.bucket.thingstodobeforedie.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity {
    
    @Column(nullable = false, unique = true)
    private String name;
    
    private String description;
    
    private String icon;
    
    @Enumerated(EnumType.STRING)
    private CategoryType type;
    
    @OneToMany(mappedBy = "category")
    private List<BlogPost> blogPosts;
    
    @OneToMany(mappedBy = "category")
    private List<BucketList> bucketLists;

} 