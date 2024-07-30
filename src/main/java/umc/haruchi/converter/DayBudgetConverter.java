package umc.haruchi.converter;

import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.Income;
import umc.haruchi.web.dto.DayBudgetRequestDTO;
import umc.haruchi.web.dto.DayBudgetResponseDTO;

import java.time.LocalDate;
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

    public static Income toIncome(DayBudgetRequestDTO.createIncomeDTO request, DayBudget dayBudget) {
        return Income.builder()
                .dayBudget(dayBudget)
                .incomeAmount(request.getIncomeAmount())
                .incomeCategory(request.getCategory())
                .build();
    }

    public static DayBudgetResponseDTO.incomeReg toCreateIncome(Income income) {
        return DayBudgetResponseDTO.incomeReg.builder()
                .incomeId(income.getId())
                .createdAt(LocalDate.now())
                .build();
    }
}
