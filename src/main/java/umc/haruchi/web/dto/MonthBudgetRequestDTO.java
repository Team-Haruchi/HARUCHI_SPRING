package umc.haruchi.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

public class MonthBudgetRequestDTO {

    @Getter
    public static class CreateMonthDTO {
        @PositiveOrZero(message = "한달 예산은 0 이상이어야 합니다.")
        @NotNull(message = "한달 예산은 필수 입력 값입니다.")
        private Long monthBudget;
    }

    @Getter
    public static class UpdateMonthDTO {
        @PositiveOrZero(message = "한달 예산은 0 이상이어야 합니다.")
        @NotNull(message = "한달 예산은 필수 입력 값입니다.")
        private Long monthBudget;
    }
}
