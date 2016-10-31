package net.himadri.passwordmanager.service;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.reflect.ClassPath;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by himadri on 2016. 10. 31..
 */
@Configuration
public class BaseConfiguration {
    @PostConstruct
    public void init() throws IOException {
        ClassPath classpath = ClassPath.from(getClass().getClassLoader());
        for (ClassPath.ClassInfo classInfo : classpath.getTopLevelClasses(
                "net.himadri.passwordmanager.entity")) {
            Class<?> clazz = classInfo.load();
            if (clazz.getAnnotation(Entity.class) != null) {
                ObjectifyService.register(clazz);
            }
        }
    }

    @Bean
    public Objectify objectify() {
        return new ObjectifyDelegate();
    }

    @Bean
    public UserService userService() {
        return UserServiceFactory.getUserService();
    }

}
