package com.doerksen.base_project.resources;

import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;

public interface WebDocumentRetrievalResource {
    Response retrieveWebDocument(@Nonnull final String url);
}
