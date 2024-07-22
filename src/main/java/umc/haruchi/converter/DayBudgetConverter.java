package umc.haruchi.converter;

import umc.haruchi.web.dto.DayBudgetResponseDTO;

public class DayBudgetConverter {
    public static DayBudgetResponseDTO.getDayBudget toGetDayBudget(Integer todayBudget) {
        return DayBudgetResponseDTO.getDayBudget.builder()
                .dayBudget(todayBudget)
                .build();
    }



}
