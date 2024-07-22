package umc.haruchi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umc.haruchi.domain.MemberToken;

import java.util.Optional;

public interface MemberTokenRepository extends JpaRepository<MemberToken, Long> {

    Optional<MemberToken> findByAccessToken(String accessToken);
    Optional<MemberToken> findByRefreshToken(String refreshToken);
}
