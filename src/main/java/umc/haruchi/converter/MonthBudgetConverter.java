package umc.haruchi.converter;

import umc.haruchi.domain.MonthBudget;
import umc.haruchi.web.dto.MonthBudgetRequestDTO;
import umc.haruchi.web.dto.MonthBudgetResponseDTO;

import java.time.LocalDate;

public class MonthBudgetConverter {
    public static MonthBudgetResponseDTO.CreateMonthResultDTO toCreateMonthResultDTO(MonthBudget monthBudget) {
        return MonthBudgetResponseDTO.CreateMonthResultDTO.builder()
                .id(monthBudget.getId())
                .build();
    }

    public static MonthBudgetResponseDTO.UpdateMonthResultDTO toUpdateMonthResultDTO(MonthBudget monthBudget) {
        return MonthBudgetResponseDTO.UpdateMonthResultDTO.builder()
                .id(monthBudget.getId())
                .updatedAt(monthBudget.getUpdatedAt())
                .build();
    }

    public static MonthBudget toMonthBudget(Long monthBudget) {
        return MonthBudget.builder()
                .monthBudget(monthBudget)
                .build();
    }

    public static MonthBudgetResponseDTO.GetMonthResultDTO toGetMonthResultDTO(MonthBudget monthBudget) {
        return MonthBudgetResponseDTO.GetMonthResultDTO.builder()
                .monthBudget(monthBudget.getMonthBudget())
                .usedAmount(monthBudget.getUsedAmount())
                .createdAt(monthBudget.getCreatedAt())
                .build();
    }
}
