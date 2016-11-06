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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserSettings that = (UserSettings) o;

        if (defaultPasswordLength != that.defaultPasswordLength) return false;
        if (timeoutLengthSeconds != that.timeoutLengthSeconds) return false;
        return userId != null ? userId.equals(that.userId) : that.userId == null;

    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + defaultPasswordLength;
        result = 31 * result + timeoutLengthSeconds;
        return result;
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "userId='" + userId + '\'' +
                ", defaultPasswordLength=" + defaultPasswordLength +
                ", timeoutLengthSeconds=" + timeoutLengthSeconds +
                '}';
    }
}
