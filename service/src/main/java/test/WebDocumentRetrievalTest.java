package test;

import com.doerksen.base_project.resources.UrlValidator;
import com.doerksen.base_project.resources.WebDocumentRetrievalResource;
import com.doerksen.base_project.resources.impl.WebDocumentRetrievalResourceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebDocumentRetrievalTest {

    private WebDocumentRetrievalResource webDocumentRetrievalResource;

    @Mock
    private UrlValidator urlValidator;

    @Before
    public void before() {
        urlValidator = mock(UrlValidator.class);
        when(urlValidator.isValidUrl(Matchers.anyString())).thenReturn(true);
        webDocumentRetrievalResource = new WebDocumentRetrievalResourceImpl(urlValidator);
    }

    @Test
    public void testBadUrlResponse() {
        when(urlValidator.isValidUrl(Matchers.anyString())).thenReturn(false);
        assertEquals(Status.BAD_REQUEST, webDocumentRetrievalResource.retrieveWebDocument("").getStatusInfo());
    }

    /*
        This is a very bad test since it uses live information. If there was more time,
        I'd wrap the Jsoup connector to abstract it out and be able to mock it.
     */
    @Test
    public void testGoodUrl() {
        Response response = webDocumentRetrievalResource.retrieveWebDocument("www.google.com");
        assertNotEquals(Status.SERVICE_UNAVAILABLE, response.getStatusInfo());
        assertEquals(Status.OK, response.getStatusInfo());
    }
}
