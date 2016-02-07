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
    private String iv;

    public Password() {
    }

    public Password(String userId, String domain, String hex, String iv) {
        this.userId = userId;
        this.domain = domain;
        this.hex = hex;
        this.iv = iv;
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

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public String getHex() {
        return hex;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }
}
