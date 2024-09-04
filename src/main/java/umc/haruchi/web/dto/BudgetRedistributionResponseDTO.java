package umc.haruchi.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.haruchi.domain.enums.ExpenditureCategory;
import umc.haruchi.domain.enums.IncomeCategory;
import umc.haruchi.domain.enums.RedistributionOption;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BudgetRedistributionResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetPushResultDTO {
        Long pushId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetPullResultDTO {
        Long pullId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetClosingResultDTO {
        Long closingId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetCalculatedAmountResultDTO {
        Long calculatedAmount;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetIncomeDTO {
        IncomeCategory incomeCategory;
        Long incomeAmount;
        Long incomeId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetExpenditureDTO {
        ExpenditureCategory expenditureCategory;
        Long expenditureAmount;
        Long expenditureId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetPullDTO {
        RedistributionOption redistributionOption;
        Long sourceDay; //여기서
        Long targetDay; //여기로 당겨왔다
        Long pullId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetPushDTO {
        RedistributionOption redistributionOption;
        Long sourceDay; //여기서
        Long targetDay; //여기로 당겨왔다
        Long pushId;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetReceiptListDTO {
        Integer dayBudget; // 오늘 최종 하루치
        Long todayExpenditureAmount; // 지출 총 합
        List<GetIncomeDTO> incomeList;
        List<GetExpenditureDTO> expenditureList;
        List<GetPullDTO> pullList;
        List<GetPushDTO> pushList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetClosingCheckDTO {
        Boolean check;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetClosingCheckLastDTO {
        Integer year;
        Integer month;
        Integer day;
    }
}
