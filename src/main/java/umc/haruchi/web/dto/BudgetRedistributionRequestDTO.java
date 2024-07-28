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

        @NotNull(message = "source id는 필수 입력 값입니다.")
        Long sourceId; //여기서

        Long targetId; //여기로
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class createPullDTO {

        RedistributionOption redistributionOption;

        @NotNull(message = "amount 값은 필수 입력 값입니다.")
        Long amount;

        Long sourceId; //여기서

        @NotNull(message = "source id는 필수 입력 값입니다.")
        Long targetId; //여기로
    }
}
