package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.UserServiceFactory;
import net.himadri.passwordmanager.entity.Password;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

@RestController
public class RetrieveController {

    @RequestMapping("/retrieve")
    public List<Password> retrieve() {
        String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
        List<Password> passwords = ofy().load().type(Password.class).filter("userId", userId).order("domain").list();
        Collections.sort(passwords, new Comparator<Password>() {
            @Override
            public int compare(Password o1, Password o2) {
                return o1.getDomain().toLowerCase().compareTo(o2.getDomain().toLowerCase());
            }
        });
        return passwords;
    }
}