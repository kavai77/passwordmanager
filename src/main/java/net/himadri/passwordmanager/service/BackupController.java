package net.himadri.passwordmanager.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import net.himadri.passwordmanager.dto.BackupData;
import net.himadri.passwordmanager.entity.Backup;
import net.himadri.passwordmanager.entity.BackupItem;
import net.himadri.passwordmanager.entity.Password;
import net.himadri.passwordmanager.entity.RegisteredUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static net.himadri.passwordmanager.App.X_AUTHORIZATION_FIREBASE;
import static org.apache.commons.lang3.Validate.isTrue;

@RestController
@RequestMapping(value = "/secure/backup")
public class BackupController {
    private static final Logger LOG = Logger.getLogger(BackupController.class.getName());

    @Autowired
    UserController userController;

    @Autowired
    PasswordController passwordController;
    
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public BackupData createBackup(@RequestHeader(X_AUTHORIZATION_FIREBASE) String firebaseToken) throws FirebaseAuthException {
        RegisteredUser user = userController.getRegisteredUser(firebaseToken);
        Backup backup = new Backup(user.getUserId(), user.getMasterPasswordHash(),
                user.getIterations(), user.getCipherAlgorithm(), user.getKeyLength(),
                user.getPbkdf2Algorithm(), user.getMasterPasswordHashAlgorithm(), new Date());
        ofy().save().entity(backup).now();

        List<Password> allPasswords = ofy().load().type(Password.class).filter("userId", user.getUserId()).list();
        for (Password password: allPasswords) {
            ofy().save().entity(new BackupItem(backup.getId(), password.getDomain(), password.getUserName(), password.getHex(), password.getIv()));
        }
        return new BackupData(backup.getId(), backup.getBackupDate().getTime(), allPasswords.size(), backup.getMasterPasswordHashAlgorithm());
    }


    @RequestMapping(value = "/restore", method = RequestMethod.POST)
    public void restoreBackup(
        @RequestParam final Long id,
        @RequestParam final String masterPasswordHash,
        @RequestHeader(X_AUTHORIZATION_FIREBASE) String firebaseToken
    ) throws FirebaseAuthException {
        String userId = FirebaseAuth.getInstance().verifyIdToken(firebaseToken).getUid();
        Backup backup = getUserBackup(userId, id);
        isTrue(StringUtils.endsWith(backup.getMasterPasswordHash(), masterPasswordHash));
        passwordController.removeAllPasswords(userId);
        RegisteredUser user = userController.getRegisteredUser(firebaseToken);
        updateUserWithBackup(backup, user);
        saveBackupPasswords(id, user);
    }

    @RequestMapping("/retrieve")
    public List<BackupData> retrieveAllBackups(
        @RequestHeader(X_AUTHORIZATION_FIREBASE) String firebaseToken
    ) throws FirebaseAuthException {
        List<BackupData> result = new ArrayList<>();
        String userId = FirebaseAuth.getInstance().verifyIdToken(firebaseToken).getUid();
        for (Backup backup: ofy().load().type(Backup.class).filter("userId", userId)) {
            int numberOfItems = ofy().load().type(BackupItem.class).filter("backupId", backup.getId()).count();
            result.add(new BackupData(backup.getId(), backup.getBackupDate().getTime(), numberOfItems, backup.getMasterPasswordHashAlgorithm()));
        }
        Collections.sort(result, new Comparator<BackupData>() {
            @Override
            public int compare(BackupData o1, BackupData o2) {
                return o1.getBackupDate().compareTo(o2.getBackupDate());
            }
        });
        Collections.reverse(result);
        return result;
    }

    @RequestMapping(value = "/remove", method = RequestMethod.DELETE)
    public void removeBackup(
        @RequestParam final Long id,
        @RequestHeader(X_AUTHORIZATION_FIREBASE) String firebaseToken
    ) throws FirebaseAuthException {
        String userId = FirebaseAuth.getInstance().verifyIdToken(firebaseToken).getUid();
        Backup backup = getUserBackup(userId, id);
        ofy().delete().entity(backup);
        List<BackupItem> backupItems = getAllBackupItems(id);
        ofy().delete().entities(backupItems);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgumentException(Exception e) {
        LOG.log(Level.SEVERE, "BAD_REQUEST: " + e.getMessage());
    }

    private void saveBackupPasswords(Long backupId, RegisteredUser user) {
        List<BackupItem> backupItems = getAllBackupItems(backupId);
        for (BackupItem backupItem: backupItems) {
            ofy().save().entity(new Password(user.getUserId(), backupItem.getDomain(), backupItem.getUserName(), backupItem.getHex(), backupItem.getIv()));
        }
    }

    private List<BackupItem> getAllBackupItems(Long backupId) {
        return ofy().load().type(BackupItem.class).filter("backupId", backupId).list();
    }

    private void updateUserWithBackup(Backup backup, RegisteredUser user) {
        user.setMasterPasswordHash(backup.getMasterPasswordHash());
        user.setCipherAlgorithm(backup.getCipherAlgorithm());
        user.setIterations(backup.getIterations());
        user.setKeyLength(backup.getKeyLength());
        user.setPbkdf2Algorithm(backup.getPbkdf2Algorithm());
        user.setMasterPasswordHashAlgorithm(backup.getMasterPasswordHashAlgorithm());
        ofy().save().entity(user);
    }

    private Backup getUserBackup(String userId, Long id) {
        Backup backup = ofy().load().type(Backup.class).id(id).safe();
        isTrue(StringUtils.equals(backup.getUserId(), userId));
        return backup;
    }
}
