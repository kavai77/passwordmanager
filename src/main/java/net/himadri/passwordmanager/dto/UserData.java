package net.himadri.passwordmanager.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import static net.himadri.passwordmanager.entity.AdminSettings.CIPHER_ALGORITHM;
import static net.himadri.passwordmanager.entity.AdminSettings.DEFAULT_HASH_ALGORITHM;
import static net.himadri.passwordmanager.entity.AdminSettings.DEFAULT_ITERATIONS;
import static net.himadri.passwordmanager.entity.AdminSettings.DEFAULT_KEYLENGTH;
import static net.himadri.passwordmanager.entity.AdminSettings.DEFAULT_PBKDF2_ALGORITHM;

@Value
@Builder
@Jacksonized
public class UserData {
    String userId;
    boolean authenticated;
    String nickName;
    boolean registered;
    int iterations;
    String cipherAlgorithm;
    String masterPasswordHashAlgorithm;
    int keyLength;
    String pbkdf2Algorithm;
    String salt;
    UserSettingsData userSettings;

    public static UserData userRegisteredInstance(String userId, String nickName,
                                                  int iterations,
                                                  String cipherAlgorithm, String masterPasswordHashAlgorithm,
                                                  int keyLength, String pbkdf2Algorithm, String salt,
                                                  UserSettingsData userSettings) {
        return new UserData(userId, true, nickName, true, iterations, cipherAlgorithm, masterPasswordHashAlgorithm, keyLength,
                pbkdf2Algorithm, salt, userSettings);
    }

    public static UserData userUnregisteredInstance(String userId, String nickName,
                                                     UserSettingsData userSettings) {
        return new UserData(userId, true, nickName, false, DEFAULT_ITERATIONS, CIPHER_ALGORITHM, DEFAULT_HASH_ALGORITHM, DEFAULT_KEYLENGTH,
                DEFAULT_PBKDF2_ALGORITHM, null, userSettings);
    }

    @Value
    @Builder
    @Jacksonized
    public static class UserSettingsData {
        int defaultPasswordLength;
        int timeoutLengthSeconds;
    }
}
