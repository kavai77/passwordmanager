package net.himadri.passwordmanager.service;

import net.himadri.passwordmanager.entity.AdminSettings;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SecureRandomControllerTest {

    private PublicController underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new PublicController(null);
    }

    @Test
    public void testSecureRandom() throws Exception {
        assertEquals(AdminSettings.RANDOM_PASSWORD_LENGTH, underTest.createSecureRandom().length());
    }
}