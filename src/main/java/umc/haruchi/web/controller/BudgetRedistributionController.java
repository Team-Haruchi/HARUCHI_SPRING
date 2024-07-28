package umc.haruchi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.PrincipalMethodArgumentResolver;
import umc.haruchi.apiPayload.ApiResponse;
import umc.haruchi.config.login.auth.MemberDetail;
import umc.haruchi.converter.DayBudgetConverter;
import umc.haruchi.domain.Income;
import umc.haruchi.service.BudgetRedistributionService;
import umc.haruchi.web.dto.BudgetRedistributionRequestDTO;
import umc.haruchi.web.dto.BudgetRedistributionResponseDTO;
import umc.haruchi.web.dto.DayBudgetRequestDTO;
import umc.haruchi.web.dto.DayBudgetResponseDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/budget-redistribution")
public class BudgetRedistributionController {

    private final BudgetRedistributionService budgetRedistributionService;

//    @Operation(summary = "넘겨쓰기 API", description = "넘겨쓰기 진행 API")
//    @PostMapping("/push")
//    public ApiResponse<BudgetRedistributionResponseDTO.BudgetPushDTO> pushBudget(@RequestBody BudgetRedistributionRequestDTO.createPushDTO request,
//                                                                                 @AuthenticationPrincipal MemberDetail memberDetail){
//        budgetRedistributionService.push(request, memberDetail.getMember());
//        return ApiResponse.onSuccess(BudgetRedistributionConverter.toBudgetPushDTO(income));
//    }

//    @Operation(summary = "당겨쓰기 API", description = "당겨쓰기 진행 API")
//    @PostMapping("/pull")
//    public ApiResponse<BudgetRedistributionRequestDTO.incomeReg> pushBudget(@RequestBody BudgetRedistributionRequestDTO.createIncomeDTO request,
//                                                                            @AuthenticationPrincipal MemberDetail memberDetail){
//        Income income = budgetRedistributionService.joinIncome(request, memberDetail.getMember);
//        return ApiResponse.onSuccess(BudgetRedistributionConverter.toCreateIncome(income));
//    }
}
