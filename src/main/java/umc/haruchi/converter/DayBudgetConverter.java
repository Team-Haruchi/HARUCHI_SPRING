package umc.haruchi.converter;

import umc.haruchi.web.dto.DayBudgetResponseDTO;

import java.util.List;

public class DayBudgetConverter {

    public static DayBudgetResponseDTO.getDayBudget toGetDayBudget(Integer todayBudget) {
        return DayBudgetResponseDTO.getDayBudget.builder()
                .dayBudget(todayBudget)
                .build();
    }

    public static DayBudgetResponseDTO.getBudget toGetBudget(List<Integer> allBudget) {
        return DayBudgetResponseDTO.getBudget.builder()
                .budget(allBudget)
                .build();
    }
}
