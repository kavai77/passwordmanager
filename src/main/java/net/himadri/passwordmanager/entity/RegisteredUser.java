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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegisteredUser that = (RegisteredUser) o;

        if (iterations != that.iterations) return false;
        if (keyLength != that.keyLength) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (masterPasswordHash != null ? !masterPasswordHash.equals(that.masterPasswordHash) : that.masterPasswordHash != null)
            return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (cipherAlgorithm != null ? !cipherAlgorithm.equals(that.cipherAlgorithm) : that.cipherAlgorithm != null)
            return false;
        return pbkdf2Algorithm != null ? pbkdf2Algorithm.equals(that.pbkdf2Algorithm) : that.pbkdf2Algorithm == null;

    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (masterPasswordHash != null ? masterPasswordHash.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + iterations;
        result = 31 * result + (cipherAlgorithm != null ? cipherAlgorithm.hashCode() : 0);
        result = 31 * result + keyLength;
        result = 31 * result + (pbkdf2Algorithm != null ? pbkdf2Algorithm.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RegisteredUser{" +
                "userId='" + userId + '\'' +
                ", masterPasswordHash='" + masterPasswordHash + '\'' +
                ", email='" + email + '\'' +
                ", iterations=" + iterations +
                ", cipherAlgorithm='" + cipherAlgorithm + '\'' +
                ", keyLength=" + keyLength +
                ", pbkdf2Algorithm='" + pbkdf2Algorithm + '\'' +
                '}';
    }
}
