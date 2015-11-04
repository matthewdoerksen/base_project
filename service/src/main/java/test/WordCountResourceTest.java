package test;

import com.doerksen.base_project.dto.WordCountDto;
import com.doerksen.base_project.resources.*;
import com.doerksen.base_project.resources.dao.ConnectionResource;
import com.doerksen.base_project.resources.dao.WordCountDao;
import com.doerksen.base_project.resources.dao.impl.WordCountDaoImpl;
import com.doerksen.base_project.resources.impl.WebDocumentRetrievalResourceImpl;
import com.doerksen.base_project.resources.impl.WordCountResourceImpl;
import com.doerksen.base_project.resources.impl.WordSplitterResourceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WordCountResourceTest {

    private WordCountResource wordCountResource;
    private final String URL = "www.google.com";

    private WebDocumentRetrievalResource webDocumentRetrievalResource;
    private WordSplitterResource wordSplitterResource;
    @Mock
    private WordDictionaryResource wordDictionaryResource;
    private WordCountDao wordCountDao;
    @Mock
    private UrlValidator urlValidator;
    @Mock
    private ConnectionResource connectionResource;

    @Before
    public void before() throws SQLException, IOException {
        urlValidator = mock(UrlValidator.class);
        when(urlValidator.isValidUrl(URL)).thenReturn(true);

        webDocumentRetrievalResource = new WebDocumentRetrievalResourceImpl(urlValidator);
        wordSplitterResource = new WordSplitterResourceImpl();

        wordDictionaryResource = mock(WordDictionaryResource.class);
        when(wordDictionaryResource.wordInDictionary(Matchers.anyString())).thenReturn(Response.status(Status.OK).build());

        connectionResource = mock(ConnectionResource.class);

        wordCountDao = new WordCountDaoImpl(connectionResource);
        wordCountResource = new WordCountResourceImpl(webDocumentRetrievalResource,
                                                      wordSplitterResource,
                                                      wordDictionaryResource,
                                                      wordCountDao,
                                                      urlValidator);
    }

    @Test
    public void testInsertGetUpdate() {
        Response response = wordCountResource.insertWordCount(URL);
        assertEquals(Status.OK, response.getStatusInfo());

        // validate (tests get as well)
        response = wordCountResource.getWordCount(URL);
        WordCountDto dto = (WordCountDto)response.getEntity();
        assertEquals(URL, dto.getUrl());

        // test update
        response = wordCountResource.updateWordCount(URL);
        assertEquals(Status.OK, response.getStatusInfo());
    }
}
