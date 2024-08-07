package umc.haruchi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.haruchi.apiPayload.ApiResponse;
import umc.haruchi.apiPayload.code.status.ErrorStatus;
import umc.haruchi.apiPayload.exception.handler.DayBudgetHandler;
import umc.haruchi.apiPayload.exception.handler.MonthBudgetHandler;
import umc.haruchi.converter.DayBudgetConverter;
import umc.haruchi.converter.MonthBudgetConverter;
import umc.haruchi.domain.DayBudget;
import umc.haruchi.domain.Member;
import umc.haruchi.domain.MonthBudget;
import umc.haruchi.domain.enums.ClosingStatus;
import umc.haruchi.domain.enums.DayBudgetStatus;
import umc.haruchi.repository.DayBudgetRepository;
import umc.haruchi.repository.MemberRepository;
import umc.haruchi.repository.MonthBudgetRepository;
import umc.haruchi.web.dto.DayBudgetRequestDTO;
import umc.haruchi.web.dto.MonthBudgetRequestDTO;
import umc.haruchi.web.dto.MonthBudgetResponseDTO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static umc.haruchi.apiPayload.code.status.ErrorStatus.NOT_SOME_DAY_BUDGET;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthBudgetService {
    private final MonthBudgetRepository monthBudgetRepository;
    private final MemberRepository memberRepository;
    private final DayBudgetRepository dayBudgetRepository;

    @Transactional
    public MonthBudget createMonthBudget(Long memberId, Integer year, Integer month, Long budget) {
        //member가 존재하는 지 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.NO_MEMBER_EXIST));

        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, year, month)
                .orElseGet(() -> MonthBudgetConverter.toMonthBudgetWithMonth(budget, year, month));

        //지정된 날짜의 MonthBudget 생성
        monthBudget.setMember(member);

        return monthBudgetRepository.save(monthBudget);
    }

    @Transactional
    public MonthBudget updateMonthBudget(Long memberId, MonthBudgetRequestDTO.UpdateMonthDTO request) {
        LocalDate today = LocalDate.now();

        //member가 존재하는 지 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.NO_MEMBER_EXIST));

        //member와 year, month 기반으로 해당하는 monthBudget 찾기
        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, today.getYear(), today.getMonthValue())
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.MONTH_BUDGET_NOT_FOUND));

        monthBudget.updateMonthBudget(request.getMonthBudget(), today.getYear(), today.getMonthValue());

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
            ClosingStatus closingStatus = (day < nowDay) ? ClosingStatus.ZERO : null;
            //현재 일자 전날까지는 0으로 설정, 현재 일자부터 말일까지는 distributedAmount로 설정
            int nowDistributedAmount = (day < nowDay) ? 0 : distributedAmount;

            //이미 생성된 dayBudget이 있으면 update, 아니면 create
            DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, currentDay)
                    .orElseGet(() -> DayBudgetConverter.toDayBudget(nowDistributedAmount, currentDay, status, closingStatus, monthBudget));

            dayBudget.setStatus(status);
            dayBudget.setDayBudget(nowDistributedAmount);

            dayBudgets.add(dayBudget);
        }

        //절사한 값 세이프박스에 저장
        member.addSafeBox(safeBoxAmount);

        return dayBudgets;
    }

    //저번 달의 dayBudget 생성에만 사용(우선 지난 달 dayBudget이 없을 때 생성용)
    @Transactional
    public List<DayBudget> createDayBudgetsWithMonth(Long memberId, Integer year, Integer month, Long budget) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.NO_MEMBER_EXIST));

        MonthBudget monthBudget = createMonthBudget(memberId, year, month, budget);

        //지정된 달의 일 수
        int dayInMonth = YearMonth.of(year, month).lengthOfMonth();

        List<DayBudget> dayBudgets = new ArrayList<>();

        for(int day = 1; day <= dayInMonth; day++) {
            final int currentDay = day;
            //이전 달의 dayBudget을 모두 INACTIVE로 생성
            DayBudgetStatus status = DayBudgetStatus.INACTIVE;
            ClosingStatus closingStatus = ClosingStatus.ZERO;
            int nowDistributedAmount = 0;

            //dayBudget 생성 혹은 업뎃
            DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, day)
                    .orElseGet(() -> DayBudgetConverter.toDayBudget(nowDistributedAmount, currentDay, status, closingStatus, monthBudget));

            dayBudget.setStatus(status);
            dayBudget.setDayBudget(nowDistributedAmount);

            dayBudgets.add(dayBudget);
        }

        return dayBudgetRepository.saveAll(dayBudgets);
    }

    public MonthBudget getMonthBudget(Long memberId) {
        LocalDate today = LocalDate.now();

        //member가 존재하는 지 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.NO_MEMBER_EXIST));

        //member와 year, month 기반으로 해당하는 monthBudget 찾기
        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, today.getYear(), today.getMonthValue())
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.MONTH_BUDGET_NOT_FOUND));

        return monthBudget;
    }

    public double getMonthUsedPercent(Long memberId) {
        LocalDate today = LocalDate.now();

        //member가 존재하는 지 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.NO_MEMBER_EXIST));

        //member와 year, month 기반으로 해당하는 monthBudget 찾기
        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, today.getYear(), today.getMonthValue())
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.MONTH_BUDGET_NOT_FOUND));

        //한달 지출률 계산
        double monthUsedAmountPercent = ((double)monthBudget.getUsedAmount() / (double)monthBudget.getMonthBudget())*100;

        //소수점 6번째자리에서 반올림해서 리턴
        return Math.round(monthUsedAmountPercent*1000000)/1000000.0;
    }

    public List<DayBudget> getWeekBudget(Long memberId) {
        LocalDate today = LocalDate.now();

        //member가 존재하는 지 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.NO_MEMBER_EXIST));

        //member와 year, month 기반으로 해당하는 monthBudget 찾기
        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, today.getYear(), today.getMonthValue())
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.MONTH_BUDGET_NOT_FOUND));

        //상대날짜 구하기
        int dayInWeek = today.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();

        //이번 주 월요일 구하기
        int firstDayOfWeek = today.getDayOfMonth() - dayInWeek;

        //남은 일수의 dayBudget 구하기
        List<DayBudget> dayBudgets = new ArrayList<>();

        //이번주에 저번달이 포함되어 있다면 저번 달의 monthBudget 가져옴
        //저번 달의 monthBudget 없으면 dayBudget에 null 채우기
        Integer daysInLastMonth = 0;
        Integer newYear = 0;
        Integer newMonth = 0;

        if(firstDayOfWeek < 1) {
            //헌재 1월이라면 작년 12월의 monthBudget
            if(monthBudget.getMonth() == 1) {
                daysInLastMonth = YearMonth.of(today.getYear()-1, 12).lengthOfMonth();
                newYear = today.getYear() - 1;
                newMonth = 12;
            }
            //아니라면 저번달의 monthBudget
            else {
                daysInLastMonth = YearMonth.of(today.getYear(), monthBudget.getMonth()-1).lengthOfMonth();
                newYear = today.getYear();
                newMonth = monthBudget.getMonth()-1;
            }

            //지난달 monthBudget 찾기(없으면 null)
            MonthBudget lastMonthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, newYear, newMonth)
                    .orElseGet(() -> null);

            //dayBudget 찾기
            for(int i=0; i< 7; i++){
                int plusDay = i;
                if(firstDayOfWeek + i < 1) {
                    plusDay += daysInLastMonth;
                    //저번달 날짜고 lastMonthBudget이 없다면 null 저장
                    if(lastMonthBudget == null) {
                        DayBudget dayBudget = null;
                        dayBudgets.add(dayBudget);
                    }
                    //저번달 날짜고 lastMonthBudget이 있다면 lastMonthBudget의 dayBudget 저장
                    else {
                        DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(lastMonthBudget, firstDayOfWeek + plusDay)
                                .orElseThrow(() -> new DayBudgetHandler(NOT_SOME_DAY_BUDGET));
                        dayBudgets.add(dayBudget);
                    }
                }
                //이번달 날짜라면 이번달 monthBudget에서 찾기
                else {
                    DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, firstDayOfWeek + plusDay)
                            .orElseThrow(() -> new DayBudgetHandler(NOT_SOME_DAY_BUDGET));
                    dayBudgets.add(dayBudget);
                }
            }
        }
        else {
            for(int i=0; i< 7; i++){
                DayBudget dayBudget = dayBudgetRepository.findByMonthBudgetAndDay(monthBudget, firstDayOfWeek+i)
                        .orElseThrow(() -> new DayBudgetHandler(NOT_SOME_DAY_BUDGET));
                dayBudgets.add(dayBudget);
            }
        }

        //이번 주의 dayBudget 리턴
        return dayBudgets;
    }

    public List<Integer> getMonthAndWeek(Long memberId) {
        LocalDate today = LocalDate.now();

        //member가 존재하는 지 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.NO_MEMBER_EXIST));

        //member와 year, month 기반으로 해당하는 monthBudget 찾기
        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, today.getYear(), today.getMonthValue())
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.MONTH_BUDGET_NOT_FOUND));

        List<Integer> currentWeek = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        Integer week = Integer.valueOf(calendar.get(Calendar.WEEK_OF_MONTH));

        //현재 달
        currentWeek.add(monthBudget.getMonth());
        currentWeek.add(week);

        return currentWeek;
    }


    public Integer getMonthLeftDay(Long memberId) {
        LocalDate today = LocalDate.now();

        //member가 존재하는 지 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.NO_MEMBER_EXIST));

        //member와 year, month 기반으로 해당하는 monthBudget 찾기
        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(memberId, today.getYear(), today.getMonthValue())
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.MONTH_BUDGET_NOT_FOUND));

        //현재 날짜
        int nowDay = today.getDayOfMonth();

        int year = monthBudget.getYear();
        int month = monthBudget.getMonth();
        int dayInMonth = YearMonth.of(year, month).lengthOfMonth();

        //남은 일자
        return dayInMonth - nowDay + 1;
    }

    public Long getMonthLeftBudget(Long memberId) {
        LocalDate today = LocalDate.now();

        //member가 존재하는 지 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.NO_MEMBER_EXIST));

        //member와 year, month 기반으로 해당하는 monthBudget 찾기
        MonthBudget monthBudget = monthBudgetRepository.findByMemberIdAndYearAndMonth(member.getId(), today.getYear(), today.getMonthValue())
                .orElseThrow(() -> new MonthBudgetHandler(ErrorStatus.MONTH_BUDGET_NOT_FOUND));

        //남은 예산
        return monthBudget.getMonthBudget() -monthBudget.getUsedAmount();
    }

    private long roundDownToNearestHundred(long amount) {
        return (amount / 100) * 100;
    }

}
