package net.himadri.passwordmanager.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private String masterPasswordHashAlgorithm;
    private Date backupDate;
}
