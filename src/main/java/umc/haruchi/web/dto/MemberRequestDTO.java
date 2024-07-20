package umc.haruchi.web.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

public class MemberRequestDTO {

    @Getter
    public static class MemberJoinDTO {

        @NotNull
        private Long monthBudget;

        @NotBlank
        @Length(min = 1, max = 5)
        @Pattern(
                regexp = "[a-z]",
                message = "닉네임은 1~5자의 영문 소문자, 숫자로 이루어져야 합니다."
        )
        private String name;

        @NotBlank
        @Length(min = 11, max = 30)
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotBlank
        @Length(min = 4, max = 65)
        private String password;

        @NotNull
        private boolean verifiedEmail;
    }
}