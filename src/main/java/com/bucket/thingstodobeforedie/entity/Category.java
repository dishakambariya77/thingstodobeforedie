package com.bucket.thingstodobeforedie.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    
    private String iconUrl;
    
    @Enumerated(EnumType.STRING)
    private CategoryType type;
    
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<BlogPost> blogPosts;
    
    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<BucketList> bucketLists;

} 