package net.himadri.passwordmanager.entity;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class DataEntity {

    @Id
    private String id;
    @Index
    private String userId;
    @Index
    private String domain;
    private String hex;

    public DataEntity() {
    }

    public DataEntity(String userId, String domain, String hex) {
        this.id = userId + domain;
        this.userId = userId;
        this.domain = domain;
        this.hex = hex;
    }

    public String getId() {
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
