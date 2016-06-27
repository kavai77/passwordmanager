package net.himadri.passwordmanager.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.OnLoad;

@Entity
public class RegisteredUser {

    @Id
    private String userId;
    private String masterPasswordHash;
    private String email;
    private int iterations;
    private String cipherAlgorithm;
    private int keyLength;

    public RegisteredUser() {
    }

    public RegisteredUser(String userId, String masterPasswordHash, String email, int iterations, String cipherAlgorithm, int keyLength) {
        this.userId = userId;
        this.masterPasswordHash = masterPasswordHash;
        this.email = email;
        this.iterations = iterations;
        this.cipherAlgorithm = cipherAlgorithm;
        this.keyLength = keyLength;
    }

    @OnLoad
    public void onLoad() {
        if (cipherAlgorithm == null) {
            cipherAlgorithm = Settings.CIPHER_ALGORITHM;
        }
        if (keyLength == 0) {
            keyLength = 256;
        }
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

    public String getCipherAlgorithm() {
        return cipherAlgorithm;
    }

    public void setCipherAlgorithm(String cipherAlgorithm) {
        this.cipherAlgorithm = cipherAlgorithm;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }
}
