package net.himadri.passwordmanager.service;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.googlecode.objectify.Objectify;
import net.himadri.passwordmanager.entity.AdminSettings;
import net.himadri.passwordmanager.entity.Password;
import net.himadri.passwordmanager.entity.RegisteredUser;
import net.himadri.passwordmanager.service.exception.NotAuthorizedException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.springframework.util.Assert.*;

@RestController
@RequestMapping(value = "/secure/password")
public class PasswordController {
    private static final Logger LOG = Logger.getLogger(PasswordController.class.getName());

    @Autowired
    UserController userController;

    @Autowired
    Objectify ofy;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/store", method = RequestMethod.POST)
    public Password store(@RequestParam String domain, @RequestParam String userName,
                          @RequestParam String hex, @RequestParam String iv)  {
        hasLength(domain);
        hasLength(hex);
        hasLength(iv);
        String userId = userService.getCurrentUser().getUserId();
        Password password = new Password(userId, domain, userName, hex, iv);
        ofy.save().entity(password).now();
        return password;
    }

    @RequestMapping(value = "/changeDomain", method = RequestMethod.POST)
    public Password changeDomain(@RequestParam Long id, @RequestParam String domain)  {
        hasLength(domain);
        notNull(id);
        Password password = getUserPassword(id);
        password.setDomain(domain);
        ofy.save().entity(password).now();
        return password;
    }

    @RequestMapping(value = "/changeUserName", method = RequestMethod.POST)
    public Password changeUserName(@RequestParam Long id, @RequestParam String userName)  {
        notNull(id);
        Password password = getUserPassword(id);
        password.setUserName(userName);
        ofy.save().entity(password).now();
        return password;
    }

    @RequestMapping(value = "/changeHex", method = RequestMethod.POST)
    public Password changeHex(@RequestParam Long id, @RequestParam String hex, @RequestParam String iv)  {
        hasLength(hex);
        notNull(id);
        Password password = getUserPassword(id);
        password.setHex(hex);
        password.setIv(iv);
        ofy.save().entity(password).now();
        return password;
    }

    @RequestMapping(value = "/deletePassword", method = RequestMethod.POST)
    public void deletePassword(@RequestParam Long id) {
        notNull(id);
        Password password = getUserPassword(id);
        ofy.delete().entity(password).now();
    }

    @RequestMapping(value = "/changeAllHex", method = RequestMethod.POST)
    public void changeAllHex(@RequestParam final String masterPasswordHash,
                             @RequestParam final String masterPasswordHashAlgorithm,
                             @RequestParam final int iterations,
                             @RequestParam String cipherAlgorithm, @RequestParam int keyLength,
                             @RequestParam String pbkdf2Algorithm,
                             @RequestBody final List<Password> allPasswords) {
        hasText(masterPasswordHash);
        hasText(masterPasswordHashAlgorithm);
        isTrue(iterations > 0);
        notNull(allPasswords);
        isTrue(StringUtils.equals(cipherAlgorithm, AdminSettings.CIPHER_ALGORITHM));
        isTrue(ArrayUtils.contains(AdminSettings.ALLOWED_KEYLENGTH, keyLength));
        User currentUser = userService.getCurrentUser();
        List<Password> oldPasswords = ofy.load().type(Password.class).filter("userId", currentUser.getUserId()).list();
        Map<Long, Password> oldPasswordIdMap = new HashMap<>();
        for (Password password: oldPasswords) {
            oldPasswordIdMap.put(password.getId(), password);
        }
        isTrue(allPasswords.size() == oldPasswords.size());
        for (Password password: allPasswords) {
            Password storedPassword = oldPasswordIdMap.get(password.getId());
            notNull(storedPassword);
            isTrue(password.getId().equals(storedPassword.getId()));
            isTrue(StringUtils.equals(password.getUserId(), storedPassword.getUserId()));
            isTrue(StringUtils.equals(password.getDomain(), storedPassword.getDomain()));
        }
        RegisteredUser oldRegisteredUser = userController.getRegisteredUser();
        try {
            ofy.save().entity(new RegisteredUser(currentUser.getUserId(), masterPasswordHash,
                    masterPasswordHashAlgorithm, currentUser.getEmail(),
                    iterations, cipherAlgorithm, keyLength, pbkdf2Algorithm));
            ofy.save().entities(allPasswords);
        } catch (RuntimeException e) {
            ofy.save().entity(oldRegisteredUser);
            ofy.save().entities(oldPasswords);
            throw e;
        }
    }

    @RequestMapping("/retrieve")
    public List<Password> retrieveAllPasswords(@RequestParam String masterPasswordHash) {
        notEmpty(masterPasswordHash);
        RegisteredUser registeredUser = userController.getRegisteredUser();
        if (!StringUtils.equals(masterPasswordHash, registeredUser.getMasterPasswordHash())){
            throw new NotAuthorizedException();
        }
        List<Password> passwords = ofy.load().type(Password.class).filter("userId", registeredUser.getUserId()).order("domain").list();
        Collections.sort(passwords, new Comparator<Password>() {
            @Override
            public int compare(Password o1, Password o2) {
                return o1.getDomain().toLowerCase().compareTo(o2.getDomain().toLowerCase());
            }
        });
        return passwords;
    }

    public void removeAllPasswords() {
        String userId = userService.getCurrentUser().getUserId();
        QueryResultIterator<Password> passwords = ofy.load().type(Password.class).filter("userId", userId).iterator();
        ofy.delete().entities(passwords);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgumentException(Exception e) {
        LOG.log(Level.SEVERE, "BAD_REQUEST: " + e.getMessage(), e);
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleNullPointerException(Exception e) {
        LOG.log(Level.SEVERE, "BAD_REQUEST", e);
    }

    @ExceptionHandler(NotAuthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleNotAuthorizedException(Exception e) {
        LOG.log(Level.SEVERE, "UNAUTHORIZED: " + e.getMessage());
    }

    private Password getUserPassword(Long id) {
        Password password = ofy.load().type(Password.class).id(id).safe();
        isTrue(StringUtils.equals(password.getUserId(), userService.getCurrentUser().getUserId()));
        return password;
    }
}
