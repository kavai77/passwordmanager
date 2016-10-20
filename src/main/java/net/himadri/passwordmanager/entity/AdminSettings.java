package net.himadri.passwordmanager.entity;

public class AdminSettings {
    public static final int DEFAULT_ITERATIONS = 2000;
    public static final int RANDOM_PASSWORD_LENGTH = 20;
    public static final String CIPHER_ALGORITHM = "AES-CBC";
    public static final int DEFAULT_KEYLENGTH = 192;
    public static final int[] ALLOWED_KEYLENGTH = {128, 192, 256};
    public static final String DEFAULT_PBKDF2_ALGORITHM = "md5";
    public static final int DEFAULT_USER_PASSWORD_LENGTH = 27;
    public static final int DEFAULT_USER_TIMEOUT_LENGTH_SECONDS = 420;
}
