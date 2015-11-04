package com.doerksen.base_project.dto;

import lombok.Value;

import java.util.Map;

@Value
public class WordCountDto {
    private final Long id;
    private final String url;
    private final Map<String, Integer> counts;
}
