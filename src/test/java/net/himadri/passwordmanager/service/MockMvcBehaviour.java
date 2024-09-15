package net.himadri.passwordmanager.service;

import com.google.firebase.auth.FirebaseToken;
import com.googlecode.objectify.LoadResult;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.cmd.Deleter;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Loader;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.cmd.Saver;
import net.himadri.passwordmanager.entity.RegisteredUser;
import net.himadri.passwordmanager.security.AuthenticationService;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockMvcBehaviour {
    static final String TEST_AUTH_TOKEN = "auth_token";

    private final DatabaseService databaseService;

    private final DateService dateService;

    private final AuthenticationService authenticationService;

    public MockMvcBehaviour(AuthenticationService authenticationService, DatabaseService databaseService, DateService dateService) {
        this.databaseService = databaseService;
        this.dateService = dateService;
        this.authenticationService = authenticationService;
        when(databaseService.ofy()).thenReturn(mock(Objectify.class));
    }

    public void givenUserIsAuthenticated() throws Exception {
        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(firebaseToken.getUid()).thenReturn("userId");
        when(firebaseToken.getEmail()).thenReturn("email");
        when(firebaseToken.getName()).thenReturn("name");
        when(authenticationService.parseFirebaseToken(anyString())).thenReturn(firebaseToken);
        when(authenticationService.getFirebaseToken()).thenCallRealMethod();
        when(authenticationService.getUid()).thenCallRealMethod();
    }

    public void givenObjectifySaverIsMocked() {
        when(databaseService.ofy().save()).thenReturn(mock(Saver.class));
        when(databaseService.ofy().save().entity(any())).thenReturn(mock(Result.class));
    }

    public void givenObjectifyLoaderIsMocked() {
        when(databaseService.ofy().load()).thenReturn(mock(Loader.class));
        when(databaseService.ofy().load().type(any(Class.class))).thenReturn(mock(LoadType.class));
        when(databaseService.ofy().load().type(Class.class).id(anyLong())).thenReturn(mock(LoadResult.class));
        when(databaseService.ofy().load().type(Class.class).id(anyString())).thenReturn(mock(LoadResult.class));
        when(databaseService.ofy().load().type(Class.class).filter(anyString(), anyString())).thenReturn(mock(Query.class));
        when(databaseService.ofy().load().type(Class.class).filter(anyString(), anyString()).order(anyString())).thenReturn(mock(Query.class));

    }

    public void givenObjectifyDeleterIsMocked() {
        when(databaseService.ofy().delete()).thenReturn(mock(Deleter.class));
        when(databaseService.ofy().delete().entity(any())).thenReturn(mock(Result.class));
    }

    public void givenUserIsRegistered() {
        RegisteredUser registeredUser = new RegisteredUser("userId", "hash", "hashAlgorithm", "email", 1000, "AES-CBC", 256, "MD5", "salt");
        when(databaseService.ofy().load().type(RegisteredUser.class).id("userId").now()).thenReturn(registeredUser);
        when(databaseService.ofy().load().type(RegisteredUser.class).id("userId").safe()).thenReturn(registeredUser);
    }

    public void givenCurrentDateIs(Date date) {
        when(dateService.currentDate()).thenReturn(date);
    }
}
