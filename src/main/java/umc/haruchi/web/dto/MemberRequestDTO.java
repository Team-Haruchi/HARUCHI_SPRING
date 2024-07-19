package umc.haruchi.web.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequestDTO {

    @Getter
    public static class MemberJoinDTO {

        @NotNull
        private Long monthBudget;

        @NotBlank
        @Size(min = 1, max = 5)
        private String name;

        @NotBlank
        @Size(min = 10, max = 20)
        @Email
        private String email;

        @NotBlank
        private String password;
    }

//    @Getter
//    public static class EmailSendDTO {
//
//        @NotNull
//        @Size(min = 10, max = 20)
//        @Email
//        private String email;
//    }
//
//    @Getter
//    public static class EmailVerifyDTO {
//
//        @NotNull
//        @Size(min = 10, max = 20)
//        @Email
//        private String email;
//
//        @NotBlank
//        private String code;
//    }
}
