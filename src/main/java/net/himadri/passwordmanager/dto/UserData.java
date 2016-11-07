package net.himadri.passwordmanager.dto;

import static net.himadri.passwordmanager.entity.AdminSettings.*;

/**
* Created by Kávai on 2016.01.24..
*/
public class UserData {
    private final String userId;
    private final boolean authenticated;
    private final String nickName;
    private final String loginUrl;
    private final String logoutURL;
    private final boolean registered;
    private final int iterations;
    private final String cipherAlgorithm;
    private final String masterPasswordHashAlgorithm;
    private final int keyLength;
    private final String pbkdf2Algorithm;
    private final UserSettingsData userSettings;

    private UserData(String userId, boolean authenticated, String nickName, String loginUrl, String logoutURL, boolean registered, int iterations,
                     String cipherAlgorithm, String masterPasswordHashAlgorithm, int keyLength, String pbkdf2Algorithm, UserSettingsData userSettings) {
        this.userId = userId;
        this.authenticated = authenticated;
        this.nickName = nickName;
        this.loginUrl = loginUrl;
        this.logoutURL = logoutURL;
        this.registered = registered;
        this.iterations = iterations;
        this.cipherAlgorithm = cipherAlgorithm;
        this.masterPasswordHashAlgorithm = masterPasswordHashAlgorithm;
        this.keyLength = keyLength;
        this.pbkdf2Algorithm = pbkdf2Algorithm;
        this.userSettings = userSettings;
    }

    public static UserData userRegisteredInstance(String userId, String nickName,
                                                  String logoutURL, int iterations,
                                                  String cipherAlgorithm, String masterPasswordHashAlgorithm,
                                                  int keyLength, String pbkdf2Algorithm,
                                                  UserSettingsData userSettings) {
        return new UserData(userId, true, nickName, null, logoutURL, true, iterations, cipherAlgorithm, masterPasswordHashAlgorithm, keyLength,
                pbkdf2Algorithm, userSettings);
    }

    public static UserData userUnregisteredInstance(String userId, String nickName, String logoutURL,
                                                     UserSettingsData userSettings) {
        return new UserData(userId, true, nickName, null, logoutURL, false, DEFAULT_ITERATIONS, CIPHER_ALGORITHM, DEFAULT_HASH_ALGORITHM, DEFAULT_KEYLENGTH,
                DEFAULT_PBKDF2_ALGORITHM, userSettings);
    }



    public static UserData userNotAuthenticatedInstance(String loginUrl) {
        return new UserData(null, false, null, loginUrl, null, false, 0, null, null, 0, null, null);
    }

    public String getUserId() {
        return userId;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getNickName() {
        return nickName;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public String getLogoutURL() {
        return logoutURL;
    }

    public boolean isRegistered() {
        return registered;
    }

    public int getIterations() {
        return iterations;
    }

    public String getCipherAlgorithm() {
        return cipherAlgorithm;
    }

    public String getMasterPasswordHashAlgorithm() {
        return masterPasswordHashAlgorithm;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public String getPbkdf2Algorithm() {
        return pbkdf2Algorithm;
    }

    public UserSettingsData getUserSettings() {
        return userSettings;
    }

    public static class UserSettingsData {
        private int defaultPasswordLength;
        private int timeoutLengthSeconds;

        public UserSettingsData() {
        }

        public UserSettingsData(int defaultPasswordLength, int timeoutLengthSeconds) {
            this.defaultPasswordLength = defaultPasswordLength;
            this.timeoutLengthSeconds = timeoutLengthSeconds;
        }

        public int getDefaultPasswordLength() {
            return defaultPasswordLength;
        }

        public int getTimeoutLengthSeconds() {
            return timeoutLengthSeconds;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UserSettingsData that = (UserSettingsData) o;

            if (defaultPasswordLength != that.defaultPasswordLength) return false;
            return timeoutLengthSeconds == that.timeoutLengthSeconds;

        }

        @Override
        public int hashCode() {
            int result = defaultPasswordLength;
            result = 31 * result + timeoutLengthSeconds;
            return result;
        }

        @Override
        public String toString() {
            return "UserSettingsData{" +
                    "defaultPasswordLength=" + defaultPasswordLength +
                    ", timeoutLengthSeconds=" + timeoutLengthSeconds +
                    '}';
        }
    }
}
