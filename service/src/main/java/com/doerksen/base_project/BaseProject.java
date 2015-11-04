package com.doerksen.base_project;

import com.doerksen.base_project.resources.UrlValidator;
import com.doerksen.base_project.resources.WebDocumentRetrievalResource;
import com.doerksen.base_project.resources.WordDictionaryResource;
import com.doerksen.base_project.resources.WordSplitterResource;
import com.doerksen.base_project.resources.dao.ConnectionResource;
import com.doerksen.base_project.resources.dao.WordCountDao;
import com.doerksen.base_project.resources.dao.impl.ConnectionResourceImpl;
import com.doerksen.base_project.resources.dao.impl.WordCountDaoImpl;
import com.doerksen.base_project.resources.impl.*;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class BaseProject extends Application<BaseProjectConfiguration> {

    public static void main(String[] args) throws Exception {
        new BaseProject().run(args);
    }

    @Override
    public void run(BaseProjectConfiguration configuration, Environment environment) throws Exception {
        // in a later version of a project I've used dependency injection
        // to avoid setting dependencies manually here
        UrlValidator urlValidator = new UrlValidatorImpl();
        WebDocumentRetrievalResource webDocumentRetrievalResource = new WebDocumentRetrievalResourceImpl(urlValidator);

        WordSplitterResource wordSplitterResource = new WordSplitterResourceImpl();
        WordDictionaryResource wordDictionaryResource = new WordDictionaryResourceImpl();

        // fake resource so we can mock having connections to a database
        // NOTE: it returns null so it isn't actually useful
        ConnectionResource connectionResource = new ConnectionResourceImpl();
        WordCountDao wordCountDao = new WordCountDaoImpl(connectionResource);

        // later we could use micro services for each of these pieces for better scalability
        // it would also mean we need to hook them up here
        environment.jersey().register(new WordCountResourceImpl(webDocumentRetrievalResource,
                                                                wordSplitterResource,
                                                                wordDictionaryResource,
                                                                wordCountDao,
                                                                urlValidator));
    }
}
