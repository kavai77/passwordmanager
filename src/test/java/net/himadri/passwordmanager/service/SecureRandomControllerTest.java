package net.himadri.passwordmanager.service;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SecureRandomControllerTest {

    private SecureRandomController underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new SecureRandomController();
    }

    @Test
    public void testSecureRandom() throws Exception {
        assertEquals(SecureRandomController.COUNT, underTest.createSecureRandom().length());
    }
}