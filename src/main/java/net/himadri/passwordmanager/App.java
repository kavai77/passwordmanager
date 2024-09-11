package net.himadri.passwordmanager;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
public class App {
    public static final String GAE_SERVICE_ACCOUNT = "/usr/share/secrets/passwordmanager-1166-e42fb1659779.json";
    public static final String X_AUTHORIZATION_FIREBASE = "X-Authorization-Firebase";

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        return GoogleCredentials.fromStream(new FileInputStream(GAE_SERVICE_ACCOUNT));
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
