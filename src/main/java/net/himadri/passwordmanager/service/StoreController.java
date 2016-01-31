package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.NotFoundException;
import net.himadri.passwordmanager.entity.Password;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.springframework.util.Assert.*;

@RestController
public class StoreController {

    @RequestMapping(value = "/store", method = RequestMethod.POST)
    public Password store(@RequestParam String domain, @RequestParam String hex)  {
        hasLength(domain);
        hasLength(hex);
        String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
        Password dataEntity = new Password(userId, domain, hex);
        ofy().save().entity(dataEntity).now();
        return dataEntity;
    }

    @RequestMapping(value = "/changeDomain", method = RequestMethod.POST)
    public Password changeDomain(@RequestParam Long id, @RequestParam String domain)  {
        hasLength(domain);
        notNull(id);
        Password password = getSafePassword(id);
        password.setDomain(domain);
        ofy().save().entity(password).now();
        return password;
    }

    @RequestMapping(value = "/changeHex", method = RequestMethod.POST)
    public Password changeHex(@RequestParam Long id, @RequestParam String hex)  {
        hasLength(hex);
        notNull(id);
        Password password = getSafePassword(id);
        password.setHex(hex);
        ofy().save().entity(password).now();
        return password;
    }

    @RequestMapping(value = "/deletePassword", method = RequestMethod.POST)
    public void deletePassword(@RequestParam Long id) {
        notNull(id);
        Password password = getSafePassword(id);
        ofy().delete().entity(password).now();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleIllegalArgumentException(Exception e) {
        // nothing to do
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleNullPointerException(Exception e) {
        // nothing to do
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleNotFoundException(Exception e) {
        // nothing to do
    }

    private Password getSafePassword(Long id) {
        Password password = ofy().load().type(Password.class).id(id).safe();
        isTrue(StringUtils.equals(password.getUserId(), UserServiceFactory.getUserService().getCurrentUser().getUserId()));
        return password;
    }
}
