package net.himadri.passwordmanager.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.googlecode.objectify.ObjectifyService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

public class AbstractSpringBootTest {
    @MockBean
    GoogleCredentials googleCredentials;

    @MockBean
    ObjectifyConfig objectifyConfig;

    @MockBean
    FilterRegistrationBean<ObjectifyService.Filter> objectifyFilterRegistration;

}
