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

}
