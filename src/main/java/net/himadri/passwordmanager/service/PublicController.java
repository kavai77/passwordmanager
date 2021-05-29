package net.himadri.passwordmanager.service;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import net.himadri.passwordmanager.dto.RecommendedSettings;
import net.himadri.passwordmanager.dto.UserData;
import net.himadri.passwordmanager.entity.AccessLog;
import net.himadri.passwordmanager.entity.AdminSettings;
import net.himadri.passwordmanager.entity.RegisteredUser;
import net.himadri.passwordmanager.entity.UserSettings;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.Date;

import static net.himadri.passwordmanager.App.X_AUTHORIZATION_FIREBASE;
import static net.himadri.passwordmanager.entity.AdminSettings.DEFAULT_ITERATIONS;

@RestController
@RequestMapping(value = "/public")
public class PublicController {

    @Autowired
    ExternalService externalService;

    @RequestMapping(value = "/secureRandom", produces = MediaType.TEXT_PLAIN_VALUE)
    public String createSecureRandom() {
        return RandomStringUtils.random(AdminSettings.RANDOM_PASSWORD_LENGTH, 0, 0, true, true, null, new SecureRandom());
    }

    @RequestMapping(value = "/authenticate")
    public UserData authenticate(@RequestHeader(X_AUTHORIZATION_FIREBASE) String firebaseToken) throws FirebaseAuthException {
        FirebaseToken token = externalService.firebaseAuth().verifyIdToken(firebaseToken);
        externalService.ofy().save().entity(new AccessLog(token.getUid(), token.getEmail(), new Date()));
        RegisteredUser registeredUser = externalService.ofy().load().type(RegisteredUser.class).id(token.getUid()).now();
        UserData.UserSettingsData userSettingsData = retrieveUserSettings(token.getUid());
        if (registeredUser != null) {
            return UserData.userRegisteredInstance(token.getUid(), token.getName(),
                    registeredUser.getIterations(), registeredUser.getCipherAlgorithm(),
                    registeredUser.getMasterPasswordHashAlgorithm(),
                    registeredUser.getKeyLength(), registeredUser.getPbkdf2Algorithm(),
                    registeredUser.getSalt(), userSettingsData);
        } else {
            return UserData.userUnregisteredInstance(token.getUid(), token.getName(), userSettingsData);
        }
    }

    @RequestMapping("/recommendedSettings")
    public RecommendedSettings getRecommendedSettings() {
        return new RecommendedSettings(DEFAULT_ITERATIONS, AdminSettings.DEFAULT_PBKDF2_ALGORITHM, AdminSettings.DEFAULT_HASH_ALGORITHM);
    }


    private UserData.UserSettingsData retrieveUserSettings(String userId) {
        UserSettings userSettings = externalService.ofy().load().type(UserSettings.class).id(userId).now();
        if (userSettings == null) {
            userSettings = new UserSettings(userId, AdminSettings.DEFAULT_USER_PASSWORD_LENGTH, AdminSettings.DEFAULT_USER_TIMEOUT_LENGTH_SECONDS);
            externalService.ofy().save().entity(userSettings);
        }
        return new UserData.UserSettingsData(userSettings.getDefaultPasswordLength(),
                userSettings.getTimeoutLengthSeconds());

    }
}