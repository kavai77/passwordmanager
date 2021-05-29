package net.himadri.passwordmanager.service;

import com.google.firebase.auth.FirebaseAuth;
import com.googlecode.objectify.Objectify;
import org.springframework.stereotype.Component;

@Component
public class ExternalService {
    public Objectify ofy() {
        return com.googlecode.objectify.ObjectifyService.ofy();
    }

    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }
}
