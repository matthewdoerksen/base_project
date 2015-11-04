package com.doerksen.base_project.resources.impl;

import com.doerksen.base_project.resources.UrlValidator;
import com.doerksen.base_project.resources.WebDocumentRetrievalResource;
import org.jsoup.Jsoup;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.time.Duration;

public class WebDocumentRetrievalResourceImpl implements WebDocumentRetrievalResource {

    // int is required for the jsoup connection timeout field
    private final int CONN_TIMEOUT = (int) Duration.ofSeconds(10).toMillis();
    private final String HTTP_PREPEND = "http://";

    private final UrlValidator urlValidator;

    public WebDocumentRetrievalResourceImpl(UrlValidator urlValidator) {
        this.urlValidator = urlValidator;
    }

    @Override
    public Response retrieveWebDocument(String url) {

        if (!urlValidator.isValidUrl(url)) {
            // we could return a list of validation errors that we received from the UrlValidator
            return Response.status(Status.BAD_REQUEST).build();
        }

        try {
            return Response.status(Status.OK).entity(Jsoup.connect(prependHttp(url)).timeout(CONN_TIMEOUT).get()).build();
        } catch (IOException e) {
            return Response.status(Status.SERVICE_UNAVAILABLE).build();
        }
    }

    /*
        I don't particularly like having to prepend http:// to the URL but it fails if it's
        not there, and it fails if I attempt to pass it in via the PathParam. A better long
        term solution would be to POST the URL so that it could be left unmodified.
     */
    private String prependHttp(final String url) {
        if (!url.startsWith(HTTP_PREPEND)) {
            return HTTP_PREPEND + url;
        }
        return url;
    }
}
