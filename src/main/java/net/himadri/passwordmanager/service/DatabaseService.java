package net.himadri.passwordmanager.service;

import com.googlecode.objectify.Objectify;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class DatabaseService {
    public Objectify ofy() {
        return com.googlecode.objectify.ObjectifyService.ofy();
    }

    public String randomString(int length) {
        return RandomStringUtils.random(length);
    }
}
