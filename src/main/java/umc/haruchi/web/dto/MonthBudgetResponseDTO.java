package umc.haruchi.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.enums.DayBudgetStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MonthBudgetResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateMonthResultDTO {
        Long id;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMonthResultDTO {
        Long id;
        LocalDateTime updatedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetMonthResultDTO {
        Long monthBudget;
        Long usedAmount;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetMonthUsedPercentResultDTO {
        double monthUsedPercent;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetWeekBudgetResultDTO {
        Long day;
        Integer dayBudget;
        DayBudgetStatus status;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetWeekBudgetResultListDTO {
        List<GetWeekBudgetResultDTO> weekBudget;
        Integer month;
        Integer week;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetMonthLeftNowResultDTO {
        Integer leftDay;
        Long leftBudget;
    }
}
