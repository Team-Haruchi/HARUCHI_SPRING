package umc.haruchi.converter;

import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.Expenditure;
import umc.haruchi.domain.Income;
import umc.haruchi.domain.MonthBudget;
import umc.haruchi.domain.enums.ClosingStatus;
import umc.haruchi.domain.enums.DayBudgetStatus;
import umc.haruchi.domain.enums.ExpenditureCategory;
import umc.haruchi.domain.enums.IncomeCategory;
import umc.haruchi.web.dto.DayBudgetRequestDTO;
import umc.haruchi.web.dto.DayBudgetResponseDTO;
import umc.haruchi.web.dto.MonthBudgetResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class DayBudgetConverter {

    public static DayBudget toDayBudget(Integer dayBudget, int day, DayBudgetStatus status, ClosingStatus closingStatus, MonthBudget monthBudget) {
        return DayBudget.builder()
                .dayBudget(dayBudget)
                .day(Long.valueOf(day))
                .dayBudgetStatus(status)
                .closingStatus(closingStatus)
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
        IncomeCategory category = null;
        switch(request.getCategory()) {
            case "미분류":
                category = IncomeCategory.NONE;
                break;
            case "용돈":
                category = IncomeCategory.ALLOWANCE;
                break;
            case "월급":
                category = IncomeCategory.SALARY;
                break;
            case "부수입":
                category = IncomeCategory.SIDELINE;
                break;
            case "상여":
                category = IncomeCategory.BONUS;
                break;
            case "금융소득":
                category = IncomeCategory.INTEREST;
                break;
            case "기타":
                category = IncomeCategory.OTHER;
                break;

        }
        return Income.builder()
                .dayBudget(dayBudget)
                .incomeAmount(request.getIncomeAmount())
                .incomeCategory(category)
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
        ExpenditureCategory category = null;
        switch (request.getCategory()){
            case "미분류":
                category = ExpenditureCategory.NONE;
                break;
            case "식비":
                category = ExpenditureCategory.FOOD;
                break;
            case "커피":
                category = ExpenditureCategory.COFFEE;
                break;
            case "교통":
                category = ExpenditureCategory.TRANSPORT;
                break;
            case "취미":
                category = ExpenditureCategory.HOBBY;
                break;
            case "패션":
                category = ExpenditureCategory.FASHION;
                break;
            case "교육":
                category = ExpenditureCategory.EDUCATION;
                break;
            case "경조사":
                category = ExpenditureCategory.EVENT;
                break;
            case "구독":
                category = ExpenditureCategory.SUBSCRIPTION;
                break;
            case "기타":
                category = ExpenditureCategory.OTHER;
                break;
        }

        return Expenditure.builder()
                .dayBudget(dayBudget)
                .expenditureAmount(request.getExpenditureAmount())
                .expenditureCategory(category)
                .build();
    }
}
