package net.himadri.passwordmanager.service;

import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import net.himadri.passwordmanager.dto.BackupData;
import net.himadri.passwordmanager.entity.Backup;
import net.himadri.passwordmanager.entity.BackupItem;
import net.himadri.passwordmanager.entity.Password;
import net.himadri.passwordmanager.entity.RegisteredUser;
import org.apache.commons.lang3.StringUtils;
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

import static net.himadri.passwordmanager.App.X_AUTHORIZATION_FIREBASE;
import static org.apache.commons.lang3.Validate.isTrue;

@RestController
@RequestMapping(value = "/secure/backup")
@RequiredArgsConstructor
public class BackupController {
    private static final Logger LOG = Logger.getLogger(BackupController.class.getName());

    private final UserController userController;
    private final PasswordController passwordController;
    private final ExternalService externalService;
    
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public BackupData createBackup(@RequestHeader(X_AUTHORIZATION_FIREBASE) String firebaseToken) throws FirebaseAuthException {
        RegisteredUser user = userController.getRegisteredUser(firebaseToken);
        var backup = Backup.builder()
                .userId(user.getUserId())
                .masterPasswordHash(user.getMasterPasswordHash())
                .iterations(user.getIterations())
                .cipherAlgorithm(user.getCipherAlgorithm())
                .keyLength(user.getKeyLength())
                .pbkdf2Algorithm(user.getPbkdf2Algorithm())
                .masterPasswordHashAlgorithm(user.getMasterPasswordHashAlgorithm())
                .backupDate(new Date())
                .build();
        externalService.ofy().save().entity(backup).now();

        List<Password> allPasswords = externalService.ofy().load().type(Password.class).filter("userId", user.getUserId()).list();
        for (Password password: allPasswords) {
            externalService.ofy().save().entity(BackupItem.builder()
                    .backupId(backup.getId())
                    .domain(password.getDomain())
                    .userName(password.getUserName())
                    .hex(password.getHex())
                    .iv(password.getIv())
                    .created(password.getCreated())
                    .modified(password.getModified())
                    .build());
        }
        return BackupData.builder()
                .id(backup.getId())
                .backupDate(backup.getBackupDate().getTime())
                .numberOfPasswords(allPasswords.size())
                .masterPasswordHashAlgorithm(backup.getMasterPasswordHashAlgorithm())
                .build();
    }


    @RequestMapping(value = "/restore", method = RequestMethod.POST)
    public void restoreBackup(
        @RequestParam final Long id,
        @RequestParam final String masterPasswordHash,
        @RequestHeader(X_AUTHORIZATION_FIREBASE) String firebaseToken
    ) throws FirebaseAuthException {
        String userId = externalService.firebaseAuth().verifyIdToken(firebaseToken).getUid();
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
        String userId = externalService.firebaseAuth().verifyIdToken(firebaseToken).getUid();
        for (Backup backup: externalService.ofy().load().type(Backup.class).filter("userId", userId)) {
            int numberOfItems = externalService.ofy().load().type(BackupItem.class).filter("backupId", backup.getId()).count();
            result.add(BackupData.builder()
                    .id(backup.getId())
                    .backupDate(backup.getBackupDate().getTime())
                    .numberOfPasswords(numberOfItems)
                    .masterPasswordHashAlgorithm(backup.getMasterPasswordHashAlgorithm())
                    .build());
        }
        result.sort(Comparator.comparing(BackupData::getBackupDate));
        Collections.reverse(result);
        return result;
    }

    @RequestMapping(value = "/remove", method = RequestMethod.DELETE)
    public void removeBackup(
        @RequestParam final Long id,
        @RequestHeader(X_AUTHORIZATION_FIREBASE) String firebaseToken
    ) throws FirebaseAuthException {
        String userId = externalService.firebaseAuth().verifyIdToken(firebaseToken).getUid();
        Backup backup = getUserBackup(userId, id);
        externalService.ofy().delete().entity(backup);
        List<BackupItem> backupItems = getAllBackupItems(id);
        externalService.ofy().delete().entities(backupItems);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgumentException(Exception e) {
        LOG.log(Level.SEVERE, "BAD_REQUEST: " + e.getMessage());
    }

    private void saveBackupPasswords(Long backupId, RegisteredUser user) {
        List<BackupItem> backupItems = getAllBackupItems(backupId);
        for (BackupItem backupItem: backupItems) {
            externalService.ofy().save().entity(Password.builder()
                    .userId(user.getUserId())
                    .domain(backupItem.getDomain())
                    .userName(backupItem.getUserName())
                    .hex(backupItem.getHex())
                    .iv(backupItem.getIv())
                    .created(backupItem.getCreated())
                    .modified(backupItem.getModified())
                    .build());
        }
    }

    private List<BackupItem> getAllBackupItems(Long backupId) {
        return externalService.ofy().load().type(BackupItem.class).filter("backupId", backupId).list();
    }

    private void updateUserWithBackup(Backup backup, RegisteredUser user) {
        user.setMasterPasswordHash(backup.getMasterPasswordHash());
        user.setCipherAlgorithm(backup.getCipherAlgorithm());
        user.setIterations(backup.getIterations());
        user.setKeyLength(backup.getKeyLength());
        user.setPbkdf2Algorithm(backup.getPbkdf2Algorithm());
        user.setMasterPasswordHashAlgorithm(backup.getMasterPasswordHashAlgorithm());
        externalService.ofy().save().entity(user);
    }

    private Backup getUserBackup(String userId, Long id) {
        Backup backup = externalService.ofy().load().type(Backup.class).id(id).safe();
        isTrue(StringUtils.equals(backup.getUserId(), userId));
        return backup;
    }
}
