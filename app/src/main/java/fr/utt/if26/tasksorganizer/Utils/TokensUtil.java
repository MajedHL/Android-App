package fr.utt.if26.tasksorganizer.Utils;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;

public class TokensUtil {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();



    public static String generateToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
