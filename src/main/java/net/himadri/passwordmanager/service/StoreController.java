package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.VoidWork;
import net.himadri.passwordmanager.entity.Password;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.springframework.util.Assert.*;

@RestController
public class StoreController {
    @Autowired RetrieveController retrieveController;
    @Autowired EncodedUserIdController encodedUserIdController;

    @RequestMapping(value = "/store", method = RequestMethod.POST)
    public Password store(@RequestParam String domain, @RequestParam String hex, @RequestParam String iv)  {
        hasLength(domain);
        hasLength(hex);
        hasLength(iv);
        String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
        Password dataEntity = new Password(userId, domain, hex, iv);
        ofy().save().entity(dataEntity).now();
        return dataEntity;
    }

    @RequestMapping(value = "/changeDomain", method = RequestMethod.POST)
    public Password changeDomain(@RequestParam Long id, @RequestParam String domain)  {
        hasLength(domain);
        notNull(id);
        Password password = getUserPassword(id);
        password.setDomain(domain);
        ofy().save().entity(password).now();
        return password;
    }

    @RequestMapping(value = "/changeHex", method = RequestMethod.POST)
    public Password changeHex(@RequestParam Long id, @RequestParam String hex, @RequestParam String iv)  {
        hasLength(hex);
        notNull(id);
        Password password = getUserPassword(id);
        password.setHex(hex);
        password.setIv(iv);
        ofy().save().entity(password).now();
        return password;
    }

    @RequestMapping(value = "/deletePassword", method = RequestMethod.POST)
    public void deletePassword(@RequestParam Long id) {
        notNull(id);
        Password password = getUserPassword(id);
        ofy().delete().entity(password).now();
    }

    @RequestMapping(value = "/changeAllHex", method = RequestMethod.POST)
    public void changeAllHex(@RequestParam(value = "md5Hash") final String masterPasswordMd5Hash,
                             @RequestParam final int iterations,
                             @RequestBody final List<Password> allPasswords) {
        hasText(masterPasswordMd5Hash);
        isTrue(iterations > 0);
        notNull(allPasswords);
        for (Password password: allPasswords) {
            Password storedPassword = getUserPassword(password.getId());
            isTrue(StringUtils.equals(password.getDomain(), storedPassword.getDomain()));
        }
        isTrue(allPasswords.size() == retrieveController.getPasswordCount());
        ofy().transact(new VoidWork() {
            @Override
            public void vrun() {
                encodedUserIdController.store(masterPasswordMd5Hash, iterations);
                ofy().save().entities(allPasswords).now();
            }
        });

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

    private Password getUserPassword(Long id) {
        Password password = ofy().load().type(Password.class).id(id).safe();
        isTrue(StringUtils.equals(password.getUserId(), UserServiceFactory.getUserService().getCurrentUser().getUserId()));
        return password;
    }
}
