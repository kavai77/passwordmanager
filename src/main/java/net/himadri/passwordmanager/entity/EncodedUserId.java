package net.himadri.passwordmanager.entity;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.OnLoad;

@Entity
public class EncodedUserId {

    @Id
    private String userId;
    private String masterPasswordMd5Hash;
    private String email;
    private int iterations;

    public EncodedUserId() {
    }

    public EncodedUserId(String userId, String masterPasswordMd5Hash, String email, int iterations) {
        this.userId = userId;
        this.masterPasswordMd5Hash = masterPasswordMd5Hash;
        this.email = email;
        this.iterations = iterations;
    }

    @OnLoad
    void onLoad() {
        if (iterations == 0) {
            iterations = 16; // the default value in legacy javascript
            ObjectifyService.ofy().save().entity(this);
        }
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

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
}
