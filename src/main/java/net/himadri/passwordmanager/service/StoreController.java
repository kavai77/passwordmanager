package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.NotFoundException;
import net.himadri.passwordmanager.entity.EncodedUserId;
import net.himadri.passwordmanager.entity.Password;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.springframework.util.Assert.*;

@RestController
public class StoreController {
    private static final Logger LOG = Logger.getLogger(StoreController.class.getName());

    @Autowired RetrieveController retrieveController;
    @Autowired EncodedUserIdController encodedUserIdController;

    @RequestMapping(value = "/store", method = RequestMethod.POST)
    public Password store(@RequestParam String domain, @RequestParam String hex, @RequestParam String iv)  {
        hasLength(domain);
        hasLength(hex);
        hasLength(iv);
        String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
        Password dataEntity = new Password(userId, domain, hex, iv);
        ofy().save().entity(dataEntity).now();
        return dataEntity;
    }

    @RequestMapping(value = "/changeDomain", method = RequestMethod.POST)
    public Password changeDomain(@RequestParam Long id, @RequestParam String domain)  {
        hasLength(domain);
        notNull(id);
        Password password = getUserPassword(id);
        password.setDomain(domain);
        ofy().save().entity(password).now();
        return password;
    }

    @RequestMapping(value = "/changeHex", method = RequestMethod.POST)
    public Password changeHex(@RequestParam Long id, @RequestParam String hex, @RequestParam String iv)  {
        hasLength(hex);
        notNull(id);
        Password password = getUserPassword(id);
        password.setHex(hex);
        password.setIv(iv);
        ofy().save().entity(password).now();
        return password;
    }

    @RequestMapping(value = "/deletePassword", method = RequestMethod.POST)
    public void deletePassword(@RequestParam Long id) {
        notNull(id);
        Password password = getUserPassword(id);
        ofy().delete().entity(password).now();
    }

    @RequestMapping(value = "/changeAllHex", method = RequestMethod.POST)
    public void changeAllHex(@RequestParam(value = "md5Hash") final String masterPasswordMd5Hash,
                             @RequestParam final int iterations,
                             @RequestBody final List<Password> allPasswords) {
        hasText(masterPasswordMd5Hash);
        isTrue(iterations > 0);
        notNull(allPasswords);
        List<Password> oldPasswords = retrieveController.retrieve();
        isTrue(allPasswords.size() == oldPasswords.size());
        for (Password password: allPasswords) {
            Password storedPassword = searchUserPassword(oldPasswords, password.getId());
            isTrue(password.getId().equals(storedPassword.getId()));
            isTrue(StringUtils.equals(password.getUserId(), storedPassword.getUserId()));
            isTrue(StringUtils.equals(password.getDomain(), storedPassword.getDomain()));
        }
        EncodedUserId oldEncodedUserId = encodedUserIdController.getEncodedUserId();
        try {
            encodedUserIdController.store(masterPasswordMd5Hash, iterations);
            ofy().save().entities(allPasswords);
        } catch (RuntimeException e) {
            ofy().save().entity(oldEncodedUserId);
            ofy().save().entities(oldPasswords);
            throw e;
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgumentException(Exception e) {
        LOG.log(Level.SEVERE, "BAD_REQUEST", e);
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleNullPointerException(Exception e) {
        LOG.log(Level.SEVERE, "BAD_REQUEST", e);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleNotFoundException(Exception e) {
        LOG.log(Level.SEVERE, "BAD_REQUEST", e);
    }

    private Password getUserPassword(Long id) {
        Password password = ofy().load().type(Password.class).id(id).safe();
        isTrue(StringUtils.equals(password.getUserId(), UserServiceFactory.getUserService().getCurrentUser().getUserId()));
        return password;
    }

    private Password searchUserPassword(List<Password> passwords, Long id) {
        for (Password password: passwords) {
            if (password.getId().equals(id)) {
                return password;
            }
        }
        throw new NotFoundException();
    }
}
