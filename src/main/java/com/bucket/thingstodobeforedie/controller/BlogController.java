package com.bucket.thingstodobeforedie.controller;

import com.bucket.thingstodobeforedie.dto.*;
import com.bucket.thingstodobeforedie.entity.BlogStatus;
import com.bucket.thingstodobeforedie.service.BlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blog-posts")
@RequiredArgsConstructor
@Tag(name = "Blog Posts", description = "Blog post management API")
public class BlogController {

    private final BlogService blogService;

    @Operation(summary = "Create a new blog post", description = "Creates a new blog post with the provided data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Blog post created successfully", 
            content = @Content(schema = @Schema(implementation = BlogPostResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiResponseMapStringString"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<BlogPostResponse> createBlogPost(
            @Valid @RequestBody BlogPostRequest request) {
        return new ResponseEntity<>(blogService.createBlogPost(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Get a blog post by ID", description = "Returns a blog post based on the provided ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Blog post found", 
            content = @Content(schema = @Schema(implementation = BlogPostResponse.class))),
        @ApiResponse(responseCode = "404", description = "Blog post not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden to view draft post")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BlogPostResponse> getBlogPostById(
            @Parameter(description = "ID of the blog post to retrieve") @PathVariable Long id,
            HttpServletRequest request) {
        // Generate a unique viewer identifier using session ID and IP address
        String viewerIdentifier = generateViewerIdentifier(request);
        // Use the viewBlogPost method which handles view counting
        return ResponseEntity.ok(blogService.viewBlogPost(id, viewerIdentifier));
    }

    /**
     * Generate a unique identifier for the current viewer to track views
     * Combines user ID (if logged in), session ID, and IP address
     */
    private String generateViewerIdentifier(HttpServletRequest request) {
        StringBuilder identifier = new StringBuilder();
        
        // Add authenticated user ID if available
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            identifier.append("user:").append(authentication.getName()).append(":");
        }
        
        // Add session ID
        HttpSession session = request.getSession(false);
        if (session != null) {
            identifier.append("session:").append(session.getId()).append(":");
        }
        
        // Add client IP address
        String ipAddress = request.getRemoteAddr();
        identifier.append("ip:").append(ipAddress);
        
        return identifier.toString();
    }

    @Operation(summary = "Get all blog posts", description = "Returns a paginated list of all blog posts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<BlogPostResponse>> getAllBlogPosts(
            @Parameter(description = "ID of the user") @PathVariable Long userId,
            @Parameter(description = "Pagination parameters") Pageable pageable,
            @Parameter(description = "Filter by blog post status (published, draft, or all)")
            @RequestParam(required = false, defaultValue = "all") String status) {
        return ResponseEntity.ok(blogService.getAllBlogPosts(userId,pageable,status));
    }

    @Operation(summary = "Get blog posts by category", description = "Returns blog posts belonging to a specific category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<BlogPostResponse>> getBlogPostsByCategory(
            @Parameter(description = "ID of the category") @PathVariable Long categoryId,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        return ResponseEntity.ok(blogService.getBlogPostsByCategory(categoryId, pageable));
    }

    @Operation(summary = "Search blog posts", description = "Returns blog posts matching the search query")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<BlogPostResponse>> searchBlogPosts(
            @Parameter(description = "Search query string") @RequestParam String query,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        return ResponseEntity.ok(blogService.searchBlogPosts(query, pageable));
    }
    
    @Operation(summary = "Get trending blog posts", description = "Returns blog posts sorted by popularity (view count)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation")
    })
    @GetMapping("/trending")
    public ResponseEntity<Page<BlogPostResponse>> getTrendingBlogs(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        return ResponseEntity.ok(blogService.getTrendingBlogs(pageable));
    }
    
    @Operation(summary = "Get top trending blog posts", description = "Returns the top 5 most viewed blog posts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation")
    })
    @GetMapping("/trending/top")
    public ResponseEntity<List<BlogPostResponse>> getTopTrendingBlogs() {
        return ResponseEntity.ok(blogService.getTopTrendingBlogs());
    }

    @Operation(summary = "Update a blog post", description = "Updates an existing blog post with the provided data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Blog post updated successfully", 
            content = @Content(schema = @Schema(implementation = BlogPostResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Blog post not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - not the author of the post")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BlogPostResponse> updateBlogPost(
            @Parameter(description = "ID of the blog post to update") @PathVariable Long id,
            @Valid @RequestBody BlogPostRequest request) {
        return ResponseEntity.ok(blogService.updateBlogPost(id, request));
    }

    @Operation(summary = "Delete a blog post", description = "Deletes an existing blog post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Blog post deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Blog post not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - not the author of the post")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlogPost(
            @Parameter(description = "ID of the blog post to delete") @PathVariable Long id) {
        blogService.deleteBlogPost(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update blog post status", description = "Changes the status of a blog post (draft/published)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Blog post status updated successfully", 
            content = @Content(schema = @Schema(implementation = BlogPostResponse.class))),
        @ApiResponse(responseCode = "404", description = "Blog post not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - not the author of the post")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<BlogPostResponse> updateBlogStatus(
            @Parameter(description = "ID of the blog post") @PathVariable Long id,
            @Parameter(description = "New status (DRAFT or PUBLISHED)") @RequestParam BlogStatus status) {
        return ResponseEntity.ok(blogService.toggleBlogStatus(id, status));
    }

    @Operation(summary = "Toggle like on a blog post", description = "Likes or unlikes a blog post for the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Like toggled successfully", 
            content = @Content(schema = @Schema(implementation = BlogPostResponse.class))),
        @ApiResponse(responseCode = "404", description = "Blog post not found")
    })
    @PostMapping("/{id}/like")
    public ResponseEntity<BlogPostResponse> toggleLike(
            @Parameter(description = "ID of the blog post") @PathVariable Long id) {
        return ResponseEntity.ok(blogService.toggleLike(id));
    }
    
    // Comment-related endpoints
    
    @Operation(summary = "Add a comment to a blog post", description = "Creates a new comment on a blog post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Comment created successfully", 
            content = @Content(schema = @Schema(implementation = CommentResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Blog post not found"),
        @ApiResponse(responseCode = "403", description = "Cannot comment on unpublished blog post")
    })
    @PostMapping("/{blogId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @Parameter(description = "ID of the blog post") @PathVariable Long blogId,
            @Valid @RequestBody CommentRequest request) {
        return new ResponseEntity<>(blogService.addComment(blogId, request), HttpStatus.CREATED);
    }
    
    @Operation(summary = "Get comments for a blog post", description = "Returns all comments for a specific blog post")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation"),
        @ApiResponse(responseCode = "404", description = "Blog post not found")
    })
    @GetMapping("/{blogId}/comments")
    public ResponseEntity<Page<CommentResponse>> getBlogComments(
            @Parameter(description = "ID of the blog post") @PathVariable Long blogId,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        return ResponseEntity.ok(blogService.getCommentsByBlogPost(blogId, pageable));
    }
    
    @Operation(summary = "Update a comment", description = "Updates an existing comment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comment updated successfully", 
            content = @Content(schema = @Schema(implementation = CommentResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Comment not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - not the author of the comment")
    })
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @Parameter(description = "ID of the comment to update") @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.ok(blogService.updateComment(commentId, request));
    }
    
    @Operation(summary = "Delete a comment", description = "Deletes an existing comment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Comment not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden - not the author of the comment")
    })
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "ID of the comment to delete") @PathVariable Long commentId) {
        blogService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Get comments by user", description = "Returns all comments created by a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<Page<CommentResponse>> getUserComments(
            @Parameter(description = "ID of the user") @PathVariable Long userId,
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        return ResponseEntity.ok(blogService.getCommentsByUser(userId, pageable));
    }

    @GetMapping("/categories/count")
    @Operation(summary = "Get category-wise blog post counts")
    public List<CategoryCount> getCategoryWiseBlogCount() {
        return blogService.getCategoryWiseBlogCount();
    }
} 