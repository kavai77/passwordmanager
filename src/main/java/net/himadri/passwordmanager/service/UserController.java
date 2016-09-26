package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import net.himadri.passwordmanager.dto.RecommendedSettings;
import net.himadri.passwordmanager.dto.UserData;
import net.himadri.passwordmanager.entity.AccessLog;
import net.himadri.passwordmanager.entity.RegisteredUser;
import net.himadri.passwordmanager.entity.Settings;
import net.himadri.passwordmanager.service.exception.NotAuthorizedException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static net.himadri.passwordmanager.entity.Settings.*;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;

@RestController
@RequestMapping(value = "/secure/user")
public class UserController {
    private static final Logger LOG = Logger.getLogger(UserController.class.getName());

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestParam(value = "md5Hash") String masterPasswordMd5Hash, @RequestParam int iterations,
                         @RequestParam String cipherAlgorithm, @RequestParam int keyLength, @RequestParam String pbkdf2Algorithm) {
        notEmpty(masterPasswordMd5Hash);
        isTrue(StringUtils.equals(cipherAlgorithm, CIPHER_ALGORITHM));
        isTrue(ArrayUtils.contains(ALLOWED_KEYLENGTH, keyLength));
        User currentUser = UserServiceFactory.getUserService().getCurrentUser();
        ofy().save().entity(new RegisteredUser(currentUser.getUserId(), masterPasswordMd5Hash, currentUser.getEmail(),
                iterations, cipherAlgorithm, keyLength, pbkdf2Algorithm)).now();
    }

    @RequestMapping("/check")
    public void check(@RequestParam(value = "md5Hash") String masterPasswordMd5Hash) {
        notEmpty(masterPasswordMd5Hash);
        User currentUser = UserServiceFactory.getUserService().getCurrentUser();
        RegisteredUser userId = ofy().load().type(RegisteredUser.class).id(currentUser.getUserId()).safe();
        if (!StringUtils.equals(masterPasswordMd5Hash, userId.getMasterPasswordHash())){
            throw new NotAuthorizedException();
        }
    }
    @RequestMapping("/recommendedSettings")
    public RecommendedSettings getRecommendedSettings() {
        return new RecommendedSettings(DEFAULT_ITERATIONS, Settings.DEFAULT_PBKDF2_ALGORITHM);
    }

    @RequestMapping("/userService")
    public UserData userService() {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        ofy().save().entity(new AccessLog(user.getUserId(), user.getEmail(), new Date()));
        RegisteredUser registeredUser = ofy().load().type(RegisteredUser.class).id(user.getUserId()).now();
        int iterations = registeredUser != null ? registeredUser.getIterations() : DEFAULT_ITERATIONS;
        String cipherAlgorithm = registeredUser != null ? registeredUser.getCipherAlgorithm() : CIPHER_ALGORITHM;
        int keyLength = registeredUser != null ? registeredUser.getKeyLength() : Settings.DEFAULT_KEYLENGTH;
        String pbkdf2Algorithm = registeredUser != null ? registeredUser.getPbkdf2Algorithm() : Settings.DEFAULT_PBKDF2_ALGORITHM;
        return new UserData(user.getUserId(), user.getNickname(), userService.createLogoutURL("/"),
                registeredUser != null, iterations, cipherAlgorithm, keyLength, pbkdf2Algorithm);
    }

    public RegisteredUser getRegisteredUser() {
        User currentUser = UserServiceFactory.getUserService().getCurrentUser();
        return ofy().load().type(RegisteredUser.class).id(currentUser.getUserId()).safe();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgumentException(Exception e) {
        LOG.log(Level.SEVERE, "BAD_REQUEST: " + e.getMessage());
    }

    @ExceptionHandler(NotAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleNotAuthorizedException(Exception e) {
        LOG.log(Level.SEVERE, "UNAUTHORIZED: " + e.getMessage());
    }

}
