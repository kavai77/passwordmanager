package net.himadri.passwordmanager.service;

import net.himadri.passwordmanager.entity.Settings;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;

@RestController
public class SecureRandomController {

    @RequestMapping(value = "/secureRandom", produces = MediaType.TEXT_PLAIN_VALUE)
    public String createSecureRandom() {
        return RandomStringUtils.random(Settings.PASSWORD_LENGTH, 0, 0, true, true, null, new SecureRandom());
    }
}