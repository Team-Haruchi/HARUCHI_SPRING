package umc.haruchi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import umc.haruchi.apiPayload.ApiResponse;
import umc.haruchi.domain.MonthBudget;
import umc.haruchi.service.MonthBudgetService;
import umc.haruchi.web.dto.MonthBudgetRequestDTO;
import umc.haruchi.web.dto.MonthBudgetResponseDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/monthly-budget")
public class MonthBudgetController {
    private final MonthBudgetService monthBudgetService;

    //한달 예산 수정
    @Operation(summary = "한달 예산 수정 API", description = "본인의 한달 예산을 수정하는 API 입니다.")
    @PatchMapping("/")
    public ApiResponse<MonthBudgetResponseDTO> updateMonthBudget(@RequestParam(name = "memberId") Long memberId, @RequestBody @Valid MonthBudgetRequestDTO.UpdateMonthDTO request) {
        monthBudgetService.updateMonthBudget(memberId, request);
        return ApiResponse.onSuccess(null);
    }
}
