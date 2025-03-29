package com.bucket.thingstodobeforedie.entity;

import lombok.Getter;

@Getter
public enum ActivityIcon {
    BLOG_POST_CREATED("add_circle"),
    BLOG_POST_PUBLISHED("publish"),
    BLOG_POST_UPDATED("edit_note"),
    BLOG_POST_DELETED("delete_forever"),

    COMMENT_ADDED("comment"),
    COMMENT_UPDATED("update"),
    COMMENT_REMOVED("delete"),

    BUCKET_LIST_CREATED("add_circle"),
    BUCKET_LIST_UPDATED("update"),
    BUCKET_LIST_DELETED("delete"),
    BUCKET_LIST_COMPLETED("check_circle"),

    BUCKET_ITEM_COMPLETED("check_circle"),
    BUCKET_ITEM_UPDATED("update"),

    PROFILE_IMAGE_UPDATED("person"),

    UNLIKED_BLOG_POST("favorite_border"),
    LIKED_BLOG_POST("favorite"),;

    private final String icon;

    ActivityIcon(String icon) {
        this.icon = icon;
    }
}
