package test;

import com.doerksen.base_project.resources.WordDictionaryResource;
import com.doerksen.base_project.resources.impl.WordDictionaryResourceImpl;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response.Status;

import static org.junit.Assert.assertEquals;

public class WordDictionaryResourceTest {

    private WordDictionaryResource wordDictionaryResource;

    @Before
    public void before() {
        wordDictionaryResource = new WordDictionaryResourceImpl();
    }

    @Test
    public void testNullWord() {
        assertEquals(Status.BAD_REQUEST, wordDictionaryResource.wordInDictionary(null).getStatusInfo());
    }

    @Test
    public void testEmptyWord() {
        assertEquals(Status.BAD_REQUEST, wordDictionaryResource.wordInDictionary("").getStatusInfo());
    }

    @Test
    public void testBlankWord() {
        assertEquals(Status.BAD_REQUEST, wordDictionaryResource.wordInDictionary("      ").getStatusInfo());
    }

    @Test
    public void testValidWord() {
        assertEquals(Status.OK, wordDictionaryResource.wordInDictionary("test").getStatusInfo());
    }
}
