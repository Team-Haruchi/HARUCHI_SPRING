package umc.haruchi.converter;

import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.Expenditure;
import umc.haruchi.domain.Income;
import umc.haruchi.domain.MonthBudget;
import umc.haruchi.domain.enums.DayBudgetStatus;
import umc.haruchi.web.dto.DayBudgetRequestDTO;
import umc.haruchi.web.dto.DayBudgetResponseDTO;
import umc.haruchi.web.dto.MonthBudgetResponseDTO;

import java.time.LocalDate;
import java.util.List;

public class DayBudgetConverter {

    public static DayBudget toDayBudget(Integer dayBudget, int day, DayBudgetStatus status, MonthBudget monthBudget) {
        return DayBudget.builder()
                .dayBudget(dayBudget)
                .day(Long.valueOf(day))
                .dayBudgetStatus(status)
                .monthBudget(monthBudget)
                .build();
    }

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


    public static DayBudgetResponseDTO.expenditureReg toCreateExpenditure(Expenditure expenditure) {
        return DayBudgetResponseDTO.expenditureReg.builder()
                .expenditureId(expenditure.getId())
                .createdAt(LocalDate.now())
                .build();
    }

    public static Expenditure toExpenditure(DayBudgetRequestDTO.createExpenditureDTO request, DayBudget dayBudget) {
        return Expenditure.builder()
                .dayBudget(dayBudget)
                .expenditureAmount(request.getExpenditureAmount())
                .expenditureCategory(request.getCategory())
                .build();
    }
}
