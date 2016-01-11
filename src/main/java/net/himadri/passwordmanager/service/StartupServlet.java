package net.himadri.passwordmanager.service;

import com.googlecode.objectify.ObjectifyService;
import net.himadri.passwordmanager.entity.AccessLog;
import net.himadri.passwordmanager.entity.DataEntity;
import net.himadri.passwordmanager.entity.EncodedUserId;

import javax.servlet.http.HttpServlet;

public class StartupServlet extends HttpServlet {
    static {
        ObjectifyService.register(DataEntity.class);
        ObjectifyService.register(EncodedUserId.class);
        ObjectifyService.register(AccessLog.class);
    }
}
