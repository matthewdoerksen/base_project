package test;

import com.doerksen.base_project.resources.WordSplitterResource;
import com.doerksen.base_project.resources.impl.WordSplitterResourceImpl;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response.Status;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class WordSplitterResourceTest {

    private WordSplitterResource wordSplitterResource;

    @Before
    public void before() {
        wordSplitterResource = new WordSplitterResourceImpl();
    }

    @Test
    public void testNullText() {
       assertEquals(Status.BAD_REQUEST, wordSplitterResource.splitTextIntoWords(null).getStatusInfo());
    }

    @Test
    public void testEmptyText() {
        assertEquals(Status.BAD_REQUEST, wordSplitterResource.splitTextIntoWords("").getStatusInfo());
    }

    @Test
    public void testBlankText() {
        assertEquals(Status.BAD_REQUEST, wordSplitterResource.splitTextIntoWords("     ").getStatusInfo());
    }

    @Test
    public void testSplitSize() {
        assertEquals(4, ((List<String>)wordSplitterResource.splitTextIntoWords("this is a test").getEntity()).size());
    }
}
