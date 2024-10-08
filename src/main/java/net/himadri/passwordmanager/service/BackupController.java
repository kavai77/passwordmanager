package net.himadri.passwordmanager.service;

import lombok.RequiredArgsConstructor;
import net.himadri.passwordmanager.dto.BackupData;
import net.himadri.passwordmanager.entity.Backup;
import net.himadri.passwordmanager.entity.BackupItem;
import net.himadri.passwordmanager.entity.Password;
import net.himadri.passwordmanager.entity.RegisteredUser;
import net.himadri.passwordmanager.security.AuthenticationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

import static org.apache.commons.lang3.Validate.isTrue;

@RestController
@RequestMapping(value = "/secure/backup")
@RequiredArgsConstructor
public class BackupController {
    private static final Logger LOG = Logger.getLogger(BackupController.class.getName());

    private final UserController userController;
    private final PasswordController passwordController;
    private final DatabaseService databaseService;
    private final AuthenticationService authenticationService;
    
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public BackupData createBackup() {
        RegisteredUser user = userController.getRegisteredUser();
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
        databaseService.ofy().save().entity(backup).now();

        List<Password> allPasswords = databaseService.ofy().load().type(Password.class).filter("userId", user.getUserId()).list();
        for (Password password: allPasswords) {
            databaseService.ofy().save().entity(BackupItem.builder()
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
        @RequestParam final String masterPasswordHash
    ) {
        String userId = authenticationService.getUid();
        Backup backup = getUserBackup(userId, id);
        isTrue(StringUtils.endsWith(backup.getMasterPasswordHash(), masterPasswordHash));
        passwordController.removeAllPasswords(userId);
        RegisteredUser user = userController.getRegisteredUser();
        updateUserWithBackup(backup, user);
        saveBackupPasswords(id, user);
    }

    @RequestMapping("/retrieve")
    public List<BackupData> retrieveAllBackups() {
        List<BackupData> result = new ArrayList<>();
        String userId = authenticationService.getUid();
        for (Backup backup: databaseService.ofy().load().type(Backup.class).filter("userId", userId)) {
            int numberOfItems = databaseService.ofy().load().type(BackupItem.class).filter("backupId", backup.getId()).count();
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
    public void removeBackup(@RequestParam final Long id) {
        String userId = authenticationService.getUid();
        Backup backup = getUserBackup(userId, id);
        databaseService.ofy().delete().entity(backup);
        List<BackupItem> backupItems = getAllBackupItems(id);
        databaseService.ofy().delete().entities(backupItems);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgumentException(Exception e) {
        LOG.log(Level.SEVERE, "BAD_REQUEST: " + e.getMessage());
    }

    private void saveBackupPasswords(Long backupId, RegisteredUser user) {
        List<BackupItem> backupItems = getAllBackupItems(backupId);
        for (BackupItem backupItem: backupItems) {
            databaseService.ofy().save().entity(Password.builder()
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
        return databaseService.ofy().load().type(BackupItem.class).filter("backupId", backupId).list();
    }

    private void updateUserWithBackup(Backup backup, RegisteredUser user) {
        user.setMasterPasswordHash(backup.getMasterPasswordHash());
        user.setCipherAlgorithm(backup.getCipherAlgorithm());
        user.setIterations(backup.getIterations());
        user.setKeyLength(backup.getKeyLength());
        user.setPbkdf2Algorithm(backup.getPbkdf2Algorithm());
        user.setMasterPasswordHashAlgorithm(backup.getMasterPasswordHashAlgorithm());
        databaseService.ofy().save().entity(user);
    }

    private Backup getUserBackup(String userId, Long id) {
        Backup backup = databaseService.ofy().load().type(Backup.class).id(id).safe();
        isTrue(StringUtils.equals(backup.getUserId(), userId));
        return backup;
    }
}
