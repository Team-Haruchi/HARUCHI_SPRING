package umc.haruchi.config.login.jwt;

import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.haruchi.domain.MemberToken;
import umc.haruchi.repository.MemberTokenRepository;
import org.springframework.security.access.AccessDeniedException;
import umc.haruchi.web.dto.MemberResponseDTO;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenService {

    private final MemberTokenRepository memberTokenRepository;

    public MemberToken findByAnyToken(String token) throws AccessDeniedException {
        return memberTokenRepository.findByAccessToken(token)
                .or(() -> memberTokenRepository.findByRefreshToken(token))
                .orElseThrow(() -> new AccessDeniedException("존재하지 않거나 만료된 토큰입니다."));
    }

    public MemberToken findByAccessToken(String token) throws AccessDeniedException {
        return memberTokenRepository.findByAccessToken(token)
                .orElseThrow(() -> new AccessDeniedException("존재하지 않거나 만료된 액세스 토큰입니다."));
    }

    public MemberToken findByRefreshToken(String token) throws AccessDeniedException {
        return memberTokenRepository.findByRefreshToken(token)
                .orElseThrow(() -> new AccessDeniedException("존재하지 않거나 만료된 리프레시 토큰입니다."));
    }

    public void checkExpired(String token) {
        findByAnyToken(token);
    }

    @Transactional
    public void expire(String token) {
        MemberToken foundToken = findByAnyToken(token);
        memberTokenRepository.delete(foundToken);
    }

    @Transactional
    public MemberResponseDTO.LoginJwtTokenDTO refresh(String refreshToken) {
        MemberToken foundToken = findByRefreshToken(refreshToken);
        String newAccessToken = JwtUtil.createAccessJwt(foundToken.getMember().getId(), foundToken.getMember().getEmail(), null);
        String newRefreshToken = JwtUtil.createRefreshJwt(foundToken.getMember().getId(), foundToken.getMember().getEmail(), null);
        foundToken.setTokens(newAccessToken, newRefreshToken);
        memberTokenRepository.save(foundToken);

        return MemberResponseDTO.LoginJwtTokenDTO.builder()
                .grantType("Bearer")
                .accessToken(newAccessToken)
                .accessTokenExpiresAt(JwtUtil.getExpiration(newAccessToken))
                .refreshToken(newRefreshToken)
                .refreshTokenExpirationAt(JwtUtil.getExpiration(newRefreshToken))
                .build();
    }
}
