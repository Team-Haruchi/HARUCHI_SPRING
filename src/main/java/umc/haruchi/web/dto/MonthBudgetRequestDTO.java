package umc.haruchi.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

public class MonthBudgetRequestDTO {

    @Getter
    public static class CreateMonthDTO {
        @NotNull(message = "한달 예산은 필수 입력 값입니다.")
        private Long monthBudget;
    }

    @Getter
    public static class UpdateMonthDTO {
        private Long monthBudget;
    }
}
