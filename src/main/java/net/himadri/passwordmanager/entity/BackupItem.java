package net.himadri.passwordmanager.entity;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;

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
    private Date created;
    private Date modified;

    public BackupItem() {
    }

    public BackupItem(Long backupId, String domain, String userName, String hex, String iv, Date created, Date modified) {
        this.backupId = backupId;
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

    public Long getBackupId() {
        return backupId;
    }

    public String getDomain() {
        return domain;
    }

    public String getUserName() {
        return userName;
    }

    public String getHex() {
        return hex;
    }

    public String getIv() {
        return iv;
    }

    public Date getCreated() {
        return created;
    }

    public Date getModified() {
        return modified;
    }
}
