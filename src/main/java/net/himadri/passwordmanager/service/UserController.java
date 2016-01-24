package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import net.himadri.passwordmanager.entity.AccessLog;
import net.himadri.passwordmanager.entity.EncodedUserId;
import net.himadri.passwordmanager.entity.UserData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import static com.googlecode.objectify.ObjectifyService.ofy;

@RestController
public class UserController{

    @RequestMapping("/userService")
    public UserData userService() {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        ofy().save().entity(new AccessLog(user.getUserId(), user.getEmail(), new Date()));
        EncodedUserId encodedUserId = ofy().load().type(EncodedUserId.class).id(user.getUserId()).now();
        return new UserData(user.getUserId(), user.getNickname(), userService.createLogoutURL("/index.html"), encodedUserId != null);
    }

}
