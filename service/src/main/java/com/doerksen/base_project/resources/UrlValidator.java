package com.doerksen.base_project.resources;

import javax.annotation.Nonnull;

public interface UrlValidator {
    boolean isValidUrl(@Nonnull String url);
}
