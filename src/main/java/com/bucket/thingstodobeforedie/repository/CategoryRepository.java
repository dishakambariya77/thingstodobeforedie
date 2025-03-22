package com.bucket.thingstodobeforedie.repository;

import com.bucket.thingstodobeforedie.entity.Category;
import com.bucket.thingstodobeforedie.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findByName(String name);
    
    List<Category> findByType(CategoryType type);
    
    List<Category> findByTypeOrderByNameAsc(CategoryType type);
    
    boolean existsByName(String name);
} 