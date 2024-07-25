package umc.haruchi.web.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import umc.haruchi.domain.enums.IncomeCategory;

public class DayBudgetRequestDTO {

    @Getter
    public static class createIncomeDTO{

        Long incomeAmount;

        IncomeCategory category;

    }
}
