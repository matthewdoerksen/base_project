package test;

import com.doerksen.base_project.resources.dao.ConnectionResource;
import com.doerksen.base_project.resources.dao.WordCount;
import com.doerksen.base_project.resources.dao.WordCountDao;
import com.doerksen.base_project.resources.dao.impl.WordCountDaoImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class WordCountDaoResourceTest {

    private WordCountDao wordCountDao;

    @Mock
    private ConnectionResource connectionResource;

    @Before
    public void before() {
        connectionResource = mock(ConnectionResource.class);
        wordCountDao = new WordCountDaoImpl(connectionResource);
    }

    @Test(expected = NoSuchElementException.class)
    public void getNonExistantUrl() throws SQLException, IOException {
        Assert.assertNull(wordCountDao.getWordCount("http://google.com"));
    }

    @Test
    public void testInsertWordCount() throws SQLException, JsonProcessingException, IOException {
        WordCount wc = (new WordCount(1L, "http://google.com", new HashMap<>()));
        Assert.assertTrue(wordCountDao.insertWordCount(wc));
        assertEquals(wc, wordCountDao.getWordCount("http://google.com"));
    }

    @Test
    public void testDuplicateInsertWordCount() throws SQLException, JsonProcessingException{
        Assert.assertTrue(wordCountDao.insertWordCount(new WordCount(1L, "http://google.com", new HashMap<>())));
        Assert.assertFalse(wordCountDao.insertWordCount(new WordCount(1L, "http://google.com", new HashMap<>())));
    }

    @Test(expected = NoSuchElementException.class)
    public void testUpdateNonExistant() throws SQLException, JsonProcessingException{
        Assert.assertFalse(wordCountDao.updateWordCount(new WordCount(1L, "http://google.com", new HashMap<>())));
    }

    @Test
    public void testUpdateWordCount() throws SQLException, JsonProcessingException, IOException {
        WordCount wc = new WordCount(1L, "http://google.com", new HashMap<String, Integer>() {{
            put("test", 1);
        }});

        Assert.assertTrue(wordCountDao.insertWordCount(new WordCount(1L, "http://google.com", new HashMap<>())));
        Assert.assertTrue(wordCountDao.updateWordCount(wc));
        WordCount retrieved = wordCountDao.getWordCount("http://google.com");
        assertEquals(wc, retrieved);
    }
}
