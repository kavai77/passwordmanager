package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import net.himadri.passwordmanager.dto.UserData;
import net.himadri.passwordmanager.entity.AccessLog;
import net.himadri.passwordmanager.entity.RegisteredUser;
import net.himadri.passwordmanager.entity.Settings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.apache.commons.lang3.Validate.notEmpty;

@RestController
@RequestMapping(value = "/secure/user")
public class UserController {
    private static final Logger LOG = Logger.getLogger(UserController.class.getName());

    @RequestMapping(value = "/store", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void store(@RequestParam(value = "md5Hash") String masterPasswordMd5Hash, @RequestParam int iterations) {
        notEmpty(masterPasswordMd5Hash);
        User currentUser = UserServiceFactory.getUserService().getCurrentUser();
        ofy().save().entity(new RegisteredUser(currentUser.getUserId(), masterPasswordMd5Hash, currentUser.getEmail(), iterations)).now();
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
    @RequestMapping("/recommendedIterations")
    public int getRecommendedIterations() {
        return Settings.DEFAULT_ITERATIONS;
    }

    @RequestMapping("/userService")
    public UserData userService() {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        ofy().save().entity(new AccessLog(user.getUserId(), user.getEmail(), new Date()));
        RegisteredUser registeredUser = ofy().load().type(RegisteredUser.class).id(user.getUserId()).now();
        int iterations = registeredUser != null ? registeredUser.getIterations() : Settings.DEFAULT_ITERATIONS;
        return new UserData(user.getUserId(), user.getNickname(), userService.createLogoutURL("/"),
                registeredUser != null, iterations);
    }

    public RegisteredUser getRegisteredUser() {
        User currentUser = UserServiceFactory.getUserService().getCurrentUser();
        return ofy().load().type(RegisteredUser.class).id(currentUser.getUserId()).safe();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgumentException(Exception e) {
        LOG.log(Level.SEVERE, "BAD_REQUEST", e);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleNotAuthorizedException(Exception e) {
        LOG.log(Level.SEVERE, "UNAUTHORIZED", e);
    }

    private class NotAuthorizedException extends RuntimeException {
    }
}
