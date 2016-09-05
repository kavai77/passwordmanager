package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.UserServiceFactory;
import net.himadri.passwordmanager.entity.Backup;
import net.himadri.passwordmanager.entity.BackupItem;
import net.himadri.passwordmanager.entity.Password;
import net.himadri.passwordmanager.entity.RegisteredUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static com.google.appengine.api.users.UserServiceFactory.getUserService;
import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.apache.commons.lang3.Validate.isTrue;

@RestController
@RequestMapping(value = "/secure/backup")
public class BackupController {
    private static final Logger LOG = Logger.getLogger(BackupController.class.getName());

    @Autowired
    UserController userController;

    @Autowired
    PasswordController passwordController;

    @RequestMapping("/create")
    public Backup createBackup() {
        RegisteredUser user = userController.getRegisteredUser();
        Backup backup = new Backup(user.getUserId(), user.getMasterPasswordHash(),
                user.getIterations(), user.getCipherAlgorithm(), user.getKeyLength(),
                new Date());
        ofy().save().entity(backup).now();

        List<Password> allPasswords = passwordController.retrieveAllPasswords();
        for (Password password: allPasswords) {
            ofy().save().entity(new BackupItem(backup.getId(), password.getDomain(), password.getHex(), password.getIv()));
        }
        return backup;
    }


    @RequestMapping("/restore")
    public void restoreBackup(@RequestParam final Long id) {
        Backup backup = getUserBackup(id);
        passwordController.removeOldPasswords();
        RegisteredUser user = userController.getRegisteredUser();
        updateUserWithBackup(backup, user);
        saveBackupPasswords(id, user);
    }

    @RequestMapping("/retrieve")
    public List<Backup> retrieveAllBackups() {
        String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
        return ofy().load().type(Backup.class).filter("userId", userId).order("backupDate").list();
    }

    @RequestMapping("/remove")
    public void removeBackup(@RequestParam final Long id) {
        Backup backup = getUserBackup(id);
        ofy().delete().entity(backup);
        List<BackupItem> backupItems = getAllBackupItems(id);
        ofy().delete().entities(backupItems);
    }

    private void saveBackupPasswords(Long backupId, RegisteredUser user) {
        List<BackupItem> backupItems = getAllBackupItems(backupId);
        for (BackupItem backupItem: backupItems) {
            ofy().save().entity(new Password(user.getUserId(), backupItem.getDomain(), backupItem.getHex(), backupItem.getIv()));
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
        ofy().save().entity(user);
    }

    private Backup getUserBackup(Long id) {
        Backup backup = ofy().load().type(Backup.class).id(id).safe();
        isTrue(StringUtils.equals(backup.getUserId(), getUserService().getCurrentUser().getUserId()));
        return backup;
    }
}
