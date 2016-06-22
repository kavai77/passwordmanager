package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import net.himadri.passwordmanager.dto.AuthData;
import net.himadri.passwordmanager.entity.Settings;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;

@RestController
@RequestMapping(value = "/public")
public class PublicController {

    @RequestMapping(value = "/secureRandom", produces = MediaType.TEXT_PLAIN_VALUE)
    public String createSecureRandom() {
        return RandomStringUtils.random(Settings.PASSWORD_LENGTH, 0, 0, true, true, null, new SecureRandom());
    }

    @RequestMapping(value = "/authenticate")
    public AuthData authenticate() {
        UserService userService = UserServiceFactory.getUserService();
        boolean userLoggedIn = userService.isUserLoggedIn();
        return new AuthData(userLoggedIn, userLoggedIn ? "" : userService.createLoginURL("/"));
    }
}