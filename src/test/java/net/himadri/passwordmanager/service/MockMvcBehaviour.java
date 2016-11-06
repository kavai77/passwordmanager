package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.googlecode.objectify.LoadResult;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.cmd.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by himadri on 2016. 11. 06..
 */
@Component
public class MockMvcBehaviour {
    @Autowired
    Objectify ofy;

    @Autowired
    UserService userService;

    public void givenUserIsAuthenticated() {
        User user = new User("email", "authDomain", "userId");
        when(userService.getCurrentUser()).thenReturn(user);
    }

    public void givenObjectifySaverIsMocked() {
        when(ofy.save()).thenReturn(mock(Saver.class));
        when(ofy.save().entity(any())).thenReturn(mock(Result.class));
    }

    public void givenObjectifyLoaderIsMocked() {
        when(ofy.load()).thenReturn(mock(Loader.class));
        when(ofy.load().type(any(Class.class))).thenReturn(mock(LoadType.class));
        when(ofy.load().type(Class.class).id(anyLong())).thenReturn(mock(LoadResult.class));
        when(ofy.load().type(Class.class).id(anyString())).thenReturn(mock(LoadResult.class));
        when(ofy.load().type(Class.class).filter(anyString(), anyString())).thenReturn(mock(Query.class));
        when(ofy.load().type(Class.class).filter(anyString(), anyString()).order(anyString())).thenReturn(mock(Query.class));

    }

    public void givenObjectifyDeleterIsMocked() {
        when(ofy.delete()).thenReturn(mock(Deleter.class));
        when(ofy.delete().entity(any())).thenReturn(mock(Result.class));
    }



}