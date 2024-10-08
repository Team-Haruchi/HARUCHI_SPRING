package umc.haruchi.web.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

public class MemberRequestDTO {

    @Getter
    public static class MemberJoinDTO {

        @NotNull(message = "한달예산은 필수 입력 값입니다.")
        @Positive(message = "0이 넘는 값을 입력해야합니다.")
        private Long monthBudget;

        @NotBlank(message = "이름은 필수 입력 값입니다.")
        @Length(max = 5)
        @Pattern(
                regexp = "^[ㄱ-ㅎㅏ-ㅣ가-힣]{1,5}$",
                message = "이름은 1~5자의 한글로만 이루어져야 합니다."
        )
        private String name;

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
//        @Length(min = 11, max = 30)
        @Pattern(
                regexp = "\\w+@\\w+\\.\\w+(\\.\\w+)?",
                message = "이메일 형식이 올바르지 않습니다."
        )
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Pattern(
                regexp = "^[a-zA-Z0-9~!@#$%^&*()]{8,30}",
                message = "비밀번호는 영어 대소문자, 숫자, 특수 문자로 구성돼야 합니다."
        )
        @Length(min = 8, max = 30)
        private String password;
    }

    @Getter
    public static class MemberLoginDTO {

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
//        @Length(min = 11, max = 30)
        @Pattern(
                regexp = "\\w+@\\w+\\.\\w+(\\.\\w+)?",
                message = "이메일 형식이 올바르지 않습니다."
        )
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Length(min = 8, max = 30)
        @Pattern(
                regexp = "^[a-zA-Z0-9~!@#$%^&*()]{8,30}",
                message = "비밀번호는 영어 대소문자, 숫자, 특수 문자로 구성돼야 합니다."
        )
        private String password;
    }

    @Getter
    public static class MemberWithdrawRequestDTO {

        @NotBlank(message = "액세스 토큰 값은 필수 입력 값입니다.")
        private String accessToken;

        @NotBlank(message = "리프레시 토큰 값은 필수 입력 값입니다.")
        private String refreshToken;

        @NotBlank(message = "탈퇴 이유는 필수 입력 값입니다.")
        @Length(min = 1, max = 20)
        private String reason;
    }
}
