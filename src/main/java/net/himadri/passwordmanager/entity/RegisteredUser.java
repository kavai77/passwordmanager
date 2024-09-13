package net.himadri.passwordmanager.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.OnLoad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredUser {

    @Id
    private String userId;
    private String masterPasswordHash;
    private String masterPasswordHashAlgorithm;
    private String email;
    private int iterations;
    private String cipherAlgorithm;
    private int keyLength;
    private String pbkdf2Algorithm;
    private String salt;

    @OnLoad
    public void onLoad() {
        if (salt == null) {
            salt = userId;
        }
    }
}
