package net.himadri.passwordmanager.entity;

/**
* Created by Kávai on 2016.01.24..
*/
public class UserData {
    public static final int DEFAULT_ITERATIONS = 10000;

    private final String userId;
    private final String nickName;
    private final String logoutURL;
    private final boolean encodedUserId;
    private int defaultIterations;

    public UserData(String userId, String nickName, String logoutURL, boolean encodedUserId) {
        this.userId = userId;
        this.nickName = nickName;
        this.logoutURL = logoutURL;
        this.encodedUserId = encodedUserId;
        this.defaultIterations = DEFAULT_ITERATIONS;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickName() {
        return nickName;
    }

    public String getLogoutURL() {
        return logoutURL;
    }

    public boolean isEncodedUserId() {
        return encodedUserId;
    }

    public int getDefaultIterations() {
        return defaultIterations;
    }
}
