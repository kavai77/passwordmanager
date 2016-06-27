package net.himadri.passwordmanager.dto;

/**
* Created by Kávai on 2016.01.24..
*/
public class UserData {
    private final String userId;
    private final String nickName;
    private final String logoutURL;
    private final boolean registered;
    private int iterations;
    private String cipherAlgorithm;
    private int keyLength;


    public UserData(String userId, String nickName, String logoutURL, boolean registered, int iterations,
                    String cipherAlgorithm, int keyLength) {
        this.userId = userId;
        this.nickName = nickName;
        this.logoutURL = logoutURL;
        this.registered = registered;
        this.iterations = iterations;
        this.cipherAlgorithm = cipherAlgorithm;
        this.keyLength = keyLength;
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
}
