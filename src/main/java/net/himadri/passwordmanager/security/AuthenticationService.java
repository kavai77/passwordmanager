package net.himadri.passwordmanager.security;

import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService {
    public FirebaseToken getFirebaseToken() {
        return (FirebaseToken) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public String getUid() {
        return getFirebaseToken().getUid();
    }
}
