package net.himadri.passwordmanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.himadri.passwordmanager.dto.UserData;
import net.himadri.passwordmanager.entity.RegisteredUser;
import net.himadri.passwordmanager.entity.UserSettings;
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

import static net.himadri.passwordmanager.App.X_AUTHORIZATION_FIREBASE;
import static net.himadri.passwordmanager.service.MockMvcBehaviour.TEST_AUTH_TOKEN;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration("/test-app-context.xml")
@Ignore
public class UserControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    ExternalService externalService;

    @Autowired
    private MockMvcBehaviour mockMvcBehaviour;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void given_GoodParameters_when_Registering_Then_Success() throws Exception {
        // given
        mockMvcBehaviour.givenUserIsAuthenticated();
        mockMvcBehaviour.givenObjectifyLoaderIsMocked();
        mockMvcBehaviour.givenObjectifySaverIsMocked();
        givenUserIsNotRegistered();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/user/register")
                        .header(X_AUTHORIZATION_FIREBASE, TEST_AUTH_TOKEN)
                        .param("masterPasswordHash", "hash")
                        .param("masterPasswordHashAlgorithm", "hashAlgorithm")
                        .param("iterations", "1000")
                        .param("cipherAlgorithm", "AES-CBC")
                        .param("keyLength", "256")
                        .param("pbkdf2Algorithm", "MD5")
                        .accept(MediaType.APPLICATION_JSON));

        // then
        RegisteredUser expectedUser = new RegisteredUser("userId", "hash", "hashAlgorithm", "email", 1000, "AES-CBC", 256, "MD5", "salt");
        resultActions
                .andExpect(status().isCreated());

        verify(externalService.ofy().save()).entity(expectedUser);
    }

    @Test
    public void given_UserAlreadyRegistered_when_Registering_Then_Failure() throws Exception {
        // given
        mockMvcBehaviour.givenUserIsAuthenticated();
        mockMvcBehaviour.givenObjectifySaverIsMocked();
        mockMvcBehaviour.givenObjectifyLoaderIsMocked();
        mockMvcBehaviour.givenUserIsRegistered();

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/secure/user/register")
                        .header(X_AUTHORIZATION_FIREBASE, TEST_AUTH_TOKEN)
                        .param("masterPasswordHash", "hash")
                        .param("masterPasswordHashAlgorithm", "hashAlgorithm")
                        .param("iterations", "1000")
                        .param("cipherAlgorithm", "AES-CBC")
                        .param("keyLength", "256")
                        .param("pbkdf2Algorithm", "MD5")
                        .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().is4xxClientError());

        verifyNoMoreInteractions(externalService.ofy().save());
    }

    @Test
    public void given_GoodParamters_when_SavingUserSettings_Then_Success() throws Exception {
        // given
        mockMvcBehaviour.givenUserIsAuthenticated();
        mockMvcBehaviour.givenObjectifyLoaderIsMocked();
        mockMvcBehaviour.givenObjectifySaverIsMocked();
        mockMvcBehaviour.givenUserIsRegistered();

        // when
        UserData.UserSettingsData userSettingsData = new UserData.UserSettingsData(1, 2);
        ResultActions resultActions = mockMvc.perform(
                post("/secure/user/userSettings")
                        .header(X_AUTHORIZATION_FIREBASE, TEST_AUTH_TOKEN)
                        .content(new ObjectMapper().writeValueAsString(userSettingsData))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk());

        verify(externalService.ofy().save()).entity(new UserSettings("userId", 1, 2));
    }

    private void givenUserIsNotRegistered() {
        when(externalService.ofy().load().type(RegisteredUser.class).id("userId").now()).thenReturn(null);
    }

}