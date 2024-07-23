package umc.haruchi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import umc.haruchi.apiPayload.ApiResponse;
import umc.haruchi.converter.DayBudgetConverter;
import umc.haruchi.service.DayBudgetService;
import umc.haruchi.web.dto.DayBudgetResponseDTO;

@RestController
@RequestMapping("/daily-budget")
public class DayBudgetController {

    @Autowired
    private DayBudgetService dayBudgetService;


    @Operation(summary = "하루 예산을 조회하는 API", description = "회원의 하루 예산을 조회하는 API 입니다.")
    @GetMapping("")
    public ApiResponse<DayBudgetResponseDTO.getDayBudget> getDailyBudget(@RequestParam(name = "memberId") Long memberId){
        Integer todayBudget = dayBudgetService.findDayBudget(memberId);
        return ApiResponse.onSuccess(DayBudgetConverter.toGetDayBudget(todayBudget));
    }

    @Operation(summary = "수입 등록 API", description = "하루 수입을 등록하는 API 입니다.")
    @PostMapping("/income")
    public ApiResponse<?> createIncome(){

        return null;
    }

    @Operation(summary = "수입 삭제 API", description = "하루 수입을 삭제하는 API 입니다.")
    @DeleteMapping("/income/{incomeId}")
    public ApiResponse<?> deleteIncome(@PathVariable Long incomeId,
                                       @RequestParam(name = "memberId") Long memberId){
        dayBudgetService.deleteIncome(memberId, incomeId);
        return null;
    }

    @Operation(summary = "수입 조회 API", description = "하루 수입을 모두 조회하는 API 입니다.")
    @PostMapping("/income/list")
    public ApiResponse<?> getIncome(@RequestParam(name = "memberId") Long memberId){

        return null;
    }
}
