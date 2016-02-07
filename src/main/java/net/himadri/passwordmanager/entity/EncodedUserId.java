package net.himadri.passwordmanager.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class EncodedUserId {
    @Id
    private String userId;
    private String masterPasswordMd5Hash;
    private String email;

    public EncodedUserId() {
    }

    public EncodedUserId(String userId, String masterPasswordMd5Hash, String email) {
        this.userId = userId;
        this.masterPasswordMd5Hash = masterPasswordMd5Hash;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getMasterPasswordMd5Hash() {
        return masterPasswordMd5Hash;
    }

    public void setMasterPasswordMd5Hash(String masterPasswordMd5Hash) {
        this.masterPasswordMd5Hash = masterPasswordMd5Hash;
    }
}
