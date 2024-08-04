package umc.haruchi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.haruchi.apiPayload.ApiResponse;
import umc.haruchi.config.login.auth.MemberDetail;
import umc.haruchi.converter.BudgetRedistributionConverter;
import umc.haruchi.domain.*;
import umc.haruchi.service.BudgetRedistributionService;
import umc.haruchi.web.dto.BudgetRedistributionRequestDTO;
import umc.haruchi.web.dto.BudgetRedistributionResponseDTO;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/budget-redistribution")
public class BudgetRedistributionController {

    private final BudgetRedistributionService budgetRedistributionService;

    @Operation(summary = "넘겨쓰기 API", description = "EVENLY(1/n),DATE(특정일),SAFEBOX(targetId null)")
    @PostMapping("/push")
    public ApiResponse<BudgetRedistributionResponseDTO.BudgetPushResultDTO> pushBudget(@Valid @RequestBody BudgetRedistributionRequestDTO.createPushDTO request,
                                                                                       @AuthenticationPrincipal MemberDetail memberDetail){
        PushPlusClosing pushPlusClosing = budgetRedistributionService.push(request, memberDetail.getMember().getId());
        return ApiResponse.onSuccess(BudgetRedistributionConverter.toBudgetPushResultDTO(pushPlusClosing));
    }

    @Operation(summary = "당겨쓰기 API", description = "EVENLY(1/n),DATE(특정일),SAFEBOX(sourceId null)")
    @PostMapping("/pull")
    public ApiResponse<BudgetRedistributionResponseDTO.BudgetPullResultDTO> pullBudget(@Valid @RequestBody BudgetRedistributionRequestDTO.createPullDTO request,
                                                                                       @AuthenticationPrincipal MemberDetail memberDetail){
        PullMinusClosing pushMinusClosing = budgetRedistributionService.pull(request, memberDetail.getMember().getId());
        return ApiResponse.onSuccess(BudgetRedistributionConverter.toBudgetPullResultDTO(pushMinusClosing));
    }

//    @Operation(summary = "지출 마감 API", description = "음수,양수,0으로 넘겨주시고, 0일 떄 옵션(EVENLY,SAFEBOX)은 ZERO로 넘겨주세요")
//    @PostMapping("/closing")
//    public ApiResponse<BudgetRedistributionResponseDTO.BudgetClosingResultDTO> closingBudget(@Valid @RequestBody BudgetRedistributionRequestDTO.createClosingDTO request,
//                                                                                             @AuthenticationPrincipal MemberDetail memberDetail){
//        if(request.getAmount() >= 0) {
//            PushPlusClosing pushPlusClosing = budgetRedistributionService.closingPlusOrZero(request, memberDetail.getMember().getId());
//            return ApiResponse.onSuccess(BudgetRedistributionConverter.toBudgetClosingResultDTO(pushPlusClosing));
//        }
//        else {
//            PullMinusClosing pullMinusClosing = budgetRedistributionService.closingMinus(request, memberDetail.getMember().getId());
//            return ApiResponse.onSuccess(BudgetRedistributionConverter.toBudgetClosingResultDTO(pullMinusClosing));
//        }
//    }

    @Operation(summary = "지출 마감 API", description = "0일떄는 옵션을 ZERO로 넘겨주시고, 마지막 날의 1/n 방식은 에러처리 되어있습니다.")
    @PostMapping("/closing")
    public ApiResponse<BudgetRedistributionResponseDTO.BudgetClosingResultDTO> closingBudget(@Valid @RequestBody BudgetRedistributionRequestDTO.createClosingDTO request,
                                                                                             @AuthenticationPrincipal MemberDetail memberDetail){
        boolean plusOrZeroOrMinus = budgetRedistributionService.plusOrZeroOrMinus(request, memberDetail.getMember().getId());
        if(plusOrZeroOrMinus) {
            PushPlusClosing pushPlusClosing = budgetRedistributionService.closingPlusOrZero(request, memberDetail.getMember().getId());
            return ApiResponse.onSuccess(BudgetRedistributionConverter.toBudgetClosingResultDTO(pushPlusClosing));
        }
        else {
            PullMinusClosing pullMinusClosing = budgetRedistributionService.closingMinus(request, memberDetail.getMember().getId());
            return ApiResponse.onSuccess(BudgetRedistributionConverter.toBudgetClosingResultDTO(pullMinusClosing));
        }
    }

    @Operation(summary = "지출 마감에서 1/n경우의 하루 차감/분배 값 조회 API", description = "지출 마감 영수증에서 고르게 분배하기 선택 시 얼마씩 분배/차감할 지 알려주는 API입니다. +, - 값을 넘겨주세요.")
    @GetMapping("/closing/amount")
    public ApiResponse<BudgetRedistributionResponseDTO.GetCalculatedAmountResultDTO> getCalculatedAmount(@RequestParam @NotNull(message = "year 값은 필수 입력 값입니다.") int year,
                                                                                                         @RequestParam @NotNull(message = "month 값은 필수 입력 값입니다.") int month,
                                                                                                         @RequestParam @NotNull(message = "day 값은 필수 입력 값입니다.") int day,
                                                                                                         @RequestParam @NotNull(message = "amount 값은 필수 입력 값입니다.") Long amount,
                                                                                                         @AuthenticationPrincipal MemberDetail memberDetail){
        Long calculatedAmount = budgetRedistributionService.calculatingAmount(year, month, day, amount, memberDetail.getMember().getId());
        return ApiResponse.onSuccess(BudgetRedistributionConverter.toGetCalculatedAmountResultDTO(calculatedAmount));
    }

    @Operation(summary = "지출 영수증 조회 API", description = "지출 영수증 조회 API입니다.")
    @GetMapping("/closing")
    public ApiResponse<BudgetRedistributionResponseDTO.GetReceiptListDTO> getReceipt(@RequestParam @NotNull(message = "year 값은 필수 입력 값입니다.") int year,
                                                                                     @RequestParam @NotNull(message = "month 값은 필수 입력 값입니다.") int month,
                                                                                     @RequestParam @NotNull(message = "day 값은 필수 입력 값입니다.") int day,
                                                                                     @AuthenticationPrincipal MemberDetail memberDetail){
        List<Income> incomeList = budgetRedistributionService.getIncomeList(year, month, day, memberDetail.getMember().getId());
        List<Expenditure> expenditureList = budgetRedistributionService.getExpenditureList(year, month, day, memberDetail.getMember().getId());
        List<PullMinusClosing> pullList = budgetRedistributionService.getPullList(year, month, day, memberDetail.getMember().getId());
        List<PushPlusClosing> pushList = budgetRedistributionService.getPushList(year, month, day, memberDetail.getMember().getId());
        Integer dayBudget = budgetRedistributionService.getDayBudget(year, month, day, memberDetail.getMember().getId());
        Long totalExpenditureAmount = budgetRedistributionService.getTotalExpenditureAmount(year, month, day, memberDetail.getMember().getId());
        return ApiResponse.onSuccess(BudgetRedistributionConverter.ToGetReceiptListDTO(incomeList, expenditureList, pullList, pushList, dayBudget, totalExpenditureAmount));
    }

    @Operation(summary = "지출 마감 확인 API", description = "true면 지출 마감, false면 마감 x")
    @GetMapping("/closing/check")
    public ApiResponse<BudgetRedistributionResponseDTO.GetClosingCheckDTO> getClosingCheck(@RequestParam @NotNull(message = "year 값은 필수 입력 값입니다.") int year,
                                                                                           @RequestParam @NotNull(message = "month 값은 필수 입력 값입니다.") int month,
                                                                                           @RequestParam @NotNull(message = "day 값은 필수 입력 값입니다.") int day,
                                                                                           @AuthenticationPrincipal MemberDetail memberDetail){
        Boolean check = budgetRedistributionService.closingCheck(year, month, day, memberDetail.getMember().getId());
        return ApiResponse.onSuccess(BudgetRedistributionConverter.ToGetClosingCheckDTO(check));
    }

    @Operation(summary = "마지막 지출 마감일 확인 API", description = "마지막 지출 마감일 확인 API")
    @GetMapping("/closing/check/last")
    public ApiResponse<BudgetRedistributionResponseDTO.GetClosingCheckLastDTO> getClosingCheckLast(@AuthenticationPrincipal MemberDetail memberDetail){
        LocalDate last = budgetRedistributionService.closingCheckLast(memberDetail.getMember().getId());
        return ApiResponse.onSuccess(BudgetRedistributionConverter.ToGetClosingCheckLastDTO(last));
    }
}
