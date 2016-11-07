package net.himadri.passwordmanager.dto;

public class BackupData {
    private final Long id;
    private final Long backupDate;
    private final int numberOfPasswords;
    private final String masterPasswordHashAlgorithm;

    public BackupData(Long id, Long backupDate, int numberOfPasswords, String masterPasswordHashAlgorithm) {
        this.id = id;
        this.backupDate = backupDate;
        this.numberOfPasswords = numberOfPasswords;
        this.masterPasswordHashAlgorithm = masterPasswordHashAlgorithm;
    }

    public Long getId() {
        return id;
    }

    public Long getBackupDate() {
        return backupDate;
    }

    public int getNumberOfPasswords() {
        return numberOfPasswords;
    }

    public String getMasterPasswordHashAlgorithm() {
        return masterPasswordHashAlgorithm;
    }
}
