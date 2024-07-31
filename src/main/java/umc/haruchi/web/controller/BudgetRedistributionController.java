package umc.haruchi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.haruchi.apiPayload.ApiResponse;
import umc.haruchi.config.login.auth.MemberDetail;
import umc.haruchi.converter.BudgetRedistributionConverter;
import umc.haruchi.domain.PullMinusClosing;
import umc.haruchi.domain.PushPlusClosing;
import umc.haruchi.service.BudgetRedistributionService;
import umc.haruchi.web.dto.BudgetRedistributionRequestDTO;
import umc.haruchi.web.dto.BudgetRedistributionResponseDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/budget-redistribution")
public class BudgetRedistributionController {

    private final BudgetRedistributionService budgetRedistributionService;

    @Operation(summary = "넘겨쓰기 API", description = "EVENLY(1/n),DATE(특정일),SAFEBOX(targetId null)")
    @PostMapping("/push")
    public ApiResponse<BudgetRedistributionResponseDTO.BudgetPushResultDTO> pushBudget(@RequestBody BudgetRedistributionRequestDTO.createPushDTO request,
                                                                                       @AuthenticationPrincipal MemberDetail memberDetail){
        PushPlusClosing pushPlusClosing = budgetRedistributionService.push(request, memberDetail.getMember().getId());
        return ApiResponse.onSuccess(BudgetRedistributionConverter.ToBudgetPushResultDTO(pushPlusClosing));
    }

    @Operation(summary = "당겨쓰기 API", description = "EVENLY(1/n),DATE(특정일),SAFEBOX(sourceId null)")
    @PostMapping("/pull")
    public ApiResponse<BudgetRedistributionResponseDTO.BudgetPullResultDTO> pullBudget(@RequestBody BudgetRedistributionRequestDTO.createPullDTO request,
                                                                                       @AuthenticationPrincipal MemberDetail memberDetail){
        PullMinusClosing pushMinusClosing = budgetRedistributionService.pull(request, memberDetail.getMember().getId());
        return ApiResponse.onSuccess(BudgetRedistributionConverter.ToBudgetPullResultDTO(pushMinusClosing));
    }

    @Operation(summary = "지출 마감 API", description = "음수,양수,0으로 넘겨주시고, 0일 떄 옵션(EVENLY,SAFEBOX)은 null로 넘겨주세요")
    @PostMapping("/closing")
    public ApiResponse<BudgetRedistributionResponseDTO.BudgetClosingResultDTO> closingBudget(@RequestBody BudgetRedistributionRequestDTO.createClosingDTO request,
                                                                                       @AuthenticationPrincipal MemberDetail memberDetail){
        if(request.getAmount() >= 0) {
            PushPlusClosing pushPlusClosing = budgetRedistributionService.closingPlusOrZero(request, memberDetail.getMember().getId());
            return ApiResponse.onSuccess(BudgetRedistributionConverter.ToBudgetClosingResultDTO(pushPlusClosing));
        }
        else {
            PullMinusClosing pullMinusClosing = budgetRedistributionService.closingMinus(request, memberDetail.getMember().getId());
            return ApiResponse.onSuccess(BudgetRedistributionConverter.ToBudgetClosingResultDTO(pullMinusClosing));
        }
    }

//    @Operation(summary = "지출 마감 영수증에서 고르게 분배하기 선택 시 얼마씩 분배/차감할 지 알려주는 API", description = "지출 마감 진행 API")
//    @PostMapping("/closing/amount")
//    public ApiResponse<BudgetRedistributionResponseDTO.BudgetClosingResultDTO> pullBudget(@RequestBody BudgetRedistributionRequestDTO.changeClosingDTO request,
//                                                                                          @AuthenticationPrincipal MemberDetail memberDetail){
//        PullMinusClosing pushPlusClosing = budgetRedistributionService.pull(request, memberDetail.getMember().getId());
//        return ApiResponse.onSuccess(BudgetRedistributionConverter.ToBudgetPullResultDTO(pushPlusClosing));
//    }
}
