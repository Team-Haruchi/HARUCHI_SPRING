package umc.haruchi.apiPayload.exception.handler;

import io.jsonwebtoken.JwtException;

public class JwtExpiredHandler extends JwtException {

    public JwtExpiredHandler(String message) {
        super(message);
    }

    public JwtExpiredHandler(String message, Throwable cause) {
        super(message, cause);
    }
}
