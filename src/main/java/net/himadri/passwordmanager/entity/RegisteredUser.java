package net.himadri.passwordmanager.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.OnLoad;

@Entity
public class RegisteredUser {

    @Id
    private String userId;
    private String masterPasswordHash;
    private String masterPasswordHashAlgorithm;
    private String email;
    private int iterations;
    private String cipherAlgorithm;
    private int keyLength;
    private String pbkdf2Algorithm;
    private String salt;

    @OnLoad
    public void onLoad() {
        if (salt == null) {
            salt = userId;
        }
    }

    public RegisteredUser() {
    }

    public RegisteredUser(String userId, String masterPasswordHash, String masterPasswordHashAlgorithm,
                          String email, int iterations, String cipherAlgorithm,
                          int keyLength, String pbkdf2Algorithm, String salt) {
        this.userId = userId;
        this.masterPasswordHash = masterPasswordHash;
        this.masterPasswordHashAlgorithm = masterPasswordHashAlgorithm;
        this.email = email;
        this.iterations = iterations;
        this.cipherAlgorithm = cipherAlgorithm;
        this.keyLength = keyLength;
        this.pbkdf2Algorithm = pbkdf2Algorithm;
        this.salt = salt;
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

    public String getMasterPasswordHashAlgorithm() {
        return masterPasswordHashAlgorithm;
    }

    public void setMasterPasswordHashAlgorithm(String masterPasswordHashAlgorithm) {
        this.masterPasswordHashAlgorithm = masterPasswordHashAlgorithm;
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

    public String getSalt() {
        return salt;
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
        if (masterPasswordHashAlgorithm != null ? !masterPasswordHashAlgorithm.equals(that.masterPasswordHashAlgorithm) : that.masterPasswordHashAlgorithm != null)
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
        result = 31 * result + (masterPasswordHashAlgorithm != null ? masterPasswordHashAlgorithm.hashCode() : 0);
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
                ", masterPasswordHashAlgorithm='" + masterPasswordHashAlgorithm + '\'' +
                ", email='" + email + '\'' +
                ", iterations=" + iterations +
                ", cipherAlgorithm='" + cipherAlgorithm + '\'' +
                ", keyLength=" + keyLength +
                ", pbkdf2Algorithm='" + pbkdf2Algorithm + '\'' +
                '}';
    }
}
