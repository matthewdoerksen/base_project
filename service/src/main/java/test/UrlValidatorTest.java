package test;

import com.doerksen.base_project.resources.UrlValidator;
import com.doerksen.base_project.resources.impl.UrlValidatorImpl;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class UrlValidatorTest {

    private UrlValidator urlValidator;

    @Before
    public void before() {
        urlValidator = new UrlValidatorImpl();
    }

    @Test
    public void testNullUrl() {
        Assert.assertFalse(urlValidator.isValidUrl(null));
    }

    @Test
    public void testEmptyUrl() {
        Assert.assertFalse(urlValidator.isValidUrl(""));
    }

    @Test
    public void testBlankUrl() {
        Assert.assertFalse(urlValidator.isValidUrl("     "));
    }

    @Test
    public void testMalformedUrl() {
        Assert.assertFalse(urlValidator.isValidUrl("google."));
    }

    @Test
    public void testValidUrl() {
        Assert.assertTrue(urlValidator.isValidUrl("http://google.com"));
    }

    @Test
    public void testValidUrl2() {
        Assert.assertTrue(urlValidator.isValidUrl("http://www.google.com"));
    }
}
