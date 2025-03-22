package com.bucket.thingstodobeforedie.util;

import com.bucket.thingstodobeforedie.entity.Category;
import com.bucket.thingstodobeforedie.entity.CategoryType;
import com.bucket.thingstodobeforedie.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class to initialize default bucket list categories
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
@Profile("!test") // Skip in test profile
public class DefaultCategoriesInitializer {

    private final CategoryRepository categoryRepository;

    @PostConstruct
    public void initDefaultCategories() {
        if (categoryRepository.count() > 0) {
            log.info("Categories already exist, skipping initialization");
            return;
        }

        log.info("Initializing default bucket list categories");
        
        List<Category> defaultCategories = Arrays.asList(
            createCategory("Travel", "Places to visit", "🌎", CategoryType.BUCKET_LIST),
            createCategory("Adventure", "Exciting activities", "🧗", CategoryType.BUCKET_LIST),
            createCategory("Career", "Professional goals", "💼", CategoryType.BUCKET_LIST),
            createCategory("Education", "Learning goals", "🎓", CategoryType.BUCKET_LIST),
            createCategory("Personal", "Self-improvement goals", "🧘", CategoryType.BUCKET_LIST),
            createCategory("Health", "Health and fitness goals", "💪", CategoryType.BUCKET_LIST),
            createCategory("Relationships", "Family and friend goals", "👨‍👩‍👧‍👦", CategoryType.BUCKET_LIST),
            createCategory("Entertainment", "Movies, books, etc. to experience", "🎬", CategoryType.BUCKET_LIST),
            createCategory("Financial", "Money-related goals", "💰", CategoryType.BUCKET_LIST),
            createCategory("Food & Drink", "Culinary experiences", "🍽️", CategoryType.BUCKET_LIST),
            createCategory("Skills", "New skills to master", "🎯", CategoryType.BUCKET_LIST),
            createCategory("Creative", "Creative projects", "🎨", CategoryType.BUCKET_LIST),
            createCategory("Spiritual", "Spiritual experiences", "🙏", CategoryType.BUCKET_LIST),
            createCategory("Philanthropy", "Ways to give back", "❤️", CategoryType.BUCKET_LIST),
            createCategory("Lifestyle", "Lifestyle changes", "🏡", CategoryType.BUCKET_LIST)
        );
        
        categoryRepository.saveAll(defaultCategories);
        log.info("Initialized {} default categories", defaultCategories.size());
    }
    
    private Category createCategory(String name, String description, String icon, CategoryType type) {
        return Category.builder()
                .name(name)
                .description(description)
                .icon(icon)
                .type(type)
                .build();
    }
} 