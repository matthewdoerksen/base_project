package com.doerksen.base_project.resources.impl;

import com.doerksen.base_project.resources.WordDictionaryResource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class WordDictionaryResourceImpl implements WordDictionaryResource {

    private final Logger log = LoggerFactory.getLogger(WordDictionaryResourceImpl.class);

    public WordDictionaryResourceImpl() {
        /*
            TODO: actually populate and read a dictionary of valid words/abbreviations/acronyms

            This could even just be a cache that's loaded up on each machine that's needed if we wanted
            to improve response times (at the cost of a bit of memory), instead of the "distributed" system
            using responses I have going on here.
         */
    }

    @Override
    public Response wordInDictionary(String word) {
        // for now I'll assume as long as it's not null or empty it is a valid word
        // NOTE: we'd also want to ignore case since it should not change word validity
        if (StringUtils.isNotBlank(word)) {
            return Response.status(Status.OK).build();
        } else {
            log.warn("Invalid word specified {}", word);
            return Response.status(Status.BAD_REQUEST).build();
        }
    }
}
