package net.himadri.passwordmanager.service;

import lombok.RequiredArgsConstructor;
import net.himadri.passwordmanager.dto.RecommendedSettings;
import net.himadri.passwordmanager.entity.AdminSettings;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;

import static net.himadri.passwordmanager.entity.AdminSettings.DEFAULT_ITERATIONS;

@RestController
@RequestMapping(value = "/public")
@RequiredArgsConstructor
public class PublicController {
    private final DatabaseService databaseService;

    @RequestMapping(value = "/secureRandom", produces = MediaType.TEXT_PLAIN_VALUE)
    public String createSecureRandom() {
        return RandomStringUtils.random(AdminSettings.RANDOM_PASSWORD_LENGTH, 0, 0, true, true, null, new SecureRandom());
    }

    @RequestMapping("/recommendedSettings")
    public RecommendedSettings getRecommendedSettings() {
        return RecommendedSettings.builder()
                .recommendedIterations(DEFAULT_ITERATIONS)
                .recommendedPbkdf2Algorithm(AdminSettings.DEFAULT_PBKDF2_ALGORITHM)
                .recommendedMasterPasswordHashAlgorithm(AdminSettings.DEFAULT_HASH_ALGORITHM)
                .build();
    }
}