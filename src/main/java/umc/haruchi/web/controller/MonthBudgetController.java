package umc.haruchi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import umc.haruchi.apiPayload.ApiResponse;
import umc.haruchi.config.login.auth.MemberDetail;
import umc.haruchi.converter.MonthBudgetConverter;
import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.Member;
import umc.haruchi.domain.MonthBudget;
import umc.haruchi.service.MonthBudgetService;
import umc.haruchi.web.dto.MonthBudgetRequestDTO;
import umc.haruchi.web.dto.MonthBudgetResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/monthly-budget")
public class MonthBudgetController {
    private final MonthBudgetService monthBudgetService;

    //한달 예산 수정
    @Operation(summary = "한달 예산 수정 API", description = "본인의 한달 예산을 수정하는 API 입니다.")
    @PatchMapping("/")
    public ApiResponse<MonthBudgetResponseDTO.UpdateMonthResultDTO> updateMonthBudget(@AuthenticationPrincipal MemberDetail memberDetail, @RequestBody @Valid MonthBudgetRequestDTO.UpdateMonthDTO request) {
        MonthBudget monthBudget = monthBudgetService.updateMonthBudget(memberDetail.getMember().getId(), request);
        return ApiResponse.onSuccess(MonthBudgetConverter.toUpdateMonthResultDTO(monthBudget));
    }

    //한달 예산 금액 조회
    @Operation(summary = "한달 예산 금액 조회 API", description = "본인의 한달 예산 금액을 조회하는 API 입니다.")
    @GetMapping("/")
    public ApiResponse<MonthBudgetResponseDTO.GetMonthResultDTO> getMonthBudget(@AuthenticationPrincipal MemberDetail memberDetail) {
        MonthBudget monthBudget = monthBudgetService.getMonthBudget(memberDetail.getMember().getId());
        return ApiResponse.onSuccess(MonthBudgetConverter.toGetMonthResultDTO(monthBudget));
    }

    //한달 지출률 조회
    @Operation(summary =  "한달 지출률 조회 API", description = "본인의 한달 지출률을 조회하는 API")
    @GetMapping("/percent")
    public ApiResponse<MonthBudgetResponseDTO.GetMonthUsedPercentResultDTO> getMonthBudgetPercent(@AuthenticationPrincipal MemberDetail memberDetail) {
        double monthBudgetPercent = monthBudgetService.getMonthUsedPercent(memberDetail.getMember().getId());
        return ApiResponse.onSuccess(MonthBudgetConverter.toGetMonthUsedPercentResultDTO(monthBudgetPercent));
    }

    //한 주 예산 금액 조회
    @Operation(summary = "한 주 예산 금액 조회 API", description = "본인의 한 주 예산 금액을 조회하는 API 입니다.")
    @GetMapping("/week")
    public ApiResponse<MonthBudgetResponseDTO.GetWeekBudgetResultListDTO> getWeekBudget(@AuthenticationPrincipal MemberDetail memberDetail) {
        List<DayBudget> weekBudget = monthBudgetService.getWeekBudget(memberDetail.getMember().getId());
        List<Integer> currentWeek = monthBudgetService.getMonthAndWeek(memberDetail.getMember().getId());
        return ApiResponse.onSuccess(MonthBudgetConverter.toGetWeekBudgetResultListDTO(weekBudget, currentWeek.get(0), currentWeek.get(1)));
    }

    //남은 일정 및 금액 조회
    @Operation(summary = "남은 일정 및 금액 조회 API", description = "본인의 남은 한달 일정 및 금액을 조회하는 API")
    @GetMapping("/left-now")
    public ApiResponse<MonthBudgetResponseDTO.GetMonthLeftNowResultDTO> getMonthLeftNow(@AuthenticationPrincipal MemberDetail memberDetail) {
        Integer leftDay = monthBudgetService.getMonthLeftDay(memberDetail.getMember().getId());
        Long leftBudget = monthBudgetService.getMonthLeftBudget(memberDetail.getMember().getId());
        return ApiResponse.onSuccess(MonthBudgetConverter.toGetMonthLeftNowResultDTO(leftDay, leftBudget));
    }
}
