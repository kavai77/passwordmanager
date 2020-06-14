package net.himadri.passwordmanager;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import net.himadri.passwordmanager.service.PasswordController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.util.Collections;

@SpringBootApplication
@ComponentScan(basePackageClasses = PasswordController.class)
public class App {
    public static final String GAE_SERVICE_ACCOUNT = "/passwordmanager-1166-e42fb1659779.json";
    public static final String X_AUTHORIZATION_FIREBASE = "X-Authorization-Firebase";

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        if (System.getenv("PORT") != null) {
            String port = System.getenv("PORT");
            app.setDefaultProperties(Collections.singletonMap("server.port", port));
        }

        app.run(args);
    }

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        return GoogleCredentials.fromStream(getClass().getResourceAsStream(GAE_SERVICE_ACCOUNT));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void applicationReadyEvent() throws IOException {
        FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(googleCredentials())
            .setProjectId("passwordmanager-1166")
            .setStorageBucket("passwordmanager-1166.appspot.com")
            .setDatabaseUrl("https://passwordmanager-1166.firebaseio.com")
            .build();
        FirebaseApp.initializeApp(options);
    }
}
