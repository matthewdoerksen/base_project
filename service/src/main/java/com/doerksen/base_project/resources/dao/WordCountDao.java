package com.doerksen.base_project.resources.dao;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.sql.SQLException;

public interface WordCountDao {
    WordCount getWordCount(String url) throws SQLException, IOException;

    boolean insertWordCount(WordCount wordCount) throws SQLException, JsonProcessingException;

    boolean updateWordCount(WordCount wordCount) throws SQLException, JsonProcessingException;
}
