package umc.haruchi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.haruchi.apiPayload.ApiResponse;
import umc.haruchi.config.login.auth.MemberDetail;
import umc.haruchi.converter.DayBudgetConverter;
import umc.haruchi.domain.Expenditure;
import umc.haruchi.domain.Income;
import umc.haruchi.service.DayBudgetService;
import umc.haruchi.web.dto.DayBudgetRequestDTO;
import umc.haruchi.web.dto.DayBudgetResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/daily-budget")
public class DayBudgetController {

    @Autowired
    private DayBudgetService dayBudgetService;


    @Operation(summary = "하루 예산을 조회하는 API.", description = "회원의 하루 예산을 조회하는 API 입니다.")
    @GetMapping("")
    public ApiResponse<DayBudgetResponseDTO.getDayBudget> getDailyBudget(@AuthenticationPrincipal MemberDetail memberDetail){
        Integer todayBudget = dayBudgetService.findDayBudget(memberDetail.getMember().getId());
        return ApiResponse.onSuccess(DayBudgetConverter.toGetDayBudget(todayBudget));
    }

    @Operation(summary = "날짜별 예산 금액 조회하는 API.", description = "오늘부터 말일까지의 예산을 조회하는 API 입니다.")
    @GetMapping("/list")
    public ApiResponse<DayBudgetResponseDTO.getBudget> getAllBudget(@AuthenticationPrincipal MemberDetail memberDetail){
        List<Integer> allBudget = dayBudgetService.findAllBudget(memberDetail.getMember().getId());
        return ApiResponse.onSuccess(DayBudgetConverter.toGetBudget(allBudget));
    }

    @Operation(summary = "수입 등록 API", description = "하루 수입을 등록하는 API 입니다.")
    @PostMapping("/income")
    public ApiResponse<DayBudgetResponseDTO.incomeReg> createIncome(@Valid @RequestBody DayBudgetRequestDTO.createIncomeDTO request,
                                                                    @AuthenticationPrincipal MemberDetail memberDetail){
        System.out.println("memberId는 바로" + memberDetail.getMember().getId());
        Income income = dayBudgetService.joinIncome(request, memberDetail.getMember().getId());
        return ApiResponse.onSuccess(DayBudgetConverter.toCreateIncome(income));
    }

    @Operation(summary = "수입 삭제 API", description = "기록했던 하루 수입을 삭제하는 API 입니다.")
    @DeleteMapping("/income/{incomeId}")
    public ApiResponse<?> deleteIncome(@PathVariable Long incomeId,
                                       @AuthenticationPrincipal MemberDetail memberDetail){
        dayBudgetService.deleteIncome(memberDetail.getMember().getId(), incomeId);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "지출 등록 API", description = "하루 지출을 등록하는 API 입니다.")
    @PostMapping("/expenditure")
    public ApiResponse<DayBudgetResponseDTO.expenditureReg> createExpenditure(@Valid @RequestBody DayBudgetRequestDTO.createExpenditureDTO request,
                                                                              @AuthenticationPrincipal MemberDetail memberDetail){
        Expenditure expenditure = dayBudgetService.joinExpenditure(request, memberDetail.getMember().getId());
        return ApiResponse.onSuccess(DayBudgetConverter.toCreateExpenditure(expenditure));
    }

    @Operation(summary = "지출 삭제 API", description = "기록했던 하루 지출을 삭제하는 APi 입니다.")
    @DeleteMapping("/expenditure/{expenditureId}")
    public ApiResponse<?> deleteExpenditure(@PathVariable Long expenditureId,
                                            @AuthenticationPrincipal MemberDetail memberDetail){
        dayBudgetService.deleteExpenditure(memberDetail.getMember().getId(), expenditureId);
        return ApiResponse.onSuccess(null);
    }

}
