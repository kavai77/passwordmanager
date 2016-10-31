package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.googlecode.objectify.Objectify;
import net.himadri.passwordmanager.dto.UserData;
import net.himadri.passwordmanager.entity.AccessLog;
import net.himadri.passwordmanager.entity.AdminSettings;
import net.himadri.passwordmanager.entity.RegisteredUser;
import net.himadri.passwordmanager.entity.UserSettings;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.Date;

@RestController
@RequestMapping(value = "/public")
public class PublicController {

    @Autowired
    Objectify ofy;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/secureRandom", produces = MediaType.TEXT_PLAIN_VALUE)
    public String createSecureRandom() {
        return RandomStringUtils.random(AdminSettings.RANDOM_PASSWORD_LENGTH, 0, 0, true, true, null, new SecureRandom());
    }

    @RequestMapping(value = "/authenticate")
    public UserData authenticate() {
        boolean userLoggedIn = userService.isUserLoggedIn();
        if (!userLoggedIn) {
            return UserData.userNotAuthenticatedInstance(userService.createLoginURL("/"));
        } else {
            User user = userService.getCurrentUser();
            ofy.save().entity(new AccessLog(user.getUserId(), user.getEmail(), new Date()));
            RegisteredUser registeredUser = ofy.load().type(RegisteredUser.class).id(user.getUserId()).now();
            UserData.UserSettingsData userSettingsData = retrieveUserSettings(user);
            if (registeredUser != null) {
                return UserData.userRegisteredInstance(user.getUserId(), user.getNickname(),
                        userService.createLogoutURL("/"),
                        registeredUser.getIterations(), registeredUser.getCipherAlgorithm(),
                        registeredUser.getKeyLength(), registeredUser.getPbkdf2Algorithm(),
                        userSettingsData);
            } else {
                return UserData.userUnregisteredInstance(user.getUserId(), user.getNickname(),
                        userService.createLogoutURL("/"), userSettingsData);
            }
        }
    }

    private UserData.UserSettingsData retrieveUserSettings(User user) {
        UserSettings userSettings = ofy.load().type(UserSettings.class).id(user.getUserId()).now();
        if (userSettings == null) {
            userSettings = new UserSettings(user.getUserId(), AdminSettings.DEFAULT_USER_PASSWORD_LENGTH, AdminSettings.DEFAULT_USER_TIMEOUT_LENGTH_SECONDS);
            ofy.save().entity(userSettings);
        }
        return new UserData.UserSettingsData(userSettings.getDefaultPasswordLength(),
                userSettings.getTimeoutLengthSeconds());

    }
}