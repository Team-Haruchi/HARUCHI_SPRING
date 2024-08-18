package umc.haruchi.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.haruchi.domain.DayBudget;

import java.time.LocalDate;
import java.util.List;

public class DayBudgetResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class getDayBudget {
        Integer dayBudget;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class getBudgetList {
        List<getBudget> budget;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class getBudget{
        Long day;
        Integer dayBudget;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class incomeReg{
        LocalDate createdAt;
        Long incomeId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class expenditureReg{
        LocalDate createdAt;
        Long expenditureId;
    }
}
