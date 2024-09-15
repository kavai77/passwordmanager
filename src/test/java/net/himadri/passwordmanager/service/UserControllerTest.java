package net.himadri.passwordmanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.himadri.passwordmanager.dto.UserData;
import net.himadri.passwordmanager.entity.RegisteredUser;
import net.himadri.passwordmanager.entity.UserSettings;
import net.himadri.passwordmanager.security.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static net.himadri.passwordmanager.App.X_AUTHORIZATION_FIREBASE;
import static net.himadri.passwordmanager.service.MockMvcBehaviour.TEST_AUTH_TOKEN;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest extends AbstractSpringBootTest{
    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private DatabaseService databaseService;

    @MockBean
    private DateService dateService;

    private MockMvcBehaviour mockMvcBehaviour;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvcBehaviour = new MockMvcBehaviour(authenticationService, databaseService, dateService);
    }
    @Test
    public void given_GoodParameters_when_Registering_Then_Success() throws Exception {
        // given
        mockMvcBehaviour.givenUserIsAuthenticated();
        mockMvcBehaviour.givenObjectifyLoaderIsMocked();
        mockMvcBehaviour.givenObjectifySaverIsMocked();
        givenUserIsNotRegistered();
        when(databaseService.randomString(anyInt())).thenReturn("salt");

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

        verify(databaseService.ofy().save()).entity(expectedUser);
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

        verifyNoMoreInteractions(databaseService.ofy().save());
    }

    @Test
    public void given_GoodParamters_when_SavingUserSettings_Then_Success() throws Exception {
        // given
        mockMvcBehaviour.givenUserIsAuthenticated();
        mockMvcBehaviour.givenObjectifyLoaderIsMocked();
        mockMvcBehaviour.givenObjectifySaverIsMocked();
        mockMvcBehaviour.givenUserIsRegistered();

        // when
        UserData.UserSettingsData userSettingsData = UserData.UserSettingsData.builder()
                .defaultPasswordLength(1)
                .timeoutLengthSeconds(2)
                .build();
        ResultActions resultActions = mockMvc.perform(
        post("/secure/user/userSettings")
                .header(X_AUTHORIZATION_FIREBASE, TEST_AUTH_TOKEN)
                .content(new ObjectMapper().writeValueAsString(userSettingsData))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk());

        verify(databaseService.ofy().save()).entity(new UserSettings("userId", 1, 2));
    }

    private void givenUserIsNotRegistered() {
        when(databaseService.ofy().load().type(RegisteredUser.class).id("userId").now()).thenReturn(null);
    }

}