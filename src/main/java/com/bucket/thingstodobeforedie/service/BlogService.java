package com.bucket.thingstodobeforedie.service;

import com.bucket.thingstodobeforedie.dto.*;
import com.bucket.thingstodobeforedie.entity.*;
import com.bucket.thingstodobeforedie.exception.ResourceNotFoundException;
import com.bucket.thingstodobeforedie.exception.UnauthorizedException;
import com.bucket.thingstodobeforedie.repository.*;
import com.bucket.thingstodobeforedie.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final CurrentUser currentUser;
    private final ViewTrackingService viewTrackingService;
    
    @Transactional
    public BlogPostDTO createBlogPost(BlogPostRequest request) {

        Category category = null;
        if (request.categoryId() != null) {
            category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }

        BlogPost blogPost = BlogPost.builder()
                .title(request.title())
                .content(request.content())
                .featuredImage(request.featuredImage())
                .status(request.status())
                .user(currentUser.getUser())
                .category(category)
                .views(0L)
                .build();

        blogPost = blogPostRepository.save(blogPost);
        return mapToBlogPostDTO(blogPost);
    }

    /**
     * Get blog post by ID without incrementing view count
     */
    @Transactional
    public BlogPostDTO getBlogPostById(Long id) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found"));
        
        return mapToBlogPostDTO(blogPost);
    }
    
    /**
     * Get blog post by ID and increment view count if conditions are met
     */
    @Transactional
    public BlogPostDTO viewBlogPost(Long id, String viewerIdentifier) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found"));
        
        // Only allow viewing published posts unless the user is the author
        if (blogPost.getStatus() == BlogStatus.DRAFT && 
            (!blogPost.getUser().getId().equals(currentUser.getUser().getId()))) {
            throw new UnauthorizedException("You don't have permission to view this draft blog post");
        }
        
        // Only increment views if post is published and viewer hasn't viewed recently
        if (blogPost.getStatus() == BlogStatus.PUBLISHED) {
            // Check if the viewer has viewed this post recently
            boolean hasRecentView = viewTrackingService.hasRecentView(id, viewerIdentifier);
            
            // If no recent view, increment and record
            if (!hasRecentView) {
                incrementBlogViews(id);
                viewTrackingService.recordView(id, viewerIdentifier);
            }
        }
        
        return mapToBlogPostDTO(blogPost);
    }
    
    /**
     * Helper method to get a blog post that's either published or owned by the current user
     */
    private BlogPost getPublishedOrOwnedBlogPost(Long id) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found"));
        
        // Only allow viewing published posts unless the user is the author
        if (blogPost.getStatus() == BlogStatus.DRAFT && 
            (!blogPost.getUser().getId().equals(currentUser.getUser().getId()))) {
            throw new UnauthorizedException("You don't have permission to view this draft blog post");
        }
        
        return blogPost;
    }
    
    public Page<BlogPostDTO> getAllBlogPosts(Pageable pageable) {
        return blogPostRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::mapToBlogPostDTO);
    }

    public Page<BlogPostDTO> getBlogPostsByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return blogPostRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(this::mapToBlogPostDTO);
    }

    public Page<BlogPostDTO> getBlogPostsByCategory(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return blogPostRepository.findByCategoryOrderByCreatedAtDesc(category, pageable)
                .map(this::mapToBlogPostDTO);
    }

    public Page<BlogPostDTO> searchBlogPosts(String searchTerm, Pageable pageable) {
        return blogPostRepository.searchBlogPosts(searchTerm, pageable)
                .map(this::mapToBlogPostDTO);
    }

    @Transactional
    public BlogPostDTO updateBlogPost(Long id, BlogPostRequest request) {
        // Get the blog post
        BlogPost blogPost = getPublishedOrOwnedBlogPost(id);
        
        // Verify the current user is the author
        if (!blogPost.getUser().getId().equals(currentUser.getUser().getId())) {
            throw new UnauthorizedException("You don't have permission to update this blog post");
        }
        
        // Update the blog post
        blogPost.setTitle(request.title());
        blogPost.setContent(request.content());
        blogPost.setFeaturedImage(request.featuredImage());
        blogPost.setStatus(request.status());
        
        // Update category if provided
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            blogPost.setCategory(category);
        }
        
        return mapToBlogPostDTO(blogPostRepository.save(blogPost));
    }
    
    @Transactional
    public void deleteBlogPost(Long id) {
        // Get the blog post
        BlogPost blogPost = getPublishedOrOwnedBlogPost(id);
        
        // Verify the current user is the author
        if (!blogPost.getUser().getId().equals(currentUser.getUser().getId())) {
            throw new UnauthorizedException("You don't have permission to delete this blog post");
        }
        
        // Delete the blog post
        blogPostRepository.delete(blogPost);
    }
    
    @Transactional
    public BlogPostDTO toggleBlogStatus(Long id, BlogStatus status) {
        // Get the blog post
        BlogPost blogPost = getPublishedOrOwnedBlogPost(id);
        
        // Verify the current user is the author
        if (!blogPost.getUser().getId().equals(currentUser.getUser().getId())) {
            throw new UnauthorizedException("You don't have permission to update this blog post");
        }
        
        // Update the status
        blogPost.setStatus(status);
        
        return mapToBlogPostDTO(blogPostRepository.save(blogPost));
    }
    
    @Transactional
    public BlogPostDTO toggleLike(Long id) {
        // Get the blog post
        BlogPost blogPost = getPublishedOrOwnedBlogPost(id);
        
        // Get the current user
        User user = currentUser.getUser();
        
        // Check if the user has already liked this post
        Optional<Like> existingLike = likeRepository.findByUserAndBlogPost(user, blogPost);
        
        if (existingLike.isPresent()) {
            // Remove the like
            likeRepository.delete(existingLike.get());
        } else {
            // Add a new like
            Like like = new Like();
            like.setUser(user);
            like.setBlogPost(blogPost);
            likeRepository.save(like);
        }
        
        return mapToBlogPostDTO(blogPost);
    }
    
    @Transactional
    public CommentDTO addComment(Long blogId, CommentRequest request) {
        // Get the blog post
        BlogPost blogPost = getPublishedOrOwnedBlogPost(blogId);
        
        // Only allow comments on published posts
        if (blogPost.getStatus() != BlogStatus.PUBLISHED) {
            throw new UnauthorizedException("Cannot comment on unpublished blog posts");
        }
        
        // Create the comment
        Comment comment = new Comment();
        comment.setContent(request.content());
        comment.setBlogPost(blogPost);
        comment.setUser(currentUser.getUser());
        comment.setCreatedAt(LocalDateTime.now());
        
        return mapToCommentDTO(commentRepository.save(comment));
    }
    
    @Transactional
    public CommentDTO updateComment(Long commentId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
                
        if (!comment.getUser().getId().equals(currentUser.getUser().getId())) {
            throw new UnauthorizedException("You don't have permission to update this comment");
        }
        
        comment.setContent(request.content());
        comment = commentRepository.save(comment);
        
        return mapToCommentDTO(comment);
    }
    
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
                
        if (!comment.getUser().getId().equals(currentUser.getUser().getId())) {
            throw new UnauthorizedException("You don't have permission to delete this comment");
        }
        
        commentRepository.delete(comment);
    }
    
    public Page<CommentDTO> getCommentsByBlogPost(Long blogId, Pageable pageable) {
        // Get the blog post to verify it exists and user has access
        BlogPost blogPost = getPublishedOrOwnedBlogPost(blogId);
        
        // Get comments for this blog post
        Page<Comment> comments = commentRepository.findByBlogPostOrderByCreatedAtDesc(blogPost, pageable);
        
        return comments.map(this::mapToCommentDTO);
    }
    
    public Page<CommentDTO> getCommentsByUser(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return commentRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(this::mapToCommentDTO);
    }

    @Transactional
    public void incrementBlogViews(Long blogId) {
        blogPostRepository.incrementViews(blogId);
    }
    
    public Page<BlogPostDTO> getTrendingBlogs(Pageable pageable) {
        return blogPostRepository.findTrendingBlogs(BlogStatus.PUBLISHED, pageable)
                .map(this::mapToBlogPostDTO);
    }
    
    public List<BlogPostDTO> getTopTrendingBlogs() {
        return blogPostRepository.findTop5ByStatusOrderByViewsDesc(BlogStatus.PUBLISHED)
                .stream()
                .map(this::mapToBlogPostDTO)
                .toList();
    }

    public List<CategoryCountDTO> getCategoryWiseBlogCount() {
        return blogPostRepository.getCategoryWiseBlogCount().stream()
                .map(obj -> new CategoryCountDTO((String) obj[0], (Long) obj[1]))
                .collect(Collectors.toList());
    }
    private BlogPostDTO mapToBlogPostDTO(BlogPost blogPost) {
        long likesCount = likeRepository.countByBlogPost(blogPost);
        long commentsCount = commentRepository.countByBlogPost(blogPost);
        boolean isLikedByCurrentUser = false;
        
        if (currentUser != null) {
                isLikedByCurrentUser = likeRepository.existsByUserAndBlogPost(currentUser.getUser(), blogPost);
        }
        
        return BlogPostDTO.builder()
                .id(blogPost.getId())
                .title(blogPost.getTitle())
                .content(blogPost.getContent())
                .featuredImage(blogPost.getFeaturedImage())
                .status(blogPost.getStatus())
                .userId(blogPost.getUser().getId())
                .author(blogPost.getUser().getFullName())
                .categoryId(blogPost.getCategory() != null ? blogPost.getCategory().getId() : null)
                .categoryName(blogPost.getCategory() != null ? blogPost.getCategory().getName() : null)
                .likesCount(likesCount)
                .commentsCount((int) commentsCount)
                .views(blogPost.getViews())
                .createdAt(blogPost.getCreatedAt())
                .updatedAt(blogPost.getUpdatedAt())
                .isLikedByCurrentUser(isLikedByCurrentUser)
                .build();
    }
    
    private CommentDTO mapToCommentDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getFullName())
                .blogPostId(comment.getBlogPost().getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
} 