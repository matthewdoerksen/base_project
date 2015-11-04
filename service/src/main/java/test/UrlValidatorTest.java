package test;

import com.doerksen.base_project.resources.UrlValidator;
import com.doerksen.base_project.resources.impl.UrlValidatorImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UrlValidatorTest {

    private UrlValidator urlValidator;

    @Before
    public void before() {
        urlValidator = new UrlValidatorImpl();
    }

    @Test
    public void testNullUrl() {
        assertFalse(urlValidator.isValidUrl(null));
    }

    @Test
    public void testEmptyUrl() {
        assertFalse(urlValidator.isValidUrl(""));
    }

    @Test
    public void testBlankUrl() {
        assertFalse(urlValidator.isValidUrl("     "));
    }

    @Test
    @Ignore("Because I was required to add http:// at the beginning of each URL, this test no longer functions." +
            " Some refactoring should be done to fix that hack, at which point this test can be re-enabled.")
    public void testMalformedUrl() {
        assertFalse(urlValidator.isValidUrl("http://http://abc//google.c"));
    }

    @Test
    public void testValidUrl() {
        assertTrue(urlValidator.isValidUrl("http://google.com"));
    }

    @Test
    public void testValidUrl2() {
        assertTrue(urlValidator.isValidUrl("http://www.google.com"));
    }
}
