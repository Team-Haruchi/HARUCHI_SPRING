package umc.haruchi.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MemberResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberJoinResultDTO {
        Long memberId;
        LocalDateTime createdAt;
    }

    // 기존 로그인 방식(access token과 refresh token 발급; 각각 만료 시간 존재)
//    @Builder
//    @Getter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class LoginJwtTokenDTO {
//        String grantType;
//        String accessToken;
//        Long accessTokenExpiresAt;
//        String refreshToken;
//        Long refreshTokenExpirationAt;
//    }

    // 새 로그인 방식(access token 발급; 만료 시간 없음)
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewLoginJwtTokenDTO {
        String grantType;
        String accessToken;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberDetailResultDTO {
        LocalDate createdAt;
        String email;
        String name;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberSafeBoxResultDTO {
        Long safeBox;
    }
}
