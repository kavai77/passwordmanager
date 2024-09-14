package net.himadri.passwordmanager.service;

import com.googlecode.objectify.Objectify;
import org.springframework.stereotype.Component;

@Component
public class DatabaseService {
    public Objectify ofy() {
        return com.googlecode.objectify.ObjectifyService.ofy();
    }
}
