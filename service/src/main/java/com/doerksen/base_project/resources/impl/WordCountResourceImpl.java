package com.doerksen.base_project.resources.impl;

import com.doerksen.base_project.resources.*;
import com.doerksen.base_project.resources.dao.WordCount;
import com.doerksen.base_project.resources.dao.WordCountDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class WordCountResourceImpl implements WordCountResource {

    private final Logger log = LoggerFactory.getLogger(WordCountResourceImpl.class);

    private final WebDocumentRetrievalResource webDocumentRetrievalResource;
    private final WordSplitterResource wordSplitterResource;
    private final WordDictionaryResource wordDictionaryResource;
    private final WordCountDao wordCountDao;
    private final UrlValidator urlValidator;

    /**
     *  Note: I don't like having the DAO get passed in here. I'd rather have a resource sit
     *        in front of the DAO to handle it instead of using it directly, but I'm running
     *        a bit short on time and I still want to write tests.
     *
     *        Also, if we wanted to improve performance, we could easily cache the entries inserted
     *        or returned from the database to prevent running an exessive number of queries.
     *
     *        There is certainly some re-use of code between the insert and update methods. However,
     *
     */
    public WordCountResourceImpl(WebDocumentRetrievalResource webDocumentRetrievalResource,
                                 WordSplitterResource wordSplitterResource,
                                 WordDictionaryResource wordDictionaryResource,
                                 WordCountDao wordCountDao,
                                 UrlValidator urlValidator) {
        this.webDocumentRetrievalResource = webDocumentRetrievalResource;
        this.wordSplitterResource = wordSplitterResource;
        this.wordDictionaryResource = wordDictionaryResource;
        this.wordCountDao = wordCountDao;
        this.urlValidator = urlValidator;
    }

    @Override
    public Response getWordCount(String url) {
        // some pre-validation since we may as well check before accessing the database
        if (!urlValidator.isValidUrl(url)) {
            log.warn("Invalid URL specified {}", url);
            return Response.status(Status.BAD_REQUEST).build();
        }

        // check if the URL is in the DB, return
        try {
            WordCount wc = wordCountDao.getWordCount(url);
            // here the DB should return null, but the stream throws a NSEE,
            // so I check for it in both places
            if (wc != null) {
                return Response.status(Status.OK).entity(wc.toDto()).build();
            } else {
                log.warn("Unable to find URL {} in DB.", url);
                return Response.status(Status.NOT_FOUND).build();
            }
        } catch (SQLException e) {
            log.error("Unable to reach external service while getting word count for URL {}", url, e);
            return Response.status(Status.SERVICE_UNAVAILABLE).build();
        } catch (IOException e) {
            log.error("Error processing URL {}", url, e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } catch (NoSuchElementException e) {
            log.warn("Unable to find URL {} in DB.", url, e);
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @Override
    public Response insertWordCount(String url) {
        // some pre-validation since we may as well check before accessing the database
        if (!urlValidator.isValidUrl(url)) {
            log.warn("Invalid URL specified {}", url);
            return Response.status(Status.BAD_REQUEST).build();
        }

        // check if the URL is in the DB, if so, don't try to insert
        try {
            WordCount wc = wordCountDao.getWordCount(url);
            if (wc != null) {
                log.error("Unable to insert already existing URL {}, use update instead.", url);
                return Response.status(Status.CONFLICT).build();
            }
        } catch (SQLException e) {
            log.error("Unable to reach external service while getting word count for URL {}", url, e);
            return Response.status(Status.SERVICE_UNAVAILABLE).build();
        } catch (IOException e) {
            log.error("Error processing URL {}", url, e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } catch (NoSuchElementException e) {
            // this can be thrown because we first try to get the item from
            // the database (and isn't handled most gracefully because it's in-memory)
            // and doesn't return null like the DB would, to be caught above.
        }

        Response docResponse = getDocumentAndBreakIntoWords(url);
        if (!docResponse.getStatusInfo().equals(Status.OK)) {
            return docResponse;
        }
        List<String> words = (List<String>)docResponse.getEntity();

        Map<String, Integer> wordCount = buildWordCountMap(words);

        try {
            wordCountDao.insertWordCount(new WordCount(url, wordCount));
        } catch (SQLException e) {
            log.error("Unable to reach external service while inserting word count for URL {}", url, e);
            return Response.status(Status.SERVICE_UNAVAILABLE).build();
        } catch (JsonProcessingException e) {
            log.error("Error processing word count JSON {}", wordCount, e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(Status.OK).build();
    }

    @Override
    public Response updateWordCount(String url) {
        // some pre-validation since we may as well check before accessing the database
        if (!urlValidator.isValidUrl(url)) {
            log.warn("Invalid URL specified {}", url);
            return Response.status(Status.BAD_REQUEST).build();
        }

        // check if the URL is in the DB, if not, return an error since we can't update it
        WordCount wc;
        try {
            wc = wordCountDao.getWordCount(url);
            if (wc == null) {
                log.warn("Unable to find URL {} in DB.", url);
                return Response.status(Status.NOT_FOUND).build();
            }
        } catch (SQLException e) {
            log.error("Unable to reach external service while getting word count for URL {}", url, e);
            return Response.status(Status.SERVICE_UNAVAILABLE).build();
        } catch (IOException e) {
            log.error("Error processing URL {}", url, e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } catch (NoSuchElementException e) {
            log.warn("Unable to find URL {} in DB.", url, e);
            return Response.status(Status.NOT_FOUND).build();
        }

        Response docResponse = getDocumentAndBreakIntoWords(url);
        if (!docResponse.getStatusInfo().equals(Status.OK)) {
            return docResponse;
        }
        List<String> words = (List<String>)docResponse.getEntity();

        // map for our word list for the URL entered
        Map<String, Integer> wordCount = buildWordCountMap(words);

        // update the object we got back from the DB earlier
        wc.setCounts(wordCount);

        // now that we've got our counts, let's update the database
        try {
            wordCountDao.updateWordCount(wc);
        } catch (SQLException e) {
            log.error("Unable to reach external service while updating word count for URL {}", url, e);
            return Response.status(Status.SERVICE_UNAVAILABLE).build();
        } catch (JsonProcessingException e) {
            log.error("Error processing word count JSON {}", wordCount, e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(Status.OK).build();
    }

    private Response getDocumentAndBreakIntoWords(String url) {
        Response document = webDocumentRetrievalResource.retrieveWebDocument(url);
        if (!document.getStatusInfo().equals(Status.OK)) {
            return document;
        }

        // I don't particularly like the cast, it'd be better if we had a custom response to return
        // that would have the type attached already, such as Response<Document>
        Document doc = (Document) document.getEntity();

        // ignore the HTML bits
        Response wordList = wordSplitterResource.splitTextIntoWords(doc.text());
        if (!wordList.getStatusInfo().equals(Status.OK)) {
            return Response.status(Status.SERVICE_UNAVAILABLE).build();
        }
        return wordList;
    }

    private Map<String, Integer> buildWordCountMap(final List<String> words) {
        Map<String, Integer> wordCount = new HashMap<>();

        // this could be replaced with a stream, but I believe it would just convolute the code
        // because we need to check the response (see WordDictionaryResource for how I believe
        // performance could be improved, compared to sending a response for every word.
        for (String word : words) {

            Optional<Integer> count = Optional.ofNullable(wordCount.get(word));

            // if our word is already present in our local "cache", we know it's
            // already been validated so we just need to increment it
            if (count.isPresent()) {
                wordCount.put(word, count.get() + 1);
            } else {
                Response validWord = wordDictionaryResource.wordInDictionary(word);
                // if there weren't any network issues, and we got a true response, it's valid, add it
                if (Status.OK.equals(validWord.getStatusInfo())) {
                    wordCount.put(word, 1);
                } else {
                    log.warn("Unable to validate word, the word count for '{}' may be incorrect.", word);
                    // We could also add retries (here and any other calls we made earlier)
                    // if we wanted to be more robust.
                }
            }
        }

        return wordCount;
    }
}
