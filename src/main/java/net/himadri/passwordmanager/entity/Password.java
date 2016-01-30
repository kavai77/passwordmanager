package net.himadri.passwordmanager.entity;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Password {

    @Id
    private Long id;
    @Index
    private String userId;
    @Index
    private String domain;
    private String hex;

    public Password() {
    }

    public Password(String userId, String domain, String hex) {
        this.userId = userId;
        this.domain = domain;
        this.hex = hex;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getDomain() {
        return domain;
    }

    public String getHex() {
        return hex;
    }
}
