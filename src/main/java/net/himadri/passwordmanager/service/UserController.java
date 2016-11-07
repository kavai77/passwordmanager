package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.googlecode.objectify.Objectify;
import net.himadri.passwordmanager.dto.UserData;
import net.himadri.passwordmanager.entity.RegisteredUser;
import net.himadri.passwordmanager.entity.UserSettings;
import net.himadri.passwordmanager.service.exception.NotAuthorizedException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import static net.himadri.passwordmanager.entity.AdminSettings.ALLOWED_KEYLENGTH;
import static net.himadri.passwordmanager.entity.AdminSettings.CIPHER_ALGORITHM;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;

@RestController
@RequestMapping(value = "/secure/user")
public class UserController {
    private static final Logger LOG = Logger.getLogger(UserController.class.getName());

    @Autowired
    Objectify ofy;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestParam String masterPasswordHash,
                         @RequestParam String masterPasswordHashAlgorithm,
                         @RequestParam int iterations,
                         @RequestParam String cipherAlgorithm,
                         @RequestParam int keyLength,
                         @RequestParam String pbkdf2Algorithm) {
        notEmpty(masterPasswordHash);
        notEmpty(masterPasswordHashAlgorithm);
        isTrue(StringUtils.equals(cipherAlgorithm, CIPHER_ALGORITHM));
        isTrue(ArrayUtils.contains(ALLOWED_KEYLENGTH, keyLength));
        User currentUser = userService.getCurrentUser();
        isTrue(ofy.load().type(RegisteredUser.class).id(currentUser.getUserId()).now() == null);
        ofy.save().entity(new RegisteredUser(currentUser.getUserId(), masterPasswordHash, masterPasswordHashAlgorithm, currentUser.getEmail(),
                iterations, cipherAlgorithm, keyLength, pbkdf2Algorithm)).now();
    }

    @RequestMapping("/checkMasterPasswordHash")
    public void checkMasterPasswordHash(String masterPasswordHash) {
        notEmpty(masterPasswordHash);
        User currentUser = userService.getCurrentUser();
        RegisteredUser userId = ofy.load().type(RegisteredUser.class).id(currentUser.getUserId()).safe();
        if (!StringUtils.equals(masterPasswordHash, userId.getMasterPasswordHash())){
            throw new NotAuthorizedException();
        }
    }

    @RequestMapping(value = "/userSettings", method = RequestMethod.POST)
    public void updateUserSettings(@RequestBody UserData.UserSettingsData userSettingsData) {
        RegisteredUser registeredUser = getRegisteredUser();
        UserSettings userSettings = new UserSettings(registeredUser.getUserId(),
                userSettingsData.getDefaultPasswordLength(),
                userSettingsData.getTimeoutLengthSeconds());
        ofy.save().entity(userSettings);

    }

    public RegisteredUser getRegisteredUser() {
        User currentUser = userService.getCurrentUser();
        return ofy.load().type(RegisteredUser.class).id(currentUser.getUserId()).safe();
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
