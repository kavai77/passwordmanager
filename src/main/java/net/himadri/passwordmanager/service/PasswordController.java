package net.himadri.passwordmanager.service;

import com.google.cloud.datastore.QueryResults;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import net.himadri.passwordmanager.entity.AdminSettings;
import net.himadri.passwordmanager.entity.Password;
import net.himadri.passwordmanager.entity.RegisteredUser;
import net.himadri.passwordmanager.service.exception.NotAuthorizedException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.googlecode.objectify.ObjectifyService.ofy;
import static net.himadri.passwordmanager.App.X_AUTHORIZATION_FIREBASE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;

@RestController
@RequestMapping(value = "/secure/password")
public class PasswordController {
    private static final Logger LOG = Logger.getLogger(PasswordController.class.getName());

    @Autowired
    UserController userController;

  @RequestMapping(value = "/store", method = RequestMethod.POST)
    public Password store(@RequestParam String domain, 
                          @RequestParam String userName,
                          @RequestParam String hex, 
                          @RequestParam String iv,
                          @RequestHeader(X_AUTHORIZATION_FIREBASE) String firebaseToken
  ) throws FirebaseAuthException {
        checkArgument(isNotBlank(domain));
        checkArgument(isNotBlank(hex));
        checkArgument(isNotBlank(iv));
        String userId = FirebaseAuth.getInstance().verifyIdToken(firebaseToken).getUid();
        Password password = new Password(userId, domain, userName, hex, iv);
        ofy().save().entity(password).now();
        return password;
    }

    @RequestMapping(value = "/changeDomain", method = RequestMethod.POST)
    public Password changeDomain(
        @RequestParam Long id,
        @RequestParam String domain,
        @RequestHeader(X_AUTHORIZATION_FIREBASE) String firebaseToken
    ) throws FirebaseAuthException {
        checkArgument(isNotBlank(domain));
        checkNotNull(id);
        Password password = getUserPassword(id, firebaseToken);
        password.setDomain(domain);
        ofy().save().entity(password).now();
        return password;
    }

    @RequestMapping(value = "/changeUserName", method = RequestMethod.POST)
    public Password changeUserName(
        @RequestParam Long id,
        @RequestParam String userName,
        @RequestHeader(X_AUTHORIZATION_FIREBASE) String firebaseToken
    ) throws FirebaseAuthException {
        checkNotNull(id);
        Password password = getUserPassword(id, firebaseToken);
        password.setUserName(userName);
        ofy().save().entity(password).now();
        return password;
    }

    @RequestMapping(value = "/changeHex", method = RequestMethod.POST)
    public Password changeHex(
        @RequestParam Long id,
        @RequestParam String hex,
        @RequestParam String iv,
        @RequestHeader(X_AUTHORIZATION_FIREBASE) String firebaseToken
    ) throws FirebaseAuthException {
        checkArgument(isNotBlank(hex));
        checkNotNull(id);
        Password password = getUserPassword(id, firebaseToken);
        password.setHex(hex);
        password.setIv(iv);
        ofy().save().entity(password).now();
        return password;
    }

    @RequestMapping(value = "/deletePassword", method = RequestMethod.POST)
    public void deletePassword(
        @RequestParam Long id,
        @RequestHeader(X_AUTHORIZATION_FIREBASE) String firebaseToken
    ) throws FirebaseAuthException {
        checkNotNull(id);
        Password password = getUserPassword(id, firebaseToken);
        ofy().delete().entity(password).now();
    }

    @RequestMapping(value = "/changeAllHex", method = RequestMethod.POST)
    public void changeAllHex(@RequestParam final String masterPasswordHash,
                             @RequestParam final String masterPasswordHashAlgorithm,
                             @RequestParam final int iterations,
                             @RequestParam String cipherAlgorithm,
                             @RequestParam int keyLength,
                             @RequestParam String pbkdf2Algorithm,
                             @RequestBody final List<Password> allPasswords,
                             @RequestHeader(X_AUTHORIZATION_FIREBASE) String firebaseToken
    ) throws FirebaseAuthException {
        checkArgument(isNotBlank(masterPasswordHash));
        checkArgument(isNotBlank(masterPasswordHashAlgorithm));
        isTrue(iterations > 0);
        checkNotNull(allPasswords);
        isTrue(StringUtils.equals(cipherAlgorithm, AdminSettings.CIPHER_ALGORITHM));
        isTrue(ArrayUtils.contains(AdminSettings.ALLOWED_KEYLENGTH, keyLength));
        FirebaseToken token = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);
        String userId = token.getUid();
        List<Password> oldPasswords = ofy().load().type(Password.class).filter("userId", userId).list();
        Map<Long, Password> oldPasswordIdMap = new HashMap<>();
        for (Password password: oldPasswords) {
            oldPasswordIdMap.put(password.getId(), password);
        }
        isTrue(allPasswords.size() == oldPasswords.size());
        for (Password password: allPasswords) {
            Password storedPassword = oldPasswordIdMap.get(password.getId());
            checkNotNull(storedPassword);
            isTrue(password.getId().equals(storedPassword.getId()));
            isTrue(StringUtils.equals(password.getUserId(), storedPassword.getUserId()));
            isTrue(StringUtils.equals(password.getDomain(), storedPassword.getDomain()));
        }
        RegisteredUser oldRegisteredUser = userController.getRegisteredUser(firebaseToken);
        try {
            ofy().save().entity(new RegisteredUser(userId, masterPasswordHash,
                    masterPasswordHashAlgorithm, token.getEmail(),
                    iterations, cipherAlgorithm, keyLength, pbkdf2Algorithm, oldRegisteredUser.getSalt()));
            ofy().save().entities(allPasswords);
        } catch (RuntimeException e) {
            ofy().save().entity(oldRegisteredUser);
            ofy().save().entities(oldPasswords);
            throw e;
        }
    }

    @RequestMapping("/retrieve")
    public List<Password> retrieveAllPasswords(
        @RequestParam String masterPasswordHash,
        @RequestHeader(X_AUTHORIZATION_FIREBASE) String firebaseToken
    ) throws FirebaseAuthException {
        notEmpty(masterPasswordHash);
        RegisteredUser registeredUser = userController.getRegisteredUser(firebaseToken);
        if (!StringUtils.equals(masterPasswordHash, registeredUser.getMasterPasswordHash())){
            throw new NotAuthorizedException();
        }
        List<Password> passwords = ofy().load().type(Password.class).filter("userId", registeredUser.getUserId()).order("domain").list();
        Collections.sort(passwords, new Comparator<Password>() {
            @Override
            public int compare(Password o1, Password o2) {
                return o1.getDomain().toLowerCase().compareTo(o2.getDomain().toLowerCase());
            }
        });
        return passwords;
    }

    public void removeAllPasswords(String userId) {
        QueryResults<Password> passwords = ofy().load().type(Password.class).filter("userId", userId).iterator();
        ofy().delete().entities(passwords);
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

    private Password getUserPassword(Long id, String firebaseToken) throws FirebaseAuthException {
        Password password = ofy().load().type(Password.class).id(id).safe();
        String userId = FirebaseAuth.getInstance().verifyIdToken(firebaseToken).getUid();
        isTrue(StringUtils.equals(password.getUserId(), userId));
        return password;
    }
}
