package umc.haruchi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.haruchi.apiPayload.ApiResponse;
import umc.haruchi.config.login.auth.MemberDetail;
import umc.haruchi.converter.BudgetRedistributionConverter;
import umc.haruchi.domain.PushPlusClosing;
import umc.haruchi.service.BudgetRedistributionService;
import umc.haruchi.web.dto.BudgetRedistributionRequestDTO;
import umc.haruchi.web.dto.BudgetRedistributionResponseDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/budget-redistribution")
public class BudgetRedistributionController {

    private final BudgetRedistributionService budgetRedistributionService;

    @Operation(summary = "넘겨쓰기 API", description = "넘겨쓰기 진행 API")
    @PostMapping("/push")
    public ApiResponse<BudgetRedistributionResponseDTO.BudgetPushResultDTO> pushBudget(@RequestBody BudgetRedistributionRequestDTO.createPushDTO request,
                                                                                       @AuthenticationPrincipal MemberDetail memberDetail){
        PushPlusClosing pushPlusClosing = budgetRedistributionService.push(request, memberDetail.getMember());
        return ApiResponse.onSuccess(BudgetRedistributionConverter.ToBudgetPushResultDTO(pushPlusClosing));
    }

//    @Operation(summary = "당겨쓰기 API", description = "당겨쓰기 진행 API")
//    @PostMapping("/pull")
//    public ApiResponse<BudgetRedistributionResponseDTO.BudgetPullResultDTO> pullBudget(@RequestBody BudgetRedistributionRequestDTO.createPullDTO request,
//                                                                                       @AuthenticationPrincipal MemberDetail memberDetail){
//        PushPlusClosing pushPlusClosing = budgetRedistributionService.pull(request, memberDetail.getMember());
//        return ApiResponse.onSuccess(BudgetRedistributionConverter.ToBudgetPullResultDTO(pushPlusClosing));
//    }
}
