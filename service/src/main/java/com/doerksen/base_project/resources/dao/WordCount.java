package com.doerksen.base_project.resources.dao;

import com.doerksen.base_project.dto.WordCountDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class WordCount {
    private Long id;
    private String url;
    private Map<String, Integer> counts;

    @JsonCreator
    public WordCount() {}

    public WordCount(String url,
                     Map<String, Integer> counts) {
        id = null;
        this.url = url;
        this.counts = counts;
    }

    public WordCountDto toDto() {
        return new WordCountDto(id, url, counts);
    }
}
