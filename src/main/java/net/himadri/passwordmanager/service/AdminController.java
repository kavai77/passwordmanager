package net.himadri.passwordmanager.service;

import net.himadri.passwordmanager.entity.RegisteredUser;
import net.himadri.passwordmanager.entity.Settings;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {
    private static final Logger LOG = Logger.getLogger(AdminController.class.getName());

    @RequestMapping("/refactor")
    public void refactor() {
        List<RegisteredUser> registeredUserList = ofy().load().type(RegisteredUser.class).list();
        for (RegisteredUser user: registeredUserList) {
            boolean modified = false;
            if (user.getCipherAlgorithm() == null) {
                user.setCipherAlgorithm(Settings.CIPHER_ALGORITHM);
                modified = true;
            }
            if (user.getKeyLength() == 0) {
                user.setKeyLength(256);
                modified = true;
            }
            if (modified) {
                ofy().save().entity(user);
            }
        }
    }
}
