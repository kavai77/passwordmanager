package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.UserServiceFactory;
import net.himadri.passwordmanager.entity.Password;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

@RestController
public class RetrieveController {

    @RequestMapping("/retrieve")
    public List<Password> retrieve() {
        String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
        return ofy().load().type(Password.class).filter("userId", userId).order("domain").list();
    }

}