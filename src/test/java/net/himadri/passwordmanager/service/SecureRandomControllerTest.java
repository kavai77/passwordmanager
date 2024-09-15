package net.himadri.passwordmanager.service;

import net.himadri.passwordmanager.entity.AdminSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecureRandomControllerTest {

    private PublicController underTest;

    @BeforeEach
    public void setUp() throws Exception {
        underTest = new PublicController(null);
    }

    @Test
    public void testSecureRandom() throws Exception {
        assertEquals(AdminSettings.RANDOM_PASSWORD_LENGTH, underTest.createSecureRandom().length());
    }
}