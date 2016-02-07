package net.himadri.passwordmanager.entity;

/**
* Created by Kávai on 2016.01.24..
*/
public class UserData {
    private final String userId;
    private final String nickName;
    private final String logoutURL;
    private final boolean encodedUserId;
    private int iterations;

    public UserData(String userId, String nickName, String logoutURL, boolean encodedUserId, int iterations) {
        this.userId = userId;
        this.nickName = nickName;
        this.logoutURL = logoutURL;
        this.encodedUserId = encodedUserId;
        this.iterations = iterations;
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

    public int getIterations() {
        return iterations;
    }
}
