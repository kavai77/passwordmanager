package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.UserServiceFactory;
import net.himadri.passwordmanager.entity.Password;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.googlecode.objectify.ObjectifyService.ofy;

@RestController
public class StoreController {

    @RequestMapping(value = "/store", method = RequestMethod.POST)
    public Password store(@RequestParam String domain, @RequestParam String hex)  {
        if(StringUtils.isEmpty(domain) || StringUtils.isEmpty(hex)) {
            throw new IllegalArgumentException();
        }
        String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
        Password dataEntity = new Password(userId, domain, hex);
        ofy().save().entities(dataEntity).now();
        return dataEntity;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleException(Exception e) {
        // nothing to do
    }
}
