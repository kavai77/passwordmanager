package net.himadri.passwordmanager.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;

@Entity
public class Backup {

    @Id
    private Long id;
    @Index
    private String userId;
    private String masterPasswordHash;
    private int iterations;
    private String cipherAlgorithm;
    private int keyLength;
    private String pbkdf2Algorithm;
    private Date backupDate;

    public Backup() {
    }

    public Backup(String userId, String masterPasswordHash, int iterations, String cipherAlgorithm, int keyLength,
                  String pbkdf2Algorithm, Date backupDate) {
        this.userId = userId;
        this.masterPasswordHash = masterPasswordHash;
        this.iterations = iterations;
        this.cipherAlgorithm = cipherAlgorithm;
        this.keyLength = keyLength;
        this.pbkdf2Algorithm = pbkdf2Algorithm;
        this.backupDate = backupDate;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getMasterPasswordHash() {
        return masterPasswordHash;
    }

    public void setMasterPasswordHash(String masterPasswordHash) {
        this.masterPasswordHash = masterPasswordHash;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public String getCipherAlgorithm() {
        return cipherAlgorithm;
    }

    public void setCipherAlgorithm(String cipherAlgorithm) {
        this.cipherAlgorithm = cipherAlgorithm;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public void setKeyLength(int keyLength) {
        this.keyLength = keyLength;
    }

    public String getPbkdf2Algorithm() {
        return pbkdf2Algorithm;
    }

    public void setPbkdf2Algorithm(String pbkdf2Algorithm) {
        this.pbkdf2Algorithm = pbkdf2Algorithm;
    }

    public Date getBackupDate() {
        return backupDate;
    }

    public void setBackupDate(Date backupDate) {
        this.backupDate = backupDate;
    }
}
