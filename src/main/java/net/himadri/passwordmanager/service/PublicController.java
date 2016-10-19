package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import net.himadri.passwordmanager.dto.UserData;
import net.himadri.passwordmanager.entity.AccessLog;
import net.himadri.passwordmanager.entity.RegisteredUser;
import net.himadri.passwordmanager.entity.Settings;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.Date;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static net.himadri.passwordmanager.entity.Settings.CIPHER_ALGORITHM;
import static net.himadri.passwordmanager.entity.Settings.DEFAULT_ITERATIONS;

@RestController
@RequestMapping(value = "/public")
public class PublicController {

    @RequestMapping(value = "/secureRandom", produces = MediaType.TEXT_PLAIN_VALUE)
    public String createSecureRandom() {
        return RandomStringUtils.random(Settings.PASSWORD_LENGTH, 0, 0, true, true, null, new SecureRandom());
    }

    @RequestMapping(value = "/authenticate")
    public UserData authenticate() {
        UserService userService = UserServiceFactory.getUserService();
        boolean userLoggedIn = userService.isUserLoggedIn();
        if (!userLoggedIn) {
            return UserData.userNotAuthenticatedInstance(userService.createLoginURL("/"));
        } else {
            User user = userService.getCurrentUser();
            ofy().save().entity(new AccessLog(user.getUserId(), user.getEmail(), new Date()));
            RegisteredUser registeredUser = ofy().load().type(RegisteredUser.class).id(user.getUserId()).now();
            int iterations = registeredUser != null ? registeredUser.getIterations() : DEFAULT_ITERATIONS;
            String cipherAlgorithm = registeredUser != null ? registeredUser.getCipherAlgorithm() : CIPHER_ALGORITHM;
            int keyLength = registeredUser != null ? registeredUser.getKeyLength() : Settings.DEFAULT_KEYLENGTH;
            String pbkdf2Algorithm = registeredUser != null ? registeredUser.getPbkdf2Algorithm() : Settings.DEFAULT_PBKDF2_ALGORITHM;
            return UserData.userAuthenticatedInstance(user.getUserId(), user.getNickname(), userService.createLogoutURL("/"),
                    registeredUser != null, iterations, cipherAlgorithm, keyLength, pbkdf2Algorithm);


        }
    }
}