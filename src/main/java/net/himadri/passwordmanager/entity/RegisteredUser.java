package net.himadri.passwordmanager.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class RegisteredUser {

    @Id
    private String userId;
    private String masterPasswordHash;
    private String email;
    private int iterations;

    public RegisteredUser() {
    }

    public RegisteredUser(String userId, String masterPasswordHash, String email, int iterations) {
        this.userId = userId;
        this.masterPasswordHash = masterPasswordHash;
        this.email = email;
        this.iterations = iterations;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getMasterPasswordHash() {
        return masterPasswordHash;
    }

    public void setMasterPasswordHash(String masterPasswordHash) {
        this.masterPasswordHash = masterPasswordHash;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
}
