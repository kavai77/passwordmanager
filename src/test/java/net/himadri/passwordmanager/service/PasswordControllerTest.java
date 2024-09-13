package net.himadri.passwordmanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.himadri.passwordmanager.entity.Password;
import org.junit.Before;
import org.junit.Ignore;
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
import java.util.Date;

import static net.himadri.passwordmanager.App.X_AUTHORIZATION_FIREBASE;
import static net.himadri.passwordmanager.service.MockMvcBehaviour.TEST_AUTH_TOKEN;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration("/test-app-context.xml")
@Ignore
public class PasswordControllerTest {
    private static final Date SOME_DATE = new Date(10);
    private static final Date SOME_OTHER_DATE = new Date(20);
    private static final Date CURRENT_DATE = new Date(30);

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    ExternalService externalService;

    @Autowired
    private MockMvcBehaviour mockMvcBehaviour;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void given_GoodParameters_when_StoringPassword_Then_PasswordIsSaved_And_Returned() throws Exception {
        // given
        mockMvcBehaviour.givenUserIsAuthenticated();
        mockMvcBehaviour.givenObjectifySaverIsMocked();
        mockMvcBehaviour.givenCurrentDateIs(CURRENT_DATE);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/store")
                        .header(X_AUTHORIZATION_FIREBASE, TEST_AUTH_TOKEN)
                        .param("domain", "domain")
                        .param("userName", "userName")
                        .param("hex", "hex")
                        .param("iv", "iv")
                        .accept(MediaType.APPLICATION_JSON));

        // then
        Password expectedPassword = new Password(null, "userId", "domain", "userName", "hex", "iv", CURRENT_DATE, CURRENT_DATE);
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedPassword)));

        verify(externalService.ofy().save()).entity(expectedPassword);
    }

    @Test
    public void given_GoodParameters_when_ChangingDomain_Then_NewPasswordIsSaved_And_Returned() throws Exception {
        // given
        mockMvcBehaviour.givenUserIsAuthenticated();
        mockMvcBehaviour.givenObjectifyLoaderIsMocked();
        mockMvcBehaviour.givenObjectifySaverIsMocked();
        mockMvcBehaviour.givenCurrentDateIs(CURRENT_DATE);
        when(externalService.ofy().load().type(Password.class).id(1L).safe()).thenReturn(
                new Password(null, "userId", "domain", "userName", "hex", "iv", SOME_DATE, SOME_OTHER_DATE));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/changeDomain")
                        .header(X_AUTHORIZATION_FIREBASE, TEST_AUTH_TOKEN)
                        .param("id", "1")
                        .param("domain", "newDomain")
                        .accept(MediaType.APPLICATION_JSON));

        // then
        Password expectedPassword = new Password(null, "userId", "newDomain", "userName", "hex", "iv", SOME_DATE, SOME_OTHER_DATE);
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedPassword)));

        verify(externalService.ofy().save()).entity(expectedPassword);
    }

    @Test
    public void given_wrongUserId_when_ChangingDomain_Then_Fails() throws Exception {
        // given
        mockMvcBehaviour.givenUserIsAuthenticated();
        mockMvcBehaviour.givenObjectifyLoaderIsMocked();
        mockMvcBehaviour.givenObjectifySaverIsMocked();
        when(externalService.ofy().load().type(Password.class).id(1L).safe()).thenReturn(
                new Password(null, "otherUserId", null, null, null, null, null, null));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/changeDomain")
                        .header(X_AUTHORIZATION_FIREBASE, TEST_AUTH_TOKEN)
                        .param("id", "1")
                        .param("domain", "newDomain")
                        .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().is4xxClientError());

        verifyNoMoreInteractions(externalService.ofy().save());
    }

    @Test
    public void given_GoodParameters_when_ChangingUserName_Then_NewUserNameIsSaved_And_Returned() throws Exception {
        // given
        mockMvcBehaviour.givenUserIsAuthenticated();
        mockMvcBehaviour.givenObjectifyLoaderIsMocked();
        mockMvcBehaviour.givenObjectifySaverIsMocked();
        mockMvcBehaviour.givenCurrentDateIs(CURRENT_DATE);
        when(externalService.ofy().load().type(Password.class).id(1L).safe()).thenReturn(
                new Password(null, "userId", "domain", "userName", "hex", "iv", SOME_DATE, SOME_OTHER_DATE));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/changeUserName")
                        .header(X_AUTHORIZATION_FIREBASE, TEST_AUTH_TOKEN)
                        .param("id", "1")
                        .param("userName", "newUserName")
                        .accept(MediaType.APPLICATION_JSON));

        // then
        Password expectedPassword = new Password(null, "userId", "domain", "newUserName", "hex", "iv", SOME_DATE, SOME_OTHER_DATE);
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedPassword)));

        verify(externalService.ofy().save()).entity(expectedPassword);
    }

    @Test
    public void given_wrongUserId_when_ChangingUserName_Then_Fails() throws Exception {
        // given
        mockMvcBehaviour.givenUserIsAuthenticated();
        mockMvcBehaviour.givenObjectifyLoaderIsMocked();
        mockMvcBehaviour.givenObjectifySaverIsMocked();
        when(externalService.ofy().load().type(Password.class).id(1L).safe()).thenReturn(
                new Password(null, "otherUserId", null, null, null, null, null, null));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/changeDomain")
                        .header(X_AUTHORIZATION_FIREBASE, TEST_AUTH_TOKEN)
                        .param("id", "1")
                        .param("userName", "newUserName")
                        .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().is4xxClientError());

        verifyNoMoreInteractions(externalService.ofy().save());
    }

    @Test
    public void given_GoodParameters_when_ChangingHexAndIv_Then_NewValuesSaved_And_Returned() throws Exception {
        // given
        mockMvcBehaviour.givenUserIsAuthenticated();
        mockMvcBehaviour.givenObjectifyLoaderIsMocked();
        mockMvcBehaviour.givenObjectifySaverIsMocked();
        mockMvcBehaviour.givenCurrentDateIs(CURRENT_DATE);
        when(externalService.ofy().load().type(Password.class).id(1L).safe()).thenReturn(
                new Password(null, "userId", "domain", "userName", "hex", "iv", SOME_DATE, SOME_OTHER_DATE));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/changeHex")
                        .header(X_AUTHORIZATION_FIREBASE, TEST_AUTH_TOKEN)
                        .param("id", "1")
                        .param("hex", "newHex")
                        .param("iv", "newIv")
                        .accept(MediaType.APPLICATION_JSON));

        // then
        Password expectedPassword = new Password(null, "userId", "domain", "userName", "newHex", "newIv", SOME_DATE, CURRENT_DATE);
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedPassword)));

        verify(externalService.ofy().save()).entity(expectedPassword);
    }

    @Test
    public void given_wrongUserId_when_ChangingHexAndIv_Then_Fails() throws Exception {
        // given
        mockMvcBehaviour.givenUserIsAuthenticated();
        mockMvcBehaviour.givenObjectifyLoaderIsMocked();
        mockMvcBehaviour.givenObjectifySaverIsMocked();
        when(externalService.ofy().load().type(Password.class).id(1L).safe()).thenReturn(
                new Password(null, "otherUserId", null, null, null, null, null, null));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/changeHex")
                        .header(X_AUTHORIZATION_FIREBASE, TEST_AUTH_TOKEN)
                        .param("id", "1")
                        .param("hex", "newHex")
                        .param("iv", "newIv")
                        .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().is4xxClientError());

        verifyNoMoreInteractions(externalService.ofy().save());
    }

    @Test
    public void given_GoodParameters_when_DeletingPassword_Then_HttpOk() throws Exception {
        // given
        mockMvcBehaviour.givenUserIsAuthenticated();
        mockMvcBehaviour.givenObjectifyLoaderIsMocked();
        mockMvcBehaviour.givenObjectifyDeleterIsMocked();
        Password loadedPassword = new Password(null, "userId", "domain", "userName", "hex", "iv", SOME_DATE, SOME_OTHER_DATE);
        when(externalService.ofy().load().type(Password.class).id(1L).safe()).thenReturn(loadedPassword);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/deletePassword")
                        .header(X_AUTHORIZATION_FIREBASE, TEST_AUTH_TOKEN)
                        .param("id", "1")
                        .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk());

        verify(externalService.ofy().delete()).entity(loadedPassword);
    }

    @Test
    public void given_wrongUserId_when_DeletingPassword_Then_Fails() throws Exception {
        // given
        mockMvcBehaviour.givenUserIsAuthenticated();
        mockMvcBehaviour.givenObjectifyLoaderIsMocked();
        mockMvcBehaviour.givenObjectifyDeleterIsMocked();
        when(externalService.ofy().load().type(Password.class).id(1L).safe()).thenReturn(
                new Password(null, "otherUserId", null, null, null, null, null, null));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/deletePassword")
                        .header(X_AUTHORIZATION_FIREBASE, TEST_AUTH_TOKEN)
                        .param("id", "1")
                        .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().is4xxClientError());

        verifyNoMoreInteractions(externalService.ofy().delete());
    }

    @Test
    public void given_GoodParameters_when_RetrievingPasswords_Then_PasswordSortedAndReturned() throws Exception {
        // given
        mockMvcBehaviour.givenUserIsAuthenticated();
        mockMvcBehaviour.givenObjectifyLoaderIsMocked();
        mockMvcBehaviour.givenUserIsRegistered();

        Password pwd1 = new Password(null, "userId", "BDomain", "userName", "hex", "iv", null, null);
        Password pwd2 = new Password(null, "userId", "aDomain", "userName", "hex", "iv", SOME_DATE, SOME_OTHER_DATE);
        when(externalService.ofy().load().type(Password.class).filter(anyString(), anyString()).order(anyString()).list())
                .thenReturn(Arrays.asList(pwd1, pwd2));

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/password/retrieve")
                        .header(X_AUTHORIZATION_FIREBASE, TEST_AUTH_TOKEN)
                        .param("masterPasswordHash", "hash")
                        .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk()).andDo(print())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(pwd2, pwd1))));

        verify(externalService.ofy().load().type(Password.class)).filter("userId", "userId");
        verify(externalService.ofy().load().type(Password.class).filter("userId", "userId")).order("domain");
    }

    @Test
    public void given_HashMismatch_when_RetrievingPassword_Then_Failure() throws Exception {
        // given
        mockMvcBehaviour.givenUserIsAuthenticated();
        mockMvcBehaviour.givenObjectifyLoaderIsMocked();
        mockMvcBehaviour.givenUserIsRegistered();

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/secure/password/retrieve")
                        .header(X_AUTHORIZATION_FIREBASE, TEST_AUTH_TOKEN)
                        .param("masterPasswordHash", "otherHash")
                        .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().is4xxClientError());
    }


}