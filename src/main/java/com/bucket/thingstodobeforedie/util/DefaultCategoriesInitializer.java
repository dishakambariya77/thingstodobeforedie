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
                createCategory("Travel", "Places to visit", "public", CategoryType.BUCKET_LIST),
                createCategory("Adventure", "Exciting activities", "terrain", CategoryType.BUCKET_LIST),
                createCategory("Career", "Professional goals", "work", CategoryType.BUCKET_LIST),
                createCategory("Education", "Learning goals", "school", CategoryType.BUCKET_LIST),
                createCategory("Personal", "Self-improvement goals", "self_improvement", CategoryType.BUCKET_LIST),
                createCategory("Health", "Health and fitness goals", "fitness_center", CategoryType.BUCKET_LIST),
                createCategory("Relationships", "Family and friend goals", "group", CategoryType.BUCKET_LIST),
                createCategory("Entertainment", "Movies, books, etc. to experience", "movie", CategoryType.BUCKET_LIST),
                createCategory("Financial", "Money-related goals", "account_balance", CategoryType.BUCKET_LIST),
                createCategory("Food & Drink", "Culinary experiences", "restaurant", CategoryType.BUCKET_LIST),
                createCategory("Skills", "New skills to master", "sports_kabaddi", CategoryType.BUCKET_LIST),
                createCategory("Creative", "Creative projects", "brush", CategoryType.BUCKET_LIST),
                createCategory("Spiritual", "Spiritual experiences", "self_improvement", CategoryType.BUCKET_LIST),
                createCategory("Philanthropy", "Ways to give back", "volunteer_activism", CategoryType.BUCKET_LIST),
                createCategory("Lifestyle", "Lifestyle changes", "home", CategoryType.BUCKET_LIST)
        );


        categoryRepository.saveAll(defaultCategories);
        log.info("Initialized {} default categories", defaultCategories.size());
    }
    
    private Category createCategory(String name, String description, String icon, CategoryType type) {
        return Category.builder()
                .name(name)
                .description(description)
                .iconUrl(icon)
                .type(type)
                .build();
    }
} 