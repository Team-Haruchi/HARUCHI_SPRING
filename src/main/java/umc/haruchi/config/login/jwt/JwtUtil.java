package umc.haruchi.config.login.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import umc.haruchi.config.login.auth.MemberDetail;
import umc.haruchi.config.redis.RedisUtil;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Long accessExpMs;
    private final Long refreshExpMs;
    private final RedisUtil redisUtil;

    public JwtUtil(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.token.access-expiration-time}") Long access,
            @Value("${spring.jwt.token.refresh-expiration-time}") Long refresh,
            RedisUtil redis) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
        accessExpMs = access;
        refreshExpMs = refresh;
        redisUtil = redis;
    }

    // jwt 토큰을 입력 받아 토큰의 페이로드에서 사용자 이름(Username) 추출
    public String getUserName(String token) throws SignatureException {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // jwt 토큰을 입력 받아 토큰의 페이로드에서 사용자 이름(roll) 추출
    public String getRoles(String token) throws SignatureException {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    // jwt 토큰의 페이로드에서 만료 시간을 검색, 밀리초 단위의 Long 값으로 반환
    public long getExpTime(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .getTime();
    }

    // 토큰 발급
    public String tokenProvider(MemberDetail memberDetail, Instant expiration) {
        Instant issuedAt = Instant.now();

        String authorities = memberDetail.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(memberDetail.getUsername())
                .claim("role", authorities)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    // memberDetail 객체에 대해 새로운 jwt 액세스 토큰 생성
    public String createJwtAccessToken(MemberDetail memberDetail) {
        Instant expiration = Instant.now().plusSeconds(accessExpMs);

        return tokenProvider(memberDetail, expiration);
    }

    // memberDetail 객체에 대해 새로운 jwt 리프레시 토큰 생성
    public String createJwtRefreshToken(MemberDetail memberDetail) {
        Instant expiration = Instant.now().plusSeconds(refreshExpMs);

        String refreshToken = tokenProvider(memberDetail, expiration);

        // 레디스에 저장
        redisUtil.save(
                memberDetail.getUsername(),
                refreshToken,
                refreshExpMs,
                TimeUnit.MILLISECONDS
        );

        return refreshToken;
    }

//    private final Key key;
//    private final RedisUtil redisUtil;
//
//    public JwtTokenProvider(@Value("${spring.jwt.secret}") String secretKey, // 256 bit 이상으로 설정
//                            RedisUtil redisUtil) {
//        //byte[] secretByteKey = DatatypeConverter.parseBase64Binary(secretKey);
//        byte[] secretByteKey = Decoders.BASE64.decode(secretKey);
//        this.key = Keys.hmacShaKeyFor(secretByteKey);
//        this.redisUtil = redisUtil;
//    }
//
//    // 유저 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
//    public MemberResponseDTO.LoginJwtTokenDTO generateToken(Authentication authentication) {
//
//        // 권한 가져오기
//        String authorities = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));
//
//        long now = (new Date()).getTime();
//
//        // AccessToken 생성
//        String accessToken = Jwts.builder()
//                .setSubject(authentication.getName())
//                .claim("auth", authorities)
////                .setIssuedAt(new Date(now))
//                .setExpiration(new Date(now + 86400000))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//
//        // RefreshToken 생성
//        String refreshToken = Jwts.builder()
//                .setExpiration(new Date(now + 86400000))
////                .setIssuedAt(new Date(now))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//
//        // redis에 refreshToken 넣기
//        redisUtil.setValues(authentication.getName(), refreshToken, Duration.ofMillis(System.currentTimeMillis() + 86400000 * 3));
//
//        return MemberResponseDTO.LoginJwtTokenDTO.builder()
//                .grantType("Bearer")
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .refreshTokenExpirationTime(now + 86400000 * 3)
//                .build();
//    }
//
//    // JWT token을 복호화해 token에 들어있는 정보를 꺼내는 메서드
//    public Authentication getAuthentication(String accessToken) {
//
//        //토큰 복호화
//        Claims claims = parseClaims(accessToken);
//
//        if (claims.get("auth") != null) {
//            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
//        }
//
//        // 클레임에서 권한 정보 가져오기
//        Collection<? extends GrantedAuthority> authorities =
//                Arrays.stream(claims.get("auth").toString().split(","))
//                        .map(SimpleGrantedAuthority::new)
//                        .collect(Collectors.toList());
//
//        // UserDetails 객체를 만들어서 Authentication 리턴
//        UserDetails principal = new User(claims.getSubject(), "", authorities);
//        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
//    }
//
//    // token 정보를 검증하는 메서드
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//            return true;
//        } catch (io.jsonwebtoken.security.SignatureException | MalformedJwtException e) {
//            log.info("Invalid JWT token", e);
//        } catch (ExpiredJwtException e) {
//            log.info("Expired JWT token", e);
//        } catch (UnsupportedJwtException e) {
//            log.info("Unsupported JWT token", e);
//        } catch (IllegalArgumentException e) {
//            log.info("JWT claims string is empty.", e);
//        }
//        return false;
//    }
//
//    // 로그아웃용 - accessToken 만료시키기
//    public Long getExpiration(String accessToken) {
//
//        // accessToken 남은 유효 시간
//        Date expiration = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody().getExpiration();
//
//        // 현재 시간
//        Long now = new Date().getTime();
//        return expiration.getTime() - now;
//    }
//
//    // secretKey를 이용해 Token Parsing
//    private Claims parseClaims(String accessToken) {
//        try {
//            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
//        } catch (ExpiredJwtException e) {
//            return e.getClaims();
//        }
//    }
}
