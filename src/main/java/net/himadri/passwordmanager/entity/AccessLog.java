package net.himadri.passwordmanager.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.Date;

@Entity
public class AccessLog {
    @Id
    private String userId;
    private String email;
    private Date lastVisited;

    public AccessLog() {
    }

    public AccessLog(String userId, String email, Date lastVisited) {
        this.userId = userId;
        this.email = email;
        this.lastVisited = lastVisited;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public Date getLastVisited() {
        return lastVisited;
    }
}
