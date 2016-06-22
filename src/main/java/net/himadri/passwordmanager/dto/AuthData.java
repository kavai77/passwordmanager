package net.himadri.passwordmanager.dto;

/**
 * Created by Kávai on 2016.06.22..
 */
public class AuthData {
    private boolean authenticated;
    private String loginUrl;

    public AuthData() {
    }

    public AuthData(boolean authenticated, String loginUrl) {
        this.authenticated = authenticated;
        this.loginUrl = loginUrl;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getLoginUrl() {
        return loginUrl;
    }
}
