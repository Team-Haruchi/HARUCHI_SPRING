package umc.haruchi.converter;

import umc.haruchi.domain.*;
import umc.haruchi.domain.enums.RedistributionOption;
import umc.haruchi.web.dto.BudgetRedistributionRequestDTO;
import umc.haruchi.web.dto.BudgetRedistributionResponseDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    public static BudgetRedistributionResponseDTO.BudgetPushResultDTO toBudgetPushResultDTO(PushPlusClosing pushPlusClosing) {
        return BudgetRedistributionResponseDTO.BudgetPushResultDTO.builder()
                .pushId(pushPlusClosing.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static BudgetRedistributionResponseDTO.BudgetPullResultDTO toBudgetPullResultDTO(PullMinusClosing pullMinusClosing) {
        return BudgetRedistributionResponseDTO.BudgetPullResultDTO.builder()
                .pullId(pullMinusClosing.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static BudgetRedistributionResponseDTO.BudgetClosingResultDTO toBudgetClosingResultDTO(PushPlusClosing pushPlusClosing) {
        return BudgetRedistributionResponseDTO.BudgetClosingResultDTO.builder()
                .closingId(pushPlusClosing.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static BudgetRedistributionResponseDTO.BudgetClosingResultDTO toBudgetClosingResultDTO(PullMinusClosing pullMinusClosing) {
        return BudgetRedistributionResponseDTO.BudgetClosingResultDTO.builder()
                .closingId(pullMinusClosing.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static BudgetRedistributionResponseDTO.GetCalculatedAmountResultDTO toGetCalculatedAmountResultDTO(Long amount) {
        return BudgetRedistributionResponseDTO.GetCalculatedAmountResultDTO.builder()
                .calculatedAmount(amount)
                .build();
    }

    public static BudgetRedistributionResponseDTO.GetIncomeDTO toGetIncomeDTO(Income income) {
        return BudgetRedistributionResponseDTO.GetIncomeDTO.builder()
                .incomeAmount(income.getIncomeAmount())
                .incomeCategory(income.getIncomeCategory())
                .incomeId(income.getId())
                .createdAt(income.getCreatedAt())
                .build();
    }

    public static BudgetRedistributionResponseDTO.GetExpenditureDTO toGetExpenditureDTO(Expenditure expenditure) {
        return BudgetRedistributionResponseDTO.GetExpenditureDTO.builder()
                .expenditureAmount(expenditure.getExpenditureAmount())
                .expenditureCategory(expenditure.getExpenditureCategory())
                .expenditureId(expenditure.getId())
                .createdAt(expenditure.getCreatedAt())
                .build();
    }

    public static BudgetRedistributionResponseDTO.GetPullDTO toGetPullDTO(PullMinusClosing pullMinusClosing) {
        BudgetRedistributionResponseDTO.GetPullDTO.GetPullDTOBuilder builder = BudgetRedistributionResponseDTO.GetPullDTO.builder()
                .redistributionOption(pullMinusClosing.getRedistributionOption())
                .targetDay(pullMinusClosing.getTargetDayBudget().getDay())
                .createdAt(pullMinusClosing.getCreatedAt())
                .pullId(pullMinusClosing.getId());

        // redistributionOption이 DATE일 때만 sourceDay를 설정
        if (pullMinusClosing.getRedistributionOption() == RedistributionOption.DATE) {
            builder.sourceDay(pullMinusClosing.getSourceDayBudget().getDay());
        }

        return builder.build();
    }

    public static BudgetRedistributionResponseDTO.GetPushDTO toGetPushDTO(PushPlusClosing pushPlusClosing) {
        BudgetRedistributionResponseDTO.GetPushDTO.GetPushDTOBuilder builder = BudgetRedistributionResponseDTO.GetPushDTO.builder()
                .redistributionOption(pushPlusClosing.getRedistributionOption())
                .sourceDay(pushPlusClosing.getSourceDayBudget().getDay())
                .createdAt(pushPlusClosing.getCreatedAt())
                .pushId(pushPlusClosing.getId());

        // redistributionOption이 DATE일 때만 targetDay 설정
        if (pushPlusClosing.getRedistributionOption() == RedistributionOption.DATE) {
            builder.targetDay(pushPlusClosing.getTargetDayBudget().getDay());
        }

        return builder.build();
    }

    public static BudgetRedistributionResponseDTO.GetReceiptListDTO ToGetReceiptListDTO(List<Income> incomeList, List<Expenditure> expenditureList, List<PullMinusClosing> pullList, List<PushPlusClosing> pushList, Integer dayBudget, Long totalExpenditureAmount) {
        List<BudgetRedistributionResponseDTO.GetIncomeDTO> toGetIncomeDTOList = incomeList.stream()
                .map(BudgetRedistributionConverter::toGetIncomeDTO).toList();

        List<BudgetRedistributionResponseDTO.GetExpenditureDTO> toGetExpenditureDTOList = expenditureList.stream()
                .map(BudgetRedistributionConverter::toGetExpenditureDTO).toList();

        List<BudgetRedistributionResponseDTO.GetPullDTO> toGetPullDTOList = pullList.stream()
                .map(BudgetRedistributionConverter::toGetPullDTO).toList();

        List<BudgetRedistributionResponseDTO.GetPushDTO> toGetPushDTOList = pushList.stream()
                .map(BudgetRedistributionConverter::toGetPushDTO).toList();

        return BudgetRedistributionResponseDTO.GetReceiptListDTO.builder()
                .incomeList(toGetIncomeDTOList)
                .expenditureList(toGetExpenditureDTOList)
                .pullList(toGetPullDTOList)
                .pushList(toGetPushDTOList)
                .dayBudget(dayBudget)
                .todayExpenditureAmount(totalExpenditureAmount)
                .build();
    }

    public static BudgetRedistributionResponseDTO.GetClosingCheckDTO ToGetClosingCheckDTO(Boolean check) {
        return BudgetRedistributionResponseDTO.GetClosingCheckDTO.builder()
                .check(check)
                .build();
    }

    public static BudgetRedistributionResponseDTO.GetClosingCheckLastDTO ToGetClosingCheckLastDTO(LocalDate last) {
        return BudgetRedistributionResponseDTO.GetClosingCheckLastDTO.builder()
                .year(last.getYear())
                .month(last.getMonthValue())
                .day(last.getDayOfMonth())
                .build();
    }
}
