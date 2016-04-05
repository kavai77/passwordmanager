package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import net.himadri.passwordmanager.entity.EncodedUserId;
import net.himadri.passwordmanager.entity.Settings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.apache.commons.lang3.Validate.notEmpty;

@RestController
public class EncodedUserIdController {
    private static final Logger LOG = Logger.getLogger(EncodedUserIdController.class.getName());

    @RequestMapping(value = "/encodedUserId/store", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void store(@RequestParam(value = "md5Hash") String masterPasswordMd5Hash, @RequestParam int iterations) {
        notEmpty(masterPasswordMd5Hash);
        User currentUser = UserServiceFactory.getUserService().getCurrentUser();
        ofy().save().entity(new EncodedUserId(currentUser.getUserId(), masterPasswordMd5Hash, currentUser.getEmail(), iterations)).now();
    }

    @RequestMapping("/encodedUserId/check")
    public void check(@RequestParam(value = "md5Hash") String masterPasswordMd5Hash) {
        notEmpty(masterPasswordMd5Hash);
        User currentUser = UserServiceFactory.getUserService().getCurrentUser();
        EncodedUserId userId = ofy().load().type(EncodedUserId.class).id(currentUser.getUserId()).safe();
        if (!StringUtils.equals(masterPasswordMd5Hash, userId.getMasterPasswordMd5Hash())){
            throw new NotAuthorizedException();
        }
    }
    @RequestMapping("/encodedUserId/recommendedIterations")
    public int getRecommendedIterations() {
        return Settings.DEFAULT_ITERATIONS;
    }

    public EncodedUserId getEncodedUserId() {
        User currentUser = UserServiceFactory.getUserService().getCurrentUser();
        return ofy().load().type(EncodedUserId.class).id(currentUser.getUserId()).safe();
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
