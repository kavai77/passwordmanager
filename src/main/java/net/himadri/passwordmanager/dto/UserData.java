package net.himadri.passwordmanager.dto;

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
    private final int keyLength;
    private final String pbkdf2Algorithm;

    private UserData(String userId, boolean authenticated, String nickName, String loginUrl, String logoutURL, boolean registered, int iterations,
                    String cipherAlgorithm, int keyLength, String pbkdf2Algorithm) {
        this.userId = userId;
        this.authenticated = authenticated;
        this.nickName = nickName;
        this.loginUrl = loginUrl;
        this.logoutURL = logoutURL;
        this.registered = registered;
        this.iterations = iterations;
        this.cipherAlgorithm = cipherAlgorithm;
        this.keyLength = keyLength;
        this.pbkdf2Algorithm = pbkdf2Algorithm;
    }

    public static UserData userAuthenticatedInstance(String userId, String nickName,
                                                       String logoutURL, boolean registered, int iterations,
                                                       String cipherAlgorithm, int keyLength, String pbkdf2Algorithm) {
        return new UserData(userId, true, nickName, null, logoutURL, registered, iterations, cipherAlgorithm, keyLength, pbkdf2Algorithm);
    }

    public static UserData userNotAuthenticatedInstance(String loginUrl) {
        return new UserData(null, false, null, loginUrl, null, false, 0, null,0, null);
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

    public int getKeyLength() {
        return keyLength;
    }

    public String getPbkdf2Algorithm() {
        return pbkdf2Algorithm;
    }
}
