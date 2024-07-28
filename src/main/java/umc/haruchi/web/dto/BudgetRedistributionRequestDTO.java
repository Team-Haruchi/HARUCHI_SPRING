package umc.haruchi.web.dto;

import jakarta.validation.constraints.NotNull;
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

        RedistributionOption redistributionOption;

        @NotNull(message = "amount 값은 필수 입력 값입니다.")
        Long amount;

        Integer sourceDay; //여기서

        @NotNull(message = "targetDay는 필수 입력 값입니다.")
        int targetDay; //여기로
    }
}
