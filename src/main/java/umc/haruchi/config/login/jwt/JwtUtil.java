package umc.haruchi.config.login.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import umc.haruchi.apiPayload.code.status.ErrorStatus;
import umc.haruchi.apiPayload.exception.handler.JwtExceptionHandler;
import umc.haruchi.config.login.auth.MemberDetailService;
import umc.haruchi.repository.MemberRepository;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtUtil implements InitializingBean {
    // 토큰의 생성, 검증, 토큰에서 인증 정보 얻기, 유효 시간 얻기 등 토큰을 활용(Util)하는 class

    private final RedisTemplate redisTemplate;
    @Value("${spring.jwt.secret}")
    private String secret;
    private static SecretKey secretKey;
    private static Long accessExpireMs;
    private static Long refreshExpireMs;
    private final MemberDetailService memberDetailService;

    @Value("${spring.jwt.token.access_expiration}")
    private void setAccessExpireMs(Long expire) {
        accessExpireMs = expire;
    }

    @Value("${spring.jwt.token.refresh_expiration}")
    private void setRefreshExpireMs(Long expire) {
        refreshExpireMs = expire;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public static String getEmail(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
    }

    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) { // 띄어쓰기 삭제
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
            throw new JwtExceptionHandler(ErrorStatus.WRONG_TYPE_SIGNATURE.getMessage(), e);
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            throw new JwtExceptionHandler(ErrorStatus.TOKEN_EXPIRED.getMessage(), e);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            throw new JwtExceptionHandler(ErrorStatus.WRONG_TYPE_TOKEN.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            throw new JwtExceptionHandler(ErrorStatus.NOT_VALID_TOKEN.getMessage(), e);
        }
    }

    public static String getRole(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    public static boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    public static long getExpiration(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .getTime();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = memberDetailService.loadUserByUsername(this.getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public static String createNewAccessJwt(Long memberId, String email, String role) {
        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .claim("memberId", memberId)
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(secretKey)
                .compact();
    }

    // 기존 토큰 발급 기능 - 보안 강화 시 주석 처리 해제
//    public static String createRefreshJwt(Long memberId, String email, String role) {
//        return Jwts.builder()
//                .header()
//                .add("typ", "JWT")
//                .and()
//                .claim("memberId", memberId)
//                .claim("email", email)
//                .claim("role", role)
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + refreshExpireMs))
//                .signWith(secretKey)
//                .compact();
//    }
//
//    public static String createAccessJwt(Long memberId, String email, String role) {
//        return Jwts.builder()
//                .header()
//                .add("typ", "JWT")
//                .and()
//                .claim("memberId", memberId)
//                .claim("email", email)
//                .claim("role", role)
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + accessExpireMs))
//                .signWith(secretKey)
//                .compact();
//    }

    public static String getPassword(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("password", String.class);
    }
}