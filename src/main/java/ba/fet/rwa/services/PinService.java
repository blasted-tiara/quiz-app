package ba.fet.rwa.services;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

public class PinService {
    private static final long SESSION_EXPIRATION_TIME =TimeUnit.HOURS.toMillis(1);

    private static final Map<String, PinSession> pinSessions = new HashMap<>();

    public String createPinForQuiz(Long quizId) {
        String pin = generatePin();

        // check if pin exists, if yes and it's not expired, generate new pin
        while (pinSessions.containsKey(pin) && !pinSessions.get(pin).isExpired()) {
            pin = generatePin();
        }

        createPinSession(quizId, pin);
        return pin;
    }

    public static PinSession getSession(String pin) {
        if (validPin(pin)) {
            return pinSessions.get(pin);
        } else {
            return null;
        }
    }

    public static boolean validPin(String pin) {
        if (!pinSessions.containsKey(pin)) {
            return false;
        }

        PinSession pinSession = pinSessions.get(pin);
        if (pinSession.isExpired()) {
            pinSessions.remove(pin);
            return false;
        }

        return true;
    }

    private String generatePin() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[8];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private void createPinSession(Long quizId, String pin) {
        PinSession pinSession = new PinSession(quizId, System.currentTimeMillis() + SESSION_EXPIRATION_TIME);
        pinSessions.put(pin, pinSession);
    }

    private static class PinSession {
        private Long quizId;
        private long expirationTime;

        public PinSession(Long quizId, long expirationTime) {
            this.quizId = quizId;
            this.expirationTime = expirationTime;
        }

        public Long getQuizId() {
            return quizId;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
}
