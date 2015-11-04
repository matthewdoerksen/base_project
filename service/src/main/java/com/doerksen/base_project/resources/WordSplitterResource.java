package com.doerksen.base_project.resources;

import javax.ws.rs.core.Response;

public interface WordSplitterResource {
    Response splitTextIntoWords(String text);
}
