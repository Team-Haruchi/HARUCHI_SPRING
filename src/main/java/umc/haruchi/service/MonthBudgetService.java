package umc.haruchi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.haruchi.apiPayload.ApiResponse;
import umc.haruchi.apiPayload.code.status.ErrorStatus;
import umc.haruchi.apiPayload.exception.handler.DayBudgetHandler;
import umc.haruchi.apiPayload.exception.handler.MonthBudgetHandler;
import umc.haruchi.converter.DayBudgetConverter;
import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.Member;
import umc.haruchi.domain.MonthBudget;
import umc.haruchi.domain.enums.DayBudgetStatus;
import umc.haruchi.repository.DayBudgetRepository;
import umc.haruchi.repository.MemberRepository;
import umc.haruchi.repository.MonthBudgetRepository;
import umc.haruchi.web.dto.DayBudgetRequestDTO;
import umc.haruchi.web.dto.MonthBudgetRequestDTO;
import umc.haruchi.web.dto.MonthBudgetResponseDTO;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthBudgetService {
    private final MonthBudgetRepository monthBudgetRepository;
    private final MemberRepository memberRepository;
    private final DayBudgetRepository dayBudgetRepository;

    @Transactional
    public MonthBudget updateMonthBudget(Long memberId, MonthBudgetRequestDTO.UpdateMonthDTO request) {
        LocalDate today = LocalDate.now();

        //member가 존재하는 지 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.NO_MEMBER_EXIST));

        //member와 year, month 기반으로 해당하는 monthBudget 찾기
        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, today.getYear(), today.getMonthValue())
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.MONTH_BUDGET_NOT_FOUND));

        monthBudget.updateMonthBudget(request.getMonthBudget());

        //하루 예산 재분배하여 저장
        List<DayBudget> dayBudgets = distributeDayBudgets(memberId);

        dayBudgetRepository.saveAll(dayBudgets);
        return monthBudgetRepository.save(monthBudget);
    }

    @Transactional
    public List<DayBudget> distributeDayBudgets(Long memberId) {
        LocalDate today = LocalDate.now();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.NO_MEMBER_EXIST));

        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, today.getYear(), today.getMonthValue())
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.MONTH_BUDGET_NOT_FOUND));

        //현재 날짜
        LocalDate now = LocalDate.now();
        int nowDay = now.getDayOfMonth();

        int year = monthBudget.getYear();
        int month = monthBudget.getMonth();
        int dayInMonth = YearMonth.of(year, month).lengthOfMonth();

        //남은 일자
        int remainingDays = dayInMonth - nowDay + 1;

        //남은 한달 예산
        long usedAmount = monthBudget.getUsedAmount() != null ? monthBudget.getUsedAmount() : 0L;
        long monthBudgetAmount = monthBudget.getMonthBudget() - usedAmount;

        //하루 예산
        long dayBudgetAmount = monthBudgetAmount / remainingDays;

        //10의자리 절사된 하루 예산
        int distributedAmount = (int) (roundDownToNearestHundred(dayBudgetAmount));

        //세이프박스에 넣을 금액
        long safeBoxAmount = (dayBudgetAmount - distributedAmount) * remainingDays;

        List<DayBudget> dayBudgets = new ArrayList<>();

        for(int day = 1; day <= dayInMonth; day++) {
            final int currentDay = day;
            //현재 일자의 전날까지는 status가 INACTIVE인 dayBudget을 생성하고
            //현재 일자부터 말일까지는 status가 ACTIVE인 dayBudget 생성
            DayBudgetStatus status = (day < nowDay) ? DayBudgetStatus.INACTIVE : DayBudgetStatus.ACTIVE;
            //현재 일자 전날까지는 0으로 설정, 현재 일자부터 말일까지는 distributedAmount로 설정
            int nowDistributedAmount = (day < nowDay) ? 0 : distributedAmount;

            //이미 생성된 dayBudget이 있으면 update, 아니면 create
            DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, currentDay)
                    .orElseGet(() -> DayBudgetConverter.toDayBudget(nowDistributedAmount, currentDay, status, monthBudget));

            dayBudget.setStatus(status);
            dayBudget.setDayBudget(nowDistributedAmount);

            dayBudgets.add(dayBudget);
        }

        //절사한 값 세이프박스에 저장
        member.addSafeBox(safeBoxAmount);

        return dayBudgets;
    }

    private long roundDownToNearestHundred(long amount) {
        return (amount / 100) * 100;
    }

}
