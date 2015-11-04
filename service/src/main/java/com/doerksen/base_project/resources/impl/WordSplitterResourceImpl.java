package com.doerksen.base_project.resources.impl;

import com.doerksen.base_project.resources.WordSplitterResource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.Arrays;

public class WordSplitterResourceImpl implements WordSplitterResource {

    private final Logger log = LoggerFactory.getLogger(WordSplitterResourceImpl.class);

    public WordSplitterResourceImpl() {

        /*
            TODO: replace the splitter with custom logic to be able to determine
            what constitutes a word. For now, just split by spaces and ignore any
            non-alphabetic characters such as periods or commas.
         */
    }

    @Override
    public Response splitTextIntoWords(String text) {
        if (StringUtils.isBlank(text)) {
            log.warn("Invalid text specified {}", text);
            return Response.status(Status.BAD_REQUEST).build();
        } else {
            return Response.status(Status.OK).entity(new ArrayList<>(Arrays.asList(text.split(" ")))).build();
        }
    }
}
