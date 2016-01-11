package net.himadri.passwordmanager.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class EncodedUserId {
    @Id
    private String userId;
    private String encoded;
    private String email;

    public EncodedUserId() {
    }

    public EncodedUserId(String userId, String encoded, String email) {
        this.userId = userId;
        this.encoded = encoded;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getEncoded() {
        return encoded;
    }

    public String getEmail() {
        return email;
    }
}
