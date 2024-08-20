package umc.haruchi.converter;

import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.Member;
import umc.haruchi.domain.MonthBudget;
import umc.haruchi.domain.enums.DayBudgetStatus;
import umc.haruchi.web.dto.MonthBudgetRequestDTO;
import umc.haruchi.web.dto.MonthBudgetResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    public static MonthBudget toMonthBudgetWithMonth(Long monthBudget, Integer year, Integer month) {
        return MonthBudget.builder()
                .monthBudget(monthBudget)
                .year(year)
                .month(month)
                .build();
    }

    public static MonthBudgetResponseDTO.GetMonthResultDTO toGetMonthResultDTO(MonthBudget monthBudget) {
        return MonthBudgetResponseDTO.GetMonthResultDTO.builder()
                .monthBudget(monthBudget.getMonthBudget())
                .usedAmount(monthBudget.getUsedAmount())
                .createdAt(monthBudget.getCreatedAt())
                .build();
    }

    public static MonthBudgetResponseDTO.GetMonthUsedPercentResultDTO toGetMonthUsedPercentResultDTO(double percent) {
        return MonthBudgetResponseDTO.GetMonthUsedPercentResultDTO.builder()
                .monthUsedPercent(percent)
                .build();
    }

    public static MonthBudgetResponseDTO.GetWeekBudgetResultDTO toGetWeekBudgetResultDTO(DayBudget dayBudget) {
        if(dayBudget == null)
            return null;

        return MonthBudgetResponseDTO.GetWeekBudgetResultDTO.builder()
                .day(dayBudget.getDay())
                .dayBudget(dayBudget.getDayBudget())
                .status(dayBudget.getDayBudgetStatus())
                .closingState(dayBudget.getClosingStatus())
                .build();
    }

    public static MonthBudgetResponseDTO.GetWeekBudgetResultListDTO toGetWeekBudgetResultListDTO(List<DayBudget> budgets, Integer month, Integer week) {
        List<MonthBudgetResponseDTO.GetWeekBudgetResultDTO> weekBudgetDTOList = budgets.stream()
                .map(MonthBudgetConverter::toGetWeekBudgetResultDTO).collect(Collectors.toList());

        return MonthBudgetResponseDTO.GetWeekBudgetResultListDTO.builder()
                .month(month)
                .week(week)
                .weekBudget(weekBudgetDTOList)
                .build();
    }

    public static MonthBudgetResponseDTO.GetMonthLeftNowResultDTO toGetMonthLeftNowResultDTO(Integer leftDay, Long leftBudget) {
        return MonthBudgetResponseDTO.GetMonthLeftNowResultDTO.builder()
                .leftDay(leftDay)
                .leftBudget(leftBudget)
                .build();
    }

    public static MonthBudget toMonthBudget(int year, int month, long amount, Member member){
        return MonthBudget.builder()
                .year(year)
                .month(month)
                .monthBudget(amount)
                .member(member)
                .build();
    }
}