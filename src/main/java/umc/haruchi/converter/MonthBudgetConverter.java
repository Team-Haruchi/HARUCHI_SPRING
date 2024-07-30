package umc.haruchi.converter;

import umc.haruchi.domain.MonthBudget;
import umc.haruchi.web.dto.MonthBudgetRequestDTO;
import umc.haruchi.web.dto.MonthBudgetResponseDTO;

import java.time.LocalDate;

public class MonthBudgetConverter {
    public static MonthBudgetResponseDTO.CreateMonthResultDTO toCreateMonthResultDTO(MonthBudget monthBudget) {
        return MonthBudgetResponseDTO.CreateMonthResultDTO.builder()
                .id(monthBudget.getId())
                .createdAt(LocalDate.now())
                .build();
    }

    public static MonthBudget toMonthBudget(Long monthBudget) {
        return MonthBudget.builder()
                .monthBudget(monthBudget)
                .build();
    }
}
