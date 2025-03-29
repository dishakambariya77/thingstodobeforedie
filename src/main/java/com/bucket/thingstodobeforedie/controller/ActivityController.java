package com.bucket.thingstodobeforedie.controller;

import com.bucket.thingstodobeforedie.dto.ActivityResponse;
import com.bucket.thingstodobeforedie.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
@Tag(name = "User Activities", description = "User activity management API")
public class ActivityController {

    private final ActivityService activityService;

    @Operation(summary = "Get current user activities", description = "Retrieves activities for the currently authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved activities", 
            content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/me")
    public ResponseEntity<Page<ActivityResponse>> getCurrentUserActivities(
            @PageableDefault(size = 5) Pageable pageable) {
        return ResponseEntity.ok(activityService.getCurrentUserActivities(pageable));
    }

    @Operation(summary = "Get activities for a specific user", description = "Retrieves activities for a specific user by user ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved activities", 
            content = @Content(schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Forbidden access")
    })
    @GetMapping("/users/{userId}")
    public ResponseEntity<Page<ActivityResponse>> getUserActivities(
            @PathVariable Long userId,
            @PageableDefault(size = 5) Pageable pageable) {
        return ResponseEntity.ok(activityService.getUserActivities(userId, pageable));
    }
} 