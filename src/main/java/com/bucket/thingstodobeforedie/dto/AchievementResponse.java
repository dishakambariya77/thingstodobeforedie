package com.bucket.thingstodobeforedie.dto;

import lombok.Builder;


@Builder
public record AchievementResponse(String title, String description, String icon, String status, Boolean completed,
                                  Integer progress) {


}
