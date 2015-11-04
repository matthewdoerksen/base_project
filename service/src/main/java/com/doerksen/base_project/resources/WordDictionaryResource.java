package com.doerksen.base_project.resources;

import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;

public interface WordDictionaryResource {
    Response wordInDictionary(@Nonnull final String word);
}
