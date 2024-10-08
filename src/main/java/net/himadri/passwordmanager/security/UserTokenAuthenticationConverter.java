package net.himadri.passwordmanager.security;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

import static net.himadri.passwordmanager.App.X_AUTHORIZATION_FIREBASE;

@Component
@RequiredArgsConstructor
public class UserTokenAuthenticationConverter implements AuthenticationConverter {
    private final AuthenticationService authenticationService;

    @Override
    public Authentication convert(HttpServletRequest request) {
        try {
            var firebaseToken = request.getHeader(X_AUTHORIZATION_FIREBASE);
            if (firebaseToken == null) {
                throw new AuthenticationCredentialsNotFoundException("Firebase token not found");
            }
            FirebaseToken token = authenticationService.parseFirebaseToken(firebaseToken);
            return new PreAuthenticatedAuthenticationToken(token, null, List.of(() -> "ROLE_USER"));
        } catch (FirebaseAuthException e) {
            throw new BadCredentialsException(e.getMessage(), e);
        }
    }
}
