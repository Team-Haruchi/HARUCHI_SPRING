package umc.haruchi.web.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import umc.haruchi.domain.enums.ExpenditureCategory;
import umc.haruchi.domain.enums.IncomeCategory;

public class DayBudgetRequestDTO {

    @Getter
    public static class createIncomeDTO{
        @NotNull(message = "수입액은 필수 입력 값입니다.")
        @Positive
        Long incomeAmount;

        @NotNull(message = "수입 카테고리는 필수 입력 값입니다.")
        IncomeCategory category;

    }

    @Getter
    public static class createExpenditureDTO{
        @NotNull(message = "지출액은 필수 입력 값입니다.")
        @Positive
        Long expenditureAmount;

        @NotNull(message = "지출 카테고리은 필수 입력 값입니다.")
        ExpenditureCategory category;
    }
}
