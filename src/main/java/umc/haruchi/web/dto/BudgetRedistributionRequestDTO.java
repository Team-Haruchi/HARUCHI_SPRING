package umc.haruchi.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.haruchi.domain.enums.RedistributionOption;

public class BudgetRedistributionRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class createPushDTO {

        RedistributionOption redistributionOption;

        @Positive
        @NotNull(message = "amount 값은 필수 입력 값입니다.")
        Long amount;

        @NotNull(message = "sourceDay는 필수 입력 값입니다.")
        int sourceDay; //여기서

        Integer targetDay; //여기로
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class createPullDTO {

        @NotNull(message = "옵션은 필수 입력 값입니다.")
        RedistributionOption redistributionOption;

        @Positive
        @NotNull(message = "amount 값은 필수 입력 값입니다.")
        Long amount;

        Integer sourceDay; //여기서

        @NotNull(message = "targetDay는 필수 입력 값입니다.")
        int targetDay; //여기로
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class createClosingDTO {

        RedistributionOption redistributionOption; //고르게, 세이프박스, 딱 맞아 떨어질때는 zero

        @NotNull(message = "year 값은 필수 입력 값입니다.")
        int year; //말월 말일인 경우 내년 1월로 인식될 수 있어서

        @NotNull(message = "mount 값은 필수 입력 값입니다.")
        int month; //말일인 경우 다음달로 인식될 수 있어서

        @NotNull(message = "day 값은 필수 입력 값입니다.")
        int day;
    }
}
