package net.himadri.passwordmanager.entity;

public class Settings {
    public static final int DEFAULT_ITERATIONS = 2000;
    public static final int PASSWORD_LENGTH = 20;
    public static final String CIPHER_ALGORITHM = "AES-CBC";
    public static final int DEFAULT_KEYLENGTH = 192;
    public static final int[] ALLOWED_KEYLENGTH = {128, 192, 256};
    public static final String DEFAULT_PBKDF2_ALGORITHM = "md5";
}
