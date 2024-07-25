package umc.haruchi.apiPayload.exception.handler;

import io.jsonwebtoken.JwtException;

public class JwtInvalidHandler extends JwtException {

    public JwtInvalidHandler(String message) {
        super(message);
    }

    public JwtInvalidHandler(String message, Throwable cause) {
        super(message, cause);
    }
}
