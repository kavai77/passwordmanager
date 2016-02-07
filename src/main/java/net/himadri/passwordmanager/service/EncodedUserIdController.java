package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import net.himadri.passwordmanager.entity.EncodedUserId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.apache.commons.lang3.Validate.notEmpty;

@RestController
public class EncodedUserIdController {

    @RequestMapping(value = "/encodedUserId/store", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void store(@RequestParam String md5Hash) {
        notEmpty(md5Hash);
        User currentUser = UserServiceFactory.getUserService().getCurrentUser();
        ofy().save().entity(new EncodedUserId(currentUser.getUserId(), md5Hash, currentUser.getEmail()));
    }

    @RequestMapping("/encodedUserId/check")
    public void check(@RequestParam String md5Hash) {
        notEmpty(md5Hash);
        User currentUser = UserServiceFactory.getUserService().getCurrentUser();
        EncodedUserId userId = ofy().load().type(EncodedUserId.class).id(currentUser.getUserId()).safe();
        if (!StringUtils.equals(md5Hash, userId.getMasterPasswordMd5Hash())){
            throw new NotAuthorizedException();
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgumentException(Exception e) {
        // nothing to do
    }

    @ExceptionHandler(NotAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleNotAuthorizedException(Exception e) {
        // nothing to do
    }

    private class NotAuthorizedException extends RuntimeException {
    }
}
