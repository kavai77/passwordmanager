package net.himadri.passwordmanager.service;

import net.himadri.passwordmanager.entity.EncodedUserId;
import net.himadri.passwordmanager.entity.RegisteredUser;
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
        List<EncodedUserId> list = ofy().load().type(EncodedUserId.class).list();
        for (EncodedUserId encodedUserId: list) {
            ofy().save().entity(new RegisteredUser(encodedUserId.getUserId(), encodedUserId.getMasterPasswordMd5Hash(), encodedUserId.getEmail(), encodedUserId.getIterations()));
        }
    }
}
