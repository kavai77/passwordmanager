package net.himadri.passwordmanager.entity;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class BackupItem {

    @Id
    private Long id;
    @Index
    private Long backupId;
    private String domain;
    private String userName;
    private String hex;
    private String iv;

    public BackupItem() {
    }

    public BackupItem(Long backupId, String domain, String userName, String hex, String iv) {
        this.backupId = backupId;
        this.domain = domain;
        this.userName = userName;
        this.hex = hex;
        this.iv = iv;
    }

    public Long getId() {
        return id;
    }

    public Long getBackupId() {
        return backupId;
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
}
