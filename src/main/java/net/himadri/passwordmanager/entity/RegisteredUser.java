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
    private String cipherAlgorithm;
    private int keyLength;
    private String pbkdf2Algorithm;

    public RegisteredUser() {
    }

    public RegisteredUser(String userId, String masterPasswordHash, String email, int iterations, String cipherAlgorithm,
                          int keyLength, String pbkdf2Algorithm) {
        this.userId = userId;
        this.masterPasswordHash = masterPasswordHash;
        this.email = email;
        this.iterations = iterations;
        this.cipherAlgorithm = cipherAlgorithm;
        this.keyLength = keyLength;
        this.pbkdf2Algorithm = pbkdf2Algorithm;
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

    public String getPbkdf2Algorithm() {
        return pbkdf2Algorithm;
    }

    public void setPbkdf2Algorithm(String pbkdf2Algorithm) {
        this.pbkdf2Algorithm = pbkdf2Algorithm;
    }
}
