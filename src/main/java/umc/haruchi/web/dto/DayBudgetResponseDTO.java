package umc.haruchi.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class DayBudgetResponseDTO {

    // 하루 예산 금액 조회 API 구현
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class getDayBudget {
        Integer dayBudget;
    }

    // 날짜별 예산 금액 조회 API 구현
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class getBudget {
        List<Integer> budget;
    }

}
