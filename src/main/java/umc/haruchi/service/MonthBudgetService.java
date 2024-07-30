package umc.haruchi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.haruchi.apiPayload.ApiResponse;
import umc.haruchi.apiPayload.code.status.ErrorStatus;
import umc.haruchi.apiPayload.exception.handler.DayBudgetHandler;
import umc.haruchi.apiPayload.exception.handler.MonthBudgetHandler;
import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.Member;
import umc.haruchi.domain.MonthBudget;
import umc.haruchi.repository.MemberRepository;
import umc.haruchi.repository.MonthBudgetRepository;
import umc.haruchi.web.dto.MonthBudgetRequestDTO;
import umc.haruchi.web.dto.MonthBudgetResponseDTO;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthBudgetService {
    private final MonthBudgetRepository monthBudgetRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public MonthBudget updateMonthBudget(Long memberId, MonthBudgetRequestDTO.UpdateMonthDTO request) {
        LocalDate today = LocalDate.now();

        //member가 존재하는 지 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.NO_MEMBER_EXIST));

        //member와 year, month 기반으로 해당하는 monthBudget 찾기
        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, today.getYear(), today.getMonthValue())
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.MONTH_BUDGET_NOT_FOUND));

        //monthBudget이 request body에 전달되었는지 확인
        if(request.getMonthBudget() == null)
            throw new MonthBudgetHandler(ErrorStatus.NOT_MONTH_BUDGET);

        if(!(request.getMonthBudget() > 0))
            throw new MonthBudgetHandler(ErrorStatus.NOT_MONTH_BUDGET);
        monthBudget.updateMonthBudget(request.getMonthBudget());
        return monthBudget;
    }
}
