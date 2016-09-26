package net.himadri.passwordmanager.service;

import com.googlecode.objectify.ObjectifyService;
import net.himadri.passwordmanager.entity.*;

import javax.servlet.http.HttpServlet;

public class StartupServlet extends HttpServlet {
    static {
        ObjectifyService.register(AccessLog.class);
        ObjectifyService.register(Password.class);
        ObjectifyService.register(RegisteredUser.class);
        ObjectifyService.register(Backup.class);
        ObjectifyService.register(BackupItem.class);
    }
}
