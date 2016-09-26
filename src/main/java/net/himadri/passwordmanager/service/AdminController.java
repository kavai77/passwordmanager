package net.himadri.passwordmanager.service;

import net.himadri.passwordmanager.entity.RegisteredUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {
    private static final Logger LOG = Logger.getLogger(AdminController.class.getName());

    @RequestMapping("/refactor")
    public void refactor() {
        for (RegisteredUser user : ofy().load().type(RegisteredUser.class)) {
            if (user.getPbkdf2Algorithm() == null) {
                user.setPbkdf2Algorithm("sha1");
                ofy().save().entity(user);
            }
        }
    }
}
