package net.himadri.passwordmanager.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class BackupData {
    Long id;
    Long backupDate;
    int numberOfPasswords;
    String masterPasswordHashAlgorithm;
}
