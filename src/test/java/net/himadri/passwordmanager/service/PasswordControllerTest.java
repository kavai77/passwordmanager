package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.googlecode.objectify.LoadResult;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.cmd.*;
import net.himadri.passwordmanager.entity.Password;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration("/test-app-context.xml")
public class PasswordControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    Objectify ofy;

    @Autowired
    UserService userService;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        User user = new User("email", "authDomain", "userId");
        when(userService.getCurrentUser()).thenReturn(user);
        when(ofy.save()).thenReturn(mock(Saver.class));
        when(ofy.save().entity(any(Password.class))).thenReturn(mock(Result.class));
        when(ofy.load()).thenReturn(mock(Loader.class));
        when(ofy.load().type(Password.class)).thenReturn(mock(LoadType.class));
        when(ofy.delete()).thenReturn(mock(Deleter.class));
    }

    @Test
    public void given_GoodParameters_when_StoringPassword_Then_PasswordIsSaved_And_Returned() throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/store")
                        .param("domain", "domain")
                        .param("userName", "userName")
                        .param("hex", "hex")
                        .param("iv", "iv")
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        // then
        Password expectedPassword = new Password("userId", "domain", "userName", "hex", "iv");
        resultActions
                .andExpect(status().isOk())
                .andExpect(model().attribute("password", expectedPassword));

        verify(ofy.save()).entity(expectedPassword);
    }

    @Test
    public void given_GoodParameters_when_ChangingDomain_Then_NewPasswordIsSaved_And_Returned() throws Exception {
        // given
        when(ofy.load().type(Password.class).id(1L)).thenReturn(mock(LoadResult.class));
        when(ofy.load().type(Password.class).id(1L).now()).thenReturn(
                new Password("userId", "domain", "userName", "hex", "iv"));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/changeDomain")
                        .param("id", "1")
                        .param("domain", "newDomain")
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        // then
        Password expectedPassword = new Password("userId", "newDomain", "userName", "hex", "iv");
        resultActions
                .andExpect(status().isOk())
                .andExpect(model().attribute("password", expectedPassword));

        verify(ofy.save()).entity(expectedPassword);
    }

    @Test
    public void given_wrongUserId_when_ChangingDomain_Then_Fails() throws Exception {
        // given
        when(ofy.load().type(Password.class).id(1L)).thenReturn(mock(LoadResult.class));
        when(ofy.load().type(Password.class).id(1L).now()).thenReturn(
                new Password("otherUserId", null, null, null, null));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/changeDomain")
                        .param("id", "1")
                        .param("domain", "newDomain")
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        // then
        resultActions
                .andExpect(status().is4xxClientError());

        verifyNoMoreInteractions(ofy.save());
    }

    @Test
    public void given_GoodParameters_when_ChangingUserName_Then_NewUserNameIsSaved_And_Returned() throws Exception {
        // given
        when(ofy.load().type(Password.class).id(1L)).thenReturn(mock(LoadResult.class));
        when(ofy.load().type(Password.class).id(1L).now()).thenReturn(
                new Password("userId", "domain", "userName", "hex", "iv"));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/changeUserName")
                        .param("id", "1")
                        .param("userName", "newUserName")
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        // then
        Password expectedPassword = new Password("userId", "domain", "newUserName", "hex", "iv");
        resultActions
                .andExpect(status().isOk())
                .andExpect(model().attribute("password", expectedPassword));

        verify(ofy.save()).entity(expectedPassword);
    }

    @Test
    public void given_wrongUserId_when_ChangingUserName_Then_Fails() throws Exception {
        // given
        when(ofy.load().type(Password.class).id(1L)).thenReturn(mock(LoadResult.class));
        when(ofy.load().type(Password.class).id(1L).now()).thenReturn(
                new Password("otherUserId", null, null, null, null));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/changeDomain")
                        .param("id", "1")
                        .param("userName", "newUserName")
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        // then
        resultActions
                .andExpect(status().is4xxClientError());

        verifyNoMoreInteractions(ofy.save());
    }

    @Test
    public void given_GoodParameters_when_ChangingHexAndIv_Then_NewValuesSaved_And_Returned() throws Exception {
        // given
        when(ofy.load().type(Password.class).id(1L)).thenReturn(mock(LoadResult.class));
        when(ofy.load().type(Password.class).id(1L).now()).thenReturn(
                new Password("userId", "domain", "userName", "hex", "iv"));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/changeHex")
                        .param("id", "1")
                        .param("hex", "newHex")
                        .param("iv", "newIv")
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        // then
        Password expectedPassword = new Password("userId", "domain", "userName", "newHex", "newIv");
        resultActions
                .andExpect(status().isOk())
                .andExpect(model().attribute("password", expectedPassword));

        verify(ofy.save()).entity(expectedPassword);
    }

    @Test
    public void given_wrongUserId_when_ChangingHexAndIv_Then_Fails() throws Exception {
        // given
        when(ofy.load().type(Password.class).id(1L)).thenReturn(mock(LoadResult.class));
        when(ofy.load().type(Password.class).id(1L).now()).thenReturn(
                new Password("otherUserId", null, null, null, null));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/changeHex")
                        .param("id", "1")
                        .param("hex", "newHex")
                        .param("iv", "newIv")
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        // then
        resultActions
                .andExpect(status().is4xxClientError());

        verifyNoMoreInteractions(ofy.save());
    }

    @Test
    public void given_GoodParameters_when_DeletingPassword_Then_HttpOk() throws Exception {
        // given
        when(ofy.load().type(Password.class).id(1L)).thenReturn(mock(LoadResult.class));
        Password loadedPassword = new Password("userId", "domain", "userName", "hex", "iv");
        when(ofy.load().type(Password.class).id(1L).now()).thenReturn(loadedPassword);
        when(ofy.delete().entity(any(Password.class))).thenReturn(mock(Result.class));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/deletePassword")
                        .param("id", "1")
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        // then
        resultActions
                .andExpect(status().isOk());

        verify(ofy.delete()).entity(loadedPassword);
    }

    @Test
    public void given_wrongUserId_when_DeletingPassword_Then_Fails() throws Exception {
        // given
        when(ofy.load().type(Password.class).id(1L)).thenReturn(mock(LoadResult.class));
        when(ofy.load().type(Password.class).id(1L).now()).thenReturn(
                new Password("otherUserId", null, null, null, null));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/deletePassword")
                        .param("id", "1")
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        // then
        resultActions
                .andExpect(status().is4xxClientError());

        verifyNoMoreInteractions(ofy.delete());
    }

    @Test
    public void when_RetrievingPasswords_Then_PasswordSortedAndReturned() throws Exception {
        // given
        when(ofy.load().type(Password.class).filter(anyString(), anyString())).thenReturn(mock(Query.class));
        when(ofy.load().type(Password.class).filter(anyString(), anyString()).order(anyString())).thenReturn(mock(Query.class));
        Password pwd1 = new Password("userId", "BDomain", "userName", "hex", "iv");
        Password pwd2 = new Password("userId", "aDomain", "userName", "hex", "iv");
        when(ofy.load().type(Password.class).filter(anyString(), anyString()).order(anyString()).list())
                .thenReturn(Arrays.asList(pwd1, pwd2));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/retrieve")
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        // then
        resultActions
                .andExpect(status().isOk()).andDo(print())
                .andExpect(model().attribute("passwordList", Arrays.asList(pwd2, pwd1)));

        verify(ofy.load().type(Password.class)).filter("userId", "userId");
        verify(ofy.load().type(Password.class).filter("userId", "userId")).order("domain");
    }

}