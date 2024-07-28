package umc.haruchi.converter;

import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.PullMinusClosing;
import umc.haruchi.domain.PushPlusClosing;
import umc.haruchi.web.dto.BudgetRedistributionRequestDTO;
import umc.haruchi.web.dto.BudgetRedistributionResponseDTO;

import java.time.LocalDate;

public class BudgetRedistributionConverter {

    public static PushPlusClosing toPushPlusClosing(BudgetRedistributionRequestDTO.createPushDTO requestDTO, DayBudget source, DayBudget target) {
        return PushPlusClosing.builder()
                .closingOption(false)
                .redistributionOption(requestDTO.getRedistributionOption())
                .amount(requestDTO.getAmount())
                .sourceDayBudget(source)
                .targetDayBudget(target)
                .build();
    }

    public static PullMinusClosing toPullMinusClosing(BudgetRedistributionRequestDTO.createPullDTO requestDTO, DayBudget source, DayBudget target) {
        return PullMinusClosing.builder()
                .closingOption(false)
                .redistributionOption(requestDTO.getRedistributionOption())
                .amount(requestDTO.getAmount())
                .sourceDayBudget(source)
                .targetDayBudget(target)
                .build();
    }

    public static BudgetRedistributionResponseDTO.BudgetPushResultDTO ToBudgetPushResultDTO(PushPlusClosing pushPlusClosing) {
        return BudgetRedistributionResponseDTO.BudgetPushResultDTO.builder()
                .pushId(pushPlusClosing.getId())
                .createdAt(LocalDate.now())
                .build();
    }

    public static BudgetRedistributionResponseDTO.BudgetPullResultDTO ToBudgetPullResultDTO(PullMinusClosing pullMinusClosing) {
        return BudgetRedistributionResponseDTO.BudgetPullResultDTO.builder()
                .pullId(pullMinusClosing.getId())
                .createdAt(LocalDate.now())
                .build();
    }
}
