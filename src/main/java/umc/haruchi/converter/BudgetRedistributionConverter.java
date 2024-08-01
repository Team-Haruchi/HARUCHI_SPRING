package umc.haruchi.converter;

import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.PullMinusClosing;
import umc.haruchi.domain.PushPlusClosing;
import umc.haruchi.web.dto.BudgetRedistributionRequestDTO;
import umc.haruchi.web.dto.BudgetRedistributionResponseDTO;

import java.time.LocalDate;

public class BudgetRedistributionConverter {

    public static PushPlusClosing toPush(BudgetRedistributionRequestDTO.createPushDTO requestDTO, DayBudget source, DayBudget target) {
        return PushPlusClosing.builder()
                .closingOption(false)
                .redistributionOption(requestDTO.getRedistributionOption())
                .amount(requestDTO.getAmount())
                .sourceDayBudget(source)
                .targetDayBudget(target)
                .build();
    }

    public static PullMinusClosing toPull(BudgetRedistributionRequestDTO.createPullDTO requestDTO, DayBudget source, DayBudget target) {
        return PullMinusClosing.builder()
                .closingOption(false)
                .redistributionOption(requestDTO.getRedistributionOption())
                .amount(requestDTO.getAmount())
                .sourceDayBudget(source)
                .targetDayBudget(target)
                .build();
    }

    public static PushPlusClosing toPlusClosing(BudgetRedistributionRequestDTO.createClosingDTO requestDTO, DayBudget dayBudget) {
        return PushPlusClosing.builder()
                .closingOption(true)
                .redistributionOption(requestDTO.getRedistributionOption())
                .amount(requestDTO.getAmount())
                .sourceDayBudget(dayBudget)
                .targetDayBudget(null)
                .build();
    }

    public static PullMinusClosing toMinusClosing(BudgetRedistributionRequestDTO.createClosingDTO requestDTO, DayBudget dayBudget) {
        return PullMinusClosing.builder()
                .closingOption(true)
                .redistributionOption(requestDTO.getRedistributionOption())
                .amount(requestDTO.getAmount())
                .sourceDayBudget(null)
                .targetDayBudget(dayBudget)
                .build();
    }

    public static BudgetRedistributionResponseDTO.budgetPushResultDTO ToBudgetPushResultDTO(PushPlusClosing pushPlusClosing) {
        return BudgetRedistributionResponseDTO.budgetPushResultDTO.builder()
                .pushId(pushPlusClosing.getId())
                .createdAt(LocalDate.now())
                .build();
    }

    public static BudgetRedistributionResponseDTO.budgetPullResultDTO ToBudgetPullResultDTO(PullMinusClosing pullMinusClosing) {
        return BudgetRedistributionResponseDTO.budgetPullResultDTO.builder()
                .pullId(pullMinusClosing.getId())
                .createdAt(LocalDate.now())
                .build();
    }

    public static BudgetRedistributionResponseDTO.budgetClosingResultDTO ToBudgetClosingResultDTO(PushPlusClosing pushPlusClosing) {
        return BudgetRedistributionResponseDTO.budgetClosingResultDTO.builder()
                .closingId(pushPlusClosing.getId())
                .createdAt(LocalDate.now())
                .build();
    }

    public static BudgetRedistributionResponseDTO.budgetClosingResultDTO ToBudgetClosingResultDTO(PullMinusClosing pullMinusClosing) {
        return BudgetRedistributionResponseDTO.budgetClosingResultDTO.builder()
                .closingId(pullMinusClosing.getId())
                .createdAt(LocalDate.now())
                .build();
    }

    public static BudgetRedistributionResponseDTO.getCalculatedAmountResultDTO ToGetCalculatedAmountResultDTO(Long amount) {
        return BudgetRedistributionResponseDTO.getCalculatedAmountResultDTO.builder()
                .calculatedAmount(amount)
                .build();
    }
}
