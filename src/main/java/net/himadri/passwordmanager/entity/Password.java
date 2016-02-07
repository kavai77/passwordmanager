package net.himadri.passwordmanager.entity;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnLoad;

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
    private int iterations;

    public Password() {
    }

    public Password(String userId, String domain, String hex, String iv, int iterations) {
        this.userId = userId;
        this.domain = domain;
        this.hex = hex;
        this.iv = iv;
        this.iterations = iterations;
    }

    @OnLoad
    void onLoad() {
        if (iterations == 0) {
            iterations = 16; // the default value in legacy javascript
        }
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

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
}
