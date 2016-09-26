package net.himadri.passwordmanager.dto;

import java.util.Date;

public class BackupData {
    private final Long id;
    private final Date backupDate;
    private final int numberOfPasswords;

    public BackupData(Long id, Date backupDate, int numberOfPasswords) {
        this.id = id;
        this.backupDate = backupDate;
        this.numberOfPasswords = numberOfPasswords;
    }

    public Long getId() {
        return id;
    }

    public Date getBackupDate() {
        return backupDate;
    }

    public int getNumberOfPasswords() {
        return numberOfPasswords;
    }
}
