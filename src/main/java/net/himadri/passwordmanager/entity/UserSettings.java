package net.himadri.passwordmanager.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class UserSettings {

    @Id
    private String userId;
    private int defaultPasswordLength;
    private int timeoutLengthSeconds;

    public UserSettings() {
    }

    public UserSettings(String userId, int defaultPasswordLength, int timeoutLengthSeconds) {
        this.userId = userId;
        this.defaultPasswordLength = defaultPasswordLength;
        this.timeoutLengthSeconds = timeoutLengthSeconds;
    }

    public String getUserId() {
        return userId;
    }

    public int getDefaultPasswordLength() {
        return defaultPasswordLength;
    }

    public void setDefaultPasswordLength(int defaultPasswordLength) {
        this.defaultPasswordLength = defaultPasswordLength;
    }

    public int getTimeoutLengthSeconds() {
        return timeoutLengthSeconds;
    }

    public void setTimeoutLengthSeconds(int timeoutLengthSeconds) {
        this.timeoutLengthSeconds = timeoutLengthSeconds;
    }
}
