package net.himadri.passwordmanager.service;

import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import net.himadri.passwordmanager.dto.UserData;
import net.himadri.passwordmanager.entity.AccessLog;
import net.himadri.passwordmanager.entity.AdminSettings;
import net.himadri.passwordmanager.entity.RegisteredUser;
import net.himadri.passwordmanager.entity.UserSettings;
import net.himadri.passwordmanager.security.AuthenticationService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.himadri.passwordmanager.entity.AdminSettings.ALLOWED_KEYLENGTH;
import static net.himadri.passwordmanager.entity.AdminSettings.CIPHER_ALGORITHM;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;

@RestController
@RequestMapping(value = "/secure/user")
@RequiredArgsConstructor
public class UserController {
    private static final Logger LOG = Logger.getLogger(UserController.class.getName());

    private final DatabaseService databaseService;
    private final AuthenticationService authenticationService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestParam String masterPasswordHash,
                         @RequestParam String masterPasswordHashAlgorithm,
                         @RequestParam int iterations,
                         @RequestParam String cipherAlgorithm,
                         @RequestParam int keyLength,
                         @RequestParam String pbkdf2Algorithm
    ) {
        notEmpty(masterPasswordHash);
        notEmpty(masterPasswordHashAlgorithm);
        isTrue(StringUtils.equals(cipherAlgorithm, CIPHER_ALGORITHM));
        isTrue(ArrayUtils.contains(ALLOWED_KEYLENGTH, keyLength));
        FirebaseToken token = authenticationService.getFirebaseToken();
        isTrue(databaseService.ofy().load().type(RegisteredUser.class).id(token.getUid()).now() == null);
        databaseService.ofy().save().entity(RegisteredUser.builder()
                .userId(token.getUid())
                .masterPasswordHash(masterPasswordHash)
                .masterPasswordHashAlgorithm(masterPasswordHashAlgorithm)
                .email(token.getEmail())
                .iterations(iterations)
                .cipherAlgorithm(cipherAlgorithm)
                .keyLength(keyLength)
                .pbkdf2Algorithm(pbkdf2Algorithm)
                .salt(RandomStringUtils.random(10))
                .build()).now();
    }

    @RequestMapping(value = "/userSettings", method = RequestMethod.POST)
    public void updateUserSettings(@RequestBody UserData.UserSettingsData userSettingsData) {
        RegisteredUser registeredUser = getRegisteredUser();
        databaseService.ofy().save().entity(UserSettings.builder()
                .userId(registeredUser.getUserId())
                .defaultPasswordLength(userSettingsData.getDefaultPasswordLength())
                .timeoutLengthSeconds(userSettingsData.getTimeoutLengthSeconds())
                .build());

    }

    @RequestMapping(value = "/authenticate")
    @ResponseBody
    public UserData authenticate() {
        FirebaseToken token = authenticationService.getFirebaseToken();
        databaseService.ofy().save().entity(new AccessLog(token.getUid(), token.getEmail(), new Date()));
        RegisteredUser registeredUser = databaseService.ofy().load().type(RegisteredUser.class).id(token.getUid()).now();
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

    public RegisteredUser getRegisteredUser() {
        String userId = authenticationService.getUid();
        return databaseService.ofy().load().type(RegisteredUser.class).id(userId).safe();
    }

    private UserData.UserSettingsData retrieveUserSettings(String userId) {
        UserSettings userSettings = databaseService.ofy().load().type(UserSettings.class).id(userId).now();
        if (userSettings == null) {
            userSettings = new UserSettings(userId, AdminSettings.DEFAULT_USER_PASSWORD_LENGTH, AdminSettings.DEFAULT_USER_TIMEOUT_LENGTH_SECONDS);
            databaseService.ofy().save().entity(userSettings);
        }
        return UserData.UserSettingsData.builder()
                .defaultPasswordLength(userSettings.getDefaultPasswordLength())
                .timeoutLengthSeconds(userSettings.getTimeoutLengthSeconds())
                .build();

    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgumentException(Exception e) {
        LOG.log(Level.SEVERE, "BAD_REQUEST: " + e.getMessage());
    }
}
