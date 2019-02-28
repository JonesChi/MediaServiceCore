package com.liskovsoft.youtubeapi.content.models;

import com.liskovsoft.youtubeapi.support.JsonPath;

public class Thumbnail {
    @JsonPath("$.url")
    private String url;
    @JsonPath("$.width")
    private int width;
    @JsonPath("$.height")
    private int height;
}