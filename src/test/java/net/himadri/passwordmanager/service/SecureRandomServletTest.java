package net.himadri.passwordmanager.service;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SecureRandomServletTest {

    private SecureRandomServlet underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new SecureRandomServlet();
    }

    @Test
    public void testSecureRandom() throws Exception {
        assertEquals(SecureRandomServlet.COUNT, underTest.createSecureRandom().length());
    }
}