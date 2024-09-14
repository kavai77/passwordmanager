package net.himadri.passwordmanager.service;

import com.google.cloud.datastore.QueryResults;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import net.himadri.passwordmanager.entity.AdminSettings;
import net.himadri.passwordmanager.entity.Password;
import net.himadri.passwordmanager.entity.RegisteredUser;
import net.himadri.passwordmanager.security.AuthenticationService;
import net.himadri.passwordmanager.service.exception.NotAuthorizedException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;

@RestController
@RequestMapping(value = "/secure/password")
@RequiredArgsConstructor
public class PasswordController {
    private static final Logger LOG = Logger.getLogger(PasswordController.class.getName());

    private final UserController userController;
    private final DateService dateService;
    private final DatabaseService databaseService;
    private final AuthenticationService authenticationService;

    @RequestMapping(value = "/store", method = RequestMethod.POST)
    @ResponseBody
    public Password store(@RequestParam String domain,
                          @RequestParam String userName,
                          @RequestParam String hex,
                          @RequestParam String iv
    ) {
        checkArgument(isNotBlank(domain));
        checkArgument(isNotBlank(hex));
        checkArgument(isNotBlank(iv));
        String userId = authenticationService.getUid();
        Password password = Password.builder()
                .userId(userId)
                .domain(domain)
                .userName(userName)
                .hex(hex)
                .iv(iv)
                .created(dateService.currentDate())
                .modified(dateService.currentDate())
                .build();
        databaseService.ofy().save().entity(password).now();
        return password;
    }

    @RequestMapping(value = "/changeDomain", method = RequestMethod.POST)
    public Password changeDomain(
            @RequestParam Long id,
            @RequestParam String domain
    ) {
        checkArgument(isNotBlank(domain));
        checkNotNull(id);
        Password password = getUserPassword(id);
        password.setDomain(domain);
        databaseService.ofy().save().entity(password).now();
        return password;
    }

    @RequestMapping(value = "/changeUserName", method = RequestMethod.POST)
    public Password changeUserName(
            @RequestParam Long id,
            @RequestParam String userName
    ) {
        checkNotNull(id);
        Password password = getUserPassword(id);
        password.setUserName(userName);
        databaseService.ofy().save().entity(password).now();
        return password;
    }

    @RequestMapping(value = "/changeHex", method = RequestMethod.POST)
    public Password changeHex(
            @RequestParam Long id,
            @RequestParam String hex,
            @RequestParam String iv
    ) {
        checkArgument(isNotBlank(hex));
        checkNotNull(id);
        Password password = getUserPassword(id);
        password.setHex(hex);
        password.setIv(iv);
        password.setModified(dateService.currentDate());
        databaseService.ofy().save().entity(password).now();
        return password;
    }

    @RequestMapping(value = "/deletePassword", method = RequestMethod.POST)
    public void deletePassword(@RequestParam Long id) {
        checkNotNull(id);
        Password password = getUserPassword(id);
        databaseService.ofy().delete().entity(password).now();
    }

    @RequestMapping(value = "/changeAllHex", method = RequestMethod.POST)
    public void changeAllHex(@RequestParam final String masterPasswordHash,
                             @RequestParam final String masterPasswordHashAlgorithm,
                             @RequestParam final int iterations,
                             @RequestParam String cipherAlgorithm,
                             @RequestParam int keyLength,
                             @RequestParam String pbkdf2Algorithm,
                             @RequestBody final List<Password> allPasswords
    ) {
        checkArgument(isNotBlank(masterPasswordHash));
        checkArgument(isNotBlank(masterPasswordHashAlgorithm));
        isTrue(iterations > 0);
        checkNotNull(allPasswords);
        isTrue(StringUtils.equals(cipherAlgorithm, AdminSettings.CIPHER_ALGORITHM));
        isTrue(ArrayUtils.contains(AdminSettings.ALLOWED_KEYLENGTH, keyLength));
        FirebaseToken token = authenticationService.getFirebaseToken();
        String userId = token.getUid();
        List<Password> oldPasswords = databaseService.ofy().load().type(Password.class).filter("userId", userId).list();
        Map<Long, Password> oldPasswordIdMap = new HashMap<>();
        for (Password password : oldPasswords) {
            oldPasswordIdMap.put(password.getId(), password);
        }
        isTrue(allPasswords.size() == oldPasswords.size());
        for (Password password : allPasswords) {
            Password storedPassword = oldPasswordIdMap.get(password.getId());
            checkNotNull(storedPassword);
            isTrue(password.getId().equals(storedPassword.getId()));
            isTrue(StringUtils.equals(password.getUserId(), storedPassword.getUserId()));
            isTrue(StringUtils.equals(password.getDomain(), storedPassword.getDomain()));
        }
        RegisteredUser oldRegisteredUser = userController.getRegisteredUser();
        try {
            databaseService.ofy().save().entity(new RegisteredUser(userId, masterPasswordHash,
                    masterPasswordHashAlgorithm, token.getEmail(),
                    iterations, cipherAlgorithm, keyLength, pbkdf2Algorithm, oldRegisteredUser.getSalt()));
            databaseService.ofy().save().entities(allPasswords);
        } catch (RuntimeException e) {
            databaseService.ofy().save().entity(oldRegisteredUser);
            databaseService.ofy().save().entities(oldPasswords);
            throw e;
        }
    }

    @RequestMapping("/retrieve")
    public List<Password> retrieveAllPasswords(@RequestParam String masterPasswordHash) {
        notEmpty(masterPasswordHash);
        RegisteredUser registeredUser = userController.getRegisteredUser();
        if (!StringUtils.equals(masterPasswordHash, registeredUser.getMasterPasswordHash())) {
            throw new NotAuthorizedException();
        }
        List<Password> passwords = databaseService.ofy().load().type(Password.class).filter("userId", registeredUser.getUserId()).order("domain").list();
        passwords.sort(Comparator.comparing(o -> o.getDomain().toLowerCase()));
        return passwords;
    }

    public void removeAllPasswords(String userId) {
        QueryResults<Password> passwords = databaseService.ofy().load().type(Password.class).filter("userId", userId).iterator();
        databaseService.ofy().delete().entities(passwords);
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
        Password password = databaseService.ofy().load().type(Password.class).id(id).safe();
        String userId = authenticationService.getUid();
        isTrue(StringUtils.equals(password.getUserId(), userId));
        return password;
    }
}
