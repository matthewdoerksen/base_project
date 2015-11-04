package com.doerksen.base_project.resources.impl;

import com.doerksen.base_project.resources.UrlValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlValidatorImpl implements UrlValidator {

    private final Logger log = LoggerFactory.getLogger(UrlValidatorImpl.class);

    private final String HTTP_PREPEND = "http://";

    /**
     *
     * @param url - a URL to be validated
     * @return    - T/F - could be modified to return a list of validation errors. e.g. cannot be blank, invalid URL format, etc.
     */
    @Override
    public boolean isValidUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        }
        try {
            URL temp = new URL(prependHttp(url));
            return true;
        } catch (MalformedURLException e) {
            // any exception should result in an invalid URL,
            // we could get fancy and return a list of validation errors in the future
            log.error("Invalid URL specified: {}", url);
        }
        return false;
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
