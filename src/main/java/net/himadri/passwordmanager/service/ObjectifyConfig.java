package net.himadri.passwordmanager.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import lombok.RequiredArgsConstructor;
import net.himadri.passwordmanager.entity.AccessLog;
import net.himadri.passwordmanager.entity.Backup;
import net.himadri.passwordmanager.entity.BackupItem;
import net.himadri.passwordmanager.entity.Password;
import net.himadri.passwordmanager.entity.RegisteredUser;
import net.himadri.passwordmanager.entity.UserSettings;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
public class ObjectifyConfig {
    private final GoogleCredentials googleCredentials;

    @Bean
    public FilterRegistrationBean<ObjectifyService.Filter> objectifyFilterRegistration() {
        final FilterRegistrationBean<ObjectifyService.Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ObjectifyService.Filter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    @PostConstruct
    public void init() {
        ObjectifyService.init(new ObjectifyFactory(
                DatastoreOptions.newBuilder()
                        .setCredentials(googleCredentials)
                        .build()
                        .getService()
        ));

        ObjectifyService.register(AccessLog.class);
        ObjectifyService.register(Backup.class);
        ObjectifyService.register(BackupItem.class);
        ObjectifyService.register(Password.class);
        ObjectifyService.register(RegisteredUser.class);
        ObjectifyService.register(UserSettings.class);
    }
}