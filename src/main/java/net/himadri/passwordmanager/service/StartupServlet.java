package net.himadri.passwordmanager.service;

import com.googlecode.objectify.ObjectifyService;
import net.himadri.passwordmanager.entity.AccessLog;
import net.himadri.passwordmanager.entity.Password;
import net.himadri.passwordmanager.entity.RegisteredUser;

import javax.servlet.http.HttpServlet;

public class StartupServlet extends HttpServlet {
    static {
        ObjectifyService.register(AccessLog.class);
        ObjectifyService.register(Password.class);
        ObjectifyService.register(RegisteredUser.class);
    }
}
