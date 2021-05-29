package net.himadri.passwordmanager.service;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DateService {
    public Date currentDate() {
        return new Date();
    }
}
