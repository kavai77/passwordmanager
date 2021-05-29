package net.himadri.passwordmanager.entity;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;

@Entity
public class Password {

    @Id
    private Long id;
    @Index
    private String userId;
    @Index
    private String domain;
    private String userName;
    private String hex;
    private String iv;
    private Date created;
    private Date modified;

    public Password() {
    }

    public Password(String userId, String domain, String userName, String hex, String iv, Date created, Date modified) {
        this.userId = userId;
        this.domain = domain;
        this.userName = userName;
        this.hex = hex;
        this.iv = iv;
        this.created = created;
        this.modified = modified;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public Date getCreated() {
        return created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Password password = (Password) o;

        if (id != null ? !id.equals(password.id) : password.id != null) return false;
        if (userId != null ? !userId.equals(password.userId) : password.userId != null) return false;
        if (domain != null ? !domain.equals(password.domain) : password.domain != null) return false;
        if (userName != null ? !userName.equals(password.userName) : password.userName != null) return false;
        if (hex != null ? !hex.equals(password.hex) : password.hex != null) return false;
        return iv != null ? iv.equals(password.iv) : password.iv == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (hex != null ? hex.hashCode() : 0);
        result = 31 * result + (iv != null ? iv.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Password{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", domain='" + domain + '\'' +
                ", userName='" + userName + '\'' +
                ", hex='" + hex + '\'' +
                ", iv='" + iv + '\'' +
                '}';
    }
}
