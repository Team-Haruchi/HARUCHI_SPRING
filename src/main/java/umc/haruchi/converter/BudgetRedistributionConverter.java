package umc.haruchi.converter;

import umc.haruchi.domain.DayBudget;
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

    public static BudgetRedistributionResponseDTO.BudgetPushResultDTO ToBudgetPushResultDTO(PushPlusClosing pushPlusClosing) {
        return BudgetRedistributionResponseDTO.BudgetPushResultDTO.builder()
                .pushId(pushPlusClosing.getId())
                .createdAt(LocalDate.now())
                .build();
    }
}
