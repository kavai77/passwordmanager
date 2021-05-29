package net.himadri.passwordmanager.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.googlecode.objectify.LoadResult;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.cmd.*;
import net.himadri.passwordmanager.entity.RegisteredUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by himadri on 2016. 11. 06..
 */
@Component
public class MockMvcBehaviour {
    static final String TEST_AUTH_TOKEN = "auth_token";

    @Autowired
    ExternalService externalService;

    @Autowired
    DateService dateService;

    @PostConstruct
    public void init() {
        when(externalService.ofy()).thenReturn(mock(Objectify.class));
    }

    public void givenUserIsAuthenticated() throws Exception {
        when(externalService.firebaseAuth()).thenReturn(mock(FirebaseAuth.class));
        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(externalService.firebaseAuth().verifyIdToken(TEST_AUTH_TOKEN)).thenReturn(firebaseToken);
        when(firebaseToken.getUid()).thenReturn("userId");
        when(firebaseToken.getEmail()).thenReturn("email");
        when(firebaseToken.getName()).thenReturn("name");
    }

    public void givenObjectifySaverIsMocked() {
        when(externalService.ofy().save()).thenReturn(mock(Saver.class));
        when(externalService.ofy().save().entity(any())).thenReturn(mock(Result.class));
    }

    public void givenObjectifyLoaderIsMocked() {
        when(externalService.ofy().load()).thenReturn(mock(Loader.class));
        when(externalService.ofy().load().type(any(Class.class))).thenReturn(mock(LoadType.class));
        when(externalService.ofy().load().type(Class.class).id(anyLong())).thenReturn(mock(LoadResult.class));
        when(externalService.ofy().load().type(Class.class).id(anyString())).thenReturn(mock(LoadResult.class));
        when(externalService.ofy().load().type(Class.class).filter(anyString(), anyString())).thenReturn(mock(Query.class));
        when(externalService.ofy().load().type(Class.class).filter(anyString(), anyString()).order(anyString())).thenReturn(mock(Query.class));

    }

    public void givenObjectifyDeleterIsMocked() {
        when(externalService.ofy().delete()).thenReturn(mock(Deleter.class));
        when(externalService.ofy().delete().entity(any())).thenReturn(mock(Result.class));
    }

    public void givenUserIsRegistered() {
        RegisteredUser registeredUser = new RegisteredUser("userId", "hash", "hashAlgorithm", "email", 1000, "AES-CBC", 256, "MD5", "salt");
        when(externalService.ofy().load().type(RegisteredUser.class).id("userId").now()).thenReturn(registeredUser);
        when(externalService.ofy().load().type(RegisteredUser.class).id("userId").safe()).thenReturn(registeredUser);
    }

    public void givenCurrentDateIs(Date date) {
        when(dateService.currentDate()).thenReturn(date);
    }
}
