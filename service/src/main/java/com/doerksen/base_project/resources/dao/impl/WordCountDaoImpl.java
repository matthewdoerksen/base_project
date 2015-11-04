package com.doerksen.base_project.resources.dao.impl;

import com.doerksen.base_project.resources.dao.ConnectionResource;
import com.doerksen.base_project.resources.dao.WordCount;
import com.doerksen.base_project.resources.dao.WordCountDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 *
 *
 *
 * NOTE: This file has not been used in code because I don't have a DB currently set up.
 *       The purpose is to show what the code to interact with the database would look like.
 *
 *       Later I'll be mocking this so I am able to return fake objects that can be used to test.
 *
 *
 *
 *
 */

public class WordCountDaoImpl implements WordCountDao {

    private final ConnectionResource connectionResource;

    private final int ROW_INSERT_SUCCESS = 1;
    private final int ROW_UPDATE_SUCCESS = 1;

    private long COUNT = 1;

    private final ObjectMapper mapper = new ObjectMapper();
    private final TypeFactory typeFactory = mapper.getTypeFactory();
    private final MapType mapType = typeFactory.constructMapType(HashMap.class, String.class, Integer.class);

    // this should really live in a file so that we're not modifying source code
    // we should also be making sure that we don't have duplicates by throwing an exception on insertion
    private final String SELECT_QUERY = "SELECT * FROM <schema.table> WHERE url = ? LIMIT 1";

    private final String INSERT_QUERY = "INSERT INTO <schema.table> (url, word_counts) VALUES (?, ?::jsonb)";

    private final String UPDATE_QUERY = "UPDATE <schema.table> SET word_counts = ? WHERE url = ?";

    private final List<WordCount> inmemoryDb = new ArrayList<>();

    public WordCountDaoImpl(ConnectionResource connectionResource) {
        this.connectionResource = connectionResource;
    }

    /*
        Note: I choose to only throw the IOException instead of also including the
        JsonParseException and JsonMappingException because they are sub-types of IOException
     */
    @Override
    public WordCount getWordCount(String url) throws SQLException, IOException {
        // try-with-resources to clean up after ourselves
        // this is a bit ugly to be manually working with SQL and connections,
        // but is fine for a quick first pass at what it would look like

        /*
        try (Statement stmt = connectionResource.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(SELECT_QUERY);
            return new WordCount(rs.getLong("id"),
                                 rs.getString("url"),
                                 mapper.readValue(rs.getString("word_counts"), mapType));
        }
        */

        return inmemoryDb.stream()
                         .filter(wc -> wc.getUrl().equals(url))
                         .findFirst()
                         .get();
    }

    @Override
    public boolean insertWordCount(WordCount wordCount) throws SQLException, JsonProcessingException {
        /*
        try (PreparedStatement stmt = connectionResource.getConnection().prepareStatement(INSERT_QUERY)) {
            stmt.setString(1, wordCount.getUrl());
            stmt.setString(2, mapper.writeValueAsString(wordCount.getCounts()));
            return ROW_INSERT_SUCCESS == stmt.executeUpdate();
        }
        */

        // the DB would throw an exception if we tried to insert when it already existed, here we'll just return false
        boolean inDb = inmemoryDb.stream()
                                 .anyMatch(wc -> wc.getUrl().equals(wordCount.getUrl()));

        if (inDb) {
            return false;
        } else {
            // hack to give us an ID since we don't have a DB
            wordCount.setId(COUNT);
            COUNT++;
            inmemoryDb.add(wordCount);
            return true;
        }
    }

    @Override
    public boolean updateWordCount(WordCount wordCount) throws SQLException, JsonProcessingException {
        /*
        try (PreparedStatement stmt = connectionResource.getConnection().prepareStatement(UPDATE_QUERY)) {
            stmt.setString(1, mapper.writeValueAsString(wordCount.getCounts()));
            stmt.setString(2, wordCount.getUrl());
            return ROW_UPDATE_SUCCESS == stmt.executeUpdate();
        }
        */

        // the DB would throw an exception if we tried to get when it doesn't exist, here we'll just return false
        WordCount existing = inmemoryDb.stream()
                .filter(wc -> wc.getUrl().equals(wordCount.getUrl()))
                                       .findFirst()
                                       .get();

        if (existing != null) {
            inmemoryDb.remove(existing);
            inmemoryDb.add(wordCount);
            return true;
        } else {
            return false;
        }
    }
}
